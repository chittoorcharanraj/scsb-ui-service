package org.recap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(MockitoJUnitRunner.class)
public class RequestLogControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestLogController requestLogController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestLogController).build();
        objectMapper = new ObjectMapper(); // Use real ObjectMapper
    }

    @Test
    public void testGetRequestsLogReports() throws Exception {
        RequestLogReportRequest request = getRequestLogReportRequest();
        RequestLogReportRequest mockResponse = getRequestLogReportRequest();

        Mockito.when(requestService.getRequestReports(Mockito.any(RequestLogReportRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/request-log/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    @Test
    public void testSubmitRequestsLogReports() throws Exception {
        RequestLogReportRequest request = getRequestLogReportRequest();
        RequestLogReportRequest mockResponse = getRequestLogReportRequest();

        Mockito.when(requestService.submitRequestReports(Mockito.any(RequestLogReportRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/request-log/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    private RequestLogReportRequest getRequestLogReportRequest() {
        RequestLogReportRequest request = new RequestLogReportRequest();
        request.setTotalRecordsCount(100L);
        request.setPageNumber(1);
        request.setPageSize(20);
        request.setTotalPageCount(5);
        request.setFromDate("2023-09-01");
        request.setToDate("2023-09-30");
        request.setInstitution("Some Institution");
        request.setStatus("Approved");
        request.setValidationStatus("Validated");
        request.setGatewayRequestLogId(12345);

        List<RequestInfo> requestInfoList = new ArrayList<>();
        requestInfoList.add(getRequestInfo());
        request.setRequestInfoList(requestInfoList);
        return request;
    }

    private RequestInfo getRequestInfo() {
        RequestInfo info = new RequestInfo();
        info.setId(101);
        info.setRequestInstitution("Requesting Institution");
        info.setItemOwningInstitution("Owning Institution");
        info.setRequestRecieved("2023-09-01");
        info.setRequestedItemBarcode("ITEM123456");
        info.setResponseMessage("Request successfully processed");
        info.setValidationMessage("Valid request");
        info.setStatusId(1);
        info.setStatus("Completed");
        info.setDate(new Date());
        return info;
    }
}
