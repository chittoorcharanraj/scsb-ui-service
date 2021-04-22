package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

public class CustomerCodeEntityUT extends BaseTestCaseUT {

    @Test
    public void getCustomerCodeEntity(){
        OwnerCodeEntity customerCodeEntity = new OwnerCodeEntity();
        customerCodeEntity.setInstitutionId(1);
        customerCodeEntity.setId(1);
        customerCodeEntity.setOwnerCode("PA");
        customerCodeEntity.setDescription("test");
        customerCodeEntity.compareTo(null);
        OwnerCodeEntity customerCodeEntity1 = new OwnerCodeEntity();
        OwnerCodeEntity customerCodeEntity2 = new OwnerCodeEntity();
        OwnerCodeEntity customerCodeEntity3 = new OwnerCodeEntity();
        customerCodeEntity1.setOwnerCode("PA");
        customerCodeEntity1.setDescription("test");
        customerCodeEntity1.setId(2);
        customerCodeEntity2.setId(2);
        customerCodeEntity2.setOwnerCode("HA");
        customerCodeEntity2.setDescription("test");
        customerCodeEntity3.setId(3);
        customerCodeEntity1.setOwnerCode("HA");
        customerCodeEntity.compareTo(customerCodeEntity1);
        customerCodeEntity.equals(customerCodeEntity);
        customerCodeEntity.equals(customerCodeEntity1);
        customerCodeEntity2.equals(customerCodeEntity1);
        customerCodeEntity2.equals(customerCodeEntity3);
        customerCodeEntity.equals(null);
        customerCodeEntity.hashCode();
    }
}
