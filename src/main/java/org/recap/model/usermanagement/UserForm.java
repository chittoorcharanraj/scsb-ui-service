package org.recap.model.usermanagement;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dharmendrag on 29/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserForm implements Serializable {

    private Integer userId;

    private String username;

    private String password;

    private boolean rememberMe;

    private String wrongCredentials;

    private boolean passwordMatcher;

    private String institution;

    private String errorMessage;

    private Set<String> permissions=new HashSet<>();


}
