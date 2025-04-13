package org.recap.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.recap.BaseTestCaseUT;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.util.HelperUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * @author Charan Raj C created on 11/04/25
 */
public class RequestServiceTest extends BaseTestCaseUT {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HelperUtil helperUtil;

    @InjectMocks
    private RequestService requestService;

    @Test
    public void submitRequestReportsSuccessTest() {
        RequestLogReportRequest request = new RequestLogReportRequest();
        ResponseEntity<RequestLogReportRequest> response = ResponseEntity.ok(request);
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(RequestLogReportRequest.class)))
                .thenReturn(response);
        try {
            RequestLogReportRequest result = requestService.submitRequestReports(request);
            assertEquals(request, result);
            verify(restTemplate).exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(RequestLogReportRequest.class));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void submitRequestReportsFailureTest() {
        RequestLogReportRequest request = new RequestLogReportRequest();
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(RequestLogReportRequest.class)))
                .thenThrow(new RuntimeException("Test exception"));
        assertThrows(RuntimeException.class, () -> requestService.submitRequestReports(request));
    }
}
