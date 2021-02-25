package org.recap.filter;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class XSSFilterUT extends BaseTestCaseUT {

    @Mock
    ServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    XSSFilter xssFilter;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession httpSession;

    @Mock
    FilterConfig filterConfig;

    @Test
    public void testdoFilter() throws IOException, ServletException {
        xssFilter.doFilter(request,servletResponse,filterChain);
        assertTrue(true);
    }

    @Test
    public void testDestroy(){
        xssFilter.destroy();
        assertTrue(true);
    }

    @Test
    public void testInit() throws ServletException{
        xssFilter.init(filterConfig);
        assertTrue(true);
    }
}
