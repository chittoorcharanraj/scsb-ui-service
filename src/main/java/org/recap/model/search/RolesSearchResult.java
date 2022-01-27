package org.recap.model.search;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Created by hemalathas on 22/12/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RolesSearchResult {

    private String rolesName;
    private String rolesDescription;
    private String permissionName;
    private Integer roleId;

}
