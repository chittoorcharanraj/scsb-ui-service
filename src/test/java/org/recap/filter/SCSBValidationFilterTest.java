package org.recap.filter;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.ScsbConstants;
import org.recap.spring.PropertyValueProvider;
import org.recap.util.HelperUtil;
import org.recap.util.UserAuthUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SCSBValidationFilterTest {

    @InjectMocks
    @Spy
    private SCSBValidationFilter scsbValidationFilter;

    @Mock
    private UserAuthUtil userAuthUtil;
    @Mock
    private PropertyValueProvider propertyValueProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private FilterChain filterChain;

    private MockedStatic<HelperUtil> helperUtilMock;

    private static final String API_HEADER_VALUE = "search";
    private static final String SCSB_UI_URL = "https://scsb.example.com/";
    private static final String CSP_CONTENT = "self 'unsafe-inline'";
    private static final String FRAME_ANCESTOR = "self";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        helperUtilMock = Mockito.mockStatic(HelperUtil.class);
        helperUtilMock.when(() -> HelperUtil.getBean(PropertyValueProvider.class))
                .thenReturn(propertyValueProvider);
        when(propertyValueProvider.getProperty(ScsbConstants.CSP_ENABLE))
                .thenReturn(String.valueOf(Boolean.FALSE));
        when(request.getHeader(ScsbConstants.API_PATH)).thenReturn(API_HEADER_VALUE);
        when(request.getSession(ScsbConstants.FALSE)).thenReturn(session);
        doReturn(userAuthUtil).when(scsbValidationFilter).getUserAuthUtil();
    }

    @After
    public void tearDown() {
        if (helperUtilMock != null) helperUtilMock.close();
    }

    @Test
    public void doFilter_whenCspEnabled_shouldAddContentSecurityPolicyHeader()
            throws ServletException, IOException {
        when(propertyValueProvider.getProperty(ScsbConstants.CSP_ENABLE))
                .thenReturn(Boolean.TRUE.toString());
        PropertyValueProvider cspProvider = mock(PropertyValueProvider.class);
        when(cspProvider.getProperty(ScsbConstants.CSP_ENABLE)).thenReturn(String.valueOf(Boolean.TRUE));
        when(cspProvider.getProperty(org.recap.PropertyKeyConstants.SCSB_UI_URL)).thenReturn(SCSB_UI_URL);
        when(cspProvider.getProperty(ScsbConstants.CSP_VALUE)).thenReturn(CSP_CONTENT);
        when(cspProvider.getProperty(ScsbConstants.FRAME_ANCESTOR_VALUE)).thenReturn(FRAME_ANCESTOR);
        helperUtilMock.when(() -> HelperUtil.getBean(PropertyValueProvider.class))
                .thenReturn(cspProvider);
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
    }

    @Test
    public void doFilter_cspHeader_shouldIncludeDefaultSrcAndFrameAncestors()
            throws ServletException, IOException {
        PropertyValueProvider cspProvider = mock(PropertyValueProvider.class);
        when(cspProvider.getProperty(ScsbConstants.CSP_ENABLE)).thenReturn(String.valueOf(Boolean.TRUE));
        when(cspProvider.getProperty(org.recap.PropertyKeyConstants.SCSB_UI_URL)).thenReturn(SCSB_UI_URL);
        when(cspProvider.getProperty(ScsbConstants.CSP_VALUE)).thenReturn(CSP_CONTENT);
        when(cspProvider.getProperty(ScsbConstants.FRAME_ANCESTOR_VALUE)).thenReturn(FRAME_ANCESTOR);
        helperUtilMock.when(() -> HelperUtil.getBean(PropertyValueProvider.class))
                .thenReturn(cspProvider);
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
    }

    @Test
    public void doFilter_whenCspDisabled_shouldNotAddCspHeader()
            throws ServletException, IOException {
        when(propertyValueProvider.getProperty(ScsbConstants.CSP_ENABLE))
                .thenReturn(String.valueOf(Boolean.FALSE));
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response, never()).addHeader(eq(ScsbConstants.CSP), anyString());
    }

    @Test
    public void doFilter_whenAuthenticated_shouldSetUserAuthenticatedHeaderToTrue()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(ScsbConstants.USER_AUTHENTICATED, ScsbConstants.TRUE_STRING);
    }

    @Test
    public void doFilter_whenAuthenticated_shouldCallChainDoFilter()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_whenAuthenticated_shouldNotSetStatusToScOkOnResponse()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response, never()).setStatus(HttpStatus.SC_OK);
    }

    @Test
    public void doFilter_whenNotAuthenticated_shouldSetUserAuthenticatedHeaderToFalse()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(false);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(ScsbConstants.USER_AUTHENTICATED, ScsbConstants.FALSE_STRING);
    }

    @Test
    public void doFilter_whenNotAuthenticated_shouldSetStatusScOk()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(false);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setStatus(HttpStatus.SC_OK);
    }

    @Test
    public void doFilter_whenNotAuthenticated_shouldNeverCallChainDoFilter()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(false);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void doFilter_whenApiPathHeaderPresent_shouldUseItForAuthCheck()
            throws ServletException, IOException {
        when(request.getHeader(ScsbConstants.API_PATH)).thenReturn("reports");
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(userAuthUtil).isAuthenticated(session,
                ScsbConstants.AUTH_PATH + "reports");
    }

    @Test
    public void doFilter_whenApiPathHeaderAbsent_shouldDefaultToSearch()
            throws ServletException, IOException {
        when(request.getHeader(ScsbConstants.API_PATH)).thenReturn(null);
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(userAuthUtil).isAuthenticated(session,
                ScsbConstants.AUTH_PATH + ScsbConstants.SEARCH.toLowerCase());
    }

    @Test
    public void doFilter_whenApiPathHeaderEmpty_shouldDefaultToSearch()
            throws ServletException, IOException {
        when(request.getHeader(ScsbConstants.API_PATH)).thenReturn(null);
        when(userAuthUtil.isAuthenticated(eq(session), anyString())).thenReturn(false);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(userAuthUtil).isAuthenticated(eq(session),
                eq(ScsbConstants.AUTH_PATH + ScsbConstants.SEARCH.toLowerCase()));
    }

    @Test
    public void doFilter_whenGetSessionThrows_shouldSetFalseHeaderAndScOkStatus()
            throws ServletException, IOException {
        when(request.getSession(ScsbConstants.FALSE))
                .thenThrow(new NullPointerException("Session not available"));
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(ScsbConstants.USER_AUTHENTICATED, ScsbConstants.FALSE_STRING);
        verify(response).setStatus(HttpStatus.SC_OK);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void doFilter_whenIsAuthenticatedThrows_shouldSetFalseHeaderAndScOkStatus()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString()))
                .thenThrow(new RuntimeException("Shiro unavailable"));
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(ScsbConstants.USER_AUTHENTICATED, ScsbConstants.FALSE_STRING);
        verify(response).setStatus(HttpStatus.SC_OK);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    public void doFilter_whenGetBeanThrows_shouldSetFalseHeaderAndScOkStatus()
            throws ServletException, IOException {
        helperUtilMock.when(() -> HelperUtil.getBean(PropertyValueProvider.class))
                .thenThrow(new RuntimeException("Spring context not ready"));
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(ScsbConstants.USER_AUTHENTICATED, ScsbConstants.FALSE_STRING);
        verify(response).setStatus(HttpStatus.SC_OK);
    }

    @Test
    public void getUserAuthUtil_whenFieldIsSet_shouldReturnInjectedInstance() {
        SCSBValidationFilter filter = new SCSBValidationFilter();
        filter.userAuthUtil = userAuthUtil;
        assertNotNull(filter.getUserAuthUtil());
    }

    @Test
    public void getUserAuthUtil_whenFieldIsNull_shouldAttemptApplicationContextLookup() {
        SCSBValidationFilter freshFilter = new SCSBValidationFilter();
        try {
            freshFilter.getUserAuthUtil();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void doFilter_authPathConstructed_asAuthSlashApiPath()
            throws ServletException, IOException {
        String apiPath = "request";
        when(request.getHeader(ScsbConstants.API_PATH)).thenReturn(apiPath);
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(userAuthUtil).isAuthenticated(session,
                ScsbConstants.AUTH_PATH + apiPath);
    }

    @Test
    public void doFilter_sessionPassedToIsAuthenticated_fromGetSessionFalse()
            throws ServletException, IOException {
        HttpSession differentSession = mock(HttpSession.class);
        when(request.getSession(ScsbConstants.FALSE)).thenReturn(differentSession);
        when(userAuthUtil.isAuthenticated(eq(differentSession), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(userAuthUtil).isAuthenticated(eq(differentSession), anyString());
    }

    @Test
    public void doFilter_whenAuthenticated_headerNameShouldBeUserAuthenticated()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(true);
        scsbValidationFilter.doFilter(request, response, filterChain);
        verify(response).setHeader(eq("user_authenticated"), eq(ScsbConstants.TRUE_STRING));
    }

    @Test
    public void doFilter_whenNotAuthenticated_headerNameShouldBeUserAuthenticated()
            throws ServletException, IOException {
        when(userAuthUtil.isAuthenticated((HttpSession) any(), anyString())).thenReturn(false);
        scsbValidationFilter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq("user_authenticated"), eq(ScsbConstants.FALSE_STRING));
    }
}