package org.recap.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCaseUT;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 30/3/17.
 */
public class CASPropertyProviderUT extends BaseTestCaseUT {

    @InjectMocks
    CASPropertyProvider casPropertyProvider;


    private String security = "test";

    private String home = "test";

    @Before
    public void setup(){
        ReflectionTestUtils.setField(casPropertyProvider,"security",security);
        ReflectionTestUtils.setField(casPropertyProvider,"home",home);
    }

    @Test
    public void testCASPropertyProvider(){
        casPropertyProvider.setSecurity(security);
        casPropertyProvider.setHome(home);
        ServiceProperties serviceProperties = casPropertyProvider.getServiceProperties();
        assertNotNull(casPropertyProvider.getHome());
        assertNotNull(casPropertyProvider.getSecurity());
    }
}
