package org.recap.helper;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;

public class HttpInterceptorUT extends BaseTestCaseUT {

    @InjectMocks
    HttpInterceptor httpInterceptor;

    @Mock
    HttpRequest request;

    @Mock
    ClientHttpRequestExecution execution;

    @Mock
    ClientHttpResponse clientHttpResponse;

    @Test
    public void testIntercept() throws IOException {
        Mockito.when(execution.execute(Mockito.any(),Mockito.any())).thenReturn(clientHttpResponse);
        ClientHttpResponse intercept=httpInterceptor.intercept(request,"body".getBytes(StandardCharsets.UTF_8),execution);
        assertNotNull(intercept);
    }
}
