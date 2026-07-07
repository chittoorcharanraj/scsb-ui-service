package org.recap.model.search;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BulkRequestResponseUT extends BaseTestCaseUT {

    @Test
    public void testBulkRequestResponse(){

        BulkRequestResponse bulkRequestResponse = new BulkRequestResponse();
        bulkRequestResponse.setBulkRequestId(1);
        bulkRequestResponse.setScreenMessage("SUCCEESS");
        bulkRequestResponse.setSuccess(true);

        assertNotNull(bulkRequestResponse.getBulkRequestId());
        assertNotNull(bulkRequestResponse.getScreenMessage());
    }
}
