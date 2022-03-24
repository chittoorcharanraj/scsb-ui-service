package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.model.jpa.CollectionGroupEntity;
import org.recap.model.jpa.ImsLocationEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.reports.TitleMatchedReport;
import org.recap.model.request.DownloadReports;
import org.recap.model.search.DeaccessionItemResultsRow;
import org.recap.model.search.IncompleteReportResultsRow;
import org.recap.model.search.ReportsForm;
import org.recap.model.submitCollection.SubmitCollectionReport;
import org.recap.repository.jpa.CollectionGroupDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.security.UserManagementService;
import org.recap.service.SCSBService;
import org.recap.util.ReportsServiceUtil;
import org.recap.util.ReportsUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class ReportsControllerUT extends BaseTestCaseUT {

    @InjectMocks
    ReportsController reportsController;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private ImsLocationDetailRepository imsLocationDetailRepository;

    @Mock
    private CollectionGroupDetailsRepository collectionGroupDetailsRepository;

    @Mock
    SCSBService scsbService;

    @Mock
    ReportsUtil reportsUtil;

    @Mock
    ReportsServiceUtil reportsServiceUtil;

    @Mock
    HttpSession session;

    @Mock
    HttpServletRequest request;

    @Mock
    public UserAuthUtil userAuthUtil;

    @Mock
    UserManagementService userManagementService;

    @Value("${" + PropertyKeyConstants.SCSB_SUPPORT_INSTITUTION + "}")
    private String supportInstitution;


    /*@Test
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
    }*/

    @Test
    public void titleMatchCountTest() throws Exception {
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.TRUE,ScsbConstants.FALSE)).thenReturn(titleMatchedReport);
        titleMatchedReport = reportsController.titleMatchCount(titleMatchedReport, new Date().toString(), new Date().toString());
        assertNotNull(titleMatchedReport);

    }

    @Test
    public void titleMatchReportsTest() throws Exception {
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.FALSE,ScsbConstants.FALSE)).thenReturn(titleMatchedReport);
        titleMatchedReport = reportsController.titleMatchReports(titleMatchedReport, new Date().toString(), new Date().toString());
        assertNotNull(titleMatchedReport);

    }

    @Test
    public void titleMatchReportsExportTest() throws Exception {
        TitleMatchedReport titleMatchedReport = getTitleMatchReport();
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.FALSE,ScsbConstants.TRUE)).thenReturn(titleMatchedReport);
        titleMatchedReport = reportsController.titleMatchReportsExport(titleMatchedReport, new Date().toString(), new Date().toString());
        assertNotNull(titleMatchedReport);

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
        Mockito.when(imsLocationDetailRepository.findAll()).thenReturn(Arrays.asList(getImsLocationEntity()));
        Mockito.when(collectionGroupDetailsRepository.findAll()).thenReturn(Arrays.asList(getCollectionGroupEntity()));
        Mockito.when(scsbService.pullCGDCodesList(any())).thenReturn(Arrays.asList("Shared"));
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

    @Test
    public void submitCollectionReport() throws Exception {
        SubmitCollectionReport submitCollectionReprot = new SubmitCollectionReport();
        String fromDate = new Date().toString();
        String toDate = new Date().toString();
        ResponseEntity<SubmitCollectionReport> submitCollectionReportResponseEntity = new ResponseEntity<>(HttpStatus.OK);
        Map<String,Date> dateMap = new HashMap<>();
        dateMap.put("fromDate",new Date());
        dateMap.put("toDate",new Date());
        Mockito.when(scsbService.dateFormatter(any(),any())).thenReturn(dateMap);
        Mockito.when(reportsUtil.submitCollectionReport(any())).thenReturn(submitCollectionReportResponseEntity);
        ResponseEntity<SubmitCollectionReport> responseEntity = reportsController.submitCollectionReport(submitCollectionReprot,fromDate,toDate);
        assertNotNull(responseEntity);
    }

    @Test
    public void accessionReport() throws Exception {
        SubmitCollectionReport submitCollectionReprot = new SubmitCollectionReport();
        String fromDate = new Date().toString();
        String toDate = new Date().toString();
        ResponseEntity<SubmitCollectionReport> submitCollectionReportResponseEntity = new ResponseEntity<>(HttpStatus.OK);
        Map<String,Date> dateMap = new HashMap<>();
        dateMap.put("fromDate",new Date());
        dateMap.put("toDate",new Date());
        Mockito.when(scsbService.dateFormatter(any(),any())).thenReturn(dateMap);
        Mockito.when(reportsUtil.accessionReport(any())).thenReturn(submitCollectionReportResponseEntity);
        ResponseEntity<SubmitCollectionReport> responseEntity = reportsController.accessionReport(submitCollectionReprot,fromDate,toDate);
        assertNotNull(responseEntity);
    }

    @Test
    public void titleMatchCount() throws Exception {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        String fromDate = new Date().toString();
        String toDate = new Date().toString();
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("1",new Date());
        dateMap.put("2",new Date());
        Mockito.when(scsbService.dateFormatter(fromDate, toDate)).thenReturn(dateMap);
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.TRUE,ScsbConstants.FALSE)).thenReturn(titleMatchedReport);
        TitleMatchedReport report = reportsController.titleMatchCount(titleMatchedReport,fromDate,toDate);
        assertNotNull(report);
        assertEquals(titleMatchedReport,report);
    }

    @Test
    public void titleMatchReports() throws Exception {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        String fromDate = new Date().toString();
        String toDate = new Date().toString();
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("1",new Date());
        dateMap.put("2",new Date());
        Mockito.when(scsbService.dateFormatter(fromDate, toDate)).thenReturn(dateMap);
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.FALSE,ScsbConstants.FALSE)).thenReturn(titleMatchedReport);
        TitleMatchedReport report = reportsController.titleMatchReports(titleMatchedReport,fromDate,toDate);
        assertNotNull(report);
        assertEquals(titleMatchedReport,report);
    }

    @Test
    public void titleMatchReportsExport() throws Exception {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        String fromDate = new Date().toString();
        String toDate = new Date().toString();
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("1",new Date());
        dateMap.put("2",new Date());
        Mockito.when(scsbService.dateFormatter(fromDate, toDate)).thenReturn(dateMap);
        Mockito.when(reportsServiceUtil.getTitleMatchReport(titleMatchedReport,ScsbConstants.FALSE,ScsbConstants.TRUE)).thenReturn(titleMatchedReport);
        TitleMatchedReport report = reportsController.titleMatchReportsExport(titleMatchedReport,fromDate,toDate);
        assertNotNull(report);
        assertEquals(titleMatchedReport,report);
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
    private ImsLocationEntity getImsLocationEntity() {
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setImsLocationCode("1");
        imsLocationEntity.setImsLocationName("test");
        imsLocationEntity.setCreatedBy("test");
        imsLocationEntity.setCreatedDate(new Date());
        imsLocationEntity.setActive(true);
        imsLocationEntity.setDescription("test");
        imsLocationEntity.setUpdatedBy("test");
        imsLocationEntity.setUpdatedDate(new Date());
        return imsLocationEntity;
    }
    private CollectionGroupEntity getCollectionGroupEntity() {
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode("GA");
        collectionGroupEntity.setCollectionGroupDescription("collection");
        collectionGroupEntity.setCreatedDate(new Date());
        collectionGroupEntity.setLastUpdatedDate(new Date());
        return collectionGroupEntity;
    }

    private TitleMatchedReport getTitleMatchReport() {
        TitleMatchedReport titleMatchedReport = new TitleMatchedReport();
        titleMatchedReport.setTitleMatch("ABC");
        titleMatchedReport.setCgd(Arrays.asList("ABC","DEF"));
        titleMatchedReport.setMessage("Title match report");
        titleMatchedReport.setOwningInst("PUL");
        titleMatchedReport.setPageNumber(1);
        titleMatchedReport.setTotalPageCount(10);
        titleMatchedReport.setTotalRecordsCount(1000);
        return titleMatchedReport;
    }
}
