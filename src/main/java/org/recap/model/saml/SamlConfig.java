package org.recap.model.saml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SamlConfig {

    private String institutionCode;
    private String idpSsoUrl;
    private String spEntityId;
    private String acsUrl;
    private String idpCertificate;
    private String spCertificate;
    private String spPrivateKey;
    private boolean active = true;
}