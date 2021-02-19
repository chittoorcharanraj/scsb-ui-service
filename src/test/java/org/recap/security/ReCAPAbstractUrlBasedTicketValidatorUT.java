package org.recap.security;

import org.jasig.cas.client.ssl.HttpURLConnectionFactory;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;


public class ReCAPAbstractUrlBasedTicketValidatorUT extends BaseTestCaseUT {

    @Mock
    ReCAPAbstractUrlBasedTicketValidator reCAPAbstractUrlBasedTicketValidator;

    @Mock
    HttpURLConnectionFactory httpURLConnectionFactory;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void constructValidationUrl(){
        String ticket = "ticket";
        String serviceUrl = "http://localhost:9095/requestItem/";
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("pgtUrl", "test");
        reCAPAbstractUrlBasedTicketValidator = new ReCAPCas20ServiceTicketValidator("http://localhost:9095/requestItem");
        reCAPAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        reCAPAbstractUrlBasedTicketValidator.setRenew(true);
        reCAPAbstractUrlBasedTicketValidator.setEncoding("Test");
        ReflectionTestUtils.setField(reCAPAbstractUrlBasedTicketValidator,"logger",logger);
        String validateUrl = reCAPAbstractUrlBasedTicketValidator.constructValidationUrl(ticket,serviceUrl);
        assertNotNull(validateUrl);
    }

    @Test
    public void validate() throws TicketValidationException {
        String ticket = "ticket";
        String serviceUrl = "http://localhost:9095/requestItem/";
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("pgtUrl", "test");
        reCAPAbstractUrlBasedTicketValidator = new ReCAPCas20ServiceTicketValidator("http://localhost:9095/requestItem");
        reCAPAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        reCAPAbstractUrlBasedTicketValidator.setRenew(true);
        reCAPAbstractUrlBasedTicketValidator.setEncoding("Test");
        reCAPAbstractUrlBasedTicketValidator.setURLConnectionFactory(httpURLConnectionFactory);
        ReflectionTestUtils.setField(reCAPAbstractUrlBasedTicketValidator,"logger",logger);
        try {
            Mockito.when(reCAPAbstractUrlBasedTicketValidator.validate(ticket,serviceUrl)).thenCallRealMethod();
            reCAPAbstractUrlBasedTicketValidator.validate(ticket, serviceUrl);
        }catch (Exception e){}
    }

    @Test
    public void encodeUrl(){
        String serviceUrl = "http://localhost:9095/requestItem/";
        String result = reCAPAbstractUrlBasedTicketValidator.encodeUrl(serviceUrl);
        assertNotNull(result);
    }

    @Test
    public void encodeUrlNull(){
        String serviceUrl = null;
        String result = reCAPAbstractUrlBasedTicketValidator.encodeUrl(serviceUrl);
        assertNull(result);
    }

    @Test
    public void getReCAPAbstractUrlBasedTicketValidator(){
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("b", "b");
        Mockito.doCallRealMethod().when(reCAPAbstractUrlBasedTicketValidator).setCasServerUrlPrefix("http");
        Mockito.doCallRealMethod().when(reCAPAbstractUrlBasedTicketValidator).setURLConnectionFactory(httpURLConnectionFactory);
        reCAPAbstractUrlBasedTicketValidator.setURLConnectionFactory(httpURLConnectionFactory);
        reCAPAbstractUrlBasedTicketValidator.setCasServerUrlPrefix("http");
        reCAPAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        reCAPAbstractUrlBasedTicketValidator.setRenew(true);
        reCAPAbstractUrlBasedTicketValidator.setEncoding("Test");
        assertNotNull(reCAPAbstractUrlBasedTicketValidator.getCasServerUrlPrefix());
        assertNotNull(reCAPAbstractUrlBasedTicketValidator.getCustomParameters());
        assertNotNull(reCAPAbstractUrlBasedTicketValidator.getEncoding());
        assertNull(reCAPAbstractUrlBasedTicketValidator.getUrlSuffix());
        assertTrue(reCAPAbstractUrlBasedTicketValidator.isRenew());
    }
}
