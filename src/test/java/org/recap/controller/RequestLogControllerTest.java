package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.request.RequestInfo;
import org.recap.model.request.RequestLogReportRequest;
import org.recap.service.RequestService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.Silent.class)
public class RequestLogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestLogController requestLogController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(requestLogController).build();
    }


    @Test
    public void testGetRequestsLogReports() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();
        RequestLogReportRequest mockResponse = getRequestLogReportRequest();
        Mockito.when(requestService.getRequestReports(requestLogReportRequest)).thenReturn(mockResponse);
        String requestJson = objectMapper.writeValueAsString(requestLogReportRequest);
        String responseJson = objectMapper.writeValueAsString(mockResponse);

        System.out.println("Request JSON: " + requestJson);
        System.out.println("Response JSON: " + responseJson);

        mockMvc.perform(post("/request-log/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))  // Use real objectMapper here
                .andExpect(status().isOk());
    }
    @Test
    public void testSubmitRequestsLogReports() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        RequestLogReportRequest requestLogReportRequest = getRequestLogReportRequest();  // Get the request object
        RequestLogReportRequest mockResponse = getRequestLogReportRequest();  // Mock the response
        Mockito.when(requestService.submitRequestReports(requestLogReportRequest)).thenReturn(mockResponse);
        String requestJson = objectMapper.writeValueAsString(requestLogReportRequest);
        String responseJson = objectMapper.writeValueAsString(mockResponse);
        System.out.println("Request JSON: " + requestJson);
        System.out.println("Response JSON: " + responseJson);
        mockMvc.perform(post("/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

    }



    public RequestLogReportRequest getRequestLogReportRequest(){
        RequestLogReportRequest requestLogReportRequest = new RequestLogReportRequest();

        requestLogReportRequest.setTotalRecordsCount(100L);
        requestLogReportRequest.setPageNumber(1);
        requestLogReportRequest.setPageSize(20);
        requestLogReportRequest.setTotalPageCount(5);
        requestLogReportRequest.setFromDate("2023-09-01");
        requestLogReportRequest.setToDate("2023-09-30");
        requestLogReportRequest.setInstitution("Some Institution");
        requestLogReportRequest.setStatus("Approved");
        requestLogReportRequest.setValidationStatus("Validated");
        requestLogReportRequest.setGatewayRequestLogId(12345);

        List<RequestInfo> requestInfoList = new ArrayList<>();
        RequestInfo requestInfo = getRequestInfo();
        requestInfoList.add(requestInfo);
        requestLogReportRequest.setRequestInfoList(requestInfoList);
        return requestLogReportRequest;
    }

    private RequestInfo getRequestInfo() {
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setId(101);
        requestInfo.setRequestInstitution("Requesting Institution");
        requestInfo.setItemOwningInstitution("Owning Institution");
        requestInfo.setRequestRecieved("2023-09-01");
        requestInfo.setRequestedItemBarcode("ITEM123456");
        requestInfo.setResponseMessage("Request successfully processed");
        requestInfo.setValidationMessage("Valid request");
        requestInfo.setStatusId(1);
        requestInfo.setStatus("Completed");
        requestInfo.setDate(new Date());
        return requestInfo;
    }
}
