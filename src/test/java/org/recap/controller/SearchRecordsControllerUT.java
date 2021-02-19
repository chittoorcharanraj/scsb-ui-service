package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchRecordsResponse;
import org.recap.model.search.SearchResultRow;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.SearchUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SearchRecordsControllerUT extends BaseTestCaseUT {

    @InjectMocks
    SearchRecordsController searchRecordsController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    SearchUtil searchUtil;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    WebDataBinder binder;

   @Test
   public void checkGetters(){
       searchRecordsController.getSearchUtil();
       searchRecordsController.getInstitutionDetailsRepository();
       searchRecordsController.getUserAuthUtil();
   }

    @Test
    public void searchRecords() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL)).thenReturn(Boolean.TRUE);
        boolean result = searchRecordsController.searchRecords(request);
        assertTrue(result);
    }

    @Test
    public void searchRecordsFailure() {
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL)).thenReturn(Boolean.FALSE);
        boolean result = searchRecordsController.searchRecords(request);
        assertFalse(result);
    }

    @Test
    public void search() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        Mockito.when(searchUtil.searchAndSetResults(searchRecordsRequest)).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.search(searchRecordsRequest);
        assertNotNull(response);
    }

    @Test
    public void searchPrevious() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        Mockito.when(searchUtil.searchAndSetResults(searchRecordsRequest)).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.searchPrevious(searchRecordsRequest);
        assertNotNull(response);
    }

    @Test
    public void searchNext() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        Mockito.when(searchUtil.searchAndSetResults(searchRecordsRequest)).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.searchNext(searchRecordsRequest);
        assertNotNull(response);
    }

    @Test
    public void searchFirst() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setPageNumber(0);
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        Mockito.when(searchUtil.searchRecord(searchRecordsRequest)).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.searchFirst(searchRecordsRequest);
        assertNotNull(response);
    }

    @Test
    public void searchLast() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        Mockito.when(searchUtil.searchAndSetResults(searchRecordsRequest)).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.searchLast(searchRecordsRequest);
        assertNotNull(response);
    }

    @Test
    public void onPageSizeChange() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setPageNumber(20);
        SearchRecordsResponse searchRecordsResponse = getSearchRecordsResponse();
        when(searchUtil.searchAndSetResults(any())).thenReturn(searchRecordsResponse);
        SearchRecordsResponse response = searchRecordsController.onPageSizeChange(searchRecordsRequest);
        assertNotNull(response);
        assertEquals(searchRecordsResponse, response);
    }

    @Test
    public void isEmptyField() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setFieldValue("10");
        ReflectionTestUtils.invokeMethod(searchRecordsController, "isEmptyField", searchRecordsRequest);
    }

    @Test
    public void isItemField() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setFieldName(RecapCommonConstants.CALL_NUMBER);
        ReflectionTestUtils.invokeMethod(searchRecordsController, "isItemField", searchRecordsRequest);
    }

    @Test
    public void initBinder(){
        searchRecordsController.initBinder(binder);
    }

    @Test
    public void processRequest() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setErrorMessage("error");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setRequestId(1);
        searchResultRow.setSelected(true);
        searchResultRow.setCollectionGroupDesignation("Private");
        searchResultRow.setOwningInstitution("PUL");
        searchResultRow.setTitle("test");
        searchResultRow.setBarcode("23456");
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setLoginInstitutionId(1);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        ReflectionTestUtils.invokeMethod(searchRecordsController, "processRequest", searchRecordsRequest, userDetailsForm, redirectAttributes);
    }

    @Test
    public void processRequestWithoutSuperAdmin() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setErrorMessage("test");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setRequestId(1);
        searchResultRow.setSelected(true);
        searchResultRow.setCollectionGroupDesignation("Private");
        searchResultRow.setOwningInstitution("PUL");
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(1);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        ReflectionTestUtils.invokeMethod(searchRecordsController, "processRequest", searchRecordsRequest, userDetailsForm, redirectAttributes);
    }

    @Test
    public void processRequestWithoutErrorMessage() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setErrorMessage("");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setRequestId(1);
        searchResultRow.setSelected(true);
        searchResultRow.setCollectionGroupDesignation("Private");
        searchResultRow.setOwningInstitution("PUL");
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        ReflectionTestUtils.invokeMethod(searchRecordsController, "processRequest", searchRecordsRequest, userDetailsForm, redirectAttributes);
    }

    @Test
    public void processRequestWithSuperAdmin() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setErrorMessage("error");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setRequestId(1);
        searchResultRow.setSelected(false);
        searchResultRow.setCollectionGroupDesignation("Private");
        searchResultRow.setOwningInstitution("PUL");
        searchResultRow.setTitle("test");
        searchResultRow.setBarcode("23456");
        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setSelectedItem(true);
        searchResultRow.setSearchItemResultRows(Arrays.asList(searchItemResultRow));
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setRecapPermissionAllowed(false);
        userDetailsForm.setLoginInstitutionId(1);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        ReflectionTestUtils.invokeMethod(searchRecordsController, "processRequest", searchRecordsRequest, userDetailsForm, redirectAttributes);
    }
    @Test
    public void processRequestWithRecapPermission() {
        SearchRecordsRequest searchRecordsRequest = getSearchRecordsRequest();
        searchRecordsRequest.setErrorMessage("error");
        SearchResultRow searchResultRow = new SearchResultRow();
        searchResultRow.setRequestId(1);
        searchResultRow.setSelected(false);
        searchResultRow.setCollectionGroupDesignation("Private");
        searchResultRow.setOwningInstitution("PUL");
        searchResultRow.setTitle("test");
        searchResultRow.setBarcode("23456");
        SearchItemResultRow searchItemResultRow = new SearchItemResultRow();
        searchItemResultRow.setSelectedItem(true);
        searchResultRow.setSearchItemResultRows(Arrays.asList(searchItemResultRow));
        searchRecordsRequest.setSearchResultRows(Arrays.asList(searchResultRow));
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setLoginInstitutionId(1);
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.findById(any())).thenReturn(Optional.of(institutionEntity));
        ReflectionTestUtils.invokeMethod(searchRecordsController, "processRequest", searchRecordsRequest, userDetailsForm, redirectAttributes);
    }

    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }

    private SearchRecordsRequest getSearchRecordsRequest() {
        SearchRecordsRequest searchRecordsRequest = new SearchRecordsRequest();
        return searchRecordsRequest;
    }

    private SearchRecordsResponse getSearchRecordsResponse() {
        SearchRecordsResponse searchRecordsResponse = new SearchRecordsResponse();
        searchRecordsResponse.setSearchResultRows(new ArrayList<>());
        searchRecordsResponse.setTotalPageCount(1);
        searchRecordsResponse.setTotalBibRecordsCount("1");
        searchRecordsResponse.setTotalItemRecordsCount("1");
        searchRecordsResponse.setTotalRecordsCount("1");
        searchRecordsResponse.setShowTotalCount(false);
        searchRecordsResponse.setErrorMessage("test");
        return searchRecordsResponse;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        userDetailsForm.setLoginInstitutionId(1);
        return userDetailsForm;
    }
}
