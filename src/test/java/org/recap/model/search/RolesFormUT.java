package org.recap.model.search;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
