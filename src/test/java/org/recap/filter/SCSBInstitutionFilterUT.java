package org.recap.filter;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by hemalathas on 30/3/17.
 */

@RunWith(MockitoJUnitRunner.Silent.class)
public class SCSBInstitutionFilterUT extends BaseTestCaseUT {

   @InjectMocks
   SCSBInstitutionFilter scsbInstitutionFilter;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    FilterChain filterChain;

    @Mock
    UserInstitutionCache userInstitutionCache;

    @Mock
    HttpSession value;

    @Mock
    private MockHttpSession session;

    @Mock
    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(session.getId()).thenReturn("1");
        Mockito.when(request.getRequestURI()).thenReturn("/home");
        Cookie mockCookie1 = mock(Cookie.class);
        Cookie mockCookie2 = mock(Cookie.class);
        Mockito.when(mockCookie1.getName()).thenReturn(ScsbConstants.IS_USER_AUTHENTICATED);
        Mockito.when(mockCookie1.getValue()).thenReturn("Y");
        Mockito.when(mockCookie2.getName()).thenReturn(ScsbConstants.LOGGED_IN_INSTITUTION);
        Mockito.when(mockCookie2.getValue()).thenReturn("PUL");
        Mockito.when(request.getCookies()).thenReturn(new Cookie[]{mockCookie1, mockCookie2});
        try (MockedStatic<HelperUtil> mockedHelperUtil = Mockito.mockStatic(HelperUtil.class)) {
            mockedHelperUtil.when(() -> HelperUtil.getBean(UserInstitutionCache.class)).thenReturn(userInstitutionCache);
            mockedHelperUtil.when(() -> HelperUtil.getLogoutUrl("PUL")).thenReturn("http://logout.url");
            scsbInstitutionFilter.doFilterInternal(request, httpServletResponse, filterChain);
            verify(userInstitutionCache).removeSessionId("1");
            verify(httpServletResponse).sendRedirect("http://logout.url");
        }

        assertTrue(true);
    }


}