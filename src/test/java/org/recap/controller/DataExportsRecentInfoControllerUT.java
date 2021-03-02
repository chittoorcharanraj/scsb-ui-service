package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.dataexportinfo.DataExportResponse;
import org.recap.model.jpa.InstitutionEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class DataExportsRecentInfoControllerUT extends BaseTestCaseUT {

    @InjectMocks
    DataExportsRecentInfoController dataExportsRecentInfoController;

    @Mock
    HomeController homeController;

    @Test
    public void getRecentDataExportsInfo(){
        dataExportsRecentInfoController.getRecentDataExportsInfo();
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
        DataExportResponse response = dataExportsRecentInfoController.exportDataDump(institutionCodes,requestingInstitutionCode,imsDepositoryCodes,fetchType,outputFormat,date,collectionGroupIds,transmissionType,emailToAddress,userName);
        assertNotNull(response);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
}
