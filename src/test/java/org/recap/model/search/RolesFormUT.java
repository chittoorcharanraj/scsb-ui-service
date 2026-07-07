package org.recap.model.search;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.*;


public class RolesFormUT extends BaseTestCaseUT {

    @Test
    public void getRolesForm(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleDescription("User Role");
        rolesForm.setNewRole(Boolean.TRUE);

        assertFalse(rolesForm.isShowResults());
        assertTrue(rolesForm.isShowIntial());
        assertNotNull(rolesForm.getTotalRecordCount());
        assertNull(rolesForm.getErrorMessage());
        assertNotNull(rolesForm.getTotalPageCount());
        assertNull(rolesForm.getMessage());
        assertNotNull(rolesForm.getSelectedPermissionNames());
        assertNull(rolesForm.getEditRoleDescription());
        assertNull(rolesForm.getEditPermissionNames());
        assertNotNull(rolesForm.getEditPermissionName());
        assertNull(rolesForm.getRoleDescriptionForDelete());
        assertTrue(rolesForm.isNewRole());
    }
}
