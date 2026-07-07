package org.recap.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.Main;
import org.recap.util.UserAuthUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ExtendWith({SpringExtension.class})
@SpringBootTest(classes = Main.class)
public class SCSBSimpleUrlLogoutSuccessHandlerUT {


    @Mock
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
