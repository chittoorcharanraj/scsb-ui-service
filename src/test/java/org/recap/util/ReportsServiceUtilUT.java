package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.search.ReportsForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;

public class ReportsServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    ReportsServiceUtil reportsServiceUtil;

    @Mock
    ReportsUtil reportsUtil;

    @Value("${scsb.support.institution}")
    private String supportInstitution;

    @Test
    public void requestCgdItemCountsException(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setCollectionGroupDesignations(Arrays.asList("PA","COMPLETE"));
        Mockito.when(reportsUtil.getInstitutions()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HD",supportInstitution));
        ReportsResponse response = reportsServiceUtil.requestCgdItemCounts(reportsForm);
        assertNotNull(response);
    }

    @Test
    public void getReportsResponseException(){
        ReportsRequest reportsRequest = new ReportsRequest();
        String reportUrl = "http://localhost:9090/test";
        ReflectionTestUtils.invokeMethod(reportsServiceUtil,"getReportsResponse",reportsRequest,reportUrl);
    }
}
