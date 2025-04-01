package org.recap.model.usermanagement;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

/**
 * Created by hemalathas on 21/2/17.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
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