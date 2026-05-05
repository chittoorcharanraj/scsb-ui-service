package org.recap.security.saml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.model.saml.SamlConfig;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SamlAuthHandlerTest {

    @InjectMocks
    private SamlAuthHandler samlAuthHandler;

    private SamlConfig buildConfig() {
        return SamlConfig.builder()
                .institutionCode("TEST_INST")
                .idpSsoUrl("https://idp.example.com/sso")
                .spEntityId("https://sp.example.com/saml/metadata")
                .acsUrl("https://sp.example.com/saml/acs")
                .idpCertificate(SELF_SIGNED_PEM)
                .spCertificate(SELF_SIGNED_PEM)
                .spPrivateKey(null)
                .build();
    }

    private String buildSamlResponseBase64(String statusValue, boolean includeNameId) {
        String nameId = includeNameId
                ? "<saml:NameID xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">testuser@example.com</saml:NameID>"
                : "";
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<samlp:Response"
                + "  xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\""
                + "  xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\""
                + "  ID=\"_resp001\" Version=\"2.0\">"
                + "<samlp:Status>"
                + "<samlp:StatusCode Value=\"" + statusValue + "\"/>"
                + "</samlp:Status>"
                + "<saml:Assertion ID=\"_assert001\">"
                + "<saml:Subject><saml:SubjectConfirmation>"
                + nameId
                + "</saml:SubjectConfirmation></saml:Subject>"
                + "<saml:AttributeStatement>"
                + "<saml:Attribute Name=\"displayName\">"
                + "<saml:AttributeValue>Test User</saml:AttributeValue>"
                + "</saml:Attribute>"
                + "<saml:Attribute Name=\"email\">"
                + "<saml:AttributeValue>testuser@example.com</saml:AttributeValue>"
                + "</saml:Attribute>"
                + "</saml:AttributeStatement>"
                + "</saml:Assertion>"
                + "</samlp:Response>";
        return Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void buildAuthnRequestUrl_shouldReturnUrlStartingWithIdpSsoUrl() {
        SamlConfig config = buildConfig();
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs");
        assertNotNull("URL must not be null", url);
        assertTrue("URL must start with IdP SSO URL", url.startsWith("https://idp.example.com/sso?SAMLRequest="));
    }

    @Test
    public void buildAuthnRequestUrl_shouldContainSamlRequestParam() {
        SamlConfig config = buildConfig();
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs");
        assertTrue("URL must contain SAMLRequest parameter", url.contains("SAMLRequest="));
    }

    @Test
    public void buildAuthnRequestUrl_withRelayState_shouldAppendRelayStateParam() {
        SamlConfig config = buildConfig();
        String url = samlAuthHandler.buildAuthnRequestUrl(
                config, "https://sp.example.com/saml/acs", "inst%3DTEST_INST");
        assertTrue("URL must contain RelayState parameter", url.contains("RelayState="));
    }

    @Test
    public void buildAuthnRequestUrl_withNullRelayState_shouldNotContainRelayStateParam() {
        SamlConfig config = buildConfig();
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs", null);
        assertFalse("URL must NOT contain RelayState when relayState is null", url.contains("RelayState="));
    }

    @Test
    public void buildAuthnRequestUrl_withBlankRelayState_shouldNotContainRelayStateParam() {
        SamlConfig config = buildConfig();
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs", "  ");
        assertFalse("URL must NOT contain RelayState when relayState is blank", url.contains("RelayState="));
    }

    @Test
    public void buildAuthnRequestUrl_withoutSpEntityId_shouldDeriveFallbackEntityId() {
        SamlConfig config = buildConfig();
        config.setSpEntityId(null);
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs");
        assertNotNull(url);
        assertTrue(url.startsWith("https://idp.example.com/sso?SAMLRequest="));
    }

    @Test
    public void buildAuthnRequestUrl_withoutAcsUrl_shouldUseCallbackUrl() {
        SamlConfig config = buildConfig();
        config.setAcsUrl(null);
        String url = samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs");
        assertNotNull(url);
        assertTrue(url.contains("SAMLRequest="));
    }

    @Test(expected = RuntimeException.class)
    public void buildAuthnRequestUrl_withNullIdpSsoUrl_shouldThrowRuntimeException() {
        SamlConfig config = buildConfig();
        config.setIdpSsoUrl(null);
        samlAuthHandler.buildAuthnRequestUrl(config, "https://sp.example.com/saml/acs");
    }

    @Test
    public void processSamlResponse_withSuccessStatusButNoSignature_shouldReturnNull() {
        SamlConfig config = buildConfig();
        String b64 = buildSamlResponseBase64(
                "urn:oasis:names:tc:SAML:2.0:status:Success", true);
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(b64, config);
        assertNull("Expected null because no XML Signature element is present", result);
    }

    @Test
    public void processSamlResponse_withNonSuccessStatus_shouldReturnNull() {
        SamlConfig config = buildConfig();
        String b64 = buildSamlResponseBase64(
                "urn:oasis:names:tc:SAML:2.0:status:AuthnFailed", true);
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(b64, config);
        assertNull("Expected null for non-Success SAML status", result);
    }

    @Test
    public void processSamlResponse_withInvalidBase64_shouldReturnNull() {
        SamlConfig config = buildConfig();
        SamlAuthHandler.SamlUserInfo result =
                samlAuthHandler.processSamlResponse("NOT_VALID_BASE64!!!", config);
        assertNull("Expected null for malformed Base64 input", result);
    }

    @Test
    public void processSamlResponse_withMalformedXml_shouldReturnNull() {
        SamlConfig config = buildConfig();
        String malformed = Base64.getEncoder()
                .encodeToString("<broken xml".getBytes(StandardCharsets.UTF_8));
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(malformed, config);
        assertNull("Expected null for malformed XML in SAML response", result);
    }

    @Test
    public void processSamlResponse_withNullIdpCertificate_shouldReturnNull() {
        SamlConfig config = buildConfig();
        config.setIdpCertificate(null);
        String b64 = buildSamlResponseBase64(
                "urn:oasis:names:tc:SAML:2.0:status:Success", true);
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(b64, config);
        assertNull("Expected null when idpCertificate is null", result);
    }

    @Test
    public void processSamlResponse_withBlankIdpCertificate_shouldReturnNull() {
        SamlConfig config = buildConfig();
        config.setIdpCertificate("  ");
        String b64 = buildSamlResponseBase64(
                "urn:oasis:names:tc:SAML:2.0:status:Success", true);
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(b64, config);
        assertNull("Expected null when idpCertificate is blank", result);
    }

    @Test
    public void processSamlResponse_withXxePayload_shouldReturnNullSafely() {
        SamlConfig config = buildConfig();
        String xxeXml = "<?xml version=\"1.0\"?>"
                + "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>"
                + "<samlp:Response xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\">"
                + "<samlp:Status><samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/></samlp:Status>"
                + "</samlp:Response>";
        String b64 = Base64.getEncoder()
                .encodeToString(xxeXml.getBytes(StandardCharsets.UTF_8));
        SamlAuthHandler.SamlUserInfo result = samlAuthHandler.processSamlResponse(b64, config);
        assertNull("XXE payload must be rejected by the hardened parser", result);
    }

    @Test
    public void generateSpMetadata_shouldContainEntityId() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue("Metadata must contain spEntityId",
                metadata.contains("https://sp.example.com/saml/metadata"));
    }

    @Test
    public void generateSpMetadata_shouldContainAcsLocation() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue("Metadata must contain ACS URL",
                metadata.contains("https://sp.example.com/saml/acs"));
    }

    @Test
    public void generateSpMetadata_withSpCertificate_shouldContainKeyDescriptor() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue("Metadata must contain KeyDescriptor when spCertificate is set",
                metadata.contains("KeyDescriptor"));
        assertTrue("Metadata must embed certificate body", metadata.contains("X509Certificate"));
    }

    @Test
    public void generateSpMetadata_withoutSpCertificate_shouldNotContainKeyDescriptor() {
        SamlConfig config = buildConfig();
        config.setSpCertificate(null);
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertFalse("No KeyDescriptor expected when spCertificate is absent",
                metadata.contains("KeyDescriptor"));
    }

    @Test
    public void generateSpMetadata_shouldBeValidXmlStart() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue("Metadata must start with XML declaration",
                metadata.startsWith("<?xml"));
        assertTrue("Metadata must contain EntityDescriptor",
                metadata.contains("EntityDescriptor"));
        assertTrue("Metadata must contain SPSSODescriptor",
                metadata.contains("SPSSODescriptor"));
    }

    @Test
    public void generateSpMetadata_shouldContainAllSupportedNameIdFormats() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue(metadata.contains("nameid-format:unspecified"));
        assertTrue(metadata.contains("nameid-format:persistent"));
        assertTrue(metadata.contains("nameid-format:emailAddress"));
    }

    @Test
    public void generateSpMetadata_shouldDeclareWantAssertionsSigned() {
        SamlConfig config = buildConfig();
        String metadata = samlAuthHandler.generateSpMetadata(config);
        assertTrue("SP metadata must declare WantAssertionsSigned=\"true\"",
                metadata.contains("WantAssertionsSigned=\"true\""));
    }

    @Test
    public void samlUserInfo_shouldStoreAllFields() {
        SamlAuthHandler.SamlUserInfo info =
                new SamlAuthHandler.SamlUserInfo("uid1", "Display Name", "uid1@example.com", "uid1", "INST1");
        assertEquals("uid1", info.userId);
        assertEquals("Display Name", info.displayName);
        assertEquals("uid1@example.com", info.email);
        assertEquals("uid1", info.uid);
        assertEquals("INST1", info.institutionCode);
    }

    @Test
    public void samlUserInfo_withNullDisplayName_shouldFallBackToUserId() {
        SamlAuthHandler.SamlUserInfo info =
                new SamlAuthHandler.SamlUserInfo("uid2", null, null, null, "INST2");
        assertEquals("displayName must fall back to userId when null",
                "uid2", info.displayName);
    }

    @Test
    public void samlUserInfo_withEmptyDisplayName_shouldFallBackToUserId() {
        SamlAuthHandler.SamlUserInfo info =
                new SamlAuthHandler.SamlUserInfo("uid3", "", null, null, "INST3");
        assertEquals("displayName must fall back to userId when empty",
                "uid3", info.displayName);
    }

    @Test
    public void samlUserInfo_toString_shouldContainUserIdAndInstitutionCode() {
        SamlAuthHandler.SamlUserInfo info =
                new SamlAuthHandler.SamlUserInfo("alice", "Alice", "alice@example.com", "alice", "MIT");
        String str = info.toString();
        assertTrue("toString must contain userId", str.contains("alice"));
        assertTrue("toString must contain institutionCode", str.contains("MIT"));
    }

    private static final String SELF_SIGNED_PEM = "MOCK_CERTIFICATE_PLACEHOLDER";
}