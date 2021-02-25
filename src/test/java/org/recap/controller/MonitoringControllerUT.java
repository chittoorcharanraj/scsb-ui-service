package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.util.UserAuthUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;

public class MonitoringControllerUT extends BaseTestCaseUT {

    @InjectMocks
    MonitoringController monitoringController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Test
    public void monitoring(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_MONITORING_URL)).thenReturn(Boolean.TRUE);
        boolean result = monitoringController.monitoring(request);
        assertTrue(result);
    }
    @Test
    public void monitoringFailure(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_MONITORING_URL)).thenReturn(Boolean.FALSE);
        boolean result = monitoringController.monitoring(request);
        assertFalse(result);
    }
    @Test
    public void logging(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_LOGGING_URL)).thenReturn(Boolean.TRUE);
        boolean result = monitoringController.logging(request);
        assertTrue(result);
    }
    @Test
    public void loggingFailure(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_LOGGING_URL)).thenReturn(Boolean.FALSE);
        boolean result = monitoringController.logging(request);
        assertFalse(result);
    }
}
