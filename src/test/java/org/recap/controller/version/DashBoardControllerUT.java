package org.recap.controller.version;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.util.PropertyUtil;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DashBoardControllerUT extends BaseTestCaseUT {

    @InjectMocks
    DashBoardController dashBoardController;

    @Mock
    PropertyUtil propertyUtil;

    @Before
    public void setup(){
        ReflectionTestUtils.setField(dashBoardController,"versionNumberService","1.0");
        ReflectionTestUtils.setField(dashBoardController,"recapAssistanceEmailTo","test@gmail.com");
        dashBoardController.setVersionNumberService("1.0");
    }

    @Test
    public void getVersionNumberService(){
        String result = dashBoardController.getVersionNumberService();
        assertNotNull(result);
    }

    @Test
    public void getEmail(){
        String result = dashBoardController.getEmail();
        assertNotNull(result);
    }

    @Test
    public void getFrozenInstitutions(){
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("PUL","TRUE");
        Mockito.when(propertyUtil.getPropertyByKeyForAllInstitutions(PropertyKeyConstants.ILS.ILS_ENABLE_CIRCULATION_FREEZE)).thenReturn(propertyMap);
        Mockito.when(propertyUtil.getPropertyByInstitutionAndKey("PUL", PropertyKeyConstants.ILS.ILS_CIRCULATION_FREEZE_MESSAGE)).thenReturn("Test");
        List<String> list = dashBoardController.getFrozenInstitutions();
        assertNotNull(list);
    }
}
