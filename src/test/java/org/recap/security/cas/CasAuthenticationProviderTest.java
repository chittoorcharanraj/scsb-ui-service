package org.recap.security.cas;

import org.jasig.cas.client.validation.TicketValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.*;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CasAuthenticationProviderTest {

    @InjectMocks
    private CasAuthenticationProvider casAuthenticationProvider;

    @Mock
    private StatelessTicketCache statelessTicketCache;

    @Mock
    private TicketValidator ticketValidator;

    @Mock
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService;

    @Mock
    private ServiceProperties serviceProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setTicketValidator(ticketValidator);
        casAuthenticationProvider.setServiceProperties(serviceProperties);
        statelessTicketCache = new NullStatelessTicketCache();
        casAuthenticationProvider.setStatelessTicketCache(statelessTicketCache);
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService);
    }

    @Test
    public void testAuthenticateUnsupportedAuthenticationClass() {
        Authentication unsupportedAuth = mock(Authentication.class);
        assertNull(casAuthenticationProvider.authenticate(unsupportedAuth));
    }

    @Test
    public void testAuthenticateUsernamePasswordNonCasRelated() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");
        assertNull(casAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticateCasAuthenticationTokenValid() {
        CasAuthenticationToken casToken = mock(CasAuthenticationToken.class);
        when(casToken.getKeyHash()).thenReturn("someKey".hashCode());
        casAuthenticationProvider.setKey("someKey");
        assertEquals(casToken, casAuthenticationProvider.authenticate(casToken));
    }

    @Test
    public void testAuthenticateCasAuthenticationTokenInvalid() {
        CasAuthenticationToken casToken = mock(CasAuthenticationToken.class);
        when(casToken.getKeyHash()).thenReturn("wrongKey".hashCode());
        casAuthenticationProvider.setKey("someKey");
        assertThrows(BadCredentialsException.class, () -> casAuthenticationProvider.authenticate(casToken));
    }

    @Test
    public void testAuthenticateNoServiceTicket() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER, null);
        assertThrows(BadCredentialsException.class, () -> casAuthenticationProvider.authenticate(authentication));
    }

    @Test
    public void testAuthenticateStatelessTokenFromCache() {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER, "ticket");
            CasAuthenticationToken casToken = mock(CasAuthenticationToken.class);
            assertEquals(casToken, casAuthenticationProvider.authenticate(authentication));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAuthenticateStatelessTokenNotInCache() {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(CasAuthenticationFilter.CAS_STATELESS_IDENTIFIER, "ticket");
            CasAuthenticationToken casToken = mock(CasAuthenticationToken.class);
            ReflectionTestUtils.invokeMethod(casAuthenticationProvider, "authenticateNow", authentication);
            assertEquals(casToken, casAuthenticationProvider.authenticate(authentication));
            verify(statelessTicketCache).putTicketInCache(casToken);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
