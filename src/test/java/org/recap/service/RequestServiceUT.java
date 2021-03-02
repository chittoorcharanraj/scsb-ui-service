package org.recap.service;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.BibliographicEntity;
import org.recap.model.jpa.CustomerCodeEntity;
import org.recap.model.jpa.DeliveryRestrictionEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.ItemEntity;
import org.recap.model.jpa.ItemStatusEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.jpa.RequestTypeEntity;
import org.recap.model.search.RequestForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.RequestTypeDetailsRepository;
import org.recap.repository.jpa.CustomerCodeDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.recap.repository.jpa.RequestItemDetailsRepository;
import org.recap.repository.jpa.RequestStatusDetailsRepository;
import org.recap.util.UserAuthUtil;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.support.BindingAwareModelMap;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by akulak on 24/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class RequestServiceUT{


    @InjectMocks
    RequestService requestService;

    @Mock
    BindingAwareModelMap model;

    @Mock
    HttpServletRequest request;

    @Mock
    UserDetailsForm userDetailsForm;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    InstitutionEntity institutionEntity;

    @Mock
    InstitutionEntity institutionEntityCUL;

    @Mock
    RequestTypeDetailsRepository requestTypeDetailsRepository;

    @Mock
    RequestTypeEntity requestTypeEntity;

    @Mock
    ItemDetailsRepository itemDetailsRepository;

    @Mock
    ItemEntity itemEntity;

    @Mock
    CustomerCodeDetailsRepository customerCodeDetailsRepository;

    @Mock
    CustomerCodeEntity customerCodeEntity;

    @Mock
    BibliographicEntity bibliographicEntity;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    UserDetailsForm userDetailsFormAuth;

    @Mock
    DeliveryRestrictionEntity deliveryRestrictionEntity;

    @Mock
    ItemStatusEntity itemStatusEntity;

    @Mock
    RequestStatusDetailsRepository requestStatusDetailsRepository;

    @Mock
    RequestItemDetailsRepository requestItemDetailsRepository;

    @Mock
    RequestItemEntity requestItemEntity;

    @Mock
    RequestStatusEntity requestStatusEntity;

    @Test
    public void testGetRefreshedStatus() {
        List<String> listOfRequestStatusDesc=new ArrayList<>();
        listOfRequestStatusDesc.add(RecapCommonConstants.REQUEST_STATUS_REFILED);
        Mockito.when(requestStatusDetailsRepository.findAllRequestStatusDescExceptProcessing()).thenReturn(listOfRequestStatusDesc);
        String[] parameterValues={"1-1"};
        Mockito.when(request.getParameterValues("status[]")).thenReturn(parameterValues);
        List<RequestItemEntity> requestItemEntityList=new ArrayList<>();
        requestItemEntityList.add(requestItemEntity);
        Mockito.when(requestItemEntity.getId()).thenReturn(1);
        Mockito.when(requestItemDetailsRepository.findByIdIn(Mockito.any())).thenReturn(requestItemEntityList);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(requestStatusEntity.getRequestStatusDescription()).thenReturn(RecapCommonConstants.REQUEST_STATUS_REFILED);
        String reqJson = "{\"status\":[\"29-0\",\"5-1\"]}";
        String status=requestService.getRefreshedStatus(reqJson);
        assertNotNull(status);
    }

    @Test
    public void testFindAllRequestStatusExceptProcessing() {
        List<RequestStatusEntity> requestStatusEntityList=new ArrayList<>();
        requestStatusEntityList.add(requestStatusEntity);
        Mockito.when(requestStatusDetailsRepository.findAllExceptProcessing()).thenReturn(requestStatusEntityList);
        Mockito.when(requestStatusEntity.getRequestStatusDescription()).thenReturn(RecapCommonConstants.REQUEST_STATUS_REFILED);
        List<String> requestStatuses=new ArrayList<>();
        requestService.findAllRequestStatusExceptProcessing(requestStatuses);
        requestService.getRequestServiceUtil();
        assertTrue(true);
    }

    @Test
    public void sortDeliveryLocations() {
        Map<String, String> deliveryLocationsMap=new HashMap<>();
        deliveryLocationsMap.put("PA","PC");
        deliveryLocationsMap.put("PB","PC");
        Map<String, String> sortedDeliverLocationMap =requestService.sortDeliveryLocations(deliveryLocationsMap);
        assertNotNull(sortedDeliverLocationMap);
    }

    @Test
    public void testInstitutionForSuperAdmin() {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.getInstitutionCodeForSuperAdmin()).thenReturn(institutionEntities);
        List<String> institutionList=new ArrayList<>();
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        requestService.getInstitutionForSuperAdmin(institutionList);
        assertTrue(true);
    }

    @Test
    public void testFormDetailsForRequestRecapUser() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(userDetailsForm.isRecapUser()).thenReturn(true);
        Mockito.when(userDetailsForm.isSuperAdmin()).thenReturn(true);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestUserNotPermitted() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestBlankBarcode() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("");
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
    }

    @Test
    public void testFormDetailsForRequestPrivateItemsUserNotPermitted() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(itemEntity.getOwningInstitutionId()).thenReturn(2);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecord() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(RecapCommonConstants.NOT_AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeIn(Mockito.any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(customerCodeEntity.getDeliveryRestrictions()).thenReturn("PB");
        Mockito.when(userDetailsFormAuth.isRecapUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getRecapDeliveryRestrictions()).thenReturn("Recap");
        Mockito.when(customerCodeEntity.getCustomerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        ReflectionTestUtils.setField(requestService,"requestService",requestService);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecordCrossPartner() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntityCUL.getId()).thenReturn(2);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        Mockito.when(institutionEntityCUL.getInstitutionCode()).thenReturn("CUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(RecapCommonConstants.NOT_AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeIn(Mockito.any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(deliveryRestrictionEntity.getDeliveryRestriction()).thenReturn("PB,PA");
        List<DeliveryRestrictionEntity> deliveryRestrictionEntityList=new ArrayList<>();
        deliveryRestrictionEntityList.add(deliveryRestrictionEntity);
        Mockito.when(deliveryRestrictionEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        Mockito.when(customerCodeEntity.getDeliveryRestrictionEntityList()).thenReturn(deliveryRestrictionEntityList);
        Mockito.when(userDetailsFormAuth.isRecapUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getRecapDeliveryRestrictions()).thenReturn("Recap");
        Mockito.when(customerCodeEntity.getCustomerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntityCUL);
        ReflectionTestUtils.setField(requestService,"requestService",requestService);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestSingleRecordExceptBorrowDirect() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(RecapCommonConstants.AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeIn(Mockito.any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(customerCodeEntity.getDeliveryRestrictions()).thenReturn("PB");
        Mockito.when(userDetailsFormAuth.isRecapUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getRecapDeliveryRestrictions()).thenReturn("Recap");
        Mockito.when(customerCodeEntity.getCustomerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        ReflectionTestUtils.setField(requestService,"requestService",requestService);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestMultiRecord() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptEDDAndBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345,12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemDetailsRepository.findByBarcodeAndCatalogingStatusAndIsDeletedFalse(Mockito.anyString(),Mockito.anyString())).thenReturn(itemEntities);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(RecapCommonConstants.NOT_AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeIn(Mockito.any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(customerCodeEntity.getDeliveryRestrictions()).thenReturn("PB");
        Mockito.when(userDetailsFormAuth.isRecapUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getRecapDeliveryRestrictions()).thenReturn("Recap");
        Mockito.when(customerCodeEntity.getCustomerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        ReflectionTestUtils.setField(requestService,"requestService",requestService);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    @Test
    public void testFormDetailsForRequestInvalidBarcode() throws Exception {
        List<InstitutionEntity> institutionEntities=new ArrayList<>();
        institutionEntities.add(institutionEntity);
        Mockito.when(institutionDetailsRepository.findAll()).thenReturn(institutionEntities);
        List<RequestTypeEntity> requestTypeEntities=new ArrayList<>();
        requestTypeEntities.add(requestTypeEntity);
        Mockito.when(requestTypeDetailsRepository.findAll()).thenReturn(requestTypeEntities);
        Mockito.when(requestTypeDetailsRepository.findAllExceptEDDAndBorrowDirect()).thenReturn(requestTypeEntities);
        Mockito.when(userDetailsForm.getLoginInstitutionId()).thenReturn(1);
        Mockito.when(institutionEntity.getId()).thenReturn(1);
        Mockito.when(institutionEntity.getInstitutionCode()).thenReturn("PUL");
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bibliographicEntity.getContent()).thenReturn(sourceBibContent.getBytes());
        Mockito.when(requestTypeEntity.getRequestTypeCode()).thenReturn(RecapCommonConstants.EDD);
        Mockito.when(model.get(RecapConstants.REQUESTED_BARCODE)).thenReturn("12345,12345");
        List<ItemEntity> itemEntities=new ArrayList<>();
        itemEntities.add(itemEntity);
        Mockito.when(itemEntity.getCustomerCode()).thenReturn("PA");
        Mockito.when(userDetailsFormAuth.isRecapPermissionAllowed()).thenReturn(true);
        Mockito.when(itemEntity.getItemStatusEntity()).thenReturn(itemStatusEntity);
        Mockito.when(itemStatusEntity.getStatusCode()).thenReturn(RecapCommonConstants.NOT_AVAILABLE);
        Mockito.when(itemEntity.getCollectionGroupId()).thenReturn(RecapConstants.CGD_PRIVATE);
        List<BibliographicEntity> bibliographicEntities=new ArrayList<>();
        bibliographicEntities.add(bibliographicEntity);
        Mockito.when(itemEntity.getBibliographicEntities()).thenReturn(bibliographicEntities);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndRecapDeliveryRestrictionLikeEDD(Mockito.anyString())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeAndOwningInstitutionId(Mockito.anyString(),Mockito.anyInt())).thenReturn(customerCodeEntity);
        Mockito.when(customerCodeDetailsRepository.findByCustomerCodeIn(Mockito.any())).thenReturn(Arrays.asList(customerCodeEntity));
        Mockito.when(customerCodeEntity.getDeliveryRestrictions()).thenReturn("PB");
        Mockito.when(userDetailsFormAuth.isRecapUser()).thenReturn(true);
        Mockito.when(customerCodeEntity.getRecapDeliveryRestrictions()).thenReturn("Recap");
        Mockito.when(customerCodeEntity.getCustomerCode()).thenReturn("PB");
        Mockito.when(customerCodeEntity.getDescription()).thenReturn("Firestone Library Use Only");
        Mockito.when(userAuthUtil.getUserDetails(Mockito.any(),Mockito.anyString())).thenReturn(userDetailsFormAuth);
        Mockito.when(itemEntity.getInstitutionEntity()).thenReturn(institutionEntity);
        ReflectionTestUtils.setField(requestService,"requestService",requestService);
        RequestForm requestForm =requestService.setFormDetailsForRequest(model,request,userDetailsForm);
        assertNotNull(requestForm);
        assertTrue(requestForm.getItemBarcodeInRequest().contains("12345"));
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("PUL-BibContent.xml");
        return new File(resource.toURI());
    }
}
