package org.recap.security;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by hemalathas on 30/3/17.
 */
@Slf4j
public class UserManagementServiceUT extends BaseTestCase{

    @Autowired
    HttpSession httpSession;



    @Autowired
    UserManagementService userManagementService;

    @Test
    public void testSuperAdminRoleId(){
        Integer response = userManagementService.getSuperAdminRoleId();
        assertNotNull(response);
    }

    @Test
    public void testUnAuthorizedUser(){
        boolean response = userManagementService.unAuthorizedUser(httpSession,"test",log);
        assertNotNull(response);
        assertEquals(response,false);
    }

}