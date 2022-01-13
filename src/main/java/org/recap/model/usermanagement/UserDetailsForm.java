package org.recap.model.usermanagement;

import lombok.*;

/**
 * Created by dharmendrag on 28/12/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsForm {
    private Integer loginInstitutionId;
    private boolean superAdmin;
    private boolean repositoryUser;
    private boolean recapPermissionAllowed;
    private boolean userAdministrator;
}
