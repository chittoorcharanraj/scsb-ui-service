package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

public class CustomerCodeEntityUT extends BaseTestCaseUT {

    @Test
    public void getCustomerCodeEntity(){
        CustomerCodeEntity customerCodeEntity = new CustomerCodeEntity();
        customerCodeEntity.setOwningInstitutionId(1);
        customerCodeEntity.setId(1);
        customerCodeEntity.setCustomerCode("PA");
        customerCodeEntity.setDeliveryRestrictions("PS");
        customerCodeEntity.setDescription("test");
        customerCodeEntity.compareTo(null);
        CustomerCodeEntity customerCodeEntity1 = new CustomerCodeEntity();
        CustomerCodeEntity customerCodeEntity2 = new CustomerCodeEntity();
        CustomerCodeEntity customerCodeEntity3 = new CustomerCodeEntity();
        customerCodeEntity1.setCustomerCode("PA");
        customerCodeEntity1.setDescription("test");
        customerCodeEntity1.setId(2);
        customerCodeEntity2.setId(2);
        customerCodeEntity2.setCustomerCode("HA");
        customerCodeEntity2.setDescription("test");
        customerCodeEntity3.setId(3);
        customerCodeEntity1.setCustomerCode("HA");
        customerCodeEntity.compareTo(customerCodeEntity1);
        customerCodeEntity.equals(customerCodeEntity);
        customerCodeEntity.equals(customerCodeEntity1);
        customerCodeEntity2.equals(customerCodeEntity1);
        customerCodeEntity2.equals(customerCodeEntity3);
        customerCodeEntity.equals(null);
        customerCodeEntity.hashCode();
    }
}
