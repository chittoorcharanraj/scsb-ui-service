package org.recap.model.usermanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Errors;

/**
 * Created by hemalathas on 21/2/17.
 */
@ExtendWith({SpringExtension.class})
public class LoginValidatorUT {

    @Mock
    Errors errors;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginValidator() {
        LoginValidator loginValidator = new LoginValidator();
        Object object = new UserForm();
        loginValidator.validate(object, errors);
    }

}