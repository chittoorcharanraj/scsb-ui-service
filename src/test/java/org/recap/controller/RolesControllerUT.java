package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.model.jpa.PermissionEntity;
import org.recap.model.jpa.RoleEntity;
import org.recap.model.search.RolesForm;
import org.recap.repository.jpa.PermissionsDetailsRepository;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.recap.security.UserManagementService;
import org.recap.util.UserAuthUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RolesControllerUT extends BaseTestCaseUT {
    
    @InjectMocks
    RolesController rolesController;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpSession session;

    @Mock
    Model model;

    @Mock
    UserAuthUtil userAuthUtil;

    @Mock
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Mock
    PermissionsDetailsRepository permissionsRepository;

    @Mock
    UserManagementService userManagementService;

    @Test
    public void testRoles() {
        when(request.getSession(false)).thenReturn(session);
        when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_ROLE_URL)).thenReturn(Boolean.TRUE);
        boolean response = rolesController.roles(request);
        assertNotNull(response);
    }
    @Test
    public void testRolesFailure() {
        when(request.getSession(false)).thenReturn(session);
        when(userAuthUtil.isAuthenticated(request, RecapConstants.SCSB_SHIRO_ROLE_URL)).thenReturn(Boolean.FALSE);
        boolean response = rolesController.roles(request);
        assertNotNull(response);
    }
    @Test
    public void testSearchForSuperAdmin(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Super Admin");
        rolesForm.setPermissionNames("Create User");
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchForAdmin(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("Super Admin");
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutPermission(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("");
        RoleEntity roleEntity = getRoleEntity();
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(roleEntity));
        Mockito.when(rolesDetailsRepositorty.findByRoleName(any(), anyString())).thenReturn(roleEntities);
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(1,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutPermissionAndWithEmptyRoleList(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("");
        Mockito.when(rolesDetailsRepositorty.findByRoleName(any(), anyString())).thenReturn(new PageImpl<>(Collections.EMPTY_LIST));
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutPermissionAndWithSpecialCharacter(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin#");
        rolesForm.setPermissionNames("");
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutRoleName(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("");
        rolesForm.setPermissionNames("Admin");
        rolesForm.setPageNumber(10);
        rolesForm.setPageSize(10);
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(getPermissionEntity());
        Mockito.when(rolesDetailsRepositorty.getIdforPermissionName(any())).thenReturn(Arrays.asList(1));
        Mockito.when(rolesDetailsRepositorty.findByRoleIDs(any(), any())).thenReturn(roleEntities);
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(1,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutRoleNameAndWithEmptyPermission(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("");
        rolesForm.setPermissionNames("Admin");
        rolesForm.setPageNumber(10);
        rolesForm.setPageSize(10);
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(null);
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithPermissionAndWithRoleName(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("Admin");
        Mockito.when(rolesDetailsRepositorty.findByRoleName(rolesForm.getRoleName())).thenReturn(getRoleEntity());
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(1,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithDiffPermissionAndWithRoleNameAndEmptyRoleEnity(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("Admin1");
        Mockito.when(rolesDetailsRepositorty.findByRoleName(rolesForm.getRoleName())).thenReturn(getRoleEntity());
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithPermissionAndWithRoleNameAndEmptyRoleEnity(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("Admin");
        Mockito.when(rolesDetailsRepositorty.findByRoleName(rolesForm.getRoleName())).thenReturn(null);
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithPermissionSpecialCharacterAndWithRoleName(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("Admin#");
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(0,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void testSearchWithoutPermissionAndWithoutRoleName(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("");
        rolesForm.setPermissionNames("");
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(rolesDetailsRepositorty.getRolesWithoutSuperAdmin(any())).thenReturn(roleEntities);
        RolesForm rolesForm1 = rolesController.search(rolesForm);
        assertEquals(1,rolesForm1.getRolesSearchResults().size());
    }
    @Test
    public void populatePermissionNames(){
        Mockito.when(permissionsRepository.findAll()).thenReturn(Arrays.asList(getPermissionEntity()));
        RolesForm rolesForm = rolesController.populatePermissionNames();
        assertNotNull(rolesForm);
    }
    @Test
    public void newRole(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setNewRoleName("Admin");
        rolesForm.setNewPermissionNames("Admin,SuperAdmin");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(RecapConstants.USER_NAME)).thenReturn("test");
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(getPermissionEntity());
        Mockito.when(rolesDetailsRepositorty.save(any())).thenReturn(getRoleEntity());
        RolesForm form = rolesController.newRole(rolesForm,request);
        assertNotNull(form);
    }
    @Test
    public void newRoleInnerException(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setNewRoleName("Admin");
        rolesForm.setNewPermissionNames("Admin,SuperAdmin");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute(RecapConstants.USER_NAME)).thenReturn("test");
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(getPermissionEntity());
        Mockito.when(rolesDetailsRepositorty.save(any())).thenThrow(new NullPointerException());
        RolesForm form = rolesController.newRole(rolesForm,request);
        assertNotNull(form);
    }
    @Test
    public void newRoleWithSpecialCharacter(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setNewRoleName("Admin#");
        rolesForm.setNewPermissionNames("Admin,SuperAdmin");
        RolesForm form = rolesController.newRole(rolesForm,request);
        assertNotNull(form);
    }
    @Test
    public void editRole(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleId(1);
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setRoleDescriptionForDelete("test desc");
        RolesForm rolesFormResponse  = rolesController.editRole(rolesForm.getRoleId(),rolesForm.getRoleName(),rolesForm.getRoleDescription(),rolesForm.getPermissionNames());
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void saveEditedRole(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleId(1);
        rolesForm.setEditRoleName("Admin");
        String[] permissionName ={"CreateUser"};
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(RecapConstants.USER_NAME)).thenReturn("SuperAdmin");
        Mockito.when(rolesDetailsRepositorty.findById(any())).thenReturn(Optional.of(getRoleEntity()));
        rolesForm.setEditPermissionName(Arrays.asList(permissionName));
        rolesForm.setEditRoleDescription("test desc");
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(getPermissionEntity());
        Mockito.when(rolesDetailsRepositorty.save(any())).thenReturn(getRoleEntity());
        RolesForm rolesFormResponse= rolesController.saveEditedRole(rolesForm.getRoleId(),rolesForm.getEditRoleName(),rolesForm.getEditRoleName(), permissionName, request);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void saveEditedRoleWithoutRoleEntity(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleId(1);
        rolesForm.setEditRoleName("Admin");
        String[] permissionName ={"CreateUser"};
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(RecapConstants.USER_NAME)).thenReturn("SuperAdmin");
        Mockito.when(rolesDetailsRepositorty.findById(any())).thenReturn(Optional.of(getRoleEntity()));
        rolesForm.setEditPermissionName(Arrays.asList(permissionName));
        rolesForm.setEditRoleDescription("test desc");
        Mockito.when(permissionsRepository.findByPermissionName(any())).thenReturn(getPermissionEntity());
        RolesForm rolesFormResponse= rolesController.saveEditedRole(rolesForm.getRoleId(),rolesForm.getEditRoleName(),rolesForm.getEditRoleName(), permissionName, request);
        assertNotNull(rolesFormResponse);
    }

    @Test
    public void deleteRole() throws Exception{
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleId(1);
        rolesForm.setRoleNameForDelete("Admin");
        rolesForm.setRoleDescriptionForDelete("test desc");
        rolesForm.setPermissionNamesForDelete("CreateUser");
        RolesForm rolesFormResponse = rolesController.deleteRole(rolesForm.getRoleId(),rolesForm.getRoleName(),rolesForm.getRoleDescription(),rolesForm.getPermissionNamesForDelete(),10,1,2);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void delete(){
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleNameForDelete("Delete#001");
        Mockito.when(rolesDetailsRepositorty.findById(rolesForm.getRoleId())).thenReturn(Optional.of(getRoleEntity()));
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(rolesDetailsRepositorty.getRolesWithoutSuperAdmin(any())).thenReturn(roleEntities);
        RolesForm rolesFormResponse = rolesController.delete(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void deleteWithoutRoleEntity(){
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleNameForDelete("Delete#001");
        Mockito.when(rolesDetailsRepositorty.findById(rolesForm.getRoleId())).thenReturn(Optional.empty());
        RolesForm rolesFormResponse = rolesController.delete(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void deleteException(){
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleNameForDelete("Delete#001");
        Mockito.when(rolesDetailsRepositorty.findById(rolesForm.getRoleId())).thenReturn(Optional.of(getRoleEntity()));
        RolesForm rolesFormResponse = rolesController.delete(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void searchPrevious(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(2);
        rolesForm.setPageSize(25);
        RolesForm rolesFormResponse = rolesController.searchPrevious(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void searchNext(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(0);
        rolesForm.setPageSize(10);
        RolesForm rolesFormResponse = rolesController.searchNext(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void searchFirst(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(0);
        rolesForm.setPageSize(25);
        RolesForm rolesFormResponse = rolesController.searchFirst(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void searchLast(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(2);
        rolesForm.setPageSize(10);
        rolesForm.setTotalPageCount(3);
        RolesForm rolesFormResponse = rolesController.searchLast(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void onPageSizeChange() throws Exception{
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(2);
        rolesForm.setPageSize(25);
        RolesForm rolesFormResponse = rolesController.onPageSizeChange(rolesForm);
        assertNotNull(rolesFormResponse);
    }
    @Test
    public void goBack(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleName("Admin");
        rolesForm.setPermissionNames("CreateUser");
        rolesForm.setPageNumber(0);
        rolesForm.setPageSize(25);
        ModelAndView modelAndView = rolesController.goBack(rolesForm,model);
        assertNotNull(modelAndView);
    }
    @Test
    public void findByPaginationWithoutPermissionName() {
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleName("Super Admin");
        rolesForm.setPermissionNames("");
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(rolesDetailsRepositorty.findByRoleName(any(), anyString())).thenReturn(roleEntities);
        rolesController.findByPagination(rolesForm);
    }
    @Test
    public void findByPaginationWithoutAdminName() {
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleName("");
        rolesForm.setPermissionNames("Super Admin");
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(permissionsRepository.findByPermissionName(rolesForm.getPermissionNames())).thenReturn(getPermissionEntity());
        Mockito.when(rolesDetailsRepositorty.getIdforPermissionName(any())).thenReturn(Arrays.asList(1));
        Mockito.when(rolesDetailsRepositorty.findByRoleIDs(any(), any())).thenReturn(roleEntities);
        rolesController.findByPagination(rolesForm);
    }
    @Test
    public void findByPaginationWithoutPermissionNameAndAdminName() {
        RolesForm rolesForm = getRolesForm();
        rolesForm.setRoleName("");
        rolesForm.setPermissionNames("");
        Page<RoleEntity> roleEntities = new PageImpl<>(Arrays.asList(getRoleEntity()));
        Mockito.when(rolesDetailsRepositorty.getRolesWithoutSuperAdmin(any())).thenReturn(roleEntities);
        rolesController.findByPagination(rolesForm);
    }
    private RoleEntity getRoleEntity() {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleName("CUL Admin");
        roleEntity.setRoleDescription("Admin for CUL Institution");
        roleEntity.setCreatedDate(new Date());
        roleEntity.setCreatedBy("superadmin");
        roleEntity.setLastUpdatedDate(new Date());
        roleEntity.setLastUpdatedBy("superadmin");
        Set<PermissionEntity> permissionEntitySet = new HashSet<>();
        PermissionEntity permissionEntity = getPermissionEntity();
        permissionEntitySet.add(permissionEntity);
        roleEntity.setPermissions(permissionEntitySet);
        return roleEntity;
    }

    private PermissionEntity getPermissionEntity() {
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setPermissionDesc("Admin");
        permissionEntity.setPermissionName("Admin");
        return permissionEntity;
    }
    private RolesForm getRolesForm(){
        RolesForm rolesForm = new RolesForm();
        rolesForm.setRoleId(1);
        rolesForm.setRoleNameForDelete("Admin");
        rolesForm.setRoleDescriptionForDelete("test desc");
        rolesForm.setPermissionNamesForDelete("CreateUser");
        rolesForm.setPageSize(5);
        rolesForm.setPageNumber(10);
        return rolesForm;
    }
}
