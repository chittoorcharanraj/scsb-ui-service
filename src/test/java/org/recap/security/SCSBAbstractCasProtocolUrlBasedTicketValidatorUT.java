package org.recap.security;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;

import java.net.URL;

public class SCSBAbstractCasProtocolUrlBasedTicketValidatorUT extends BaseTestCase {

    @Mock
    SCSBAbstractCasProtocolUrlBasedTicketValidator SCSBAbstractCasProtocolUrlBasedTicketValidator;


    @Test
    public void retrieveResponseFromServer() throws Exception{
        String ticket = "ticket";
        URL validateurl = new URL("http://localhost:9095/requestItem/patronValidationBulkRequest");
        //Mockito.doCallRealMethod().when(reCAPAbstractCasProtocolUrlBasedTicketValidator).retrieveResponseFromServer(validateurl,ticket);
      //  reCAPAbstractCasProtocolUrlBasedTicketValidator.retrieveResponseFromServer(validateurl,ticket);
    }
}
