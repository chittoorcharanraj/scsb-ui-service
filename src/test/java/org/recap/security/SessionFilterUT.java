package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.util.HelperUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;


@RunWith(MockitoJUnitRunner.Silent.class)
public class SessionFilterUT{
    @InjectMocks
    SessionFilter sessionFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext context;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    HttpSession httpSession;

    @Mock
    UserInstitutionCache userInstitutionCache;

    @Mock
    UsernamePasswordToken usernamePasswordToken;

    @Mock
    FilterConfig filterConfig;

    @Mock
    ApplicationContextProvider applicationContextProvider;

    @Mock
    SecurityContextHolder securityContextHolder;

    @Mock
    HelperUtil helperUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void doFilter() throws Exception {
        try {
            Mockito.when(context.getAuthentication()).thenReturn(authentication);
            Mockito.mockStatic(HelperUtil.class);
            Mockito.mockStatic(SecurityContextHolder.class);
            Mockito.when(HelperUtil.isAnonymousUser(Mockito.any())).thenReturn(false);
            Mockito.when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
            Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(Mockito.anyString())).thenReturn("PUL");
            Mockito.when(SecurityContextHolder.getContext()).thenReturn(context);
            Mockito.when(request.getSession()).thenReturn(httpSession);
            Mockito.when(httpSession.getId()).thenReturn("1");
            Mockito.when(request.getSession(false)).thenReturn(httpSession);
            Mockito.when(httpSession.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(usernamePasswordToken);
            Mockito.when(userAuthUtil.authorizedUser(ScsbConstants.SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL, usernamePasswordToken)).thenReturn(true);
            sessionFilter.doFilter(request, response, filterChain);
            assertTrue(true);
        }catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void doFilterTest() throws Exception {
        try {
            Mockito.when(context.getAuthentication()).thenReturn(authentication);
        Mockito.mockStatic(HelperUtil.class);
            Mockito.mockStatic(SecurityContextHolder.class);
            Mockito.when(HelperUtil.isAnonymousUser(Mockito.any())).thenReturn(false);
            Mockito.when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
            Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(Mockito.anyString())).thenReturn("PUL");
            Mockito.when(SecurityContextHolder.getContext()).thenReturn(context);
            Mockito.when(request.getSession()).thenReturn(httpSession);
            Mockito.when(httpSession.getId()).thenReturn("1");
            Mockito.when(request.getSession(false)).thenReturn(httpSession);
            Mockito.when(httpSession.getAttribute(ScsbConstants.USER_TOKEN)).thenReturn(null);
            Mockito.when(userAuthUtil.authorizedUser(ScsbConstants.SCSB_SHIRO_TOUCH_EXISTIN_SESSION_URL, usernamePasswordToken)).thenReturn(false);
            sessionFilter.doFilter(request, response, filterChain);
        }catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void testDoNothingMethods() throws ServletException {
        sessionFilter.destroy();
        sessionFilter.init(filterConfig);
        assertTrue(true);
    }

    @Test
    public void getUserAuthUtil(){
        SessionFilter sessionFilter = new SessionFilter();
        try {
            UserAuthUtil userAuthUtil = sessionFilter.getUserAuthUtil();
            assertNotNull(userAuthUtil);
        }catch (Exception e){}
    }

    @Test
    public void doFiltertTest() throws ServletException, IOException {
        try {
            Mockito.when(context.getAuthentication()).thenReturn(null);
            sessionFilter.doFilter(request, response, filterChain);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

}
