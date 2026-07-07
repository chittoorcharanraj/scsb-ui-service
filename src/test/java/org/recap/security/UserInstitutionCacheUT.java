package org.recap.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charan Raj C created on 16/09/24
 */
@ExtendWith({SpringExtension.class})
public class UserInstitutionCacheUT {

    @InjectMocks
    UserInstitutionCache userInstitutionCache;

    private Map<String, String> sessionIdAndInstitutionType = new HashMap<>();

    @Test
    public void addRequestSessionIdTest(){
        userInstitutionCache.addRequestSessionId("key", "test");
    }

    @Test
    public void getInstitutionForRequestSessionIdTest(){
        userInstitutionCache.getInstitutionForRequestSessionId("test");
    }

    @Test
    public void removeSessionIdTest(){
        userInstitutionCache.removeSessionId("test");
    }
}
