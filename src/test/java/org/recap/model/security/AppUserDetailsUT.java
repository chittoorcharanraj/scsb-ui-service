package org.recap.model.security;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class AppUserDetailsUT extends BaseTestCaseUT {

    AppUserDetails appUserDetails = new AppUserDetails();
    Collection<? extends GrantedAuthority> authorities = null;
    String userId = "1";
    //AppUserDetails appUserDetails1 = new AppUserDetails(userId,authorities);
    @Test
    public void testAppUser(){
        appUserDetails.setUserId("1");
        appUserDetails.setRoles(new ArrayList<>());
        assertNotNull(appUserDetails.getUsername());
        assertNull(appUserDetails.getPassword());
        assertNotNull(appUserDetails.toString());
        assertNull(appUserDetails.getAuthorities());
        assertTrue(appUserDetails.isAccountNonExpired());
        assertTrue(appUserDetails.isCredentialsNonExpired());
        assertTrue(appUserDetails.isAccountNonLocked());
        assertTrue(appUserDetails.isEnabled());
    }

}
