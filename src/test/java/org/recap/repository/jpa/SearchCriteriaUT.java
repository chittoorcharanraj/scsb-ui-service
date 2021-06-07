package org.recap.repository.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.recap.repository.SearchCriteria;
import org.recap.repository.SearchOperation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SearchCriteriaUT extends BaseTestCaseUT {

    @Test
    public void getSearchCriteria(){
        String key = "test";
        Object value ="test";
        SearchOperation operation = SearchOperation.IN;
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setKey(key);
        searchCriteria.setValue(value);
        searchCriteria.setOperation(operation);
        SearchCriteria searchCriteria1 = new SearchCriteria(key,value,operation);
        assertNotNull(searchCriteria);
        assertNotNull(searchCriteria1);
    }
}
