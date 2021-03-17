package org.recap.security;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.RecapConstants;
import org.recap.model.jpa.RoleEntity;
import org.recap.repository.jpa.RolesDetailsRepositorty;
import org.recap.util.UserAuthUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Created by dharmendrag on 15/12/16.
 */
@Service
public class UserManagementService {

    /**
     * The Roles details repositorty.
     */
    @Autowired
    RolesDetailsRepositorty rolesDetailsRepositorty;

    @Autowired
    UserAuthUtil userAuthUtil;
    /**
     * Un authorized logged-in user.
     *
     * @param session    the session
     * @param moduleName the module name
     * @param logger     the logger
     * @return the string
     */
    public boolean unAuthorizedUser(HttpSession session, String moduleName, Logger logger) {
        try {
            logger.debug("{} authorization Rejected for : {}", moduleName, (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN));
          userAuthUtil.authorizedUser(RecapConstants.SCSB_SHIRO_LOGOUT_URL, (UsernamePasswordToken) session.getAttribute(RecapConstants.USER_TOKEN));
        } finally {
            if (session != null) {
                session.invalidate();
            }
        }
        return RecapConstants.FALSE;
    }

    /**
     * Get super admin role id integer.
     *
     * @return the integer
     */
    public Integer getSuperAdminRoleId(){
        RoleEntity roleEntity=rolesDetailsRepositorty.findByRoleName(RecapConstants.ROLES_SUPER_ADMIN);
        return roleEntity.getId();
    }
}
