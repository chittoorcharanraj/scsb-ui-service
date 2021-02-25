package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RecapErrorPageControllerUT extends BaseTestCaseUT {

    @InjectMocks
    RecapErrorPageController recapErrorPageController;

    @Test
    public void testErrorPage(){
        String response = recapErrorPageController.recapErrorPage();
        assertNotNull(response);
        assertEquals(response,"error");
    }

    @Test
    public void getErrorPath(){
        recapErrorPageController.getErrorPath();
    }

}
