package org.springframework.security.cas.authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.client.proxy.Cas20ProxyRetriever;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.TicketValidator;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbConstants;
import org.recap.security.SCSBCas20ServiceTicketValidator;
import org.recap.util.HelperUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.*;
import org.springframework.util.Assert;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.http.HttpRequest;

@Slf4j
@RequestScope
public class CasAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {
    private static final String CAS_STATEFUL = "_cas_stateful_";
    private static final String CAS_STATELESS = "_cas_stateless_";

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    @Setter
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService;
    private StatelessTicketCache statelessTicketCache = new NullStatelessTicketCache();

    @Setter
    private String key;

    @Setter
    private TicketValidator ticketValidator;
    @Setter
    private ServiceProperties serviceProperties;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    @Autowired
    private HttpServletRequest request;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationUserDetailsService, "An authenticationUserDetailsService must be set");
        Assert.notNull(ticketValidator, "A ticketValidator must be set");
        Assert.notNull(statelessTicketCache, "A statelessTicketCache must be set");
        Assert.hasText(key, "A Key is required so CasAuthenticationProvider can identify previously authenticated tokens");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken token &&
                !(CAS_STATEFUL.equals(token.getPrincipal()) || CAS_STATELESS.equals(token.getPrincipal()))) {
            return null;
        }

        if (authentication instanceof CasAuthenticationToken token) {
            if (this.key.hashCode() == token.getKeyHash()) {
                return authentication;
            }
            throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.incorrectKey", "Incorrect authentication token key"));
        }

        if (authentication.getCredentials() == null || authentication.getCredentials().toString().isEmpty()) {
            throw new BadCredentialsException(messages.getMessage("CasAuthenticationProvider.noServiceTicket", "No CAS service ticket provided"));
        }

        boolean stateless = isStateless(authentication);
        CasAuthenticationToken result = stateless ? statelessTicketCache.getByTicketId(authentication.getCredentials().toString()) : null;

        if (result == null) {
            result = authenticateNow(authentication);
            result.setDetails(authentication.getDetails());
        }

        if (stateless) {
            statelessTicketCache.putTicketInCache(result);
        }

        return result;
    }

    private boolean isStateless(Authentication authentication) {
        return authentication instanceof CasServiceTicketAuthenticationToken token1 && token1.isStateless()
                || authentication instanceof UsernamePasswordAuthenticationToken token && CAS_STATELESS.equals(token.getPrincipal());
    }

    private CasAuthenticationToken authenticateNow(Authentication authentication) throws AuthenticationException {
        try {
            String institution = getInstitutionFromRequest();
            if(institution == null){
                 institution = (String) request.getSession().getAttribute(ScsbConstants.SCSB_INSTITUTION_CODE);
            }
            log.info("Institution code: {}", institution);

            String casServerUrl = HelperUtil.getBean(PropertyUtil.class).getPropertyByInstitutionAndKey(institution, PropertyKeyConstants.ILS.ILS_AUTH_SERVICE_PREFIX);
            SCSBCas20ServiceTicketValidator ticketValidator = (SCSBCas20ServiceTicketValidator) this.ticketValidator;
            ticketValidator.setCasServerUrlPrefix(casServerUrl);
            ticketValidator.setProxyRetriever(new Cas20ProxyRetriever(casServerUrl, "UTF-8", ticketValidator.getURLConnectionFactory()));

            Assertion assertion = ticketValidator.validate(authentication.getCredentials().toString(), getServiceUrl(authentication));
            UserDetails userDetails = loadUserByAssertion(assertion);
            userDetailsChecker.check(userDetails);

            return new CasAuthenticationToken(this.key, userDetails, authentication.getCredentials(), authoritiesMapper.mapAuthorities(userDetails.getAuthorities()), userDetails, assertion);
        } catch (TicketValidationException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

    private String getInstitutionFromRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return (String) servletRequestAttributes.getRequest().getAttribute(ScsbConstants.SCSB_INSTITUTION_CODE);
        }
        throw new IllegalStateException("No request context available to fetch institution.");
    }

    private String getServiceUrl(Authentication authentication) {
        if (authentication.getDetails() instanceof ServiceAuthenticationDetails details) {
            return details.getServiceUrl();
        }
        if (serviceProperties == null || serviceProperties.getService() == null) {
            throw new IllegalStateException("Service properties are not set correctly.");
        }
        return serviceProperties.getService();
    }

    protected UserDetails loadUserByAssertion(Assertion assertion) {
        return authenticationUserDetailsService.loadUserDetails(new CasAssertionAuthenticationToken(assertion, ""));
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)
                || CasAuthenticationToken.class.isAssignableFrom(authentication)
                || CasAssertionAuthenticationToken.class.isAssignableFrom(authentication)
                || CasServiceTicketAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
