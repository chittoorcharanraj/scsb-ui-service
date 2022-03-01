package org.recap.security;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.ssl.HttpURLConnectionFactory;
import org.jasig.cas.client.validation.TicketValidationException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


@Slf4j
public class SCSBAbstractUrlBasedTicketValidatorUT extends BaseTestCaseUT {

    @Mock
    SCSBAbstractUrlBasedTicketValidator SCSBAbstractUrlBasedTicketValidator;

    @Mock
    HttpURLConnectionFactory httpURLConnectionFactory;



    @Test
    public void constructValidationUrl(){
        String ticket = "ticket";
        String serviceUrl = "http://localhost:9095/requestItem/";
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("pgtUrl", "test");
        SCSBAbstractUrlBasedTicketValidator = new SCSBCas20ServiceTicketValidator("http://localhost:9095/requestItem");
        SCSBAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        SCSBAbstractUrlBasedTicketValidator.setRenew(true);
        SCSBAbstractUrlBasedTicketValidator.setEncoding("Test");
        //ReflectionTestUtils.setField(SCSBAbstractUrlBasedTicketValidator,"logger",log);
        String validateUrl = SCSBAbstractUrlBasedTicketValidator.constructValidationUrl(ticket,serviceUrl);
        assertNotNull(validateUrl);
    }

    @Test
    public void validate() throws TicketValidationException {
        String ticket = "ticket";
        String serviceUrl = "http://localhost:9095/requestItem/";
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("pgtUrl", "test");
        SCSBAbstractUrlBasedTicketValidator = new SCSBCas20ServiceTicketValidator("http://localhost:9095/requestItem");
        SCSBAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        SCSBAbstractUrlBasedTicketValidator.setRenew(true);
        SCSBAbstractUrlBasedTicketValidator.setEncoding("Test");
        SCSBAbstractUrlBasedTicketValidator.setURLConnectionFactory(httpURLConnectionFactory);
        //ReflectionTestUtils.setField(SCSBAbstractUrlBasedTicketValidator,"logger",log);
        try {
            Mockito.when(SCSBAbstractUrlBasedTicketValidator.validate(ticket,serviceUrl)).thenCallRealMethod();
            SCSBAbstractUrlBasedTicketValidator.validate(ticket, serviceUrl);
        }catch (Exception e){}
    }

    @Test
    public void encodeUrl(){
        String serviceUrl = "http://localhost:9095/requestItem/";
        String result = SCSBAbstractUrlBasedTicketValidator.encodeUrl(serviceUrl);
        assertNotNull(result);
    }

    @Test
    public void encodeUrlNull(){
        String serviceUrl = null;
        String result = SCSBAbstractUrlBasedTicketValidator.encodeUrl(serviceUrl);
        assertNull(result);
    }

    @Test
    public void getReCAPAbstractUrlBasedTicketValidator(){
        Map<String,String> customParameters = new HashMap<>();
        customParameters.put("b", "b");
        Mockito.doCallRealMethod().when(SCSBAbstractUrlBasedTicketValidator).setCasServerUrlPrefix("http");
        Mockito.doCallRealMethod().when(SCSBAbstractUrlBasedTicketValidator).setURLConnectionFactory(httpURLConnectionFactory);
        SCSBAbstractUrlBasedTicketValidator.setURLConnectionFactory(httpURLConnectionFactory);
        SCSBAbstractUrlBasedTicketValidator.setCasServerUrlPrefix("http");
        SCSBAbstractUrlBasedTicketValidator.setCustomParameters(customParameters);
        SCSBAbstractUrlBasedTicketValidator.setRenew(true);
        SCSBAbstractUrlBasedTicketValidator.setEncoding("Test");
        assertNotNull(SCSBAbstractUrlBasedTicketValidator.getCasServerUrlPrefix());
        assertNotNull(SCSBAbstractUrlBasedTicketValidator.getCustomParameters());
        assertNotNull(SCSBAbstractUrlBasedTicketValidator.getEncoding());
        assertNull(SCSBAbstractUrlBasedTicketValidator.getUrlSuffix());
        assertTrue(SCSBAbstractUrlBasedTicketValidator.isRenew());
    }
}
