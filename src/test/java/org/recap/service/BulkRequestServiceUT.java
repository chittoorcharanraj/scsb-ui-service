package org.recap.service;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.recap.ScsbConstants;
import org.recap.model.jpa.*;
import org.recap.model.search.BulkRequestForm;
import org.recap.model.search.BulkSearchResultRow;
import org.recap.repository.jpa.BulkCustomerCodeDetailsRepository;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class BulkRequestServiceUT {

    @InjectMocks
    @Spy
    BulkRequestService bulkRequestService;

    @Mock
    BulkRequestForm bulkRequestForm;

    @Mock
    BulkSearchRequestService bulkSearchRequestService;

    @Mock
    BulkRequestItemEntity bulkRequestItemEntity;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    BulkCustomerCodeDetailsRepository bulkCustomerCodeDetailsRepository;

    @Mock
    SecurityUtil securityUtil;

    @Mock
    BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Mock
    RequestItemEntity requestItemEntity;

    @Mock
    RequestStatusEntity requestStatusEntity;

    @Mock
    HttpServletRequest request;

    @Mock
    RestTemplate restTemplate;
    @Mock
    RestHeaderService restHeaderService;
    @Mock
    HttpHeaders httpHeaders;
    @Mock
    HttpSession session;
    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Test
    public void getProcessCreateBulkRequest() {
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        BulkCustomerCodeEntity bulkCustomerCodeEntity = new BulkCustomerCodeEntity();
        UsersEntity usersEntity = new UsersEntity();
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        getMocked(bulkRequestForm, bulkCustomerCodeEntity, usersEntity, bulkRequestItemEntity, responseEntity);
        BulkRequestForm form = bulkRequestService.processCreateBulkRequest(bulkRequestForm, request);
        assertNotNull(form);
    }

    @Test
    public void getProcessCreateBulkRequestFailePatronValidation() {
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        BulkCustomerCodeEntity bulkCustomerCodeEntity = new BulkCustomerCodeEntity();
        UsersEntity usersEntity = new UsersEntity();
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        ResponseEntity<Boolean> responseEntity = new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
        getMocked(bulkRequestForm, bulkCustomerCodeEntity, usersEntity, bulkRequestItemEntity, responseEntity);
        BulkRequestForm form = bulkRequestService.processCreateBulkRequest(bulkRequestForm, request);
        assertNotNull(form);
    }

    private void getMocked(BulkRequestForm bulkRequestForm, BulkCustomerCodeEntity bulkCustomerCodeEntity, UsersEntity usersEntity, BulkRequestItemEntity bulkRequestItemEntity, ResponseEntity<Boolean> responseEntity) {
        ReflectionTestUtils.setField(bulkRequestService, "scsbUrl", "http://test/scsburl");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(ScsbConstants.USER_ID)).thenReturn(1);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(bulkRequestService.getRestTemplate()).thenReturn(restTemplate);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Boolean>>any());
        Mockito.when(bulkRequestDetailsRepository.save(any())).thenReturn(bulkRequestItemEntity);
        Mockito.when(userDetailsRepository.findById(any())).thenReturn(Optional.of(usersEntity));
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(bulkCustomerCodeDetailsRepository.findByCustomerCode(bulkRequestForm.getDeliveryLocationInRequest())).thenReturn(bulkCustomerCodeEntity);
    }

    @Test
    public void processSearchRequest() {
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Page<BulkRequestItemEntity> bulkRequestItemEntities = new PageImpl<>(Arrays.asList(bulkRequestItemEntity));
        Mockito.when(securityUtil.getDecryptedValue(any())).thenReturn("test@gmail.com");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getInstitutionEntity()));
        Mockito.when(userDetailsRepository.findByLoginId(any())).thenReturn(new UsersEntity());
        Mockito.when(bulkSearchRequestService.processSearchRequest(any())).thenReturn(bulkRequestItemEntities);
        BulkRequestForm form = bulkRequestService.processSearchRequest(bulkRequestForm);
        assertNotNull(form);
    }

    @Test
    public void processSearchRequestException() {
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        bulkRequestForm.setBulkSearchResultRows(Arrays.asList(getBulkSearchResultRow()));
        BulkRequestItemEntity bulkRequestItemEntity = getBulkRequestItemEntity();
        Page<BulkRequestItemEntity> bulkRequestItemEntities = new PageImpl<>(Arrays.asList(bulkRequestItemEntity));
        Mockito.when(securityUtil.getDecryptedValue(any())).thenReturn("test@gmail.com");
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin(any())).thenReturn(Arrays.asList(getInstitutionEntity()));
        Mockito.when(userDetailsRepository.findByLoginId(any())).thenReturn(new UsersEntity());
        Mockito.when(bulkSearchRequestService.processSearchRequest(any())).thenReturn(bulkRequestItemEntities);
        BulkRequestForm form = bulkRequestService.processSearchRequest(bulkRequestForm);
        assertNotNull(form);
    }

    @Test
    public void processOnPageSizeChangeException() {
        BulkRequestForm bulkRequestForm = getBulkRequestForm();
        bulkRequestForm.setTotalRecordsCount("test");
        bulkRequestService.processOnPageSizeChange(bulkRequestForm);
    }

    @Test
    public void processDeliveryLocations() {
        Mockito.when(institutionDetailsRepository.findByInstitutionCode(any())).thenReturn(getInstitutionEntity());
        Mockito.when(bulkCustomerCodeDetailsRepository.findByOwningInstitutionId(any())).thenReturn(Arrays.asList(new BulkCustomerCodeEntity()));
        BulkRequestForm form = bulkRequestService.processDeliveryLocations(bulkRequestForm);
        assertNotNull(form);
    }

    @Test
    public void testSaveUpdatedRequestStatus() throws Exception {
        List<RequestItemEntity> requestItemEntities = new ArrayList<>();
        requestItemEntities.add(requestItemEntity);
        Mockito.when(bulkRequestItemEntity.getRequestItemEntities()).thenReturn(requestItemEntities);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(bulkRequestItemEntity.getBulkRequestStatus()).thenReturn("PROCESSED");
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn("EXCEPTION");
        Mockito.when(bulkRequestDetailsRepository.findById(1)).thenReturn(Optional.of(bulkRequestItemEntity));
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bulkRequestItemEntity.getBulkRequestFileData()).thenReturn(sourceBibContent.getBytes(StandardCharsets.UTF_8));
        BulkRequestItemEntity request = bulkRequestService.saveUpadatedRequestStatus(1);
        assertNotNull(request);
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("BulkRequest.csv");
        return new File(resource.toURI());
    }

    @Test
    public void testProcessOnPageSizeChange() {
        Page<BulkRequestItemEntity> bulkRequestItemEntities = PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(2l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList = new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult = bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }

    @Test
    public void testProcessOnPageSizeChangeResultsNotFound() {
        Page<BulkRequestItemEntity> bulkRequestItemEntities = PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(0l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList = new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult = bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }

    @Test
    public void testProcessOnPageSizeChangeException() {
        Page<BulkRequestItemEntity> bulkRequestItemEntities = PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(2l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList = new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestItemEntity.getEmailId()).thenReturn("test@gmail.com");
        Mockito.when(securityUtil.getDecryptedValue(any())).thenThrow(NullPointerException.class);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult = bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }

    private BulkRequestForm getBulkRequestForm() {

        BulkRequestForm bulkRequestForm = new BulkRequestForm();
        bulkRequestForm.setBulkRequestName("Test");
        bulkRequestForm.setDeliveryLocation("test");
        bulkRequestForm.setDeliveryLocationInRequest("test");
        bulkRequestForm.setDisableRequestingInstitution(false);
        bulkRequestForm.setDisableSearchInstitution(true);
        bulkRequestForm.setErrorMessage("ERROR");
        bulkRequestForm.setFileName("test");
        bulkRequestForm.setInstitution("NYPL");
        bulkRequestForm.setItemBarcode("123");
        bulkRequestForm.setPatronBarcode("23456");
        bulkRequestForm.setPatronBarcodeInRequest("23456");
        bulkRequestForm.setItemOwningInstitution("PUL");
        bulkRequestForm.setRequestingInstitution("PUL");
        bulkRequestForm.setRequestingInstituionHidden("PUL");
        bulkRequestForm.setRequestId(1);
        bulkRequestForm.setRequestIdSearch("1");
        bulkRequestForm.setItemTitle("test");
        bulkRequestForm.setMessage("test");
        bulkRequestForm.setPageNumber(200);
        bulkRequestForm.setPageSize(20);
        bulkRequestForm.setTotalRecordsCount("40");
        bulkRequestForm.setTotalPageCount(2);
        bulkRequestForm.setStatus("PROCESSED");
        bulkRequestForm.setSubmitted(true);
        bulkRequestForm.setFile(new MockMultipartFile("test", new byte[1]));
        return bulkRequestForm;
    }

    private BulkSearchResultRow getBulkSearchResultRow() {
        BulkSearchResultRow bulkSearchResultRow = new BulkSearchResultRow();

        bulkSearchResultRow.setBulkRequestId(1);
        bulkSearchResultRow.setBulkRequestName("test");
        bulkSearchResultRow.setBulkRequestNotes("test");
        bulkSearchResultRow.setCreatedBy("test");
        bulkSearchResultRow.setCreatedDate(new Date());
        bulkSearchResultRow.setEmailAddress("test@gmail.com");
        bulkSearchResultRow.setDeliveryLocation("test");
        bulkSearchResultRow.setFileName("test");
        bulkSearchResultRow.setPatronBarcode("123");
        bulkSearchResultRow.setRequestingInstitution("PUL");
        bulkSearchResultRow.setStatus("SUCCESS");
        return bulkSearchResultRow;
    }

    private BulkRequestItemEntity getBulkRequestItemEntity() {
        BulkRequestItemEntity bulkRequestItemEntity = new BulkRequestItemEntity();
        bulkRequestItemEntity.setBulkRequestFileName("test");
        bulkRequestItemEntity.setBulkRequestName("test");
        bulkRequestItemEntity.setBulkRequestStatus("SUCCESS");
        bulkRequestItemEntity.setCreatedBy("test");
        bulkRequestItemEntity.setCreatedDate(new Date());
        bulkRequestItemEntity.setEmailId("test@gmail.com");
        bulkRequestItemEntity.setLastUpdatedDate(new Date());
        bulkRequestItemEntity.setNotes("test");
        bulkRequestItemEntity.setPatronId("123");
        bulkRequestItemEntity.setRequestingInstitutionId(1);
        bulkRequestItemEntity.setStopCode("test");
        bulkRequestItemEntity.setBulkRequestFileData(new byte[1]);
        return bulkRequestItemEntity;
    }
}
