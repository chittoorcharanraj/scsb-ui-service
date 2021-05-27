package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.dataexportinfo.DataExportResponse;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class DataExportsRecentInfoControllerUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportsRecentInfoController dataExportsRecentInfoController;

    @Mock
    HomeController homeController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    UserManagementService userManagementService;

    /*@Test
    public void validateDataExportValidRequest(){
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_DATAEXPORT_URL)).thenReturn(true);
        Mockito.when(request.getSession(false)).thenReturn(session);
        boolean validateDataExport=dataExportsRecentInfoController.validateDataExport(request);
        assertTrue(validateDataExport);
    }

    @Test
    public void validateDataExportInValidRequest(){
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_DATAEXPORT_URL)).thenReturn(false);
        Mockito.when(request.getSession(false)).thenReturn(session);
        boolean validateDataExport=dataExportsRecentInfoController.validateDataExport(request);
        assertFalse(validateDataExport);
    }*/

    @Test
    public void getRecentDataExportsInfo(){
        dataExportsRecentInfoController.getRecentDataExportsInfo(request);
    }
    @Test
    public void dataExportDescriptionFields(){
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(homeController.fecthingInstituionsFromDB()).thenReturn(Arrays.asList(institutionEntity));
        dataExportsRecentInfoController.dataExportDescriptionFields();
    }
    @Test
    public void exportDataDump(){
        String institutionCodes = "PUL";
        String requestingInstitutionCode = "PUL";
        String fetchType = "FULL";
        String outputFormat = "XML";
        String date = new Date().toString();
        String collectionGroupIds = "CGP";
        String transmissionType = "FULL";
        String emailToAddress = "test@gmail.com";
        String imsDepositoryCodes = "HD";
        String userName = "test";
        DataExportResponse response = dataExportsRecentInfoController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress,userName,request);
        assertNotNull(response);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
}
