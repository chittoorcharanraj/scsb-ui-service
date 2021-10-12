package org.recap.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;
import org.recap.PropertyKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.cas.authentication.StatelessTicketCache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

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
