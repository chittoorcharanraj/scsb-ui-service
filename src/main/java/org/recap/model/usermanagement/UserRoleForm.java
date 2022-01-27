package org.recap.model.usermanagement;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmendrag on 23/12/16.
 */


@Data
@EqualsAndHashCode(callSuper = false)
public class UserRoleForm {

    private int userId;
    private int institutionId;
    private int editUserId;
    private int[] roleId;
    private int[] editRoleId;

    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private Integer editInstitutionId;
    private Integer afterDelPageNumber=0;
    private Integer afterDelPageSize =10;
    private Integer afterDelTotalPageCount=0;

    private String searchNetworkId;
    private String networkLoginId;
    private String roleName;
    private String totalRecordsCount = "0";
    private String institutionName;
    private String message;
    private String errorMessage;
    private String errorMessageForEmail;
    private String editErromessage;
    private String sectionName;
    private String buttonName;
    private String editNetworkId;
    private String userDescriptionErrMsg;
    private String userDescription;
    private String editNetworkLoginId;
    private String editUserDescription;
    private String userEmailId;
    private String emailId;
    private String editEmailId;

    private boolean allowCreateEdit;
    private boolean isCreatedRequest;
    private boolean showPagination = false;
    private boolean showSearch = false;
    private boolean showErrorMessage = false;
    private boolean showCreateSuccess = false;
    private boolean showCreateError = false;
    private boolean showEditSuccess = false;
    private boolean showEditError = false;
    private boolean showCreateEmailError = false;
    private boolean deleteSuccessMsg = false;
    private boolean selected;
    private boolean submitted;
    private boolean showResults = false;
    private boolean deletedSuccessMsg = false;
    private boolean deleteErrorMsg= false;
    private boolean showUserSearchView = true;

    private List<Object> roles = new ArrayList<>();
    private List<Object> institutions = new ArrayList<>();
    private List<Integer> showSelectedForCreate = new ArrayList<>();
    private List<Integer> selectedForCreate = new ArrayList<>();
    private List<Integer> editSelectedForCreate = new ArrayList<>();
    private List<UserRoleForm> userRoleFormList = new ArrayList<>();
    private boolean showEditDeleteIcon=true;
    private String createdBy;
    private String lastUpdatedBy;
    private Boolean isExport = false;

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }
}