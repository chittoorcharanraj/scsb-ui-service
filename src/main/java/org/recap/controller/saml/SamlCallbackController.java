package org.recap.controller.saml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.controller.AbstractController;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.saml.SamlConfig;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.security.saml.SamlAuthHandler;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;


@Slf4j
@Controller
public class SamlCallbackController extends AbstractController {

    @Autowired
    private SamlAuthHandler samlAuthHandler;

    @Autowired
    private UserInstitutionCache userInstitutionCache;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @GetMapping("/auth/saml")
    public String initiateSaml(@RequestParam("institution") String institution,
                               HttpServletRequest request) {

        log.info("SAML: Initiating SSO for institution '{}'", institution);

        SamlConfig config = buildSamlConfig(institution);

        if (!config.isActive()) {
            log.warn("SAML [{}]: SAML is disabled (saml.active=false)", institution);
            return "redirect:/?error=saml_disabled";
        }
        if (StringUtils.isBlank(config.getIdpSsoUrl())) {
            log.error("SAML [{}]: IdP SSO URL not configured in scsb_properties_t (key: {})",
                    institution, ScsbConstants.IDP_SSO_URL);
            return "redirect:/?error=saml_config_missing";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(ScsbConstants.SAML_INSTITUTION_CODE, institution);
        userInstitutionCache.addRequestSessionId(session.getId(), institution);

        String callbackUrl = buildAcsUrl(request, config);
        String redirectUrl = samlAuthHandler.buildAuthnRequestUrl(config, callbackUrl);

        log.info("SAML [{}]: Redirecting to IdP SSO: {}", institution, config.getIdpSsoUrl());
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/home")
    public String samlAcs(@RequestParam("SAMLResponse") String samlResponse,
                          @RequestParam(value = "RelayState", required = false) String relayState,
                          HttpServletRequest request) {

        log.info("SAML ACS: Received SAMLResponse POST");

        HttpSession preAuthSession = request.getSession(false);
        if (preAuthSession == null) {
            log.error("SAML ACS: No session ? timeout before IdP POST");
            return "redirect:/?error=saml_session_expired";
        }

        String institutionCode = (String) preAuthSession.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE);
        log.info("SAML ACS: institution from session = '{}'", institutionCode);

        if (StringUtils.isBlank(institutionCode)) {
            log.error("SAML ACS: Institution code missing from session");
            return "redirect:/?error=saml_session_expired";
        }

        SamlConfig config = buildSamlConfig(institutionCode);
        if (StringUtils.isBlank(config.getIdpSsoUrl())) {
            log.error("SAML ACS [{}]: IdP SSO URL not configured in scsb_properties_t", institutionCode);
            return "redirect:/?error=saml_config_missing";
        }
        log.info("SAML ACS [{}]: Config loaded from scsb_properties_t", institutionCode);

        SamlAuthHandler.SamlUserInfo userInfo =
                samlAuthHandler.processSamlResponse(samlResponse, config);

        if (userInfo == null) {
            log.error("SAML ACS [{}]: SAMLResponse validation failed", institutionCode);
            return "redirect:/?error=saml_auth_failed";
        }
        log.info("SAML ACS [{}]: SAMLResponse valid, userId='{}'", institutionCode, userInfo.userId);

        UsersEntity usersEntity = userDetailsRepository.findByLoginId(userInfo.userId);
        if (usersEntity == null) {
            log.error("SAML ACS [{}]: User '{}' not found in USER_T", institutionCode, userInfo.userId);
            return "redirect:/?error=saml_user_not_found";
        }
        log.info("SAML ACS [{}]: User '{}' found in USER_T", institutionCode, userInfo.userId);

        HttpSession session = processSessionFixation(request, institutionCode);
        session.setAttribute(ScsbConstants.SAML_AUTHENTICATED, Boolean.TRUE);

        UsernamePasswordAuthenticationToken springAuth =
                new UsernamePasswordAuthenticationToken(
                        userInfo.userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
        SecurityContextHolder.getContext().setAuthentication(springAuth);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        UsernamePasswordToken shiroToken = new UsernamePasswordToken(
                userInfo.userId + ScsbConstants.TOKEN_SPLITER + institutionCode,
                "",
                true);

        java.util.Map<String, Object> resultMap;
        try {
            resultMap = getUserAuthUtil().doAuthentication(shiroToken);
        } catch (Exception e) {
            log.error("SAML ACS [{}]: Exception occurred in authentication: {}", institutionCode, e.getLocalizedMessage());
            return "redirect:/?error=saml_auth_failed";
        }

        if (resultMap.get(ScsbConstants.IS_USER_AUTHENTICATED) != null && !(Boolean) resultMap.get(ScsbConstants.IS_USER_AUTHENTICATED)) {
            String errorMessage = (String) resultMap.get(ScsbConstants.USER_AUTH_ERRORMSG);
            log.error("SAML ACS [{}]: User: {}, Error: {}", institutionCode, userInfo.userId, errorMessage);
            return "redirect:/?error=saml_auth_failed";
        }

        session.setAttribute(ScsbConstants.USER_TOKEN, shiroToken);
        session.setAttribute(ScsbConstants.USER_AUTH, resultMap);
        setValuesInSession(session, resultMap);
        session.setAttribute(ScsbConstants.IS_USER_AUTHENTICATED, true);

        log.info("SAML ACS [{}]: Session fully established for user '{}'. Redirecting to search.",
                institutionCode, userInfo.userId);
        return "redirect:/search";
    }

    @GetMapping(value = "/saml/metadata/{institutionCode}", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String spMetadataWithCode(@PathVariable("institutionCode") String institutionCode) {
        log.info("SAML metadata: request for institution '{}' (path variable)", institutionCode);
        return generateMetadata(institutionCode);
    }

    @GetMapping(value = "/saml/metadata", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String spMetadataFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String institutionCode = (session != null)
                ? (String) session.getAttribute(ScsbConstants.SAML_INSTITUTION_CODE)
                : null;

        if (StringUtils.isBlank(institutionCode)) {
            log.warn("SAML metadata: no institution code in session and none provided in path");
            return "<!-- ERROR: institution code required. "
                    + "Use /saml/metadata/{institutionCode} or initiate login first. -->";
        }

        log.info("SAML metadata: resolving institution '{}' from session", institutionCode);
        return generateMetadata(institutionCode);
    }

    private SamlConfig buildSamlConfig(String institutionCode) {
        return SamlConfig.builder()
                .institutionCode(institutionCode)
                .idpSsoUrl(getProperty(institutionCode, ScsbConstants.IDP_SSO_URL))
                .spEntityId(getProperty(institutionCode, ScsbConstants.SP_ENTITY_ID))
                .acsUrl(getProperty(institutionCode, ScsbConstants.ACS_URL))
                .idpCertificate(getProperty(institutionCode, ScsbConstants.IDP_CERTIFICATE))
                .spCertificate(getProperty(institutionCode, ScsbConstants.SP_CERTIFICATE))
                .spPrivateKey(getProperty(institutionCode, ScsbConstants.SP_PRIVATE_KEY))
                .active(!"false".equalsIgnoreCase(
                        getProperty(institutionCode, ScsbConstants.SAML_ACTIVE))) // default true
                .build();
    }

    private String getProperty(String institutionCode, String key) {
        try {
            return propertyUtil.getPropertyByInstitutionAndKey(institutionCode, key);
        } catch (Exception e) {
            log.warn("SAML [{}]: Property '{}' not found in scsb_properties_t ? using null",
                    institutionCode, key);
            return null;
        }
    }

    private String generateMetadata(String institutionCode) {
        SamlConfig config = buildSamlConfig(institutionCode);
        if (StringUtils.isBlank(config.getIdpSsoUrl())) {
            log.warn("SAML metadata: IdP SSO URL not configured for institution '{}'", institutionCode);
            return "<!-- No SAML configuration found for institution: " + institutionCode + " -->";
        }
        return samlAuthHandler.generateSpMetadata(config);
    }

    private HttpSession processSessionFixation(HttpServletRequest request,
                                               String institutionCode) {
        String oldSessionId = request.getSession().getId();
        String cachedInstitution =
                userInstitutionCache.getInstitutionForRequestSessionId(oldSessionId);
        String effectiveInstitution =
                StringUtils.isNotBlank(cachedInstitution) ? cachedInstitution : institutionCode;

        userInstitutionCache.removeSessionId(oldSessionId);
        request.getSession(false).invalidate();

        HttpSession newSession = request.getSession(true);
        userInstitutionCache.addRequestSessionId(newSession.getId(), effectiveInstitution);
        return newSession;
    }

    private String buildAcsUrl(HttpServletRequest request, SamlConfig config) {
        if (config.getAcsUrl() != null && !config.getAcsUrl().isBlank()) {
            return config.getAcsUrl();
        }
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        StringBuilder base = new StringBuilder(scheme).append("://").append(host);
        if (("http".equals(scheme) && port != 80)
                || ("https".equals(scheme) && port != 443)) {
            base.append(":").append(port);
        }
        return base.append("/home").toString();
    }
}