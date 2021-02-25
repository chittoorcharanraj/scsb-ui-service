package org.recap.controller;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.usermanagement.UserForm;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.UserAuthUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class HomeControllerUT extends BaseTestCaseUT {

    @InjectMocks
    HomeController homeController;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Before
    public void setup(){
        UserForm userForm = new UserForm();
        userForm.setUsername("SuperAdmin");
        userForm.setInstitution("1");
        userForm.setPassword("12345");
        UsernamePasswordToken token = new UsernamePasswordToken(userForm.getUsername()+ RecapConstants.TOKEN_SPLITER +userForm.getInstitution(),userForm.getPassword(),true);
        session.setAttribute(RecapConstants.REQUEST_PRIVILEGE,token);
    }
    @Test
    public void loadInstitutions(){
        InstitutionEntity institutionEntity = getInstitutionEntity();
        Mockito.when(institutionDetailsRepository.getInstitutionCodes()).thenReturn(Arrays.asList(institutionEntity));
        homeController.loadInstitutions();
    }
    @Test
    public void fecthingInstituionsFromDBException(){
        Mockito.when(institutionDetailsRepository.getInstitutionCodes()).thenThrow(new NullPointerException());
        homeController.fecthingInstituionsFromDB();
    }
    @Test
    public void login(){
        Object obj = new Object();
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL)).thenReturn(Boolean.TRUE);
        Mockito.when(session.getAttribute(anyString())).thenReturn(obj);
        homeController.login(request);
    }
    @Test
    public void loginException(){
        Object obj = new Object();
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_SEARCH_URL)).thenReturn(Boolean.TRUE);
        Mockito.when(session.getAttribute(anyString())).thenReturn(obj);
        homeController.login(request);
    }
    private InstitutionEntity getInstitutionEntity() {
        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setInstitutionCode("UC");
        institutionEntity.setInstitutionName("University of Chicago");
        return institutionEntity;
    }
}
