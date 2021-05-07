package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ScsbErrorPageControllerUT extends BaseTestCaseUT {

    @InjectMocks
    ScsbErrorPageController scsbErrorPageController;

    @Test
    public void testErrorPage(){
        String response = scsbErrorPageController.recapErrorPage();
        assertNotNull(response);
        assertEquals(response,"error");
    }

    @Test
    public void getErrorPath(){
        scsbErrorPageController.getErrorPath();
    }

}
