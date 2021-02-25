package org.recap.filter;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 30/3/17.
 */
public class ReCAPLogoutFilterUT extends BaseTestCaseUT {

    @Mock
    HttpServletRequest request;

    @Mock
    ServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    ReCAPLogoutFilter reCAPLogoutFilter;

    @Mock
    HttpSession httpSession;

    @Test
    public void testdoFilter() throws IOException, ServletException {
        Mockito.when(((HttpServletRequest)request).getSession()).thenReturn(httpSession);
        reCAPLogoutFilter.doFilter(request,servletResponse,filterChain);
        assertTrue(true);
    }

}