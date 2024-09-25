package org.recap.security;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RunWith(MockitoJUnitRunner.Silent.class)
public class SCSBSimpleUrlLogoutSuccessHandlerUT {


    @Autowired
    UserAuthUtil userAuthUtil;

    @Mock
    HttpServletResponse httpServletResponse;

    @Mock
    HttpServletRequest httpServletRequest;


    @Mock
    Authentication authentication;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

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
