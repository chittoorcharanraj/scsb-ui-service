package org.recap.controller;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.util.PropertyUtil;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class MessageRestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    MessageRestController messageRestController;

    @Mock
    PropertyUtil propertyUtil;

    @Test
    public void getValue(){
        JSONObject jsonObject = new JSONObject();
        Mockito.when(propertyUtil.getPropertyByInstitution(any())).thenReturn(jsonObject);
        Map<String, Object> objectMap = messageRestController.getValue(any());
        assertNotNull(objectMap);
    }

}
