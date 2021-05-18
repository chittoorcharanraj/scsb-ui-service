package org.recap.security;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.PropertyKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.cas.ServiceProperties;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 30/3/17.
 */
public class CASPropertyProviderUT extends BaseTestCase {

    @Autowired
    CASPropertyProvider casPropertyProvider;

    @Value("${" + PropertyKeyConstants.SCSB_APP_SERVICE_SECURITY + "}")
    private String security;

    @Value("${" + PropertyKeyConstants.SCSB_APP_SERVICE_HOME + "}")
    private String home;

    @Test
    public void testCASPropertyProvider(){
        casPropertyProvider.setSecurity(security);
        casPropertyProvider.setHome(home);
        ServiceProperties serviceProperties = casPropertyProvider.getServiceProperties();
        assertNotNull(casPropertyProvider.getHome());
        assertNotNull(casPropertyProvider.getSecurity());
    }

}
