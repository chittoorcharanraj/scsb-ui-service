package org.recap.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Charan Raj C created on 16/09/24
 */
@RunWith(MockitoJUnitRunner.Silent.class)
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
