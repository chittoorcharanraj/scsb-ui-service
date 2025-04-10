package org.recap.security;

import org.apereo.cas.client.proxy.ProxyRetriever;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Charan Raj C on 10/4/2025.
 */
public class SCSBCas20ServiceTicketValidatorTest extends BaseTestCaseUT {

    private SCSBCas20ServiceTicketValidator validator;

    @Before
    public void setUp() {
        validator = new SCSBCas20ServiceTicketValidator("https://cas.example.org/cas");
        validator.setProxyRetriever(Mockito.mock(ProxyRetriever.class));
    }

    @Test
    public void testParseResponseFromServer_withValidResponseWithoutPGT() throws Exception {
        String casResponse =
                "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
                        "  <cas:authenticationSuccess>\n" +
                        "    <cas:user>testuser</cas:user>\n" +
                        "    <cas:attributes>\n" +
                        "      <cas:email>testuser@example.com</cas:email>\n" +
                        "      <cas:role>admin</cas:role>\n" +
                        "    </cas:attributes>\n" +
                        "  </cas:authenticationSuccess>\n" +
                        "</cas:serviceResponse>";

        Assertion assertion = validator.parseResponseFromServer(casResponse);

        assertNotNull(assertion);
        assertEquals("testuser", assertion.getPrincipal().getName());

        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        assertEquals("testuser@example.com", attributes.get("email"));
        assertEquals("admin", attributes.get("role"));
    }

    @Test(expected = TicketValidationException.class)
    public void testParseResponseFromServer_withAuthenticationFailure() throws Exception {
        String failureResponse =
                "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
                        "  <cas:authenticationFailure code=\"INVALID_TICKET\">\n" +
                        "    Ticket ST-12345-abcdef not recognized\n" +
                        "  </cas:authenticationFailure>\n" +
                        "</cas:serviceResponse>";

        validator.parseResponseFromServer(failureResponse);
    }

    @Test(expected = TicketValidationException.class)
    public void testParseResponseFromServer_withMissingPrincipal() throws Exception {
        String noUserResponse =
                "<cas:serviceResponse xmlns:cas='http://www.yale.edu/tp/cas'>\n" +
                        "  <cas:authenticationSuccess>\n" +
                        "    <cas:attributes>\n" +
                        "      <cas:email>nouser@example.com</cas:email>\n" +
                        "    </cas:attributes>\n" +
                        "  </cas:authenticationSuccess>\n" +
                        "</cas:serviceResponse>";

        validator.parseResponseFromServer(noUserResponse);
    }
}
