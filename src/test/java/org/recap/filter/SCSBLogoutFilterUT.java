package org.recap.filter;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by hemalathas on 30/3/17.
 */
public class SCSBLogoutFilterUT extends BaseTestCaseUT {

    @Mock
    HttpServletRequest request;

    @Mock
    ServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    SCSBLogoutFilter SCSBLogoutFilter;

    @Mock
    HttpSession httpSession;

    @Test
    public void testdoFilter() throws IOException, ServletException {
        Mockito.when(((HttpServletRequest)request).getSession()).thenReturn(httpSession);
        SCSBLogoutFilter.doFilter(request,servletResponse,filterChain);
        assertTrue(true);
    }

}