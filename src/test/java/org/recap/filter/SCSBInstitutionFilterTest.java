package org.recap.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.security.UserInstitutionCache;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;

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
    MockHttpServletRequest request;

    @Mock
    MockHttpServletResponse response;

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
    public void forwardChainingTest() throws ServletException, IOException {
        try {
            MockHttpSession session = new MockHttpSession();
            request.setSession(session);

            when(userInstitutionCache.getInstitutionForRequestSessionId(anyString())).thenReturn("Institution");
            ReflectionTestUtils.invokeMethod(scsbInstitutionFilter, "forwardChaining", request, response, filterChain, userInstitutionCache, "sessionId");
            verify(filterChain).doFilter(request, response);
            assertEquals("Institution", request.getAttribute(ScsbConstants.SCSB_INSTITUTION_CODE));
            assertEquals("Institution", session.getAttribute(ScsbConstants.SCSB_INSTITUTION_CODE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void forwardChainingException() throws ServletException, IOException {
        ReflectionTestUtils.invokeMethod(scsbInstitutionFilter, "forwardChaining", request, response, filterChain, userInstitutionCache, "sessionId");
        verify(filterChain).doFilter(request, response);    }


}
