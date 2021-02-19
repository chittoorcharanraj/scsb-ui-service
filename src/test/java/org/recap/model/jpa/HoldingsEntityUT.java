package org.recap.model.jpa;

import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.BaseTestCaseUT;

import java.util.Date;

public class HoldingsEntityUT extends BaseTestCaseUT {

    @Test
    public void testHoldingsEntity(){
        HoldingsEntity holdingsEntity = new HoldingsEntity();
        HoldingsEntity holdingsEntity1 = new HoldingsEntity();
        holdingsEntity.setCreatedBy("test");
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setDeleted(false);
        holdingsEntity.setId(1);
        holdingsEntity.setLastUpdatedBy("test");
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setOwningInstitutionHoldingsId("1");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.equals(null);
        holdingsEntity.equals(holdingsEntity);
        holdingsEntity.equals(holdingsEntity1);
        holdingsEntity.hashCode();
    }
}
