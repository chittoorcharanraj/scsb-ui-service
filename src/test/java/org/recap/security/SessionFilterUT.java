package org.recap.security;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.spring.ApplicationContextProvider;
import org.recap.util.UserAuthUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static org.junit.Assert.assertNotNull;

public class SessionFilterUT extends BaseTestCaseUT {
    @InjectMocks
    SessionFilter sessionFilter;
    @Mock
    ServletRequest request;
    @Mock
    ServletResponse response;
    @Mock
    FilterChain filterChain;
    @Mock
    Authentication authentication;
    @Mock
    SecurityContext context;
    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    ApplicationContextProvider applicationContextProvider;

    @Test
    public void doFilter() throws Exception {
       // Mockito.when(context.getAuthentication()).thenReturn(authentication);
//        Mockito.doCallRealMethod().when(sessionFilter).doFilter(request, response, filterChain);
        sessionFilter.doFilter(request, response, filterChain);
    }

    @Test
    public void destroy() {
        sessionFilter.destroy();
    }
    @Test
    public void getUserAuthUtil() {
        UserAuthUtil userAuthUtil = sessionFilter.getUserAuthUtil();
        assertNotNull(userAuthUtil);
    }
    @Test
    public void getUserAuthUtilNewUserAuthUtil() {
        SessionFilter sessionFilter = new SessionFilter();
        try {
           sessionFilter.getUserAuthUtil();
        }catch (Exception e){}

    }
}
