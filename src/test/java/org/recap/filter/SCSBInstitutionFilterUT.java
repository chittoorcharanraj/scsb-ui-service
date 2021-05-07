package org.recap.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.recap.BaseTestCaseUT;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by hemalathas on 30/3/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({HelperUtil.class,Cookie[].class})
public class SCSBInstitutionFilterUT extends BaseTestCaseUT {

   @InjectMocks
   SCSBInstitutionFilter SCSBInstitutionFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    FilterChain filterChain;

    @Mock
    UserInstitutionCache userInstitutionCache;

    @Mock
    HttpSession value;

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        PowerMockito.mockStatic(HelperUtil.class);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        Mockito.when(request.getSession()).thenReturn(value);
        Mockito.when(value.getId()).thenReturn("1");
        Cookie mockCookie = Mockito.mock(Cookie.class);
        Mockito.when(mockCookie.getName()).thenReturn(ScsbConstants.IS_USER_AUTHENTICATED).thenReturn(ScsbConstants.LOGGED_IN_INSTITUTION);
        Mockito.when(mockCookie.getValue()).thenReturn("Y");
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        Mockito.when(request.getRequestURI()).thenReturn("/home");
        SCSBInstitutionFilter.doFilterInternal(request,httpServletResponse,filterChain);
        assertTrue(true);
    }

    @Test
    public void testDoFilterInternalIOException() throws ServletException, IOException {
        PowerMockito.mockStatic(HelperUtil.class);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        when(HelperUtil.getLogoutUrl(Mockito.any())).thenReturn("logoutUrl");
        Mockito.when(request.getSession()).thenReturn(value);
        Mockito.when(value.getId()).thenReturn("1");
        Cookie mockCookie = Mockito.mock(Cookie.class);
        Mockito.when(mockCookie.getName()).thenReturn(ScsbConstants.IS_USER_AUTHENTICATED).thenReturn(ScsbConstants.LOGGED_IN_INSTITUTION);
        Mockito.when(mockCookie.getValue()).thenReturn("Y");
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        Mockito.when(request.getRequestURI()).thenReturn("/home");
        Mockito.doThrow(IOException.class).when(httpServletResponse).sendRedirect(Mockito.anyString());
        SCSBInstitutionFilter.doFilterInternal(request,httpServletResponse,filterChain);
        assertTrue(true);
    }


    @Test
    public void testDoFilterInternalReturnInstitution() throws ServletException, IOException {
        PowerMockito.mockStatic(HelperUtil.class);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        Mockito.when(userInstitutionCache.getInstitutionForRequestSessionId(Mockito.anyString())).thenReturn("PUL");
        when(HelperUtil.getInstitutionFromRequest(Mockito.any())).thenReturn("PUL");
        Mockito.when(request.getSession()).thenReturn(value);
        Mockito.when(value.getId()).thenReturn("1");
        Cookie mockCookie = Mockito.mock(Cookie.class);
        Mockito.when(mockCookie.getName()).thenReturn(ScsbConstants.IS_USER_AUTHENTICATED).thenReturn(ScsbConstants.LOGGED_IN_INSTITUTION);
        Mockito.when(mockCookie.getValue()).thenReturn("Y");
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        SCSBInstitutionFilter.doFilterInternal(request,httpServletResponse,filterChain);
        assertTrue(true);
    }

    @Test
    public void testDoFilterInternalWithNoInstitution() throws ServletException, IOException {
        PowerMockito.mockStatic(HelperUtil.class);
        when(HelperUtil.getBean(Mockito.any())).thenReturn(userInstitutionCache);
        Mockito.when(request.getSession()).thenReturn(value);
        Mockito.when(value.getId()).thenReturn("1");
        Cookie mockCookie = Mockito.mock(Cookie.class);
        Mockito.when(mockCookie.getName()).thenReturn(ScsbConstants.IS_USER_AUTHENTICATED).thenReturn(ScsbConstants.LOGGED_IN_INSTITUTION);
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{mockCookie});
        Mockito.when(request.getRequestURI()).thenReturn("/home");
        SCSBInstitutionFilter.doFilterInternal(request,httpServletResponse,filterChain);
        assertTrue(true);
    }

}