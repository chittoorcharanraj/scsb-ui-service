package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MonitoringControllerUT extends BaseTestCaseUT {

    @InjectMocks
    MonitoringController monitoringController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    UserManagementService userManagementService;

    private final String scsbURL = "scsbURL";

    private final String dockerURL = "dockerURL";

    private final String awsURL = "awsURL";

    private final String loggingURL = "loggingURL";

    private final String embedLogURL = "embedLogURL";

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(monitoringController, "scsbURL", scsbURL);
        ReflectionTestUtils.setField(monitoringController, "dockerURL", dockerURL);
        ReflectionTestUtils.setField(monitoringController, "awsURL", awsURL);
        ReflectionTestUtils.setField(monitoringController, "loggingURL", loggingURL);
        ReflectionTestUtils.setField(monitoringController, "embedLogURL", embedLogURL);
    }

   /* @Test
    public void monitoring() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_MONITORING_URL)).thenReturn(Boolean.TRUE);
        boolean result = monitoringController.monitoring(request);
        assertTrue(result);
    }

    @Test
    public void monitoringFailure() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_MONITORING_URL)).thenReturn(Boolean.FALSE);
        boolean result = monitoringController.monitoring(request);
        assertFalse(result);
    }

    @Test
    public void logging() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_LOGGING_URL)).thenReturn(Boolean.TRUE);
        boolean result = monitoringController.logging(request);
        assertTrue(result);
    }

    @Test
    public void loggingFailure() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_LOGGING_URL)).thenReturn(Boolean.FALSE);
        boolean result = monitoringController.logging(request);
        assertFalse(result);
    }*/

    @Test
    public void properties() {
        Map<String, String> prop = monitoringController.properties();
        assertEquals(prop.get("scsbURL"), scsbURL);
        assertEquals(prop.get("dockerURL"), dockerURL);
        assertEquals(prop.get("awsURL"), awsURL);
        assertEquals(prop.get("loggingURL"), loggingURL);
        assertEquals(prop.get("embedLogURL"), embedLogURL);
        assertNotNull(prop);
    }
}
