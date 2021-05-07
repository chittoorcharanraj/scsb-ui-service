package org.recap.security;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.BaseTestCase;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertNotNull;



public class SCSBSimpleUrlLogoutSuccessHandlerUT extends BaseTestCase{


    @Autowired
    UserAuthUtil userAuthUtil;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    HttpServletRequest httpServletRequest;


    @Mock
    Authentication authentication;

    @Test
    public void onLogoutSuccess(){
        SCSBSimpleUrlLogoutSuccessHandler SCSBSimpleUrlLogoutSuccessHandler = new SCSBSimpleUrlLogoutSuccessHandler(userAuthUtil);
        try {
            SCSBSimpleUrlLogoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
