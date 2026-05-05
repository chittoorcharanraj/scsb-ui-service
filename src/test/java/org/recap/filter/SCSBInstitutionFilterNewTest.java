package org.recap.filter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SCSBInstitutionFilterNewTest {

    @InjectMocks
    private SCSBInstitutionFilter filter;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserInstitutionCache userInstitutionCache;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;

    private MockedStatic<HelperUtil> helperUtilMock;

    private static final String SESSION_ID = "session-001";
    private static final String INSTITUTION = "PUL";
    private static final String LOGOUT_URL = "https://cas.example.com/logout";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        helperUtilMock = Mockito.mockStatic(HelperUtil.class);
        helperUtilMock.when(() -> HelperUtil.getBean(UserInstitutionCache.class))
                .thenReturn(userInstitutionCache);
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(any()))
                .thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn(SESSION_ID);
    }

    @After
    public void tearDown() {
        if (helperUtilMock != null) {
            helperUtilMock.close();
        }
    }

    @Test
    public void doFilterInternal_homePath_authCookieYesAndInstitutionCookie_shouldRedirectToLogout()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        helperUtilMock.when(() -> HelperUtil.getLogoutUrl(INSTITUTION)).thenReturn(LOGOUT_URL);
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, INSTITUTION);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie, institutionCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache).removeSessionId(SESSION_ID);
        verify(response).sendRedirect(LOGOUT_URL);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void doFilterInternal_homePath_institutionCookieBeforeAuthCookie_shouldRedirectToLogout()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        helperUtilMock.when(() -> HelperUtil.getLogoutUrl(INSTITUTION)).thenReturn(LOGOUT_URL);
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, INSTITUTION);
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie, institutionCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache).removeSessionId(SESSION_ID);
        verify(response).sendRedirect(LOGOUT_URL);
    }

    @Test
    public void doFilterInternal_homePath_authAndInstitutionCookies_shouldExpireBothCookies()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        helperUtilMock.when(() -> HelperUtil.getLogoutUrl(INSTITUTION)).thenReturn(LOGOUT_URL);
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, INSTITUTION);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie, institutionCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(response, atLeast(2)).addCookie(any(Cookie.class));
    }

    @Test
    public void doFilterInternal_homePath_sendRedirectThrowsIOException_shouldSwallowException()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        helperUtilMock.when(() -> HelperUtil.getLogoutUrl(INSTITUTION)).thenReturn(LOGOUT_URL);
        doThrow(new IOException("Network error")).when(response).sendRedirect(anyString());
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, INSTITUTION);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie, institutionCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(response).sendRedirect(LOGOUT_URL);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void doFilterInternal_homePath_authCookieYesButNoInstitutionCookie_shouldForwardChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID)).thenReturn(null);
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "Y");
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(response, never()).sendRedirect(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_homePath_authCookieNotY_shouldForwardChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID)).thenReturn(null);
        Cookie authCookie = new Cookie(ScsbConstants.IS_USER_AUTHENTICATED, "N");
        Cookie institutionCookie = new Cookie(ScsbConstants.LOGGED_IN_INSTITUTION, INSTITUTION);
        when(request.getCookies()).thenReturn(new Cookie[]{authCookie, institutionCookie});
        filter.doFilterInternal(request, response, filterChain);
        verify(response, never()).sendRedirect(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_homePath_emptyCookies_shouldForwardChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_homePath_cacheHasInstitution_shouldSetAttributeAndChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/home");
        when(request.getCookies()).thenReturn(new Cookie[]{});
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn(INSTITUTION);
        filter.doFilterInternal(request, response, filterChain);
        verify(request).setAttribute(ScsbConstants.SCSB_INSTITUTION_CODE, INSTITUTION);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_nonHomePath_withInstitutionCode_shouldRegisterSessionAndForwardChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/search");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request))
                .thenReturn(INSTITUTION);
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn(INSTITUTION);
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache).addRequestSessionId(SESSION_ID, INSTITUTION);
        verify(request).setAttribute(ScsbConstants.SCSB_INSTITUTION_CODE, INSTITUTION);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_nonHomePath_differentParamAndCacheInstitution_shouldUseParamForRegistration()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/request");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request))
                .thenReturn("CUL");
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn("CUL");
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache).addRequestSessionId(SESSION_ID, "CUL");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_nonHomePath_blankInstitutionCode_shouldSkipRegistrationAndForwardChain()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/collections");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request))
                .thenReturn(null);
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache, never()).addRequestSessionId(anyString(), anyString());
        verify(request, never()).setAttribute(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilterInternal_nonHomePath_whitespaceOnlyInstitution_shouldSkipRegistration()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/reports");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request))
                .thenReturn("   ");
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(userInstitutionCache, never()).addRequestSessionId(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void forwardChaining_whenCacheReturnsInstitution_shouldSetAttributeBeforeFilter()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/search");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request)).thenReturn(null);
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID))
                .thenReturn("NYPL");
        filter.doFilterInternal(request, response, filterChain);
        verify(request).setAttribute(ScsbConstants.SCSB_INSTITUTION_CODE, "NYPL");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void forwardChaining_whenCacheReturnsNull_shouldNotSetAttributeButStillFilter()
            throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/search");
        helperUtilMock.when(() -> HelperUtil.getInstitutionFromRequest(request)).thenReturn(null);
        when(userInstitutionCache.getInstitutionForRequestSessionId(SESSION_ID)).thenReturn(null);
        filter.doFilterInternal(request, response, filterChain);
        verify(request, never()).setAttribute(eq(ScsbConstants.SCSB_INSTITUTION_CODE), any());
        verify(filterChain).doFilter(request, response);
    }


    @Test
    public void filter_shouldNotBeNull() {
        assertNotNull(filter);
    }
}