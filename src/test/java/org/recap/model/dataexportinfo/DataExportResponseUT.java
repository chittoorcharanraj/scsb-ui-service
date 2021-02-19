package org.recap.model.dataexportinfo;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import static org.junit.Assert.assertNotNull;

public class DataExportResponseUT extends BaseTestCaseUT {

    @Test
    public void getDataExportResponse(){
        DataExportResponse dataExportResponse = new DataExportResponse();
        dataExportResponse.setErrorMessage("Error");
        dataExportResponse.setMessage("testMessage");

        assertNotNull(dataExportResponse.getMessage());
        assertNotNull(dataExportResponse.getErrorMessage());
    }
}
