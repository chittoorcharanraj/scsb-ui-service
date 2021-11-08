package org.recap.model.search;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hemalathas on 22/12/16.
 */

@Setter
@Getter
public class RolesForm {
    private String roleName;
    private String roleDescription;
    private String permissionNames;
    private boolean showResults = false;
    private boolean newRole = false;
    private String totalRecordCount="0";
    private String errorMessage;
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private Integer afterDelPageNumber=0;
    private Integer afterDelPageSize =10;
    private Integer afterDelTotalPageCount=0;
    private String message;
    private String newRoleName;
    private String newRoleDescription;
    private String newPermissionNames;
    private String editRoleName;
    private String editRoleDescription;
    private String editPermissionNames;
    private List<String> editPermissionName = new ArrayList<>();
    private String roleNameForDelete;
    private String roleDescriptionForDelete;
    private String permissionNamesForDelete;
    private List<String> permissionNameList = new ArrayList<>();
    private List<String> selectedPermissionNames = new ArrayList<>();
    private Integer roleId;
    private List<RolesSearchResult> rolesSearchResults = new ArrayList<>();
    private boolean showIntial = true;

    /**
     * Reset.
     */
    public void reset(){
        this.errorMessage=null;
        this.message=null;
        this.totalRecordCount=String.valueOf(0);
        this.rolesSearchResults=new ArrayList<>();
        this.totalPageCount=0;

    }

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }

}
