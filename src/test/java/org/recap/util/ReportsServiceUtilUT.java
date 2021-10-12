package org.recap.util;


import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.recap.BaseTestCaseUT;

import org.recap.model.reports.ReportsRequest;
import org.recap.model.reports.ReportsResponse;
import org.recap.model.reports.TitleMatchedReport;
import org.recap.model.reports.TitleMatchedReports;
import org.recap.model.search.ReportsForm;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static junit.framework.TestCase.assertNotNull;

public class ReportsServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    ReportsServiceUtil reportsServiceUtil;

    @Mock
    ReportsUtil reportsUtil;

    @Test
    public void requestCgdItemCountsException(){
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setCollectionGroupDesignations(Arrays.asList("PA","COMPLETE"));
        Mockito.when(reportsUtil.getInstitutions()).thenReturn(Arrays.asList("PUL","CUL","NYPL","HD",""));
        ReportsResponse response = reportsServiceUtil.requestCgdItemCounts(reportsForm);
        assertNotNull(response);
    }

    @Test
    public void getReportsResponseException(){
        ReportsRequest reportsRequest = new ReportsRequest();
        String reportUrl = "http://localhost:9090/test";
        ReflectionTestUtils.invokeMethod(reportsServiceUtil,"getReportsResponse",reportsRequest,reportUrl);
    }

    @Test
    public void mapData(){
        TitleMatchedReport titleMatchedReport = getTitleMatchedReport();
        ReflectionTestUtils.invokeMethod(reportsServiceUtil,"mapData",titleMatchedReport);
    }

    @Test
    public void getTitleMatchReportException(){
        TitleMatchedReport titleMatchedReport = getTitleMatchedReport();
        reportsServiceUtil.getTitleMatchReport(titleMatchedReport,true,true);
    }

    private TitleMatchedReport getTitleMatchedReport() {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        TitleMatchedReports titleMatchedReports = new TitleMatchedReports();
        titleMatchedReports.setId(1);
        titleMatchedReports.setDuplicateCode("DB");
        TitleMatchedReports titleMatchedReports2 = new TitleMatchedReports();
        titleMatchedReports2.setId(1);
        titleMatchedReports2.setDuplicateCode("DB");
        TitleMatchedReports titleMatchedReports3 = new TitleMatchedReports();
        titleMatchedReports3.setId(1);
        titleMatchedReports3.setDuplicateCode("BB");
        titleMatchedReport.setTitleMatchedReports(Arrays.asList(titleMatchedReports,titleMatchedReports2,titleMatchedReports3));
        return titleMatchedReport;
    }
}
