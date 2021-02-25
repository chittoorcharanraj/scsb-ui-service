package org.recap.filter;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNull;

public class XSSRequestWrapperUT extends BaseTestCaseUT {

    @Mock
    HttpSession httpSession;

    @Mock
    HttpServletRequest servletRequest;

    @Test
    public void testGetParameterValues() throws Exception{
        String[] values;
        String parameter = "+-0123456789#";
        XSSRequestWrapper xssRequestWrapper=new XSSRequestWrapper(servletRequest);
        values = xssRequestWrapper.getParameterValues(parameter);
        assertNull(values);
    }

    @Test
    public void testGetHeaders() throws Exception{
        String name = "test";
        XSSRequestWrapper xssRequestWrapper=new XSSRequestWrapper(servletRequest);
        String value = xssRequestWrapper.getHeader(name);
        assertNull(value);
    }

    @Test
    public void testGetParameter() throws Exception{
        String parameter = "test";
        XSSRequestWrapper xssRequestWrapper=new XSSRequestWrapper(servletRequest);
        String value = xssRequestWrapper.getParameter(parameter);
        assertNull(value);
    }

}
