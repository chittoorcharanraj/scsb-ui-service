package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.request.DownloadReports;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.util.ReportsUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public class ReportsControllerUT extends BaseTestCaseUT {

    @InjectMocks
    ReportsController reportsController;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    ReportsUtil reportsUtil;

    @Mock
    HttpSession session;

    @Mock
    HttpServletRequest request;

    @Mock
    public UserAuthUtil userAuthUtil;

    @Mock
    UserManagementService userManagementService;

    @Value("${scsb.support.institution}")
    private String supportInstitution;

    @Test
    public void reports(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_REPORT_URL)).thenReturn(Boolean.TRUE);
        boolean response = reportsController.reports(request);
        assertTrue(response);
    }
    @Test
    public void reportsFailed(){
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, ScsbConstants.SCSB_SHIRO_REPORT_URL)).thenReturn(Boolean.FALSE);
        boolean response = reportsController.reports(request);
        assertFalse(response);
    }
    @Test
    public void reportCountsForPartner() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbCommonConstants.REPORTS_REQUEST);
        reportsForm.setRequestFromDate("11/01/2016");
        reportsForm.setRequestToDate("12/01/2016");
        reportsForm.setShowBy(ScsbCommonConstants.REPORTS_PARTNERS);
        Mockito.when(reportsUtil.populatePartnersCountForRequest(any(), any(), any())).thenReturn(reportsForm);
        ReportsForm form = reportsController.reportCounts(reportsForm,request);
        assertNotNull(form);
    }
    @Test
    public void reportCountsForRequestType() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbCommonConstants.REPORTS_REQUEST);
        reportsForm.setRequestFromDate("11/01/2016");
        reportsForm.setRequestToDate("12/01/2016");
        reportsForm.setShowBy(ScsbCommonConstants.REPORTS_REQUEST_TYPE);
        Mockito.when(reportsUtil.populateRequestTypeInformation(any(), any(), any())).thenReturn(reportsForm);
        ReportsForm form = reportsController.reportCounts(reportsForm,request);
        assertNotNull(form);
    }
    @Test
    public void reportCountsForAccession() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbCommonConstants.REPORTS_ACCESSION_DEACCESSION);
        Mockito.when(reportsUtil.populateAccessionDeaccessionItemCounts(any())).thenReturn(reportsForm);
        ReportsForm form = reportsController.reportCounts(reportsForm,request);
        assertNotNull(form);
    }
    @Test
    public void reportCountsForCollectionGroupDesignation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType("CollectionGroupDesignation");
        Mockito.when(reportsUtil.populateCGDItemCounts(any())).thenReturn(reportsForm);
        ReportsForm form = reportsController.reportCounts(reportsForm,request);
        assertNotNull(form);
    }
    @Test
    public void cgdCounts() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        Mockito.when(reportsUtil.populateCGDItemCounts(any())).thenReturn(reportsForm);
        ReportsForm form = reportsController.cgdCounts();
        assertNotNull(form);
    }
    @Test
    public void deaccessionInformation() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        Mockito.when(reportsUtil.deaccessionReportFieldsInformation(any())).thenReturn(Arrays.asList(new DeaccessionItemResultsRow()));
        ReportsForm form = reportsController.deaccessionInformation(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchFirst() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.searchFirst(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchFirstRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REQUEST);
        ReportsForm form = reportsController.searchFirst(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchFirstEmptyList() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Collections.EMPTY_LIST);
        ReportsForm form = reportsController.searchFirst(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchPrevious() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.searchPrevious(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchPreviousRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REQUEST);
        ReportsForm form = reportsController.searchPrevious(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchNext() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.searchNext(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchNextRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REQUEST);
        ReportsForm form = reportsController.searchNext(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchLast() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.searchLast(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void searchLastRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REQUEST);
        ReportsForm form = reportsController.searchLast(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void incompleteReportPageSizeChange() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REPORTS_INCOMPLETE_RECORDS);
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.incompleteReportPageSizeChange(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void incompleteReportPageSizeChangeRequest() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setRequestType(ScsbConstants.REQUEST);
        ReportsForm form = reportsController.incompleteReportPageSizeChange(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void getInstitutionForIncompleteReport(){
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(supportInstitution)).thenReturn(Arrays.asList(getInstitutionEntity()));
        reportsController.getInstitutionForIncompleteReport();
    }

    @Test
    public void incompleteRecordsReport() throws Exception {
        ReportsForm reportsForm = new ReportsForm();
        Mockito.when(reportsUtil.incompleteRecordsReportFieldsInformation(any())).thenReturn(Arrays.asList(new IncompleteReportResultsRow()));
        ReportsForm form = reportsController.incompleteRecordsReport(reportsForm);
        assertNotNull(form);
    }
    @Test
    public void exportIncompleteRecords() throws Exception{
        ReportsForm reportsForm = new ReportsForm();
        reportsForm.setIncompleteRequestingInstitution("PUL");
        File csvFile = getBibContentFile();
        Mockito.when(reportsUtil.exportIncompleteRecords(any(), any())).thenReturn(csvFile);
        DownloadReports fileContent= reportsController.exportIncompleteRecords(reportsForm);
        assertNotNull(fileContent);
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("test.csv");
        return new File(resource.toURI());
    }
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
}
