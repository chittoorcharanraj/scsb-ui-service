package org.recap.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.model.deaccession.DeAccessionRequest;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.repository.jpa.ItemChangeLogDetailsRepository;
import org.recap.service.RestHeaderService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class CollectionServiceUtilUT extends BaseTestCaseUT {

    @InjectMocks
    @Spy
    CollectionServiceUtil collectionServiceUtil;

    @Mock
    RestTemplate restTemplate;

    @Mock
    ItemChangeLogDetailsRepository itemChangeLogDetailsRepository;

    @Mock
    RestHeaderService restHeaderService;

    @Mock
    HttpHeaders httpHeaders;

    private String scsbUrl = "http://localhost:8080/testurl";

    @Before
    public void setup(){
        ReflectionTestUtils.setField(collectionServiceUtil,"scsbUrl",scsbUrl);
    }

    @Test
    public void updateCGDForItem(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        ResponseEntity responseEntity = new ResponseEntity("Success", HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        collectionServiceUtil.updateCGDForItem(bibliographicMarcForm);
    }
    @Test
    public void updateCGDForItemFailure(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        ResponseEntity responseEntity = new ResponseEntity("Failure", HttpStatus.OK);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(responseEntity).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        collectionServiceUtil.updateCGDForItem(bibliographicMarcForm);
    }
    @Test
    public void updateCGDForItemException(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new NullPointerException()).when(restTemplate).exchange(
                ArgumentMatchers.any(),
                ArgumentMatchers.any(HttpMethod.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class>any());
        collectionServiceUtil.updateCGDForItem(bibliographicMarcForm);
    }

    @Test
    public void getDeAccessionRequest(){
        DeAccessionRequest deAccessionRequest = collectionServiceUtil.getDeAccessionRequest();
        assertNotNull(deAccessionRequest);
    }

    @Test
    public void deAccessionItem(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");
        Map<String,String> map = new HashMap<>();
        map.put("3254652", ScsbCommonConstants.SUCCESS);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);
    }
    @Test
    public void deAccessionItemRequestedItemDeaccessioned(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");
        Map<String,String> map = new HashMap<>();
        map.put("3254652", ScsbCommonConstants.REQUESTED_ITEM_DEACCESSIONED);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);
    }
    @Test
    public void deAccessionItemLasRejected(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");
        Map<String,String> map = new HashMap<>();
        map.put("3254652", ScsbCommonConstants.LAS_REJECTED);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);
    }
    @Test
    public void deAccessionItemFailure(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");
        Map<String,String> map = new HashMap<>();
        map.put("3254652", ScsbCommonConstants.FAILURE);
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doReturn(map).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);
    }
    @Test
    public void deAccessionItemException(){
        BibliographicMarcForm bibliographicMarcForm = getBibliographicMarcForm();
        bibliographicMarcForm.setCgdChangeNotes("Notes for deaccession");
        Mockito.when(restHeaderService.getHttpHeaders()).thenReturn(httpHeaders);
        Mockito.when(collectionServiceUtil.getRestTemplate()).thenReturn(restTemplate);
        doThrow(new NullPointerException()).when(restTemplate).postForObject(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.any(),
                ArgumentMatchers.<Class<Map>>any());
        collectionServiceUtil.deAccessionItem(bibliographicMarcForm);
    }

    @Test
    public void getCheckers(){
        collectionServiceUtil.getRestHeaderService();
        collectionServiceUtil.getScsbUrl();
        collectionServiceUtil.getItemChangeLogDetailsRepository();
        collectionServiceUtil.getCustomerCodeDetailsRepository();
        collectionServiceUtil.getItemDetailsRepository();
    }

    private BibliographicMarcForm getBibliographicMarcForm() {
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        bibliographicMarcForm.setBibId(3);
        bibliographicMarcForm.setBarcode("235452");
        bibliographicMarcForm.setOwningInstitution("PUL");
        bibliographicMarcForm.setCollectionGroupDesignation("Shared");
        bibliographicMarcForm.setNewCollectionGroupDesignation("Private");
        bibliographicMarcForm.setCgdChangeNotes("Notes for updating CGD");
        bibliographicMarcForm.setItemId(1);
        bibliographicMarcForm.setUsername("test");
        bibliographicMarcForm.setBarcode("3254652");
        bibliographicMarcForm.setWarningMessage("WarningMessage");
        return bibliographicMarcForm;
    }
}
