package org.recap.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
