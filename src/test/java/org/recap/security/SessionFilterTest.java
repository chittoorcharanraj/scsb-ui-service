package org.recap.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.util.UserAuthUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Created by Charan Raj C on 10/4/2025.
 */
public class SessionFilterTest extends BaseTestCaseUT {

    @InjectMocks
    private SessionFilter sessionFilter;

    @Mock
    private UserAuthUtil userAuthUtil;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private UserInstitutionCache userInstitutionCache;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn("session123");
        when(userInstitutionCache.getInstitutionForRequestSessionId("session123")).thenReturn("CUL");
        sessionFilter = new SessionFilter() {
            @Override
            public UserAuthUtil getUserAuthUtil() {
                return userAuthUtil;
            }
        };
    }

    @Test
    public void testDoFilter_withAuthenticatedUserWithToken() throws IOException, ServletException {
        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "pass");
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        UsernamePasswordToken shiroToken = new UsernamePasswordToken("testUser", "pass");
        when(session.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(shiroToken);
        try {
            sessionFilter.doFilter(request, response, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(response, atLeastOnce()).addCookie(argThat(cookie -> cookie.getName().equals(ScsbConstants.USER_NAME)));
    }

    @Test
    public void testDoFilter_withAuthenticatedUserWithoutToken() throws IOException, ServletException {
        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "pass");
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        when(session.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(null);
        try {
            sessionFilter.doFilter(request, response, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verify(response, atLeastOnce()).addCookie(any(Cookie.class));
        verify(userAuthUtil, never()).authorizedUser(anyString(), any());
    }

    @Test
    public void testDoFilter_withAnonymousUser() throws IOException, ServletException {
        Authentication auth = new AnonymousAuthenticationToken("key", "anonymousUser", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        sessionFilter.doFilter(request, response, filterChain);
        verify(response, never()).addCookie(any());
        verify(userAuthUtil, never()).authorizedUser(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_withNullAuthentication() throws IOException, ServletException {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        sessionFilter.doFilter(request, response, filterChain);
        verify(response, never()).addCookie(any());
        verify(userAuthUtil, never()).authorizedUser(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testDoFilter_withNullSecurityContext() throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        sessionFilter.doFilter(request, response, filterChain);
        verify(response, never()).addCookie(any());
        verify(userAuthUtil, never()).authorizedUser(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }
}
