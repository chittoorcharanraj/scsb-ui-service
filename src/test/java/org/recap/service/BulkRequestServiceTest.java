package org.recap.service;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbConstants;
import org.recap.model.jpa.BulkCustomerCodeEntity;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.jpa.UsersEntity;
import org.recap.model.search.BulkRequestForm;
import org.recap.model.search.BulkRequestResponse;
import org.recap.repository.jpa.BulkCustomerCodeDetailsRepository;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Charan Raj C created on 10/04/25
 */
public class BulkRequestServiceTest extends BaseTestCaseUT {

    @InjectMocks
    private BulkRequestService bulkRequestService;

    @Mock
    private BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    private BulkCustomerCodeDetailsRepository bulkCustomerCodeDetailsRepository;

    @Mock
    private RestHeaderService restHeaderService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpSession session;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bulkRequestService = spy(bulkRequestService);
        doReturn(restTemplate).when(bulkRequestService).getRestTemplate();
    }

    @Test
    public void testProcessCreateBulkRequest_SuccessfulFlow() throws Exception {
        BulkRequestForm form = new BulkRequestForm();
        form.setRequestingInstitution("Institution");
        form.setDeliveryLocationInRequest("Institution-CODE");
        form.setPatronEmailAddress("test@email.com");
        form.setBulkRequestName("BulkTest");
        form.setPatronBarcodeInRequest("123456789");

        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "item1\nitem2".getBytes());
        form.setFile(mockFile);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        BulkCustomerCodeEntity customerCodeEntity = new BulkCustomerCodeEntity();
        customerCodeEntity.setImsLocationId(1);

        UsersEntity user = new UsersEntity();
        user.setLoginId("testUser");

        BulkRequestItemEntity savedEntity = new BulkRequestItemEntity();
        savedEntity.setId(100);

        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Boolean> patronResponse = ResponseEntity.ok(true);
        ResponseEntity<Object> exchangeResponse = ResponseEntity.ok(new Object());

        when(institutionDetailsRepository.findByInstitutionCode("Institution")).thenReturn(institutionEntity);
        when(bulkCustomerCodeDetailsRepository.findByCustomerCode("Institution-CODE")).thenReturn(customerCodeEntity);
        when(httpServletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute(ScsbConstants.USER_ID)).thenReturn(1);
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(user));
        when(securityUtil.getEncryptedValue("test@email.com")).thenReturn("encryptedEmail");
        when(bulkRequestDetailsRepository.save(any())).thenReturn(savedEntity);
        when(restHeaderService.getHttpHeaders()).thenReturn(headers);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class)))
                .thenReturn(patronResponse);
        when(restTemplate.exchange(any(), eq(HttpMethod.POST), any(HttpEntity.class), eq(BulkRequestResponse.class)))
                .thenReturn((ResponseEntity) exchangeResponse);

        try{
        BulkRequestForm result = bulkRequestService.processCreateBulkRequest(form, httpServletRequest);
        assertTrue(result.isSubmitted());
        assertEquals("test.csv", result.getFileName());
        assertFalse(result.isShowRequestErrorMsg());}catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testProcessCreateBulkRequest_PatronValidationFails() {
        BulkRequestForm form = new BulkRequestForm();
        form.setRequestingInstitution("Institution");
        form.setPatronBarcodeInRequest("invalid");

        ResponseEntity<Boolean> responseEntity = ResponseEntity.ok(false);
        when(restHeaderService.getHttpHeaders()).thenReturn(new HttpHeaders());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class)))
                .thenReturn(responseEntity);

        BulkRequestForm result = bulkRequestService.processCreateBulkRequest(form, httpServletRequest);

        assertTrue(result.isShowRequestErrorMsg());
        assertEquals("Patron Barcode is incorrect", result.getErrorMessage());
    }

    @Test
    public void testProcessCreateBulkRequest_IOException() throws Exception {
        BulkRequestForm form = new BulkRequestForm();
        form.setRequestingInstitution("Institution");
        form.setDeliveryLocationInRequest("Institution-CODE");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenThrow(new IOException("File error"));
        form.setFile(mockFile);

        when(restHeaderService.getHttpHeaders()).thenReturn(new HttpHeaders());
        ResponseEntity<Boolean> responseEntity = ResponseEntity.ok(true);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Boolean.class)))
                .thenReturn(responseEntity);

        BulkRequestForm result = bulkRequestService.processCreateBulkRequest(form, httpServletRequest);

        assertFalse(result.isSubmitted());
    }
}
