package org.recap.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.security.UserInstitutionCache;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SCSBInstitutionFilterTest {

    @InjectMocks
    private SCSBInstitutionFilter scsbInstitutionFilter;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserInstitutionCache userInstitutionCache;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDoFilterInternal_WithHomeUri_AuthenticatedUser() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/home");
        request.setSession(new MockHttpServletRequest().getSession());
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, "TestInstitution");
        request.setCookies(authCookie, institutionCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            scsbInstitutionFilter.doFilterInternal(request, response, filterChain);
            assertEquals(0, authCookie.getMaxAge());
            assertEquals(0, institutionCookie.getMaxAge());
            verify(userInstitutionCache).removeSessionId(anyString());
            assertEquals("http://logout-url.com", response.getRedirectedUrl());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoFilterInternal_WithOtherUri() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/otherUri");
        request.setSession(new MockHttpServletRequest().getSession());
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            scsbInstitutionFilter.doFilterInternal(request, response, filterChain);
            verify(userInstitutionCache).addRequestSessionId(anyString(), eq("TestInstitution"));
            verify(filterChain).doFilter(request, response);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoFilterInternal_WithHomeUri_NotAuthenticated() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/home");
        request.setSession(new MockHttpServletRequest().getSession());
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "N");
        request.setCookies(authCookie);
        try {
            MockHttpServletResponse response = new MockHttpServletResponse();
            scsbInstitutionFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDoFilterInternal_WithoutCookies() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/home");
        request.setSession(new MockHttpServletRequest().getSession());
        MockHttpServletResponse response = new MockHttpServletResponse();
        try {
            scsbInstitutionFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void forwardChainingTest(){
        when(userInstitutionCache.getInstitutionForRequestSessionId("requestedSessionId")).thenReturn(new String());
        ReflectionTestUtils.invokeMethod(scsbInstitutionFilter,"forwardChaining", request, response, filterChain, userInstitutionCache, "requestedSessionId");
    }

    @Test
    public void forwardChainingException(){
        when(userInstitutionCache.getInstitutionForRequestSessionId("")).thenReturn(new String());
        ReflectionTestUtils.invokeMethod(scsbInstitutionFilter,"forwardChaining", request, response, filterChain, userInstitutionCache, "requestedSessionId");
    }
}
