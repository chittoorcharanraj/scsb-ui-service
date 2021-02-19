package org.recap.controller;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.CancelRequestResponse;
import org.recap.model.jpa.*;
import org.recap.model.request.ItemRequestInformation;
import org.recap.model.request.ItemResponseInformation;
import org.recap.model.request.ReplaceRequest;
import org.recap.model.search.RequestForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.service.RequestService;
import org.recap.service.RestHeaderService;
import org.recap.util.RequestServiceUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class RequestControllerUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    RequestController requestController;

    @Mock
    RequestService requestService;

    @Mock
    HttpServletRequest request;

    @Mock
    RestTemplate restTemplate;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    HttpHeaders httpHeaders;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestServiceUtil requestServiceUtil;

    String scsbUrl = "http://localhost:9040/testurl";

    String scsbShiro = "testscsb";

    @Before
    public void setUp(){
       // ReflectionTestUtils.invokeMethod(requestController,"scsbUrl",scsbUrl);
       // ReflectionTestUtils.invokeMethod(requestController,"scsbShiro",scsbShiro);
    }

    @Test
    public void request(){
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_REQUEST_URL)).thenReturn(Boolean.TRUE);
        boolean result = requestController.request(request);
        assertNotNull(result);
    }

    @Test
    public void searchRequests(){
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchRequests(requestForm);
        assertNotNull(form);
    }
    @Test
    public void searchRequestsException(){
        RequestForm form = requestController.searchRequests(null);
        assertNull(form);
    }

    @Test
    public void goToSearchRequest(){
        RequestForm requestForm = getRequestForm();
        UserDetailsForm userDetailsForm = getUserDetails();
        InstitutionEntity institutionEntity = getRequestItemEntity().getInstitutionEntity();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.doNothing().when(requestService).findAllRequestStatusExceptProcessing(any());
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        RequestForm form = requestController.goToSearchRequest(requestForm,request);
        assertNotNull(form);
    }

    @Test
    public void goToSearchRequestException(){
        RequestForm requestForm = getRequestForm();
        UserDetailsForm userDetailsForm = getUserDetails();
        InstitutionEntity institutionEntity = getRequestItemEntity().getInstitutionEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.doNothing().when(requestService).findAllRequestStatusExceptProcessing(any());
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        RequestForm form = requestController.goToSearchRequest(requestForm,request);
        assertNotNull(form);
    }

    @Test
    public void searchFirst(){
        RequestForm requestForm = getRequestForm();
        requestForm.setInstitution("UC");
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchFirst(requestForm);
        assertNotNull(form);
    }
    @Test
    public void searchNext(){
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchNext(requestForm);
        assertNotNull(form);
    }
    @Test
    public void searchLast(){
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchLast(requestForm);
        assertNotNull(form);
    }
    @Test
    public void searchPrevious(){
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = getPage();
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.searchPrevious(requestForm);
        assertNotNull(form);
    }
    @Test
    public void onRequestPageSizeChange(){
        RequestForm requestForm = getRequestForm();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(requestServiceUtil.searchRequests(any())).thenReturn(requestItemEntities);
        RequestForm form = requestController.onRequestPageSizeChange(requestForm);
        assertNotNull(form);
    }
    @Test
    public void loadCreateRequest(){
        RequestForm requestForm = getRequestForm();
        UserDetailsForm userDetailsForm = getUserDetails();
        userDetailsForm.setSuperAdmin(Boolean.TRUE);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        Mockito.when(requestService.setDefaultsToCreateRequest(any())).thenReturn(requestForm);
        RequestForm form = requestController.loadCreateRequest(request);
        assertNotNull(form);
    }
    @Test
    public void loadSearchRequest(){
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Page<RequestItemEntity> requestItemEntities = new PageImpl<RequestItemEntity>(new ArrayList<>());
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.doNothing().when(requestService).findAllRequestStatusExceptProcessing(any());
        Mockito.doNothing().when(requestService).getInstitutionForSuperAdmin(any());
        Mockito.when(userAuthUtil.getUserDetails(session, RecapConstants.REQUEST_PRIVILEGE)).thenReturn(userDetailsForm);
        RequestForm form = requestController.loadSearchRequest(request);
        assertNotNull(form);
    }

    @Test
    public void populateItem() throws JSONException {
        RequestForm requestForm = getRequestForm();
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn("test");
        String result = requestController.populateItem(requestForm,request);
        assertNotNull(result);
        assertEquals("test",result);
    }

    @Test
    public void populateItemException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenThrow(new NullPointerException());
        String result = requestController.populateItem(requestForm,request);
        assertNotNull(result);
    }
    @Test
    public void createRequest() throws JSONException {
        RequestForm requestForm = getRequestForm();
        ResponseEntity responseEntity1 = new ResponseEntity<>(getItemResponseInformation(),HttpStatus.OK);
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(responseEntity1).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm,request);
        assertNotNull(form);
    }
    @Test
    public void createRequestException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm,request);
        assertNotNull(form);
    }
    @Test
    public void createRequestHttpClientErrorException() throws JSONException {
        RequestForm requestForm = getRequestForm();
        requestForm.setVolumeNumber(null);
        CustomerCodeEntity customerCodeEntity = getCustomerCodeEntity();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(null);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCode(any())).thenReturn(customerCodeEntity);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<ItemRequestInformation>>any());
        RequestForm form = requestController.createRequest(requestForm,request);
        assertNotNull(form);
    }
    @Test
    public void createRequestWithNoPermissionErrorMessage() throws JSONException {
        RequestForm requestForm = getRequestForm();
        JSONObject json = getJsonObject();
        String message = json.toString();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(message);
        RequestForm form = requestController.createRequest(requestForm,request);
        assertNotNull(form);
    }
    @Test
    public void createRequestWithErrorMessage() throws JSONException {
        RequestForm requestForm = getRequestForm();
        JSONObject json = getJsonObject1();
        String message = json.toString();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when((String) session.getAttribute(RecapConstants.USER_NAME)).thenReturn("Admin");
        Mockito.when(requestService.populateItemForRequest(requestForm, request)).thenReturn(message);
        RequestForm form = requestController.createRequest(requestForm,request);
        assertNotNull(form);
    }

    @Test
    public void cancelRequest(){
        RequestForm requestForm = getRequestForm();
        CancelRequestResponse cancelRequestResponse = getCancelRequestResponse();
        ResponseEntity<CancelRequestResponse> responseEntity = new ResponseEntity<CancelRequestResponse>(cancelRequestResponse,HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        Mockito.when(requestItemDetailsRepository.findById(any())).thenReturn(Optional.of(getRequestItemEntity()));
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        String result = requestController.cancelRequest(requestForm);
        assertNotNull(result);
    }
    @Test
    public void cancelRequestException(){
        RequestForm requestForm = getRequestForm();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        String result = requestController.cancelRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void resubmitRequest(){
        RequestForm requestForm = getRequestForm();
        Map<String,String> map = new HashMap<>();
        map.put("1", RecapCommonConstants.SUCCESS);
        ResponseEntity responseEntity = new ResponseEntity<>(map,HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }
    @Test
    public void resubmitInvalidRequest(){
        RequestForm requestForm = getRequestForm();
        Map<String,String> map = new HashMap<>();
        map.put(RecapCommonConstants.INVALID_REQUEST, "Invalid Request");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }
    @Test
    public void resubmitRequestFailed(){
        RequestForm requestForm = getRequestForm();
        Map<String,String> map = new HashMap<>();
        map.put(RecapCommonConstants.FAILURE, "Resubmit Failed");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }
    @Test
    public void resubmitRequestException(){
        RequestForm requestForm = getRequestForm();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(requestController.getScsbUrl()).thenReturn(scsbUrl);
        Mockito.when(requestController.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        String result = requestController.resubmitRequest(requestForm);
        assertNotNull(result);
    }

    @Test
    public void refreshStatus(){
        Mockito.when(requestService.getRefreshedStatus(request)).thenReturn(RecapCommonConstants.COMPLETE_STATUS);
        String result = requestController.refreshStatus(request);
        assertNotNull(result);
        assertEquals("Complete",result);

    }

    private CancelRequestResponse getCancelRequestResponse() {
        CancelRequestResponse cancelRequestResponse = new CancelRequestResponse();
        cancelRequestResponse.setSuccess(true);
        cancelRequestResponse.setScreenMessage("Request cancelled.");
        return cancelRequestResponse;
    }

    private CustomerCodeEntity getCustomerCodeEntity() {
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setCustomerCode("PG");
        customerCodeEntity.setPwdDeliveryRestrictions("PA");
        customerCodeEntity.setDeliveryRestrictions("PA");
        customerCodeEntity.setDescription("Test");
        customerCodeEntity.setOwningInstitutionId(1);
        customerCodeEntity.setId(1);
        return customerCodeEntity;
    }

    private JSONObject getJsonObject() {
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        jsonObject.put("1","PA");
        jsonObject.put("2","PB");
        array.put("RECALL");
        array.put("RETRIEVE");
        json.put("error", "No Error");
        json.put("noPermissionErrorMessage", "No");
        json.put("itemTitle", "testName");
        json.put("itemOwningInstitution", "CUL");
        json.put("deliveryLocation",jsonObject);
        json.put("requestTypes",array);
        return json;
    }
    private JSONObject getJsonObject1() {
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        jsonObject.put("1","PA");
        jsonObject.put("2","PB");
        array.put("RECALL");
        array.put("RETRIEVE");
        json.put("error", "No Error");
        json.put("errorMessage", "Request Error");
        json.put("itemTitle", "testName");
        json.put("itemOwningInstitution", "CUL");
        json.put("deliveryLocation",jsonObject);
        json.put("requestTypes",array);
        return json;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setRecapUser(true);
        userDetailsForm.setSuperAdmin(true);
        return userDetailsForm;
    }

    private RequestItemEntity getRequestItemEntity(){
        RequestStatusEntity requestStatusEntity=new RequestStatusEntity();
        requestStatusEntity.setRequestStatusDescription("RECALL");
        RequestTypeEntity requestTypeEntity = new RequestTypeEntity();
        requestTypeEntity.setRequestTypeCode("RECALL");
        RequestItemEntity requestItemEntity = new RequestItemEntity();
        requestItemEntity.setRequestStatusId(15);
        requestItemEntity.setId(16);
        requestItemEntity.setRequestStatusEntity(requestStatusEntity);
        requestItemEntity.setRequestTypeEntity(requestTypeEntity);
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        requestItemEntity.setInstitutionEntity(institutionEntity);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setStatusCode("Complete");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("CU12513083");
        itemEntity.setCatalogingStatus("Complete");
        itemEntity.setItemStatusEntity(itemStatusEntity);
        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent("Mock Bib Content".getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionBibId("2");
        bibliographicEntity.setOwningInstitutionId(3);
        itemEntity.setBibliographicEntities(Arrays.asList(bibliographicEntity));
        itemEntity.setInstitutionEntity(institutionEntity);;
        requestItemEntity.setItemEntity(itemEntity);
        requestItemEntity.setRequestingInstitutionId(2);
        return requestItemEntity;
    }

    private ReplaceRequest getReplaceRequest(){
        ReplaceRequest replaceRequest = new ReplaceRequest();
        RequestForm requestForm = getRequestForm();
        replaceRequest.setEndRequestId("10");
        replaceRequest.setFromDate((new Date()).toString());
        replaceRequest.setToDate((new Date()).toString());
        replaceRequest.setReplaceRequestByType(RecapCommonConstants.REQUEST_IDS);
        replaceRequest.setRequestStatus(RecapConstants.EXCEPTION);
        String requestId = String.valueOf(requestForm.getRequestId());
        replaceRequest.setRequestIds(requestId);
        replaceRequest.setStartRequestId("1");
        return replaceRequest;
    }
    private RequestForm getRequestForm(){
        RequestForm requestForm = new RequestForm();
        requestForm.setRequestId(1);
        requestForm.setPatronBarcode("43265854");
        requestForm.setSubmitted(true);
        requestForm.setItemBarcode("32101074849843");
        requestForm.setStatus("active");
        requestForm.setDeliveryLocation("PB");
        requestForm.setVolumeNumber("1");
        requestForm.setMessage("testing");
        requestForm.setErrorMessage("error");
        requestForm.setIssue("issue");
        requestForm.setTotalPageCount(1);
        requestForm.setTotalRecordsCount("10");
        requestForm.setPageSize(1);
        requestForm.setPageNumber(1);
        requestForm.setInstitutionList(Arrays.asList("CU"));
        requestForm.setRequestingInstitutions(Arrays.asList("PUL"));
        requestForm.setRequestTypes(Arrays.asList("Recall"));
        requestForm.setItemBarcodeInRequest("123");
        requestForm.setPatronBarcodeInRequest("46259871");
        requestForm.setRequestingInstitution("CUL");
        requestForm.setPatronEmailAddress("hemalatha.s@htcindia.com");
        requestForm.setInstitution("CU");
        requestForm.setItemTitle("test");
        requestForm.setItemOwningInstitution("PUL");
        requestForm.setRequestType("recall");
        requestForm.setRequestNotes("test");
        requestForm.setStartPage("2");
        requestForm.setEndPage("5");
        requestForm.setArticleAuthor("john");
        requestForm.setArticleTitle("test");
        requestForm.setDeliveryLocationInRequest("PB");
        return requestForm;
    }

    private ItemRequestInformation getItemRequestInformation(){
        ItemRequestInformation itemRequestInformation = new ItemRequestInformation();
        itemRequestInformation.setUsername("Admin");
        itemRequestInformation.setItemBarcodes(Arrays.asList("123"));
        itemRequestInformation.setPatronBarcode("46259871");
        itemRequestInformation.setRequestingInstitution("PUL");
        itemRequestInformation.setEmailAddress("hemalatha.s@htcindia.com");
        itemRequestInformation.setTitle("test");
        itemRequestInformation.setTitleIdentifier("test");
        itemRequestInformation.setItemOwningInstitution("PUL");
        itemRequestInformation.setRequestType("recall");
        itemRequestInformation.setRequestNotes("test");
        itemRequestInformation.setStartPage("2");
        itemRequestInformation.setEndPage("5");
        itemRequestInformation.setAuthor("john");
        itemRequestInformation.setChapterTitle("test");
        itemRequestInformation.setDeliveryLocation("PB");
        return itemRequestInformation;
    }

    private ItemResponseInformation getItemResponseInformation(){
        ItemResponseInformation itemResponseInformation = new ItemResponseInformation();
        itemResponseInformation.setPatronBarcode("46259871");
        itemResponseInformation.setItemBarcode("123");
        itemResponseInformation.setSuccess(Boolean.FALSE);
        return itemResponseInformation;
    }

    private UserDetailsForm getUserDetails(){
        UserDetailsForm userDetailsForm=new UserDetailsForm();
        userDetailsForm.setLoginInstitutionId(2);
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setRecapPermissionAllowed(false);
        userDetailsForm.setRecapUser(false);
        return userDetailsForm;
    }


    public Page getPage(){
        Page<RequestItemEntity> page = new Page<RequestItemEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 2;
            }

            @Override
            public <R> Page<R> map(Function<? super RequestItemEntity, ? extends R> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<RequestItemEntity> getContent() {

                return Arrays.asList(getRequestItemEntity());
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<RequestItemEntity> iterator() {
                return null;
            }
        };
        return page;
    }
}
