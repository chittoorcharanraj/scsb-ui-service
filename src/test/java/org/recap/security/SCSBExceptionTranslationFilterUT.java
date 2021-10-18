package org.recap.security;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;


public class SCSBExceptionTranslationFilterUT extends BaseTestCaseUT {

    @InjectMocks
    SCSBExceptionTranslationFilter SCSBExceptionTranslationFilter;

    @Mock
    ThrowableAnalyzer throwableAnalyzer;

    @Mock
    RequestCache requestCache;

    @Mock
    AuthenticationException authenticationException;

    @Mock
    AccessDeniedException accessDeniedException;

    @Mock
    AuthenticationTrustResolver authenticationTrustResolver;

    @Mock
    AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    AccessDeniedHandler accessDeniedHandler;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    FilterChain filterChain;

    @Mock
    HttpServletResponse servletResponse;


    @Test
    public void afterPropertiesSet() {
        ReflectionTestUtils.setField(SCSBExceptionTranslationFilter, "authenticationEntryPoint", authenticationEntryPoint);
        SCSBExceptionTranslationFilter.afterPropertiesSet();
    }
    @Test
    public void checkGetters(){
        SCSBExceptionTranslationFilter.getAuthenticationEntryPoint();
        SCSBExceptionTranslationFilter.getAuthenticationTrustResolver();
    }

    @Test
    public void sendStartAuthentication() throws Exception{
        AuthenticationException authenticationException = new InsufficientAuthenticationException("");
       // Mockito.when(servletRequest.getParameter("institution")).thenReturn("PUL");
        SCSBExceptionTranslationFilter.sendStartAuthentication(servletRequest,servletResponse,filterChain,authenticationException);
    }

    @Test
    public void setAccessDeniedHandler() {
        SCSBExceptionTranslationFilter.setAccessDeniedHandler(accessDeniedHandler);
    }

    @Test
    public void setAuthenticationTrustResolver() {
        SCSBExceptionTranslationFilter.setAuthenticationTrustResolver(authenticationTrustResolver);
    }

    @Test
    public void doFilter() throws IOException, ServletException {
        SCSBExceptionTranslationFilter.doFilter(servletRequest, servletResponse, filterChain);
    }
    @Test
    public void doFilterIOException(){
        try {
            Mockito.doThrow(new IOException()).when(filterChain).doFilter(servletRequest,servletResponse);
            SCSBExceptionTranslationFilter.doFilter(servletRequest, servletResponse, filterChain);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doFilterServletException(){
        try {
            Mockito.doThrow(new ServletException()).when(filterChain).doFilter(servletRequest,servletResponse);
            SCSBExceptionTranslationFilter.doFilter(servletRequest, servletResponse, filterChain);
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void doFilterRunTimeException(){
        try {
            Mockito.doThrow(new RuntimeException()).when(filterChain).doFilter(servletRequest,servletResponse);
            SCSBExceptionTranslationFilter.doFilter(servletRequest, servletResponse, filterChain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void handleSpringSecurityAuthenticationException(){
        ReflectionTestUtils.invokeMethod(SCSBExceptionTranslationFilter,"handleSpringSecurityException",servletRequest,servletResponse,filterChain,authenticationException);
    }

    @Test
    public void handleSpringSecurityAccessDeniedExceptionWithAuthenticationTrustResolver(){
        Mockito.when(authenticationTrustResolver.isAnonymous(any())).thenReturn(Boolean.TRUE);
        ReflectionTestUtils.invokeMethod(SCSBExceptionTranslationFilter,"handleSpringSecurityException",servletRequest,servletResponse,filterChain,accessDeniedException);
    }
    @Test
    public void handleSpringSecurityAccessDeniedException(){
        ReflectionTestUtils.invokeMethod(SCSBExceptionTranslationFilter,"handleSpringSecurityException",servletRequest,servletResponse,filterChain,accessDeniedException);
    }

    @Test
    public void setThrowableAnalyzer(){
        SCSBExceptionTranslationFilter.setThrowableAnalyzer(throwableAnalyzer);
    }
}
