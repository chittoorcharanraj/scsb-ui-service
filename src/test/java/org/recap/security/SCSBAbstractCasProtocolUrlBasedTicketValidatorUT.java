package org.recap.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.URL;

@Disabled
public class SCSBAbstractCasProtocolUrlBasedTicketValidatorUT {

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
