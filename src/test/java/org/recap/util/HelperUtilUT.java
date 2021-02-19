package org.recap.util;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCase;

import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by hemalathas on 31/3/17.
 */
public class HelperUtilUT extends BaseTestCaseUT {

    @InjectMocks
    HelperUtil mockedHelperUtil;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletRequest request;

    @Mock
    Authentication auth;

    @Mock
    Collection<GrantedAuthority> authorities;

    @Test
    public void testGetAttributeValueFromRequest(){
        HelperUtil helperUtil = new HelperUtil();
        String response = helperUtil.getAttributeValueFromRequest(httpServletRequest,"test");
        assertNull(response);
    }

    @Ignore
    public void testGetLogoutUrl() throws Exception{
        String  institutionCode = "NYPL";
        HelperUtil helperUtil = new HelperUtil();
        String casLogoutUrl = helperUtil.getLogoutUrl(institutionCode);
        assertNotNull(casLogoutUrl);
    }
    @Ignore
    public void testGetLogoutUrl2() throws Exception{
        String  institutionCode = "PUL";
        HelperUtil helperUtil = new HelperUtil();
        String casLogoutUrl = helperUtil.getLogoutUrl(institutionCode);
        assertNotNull(casLogoutUrl);
    }

    @Test
    public void testLogoutFromShiro() throws Exception{
        Object attribute = "recap" ;
        HelperUtil helperUtil = new HelperUtil();
        helperUtil.logoutFromShiro(attribute);

    }
    @Test
    public void testIsAnonymousUser() throws Exception{
        Mockito.when(auth.getName()).thenReturn(RecapConstants.ANONYMOUS_USER);
        boolean result=mockedHelperUtil.isAnonymousUser(auth);
        assertNotNull(result);
    }

}