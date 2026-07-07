package org.recap.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.service.RestHeaderService;
import org.recap.util.UserAuthUtil;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

@ExtendWith({SpringExtension.class})
public class AbstractControllerUT {

    @Mock
    UserAuthUtil userAuthUtil;
    @Mock
    HttpSession session;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getRestHeaderService(){
        AbstractController abstractController = new AbstractController();
        RestHeaderService restHeaderService = abstractController.getRestHeaderService();
    }
    @Test
    public void getRestTemplate(){
        AbstractController abstractController = new AbstractController();
        RestTemplate restTemplate = abstractController.getRestTemplate();
    }

          /*  @Test
    public void isAuthenticated(){
        String roleUrl = "http://localhost:9093/search/roles";
        Mockito.doCallRealMethod().when(authenticationController).isAuthenticated(session,roleUrl);
        authenticationController.isAuthenticated(session,roleUrl);
    }
    @Test
    public void getUserDetails(){
        String privilege = RecapConstants.REQUEST_PRIVILEGE;
        Mockito.doCallRealMethod().when(authenticationController).getUserDetails(session,privilege);
        authenticationController.getUserDetails(session,privilege);
    }
    @Test
    public void getRoles(){
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setLoginInstitutionId(1);
        userDetailsForm.setSuperAdmin(true);
        Mockito.doCallRealMethod().when(authenticationController).getRoles(userDetailsForm);
        List<Object> result = authenticationController.getRoles(userDetailsForm);
    }
    @Test
    public void getInstitutions(){
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setRecapPermissionAllowed(true);
        userDetailsForm.setLoginInstitutionId(1);
        userDetailsForm.setSuperAdmin(true);
        Mockito.doCallRealMethod().when(authenticationController).getInstitutions(userDetailsForm);
        List<Object> result = authenticationController.getInstitutions(userDetailsForm);
    }*/
}
