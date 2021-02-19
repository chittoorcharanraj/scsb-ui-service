package org.recap.model.search;


import org.junit.Test;
import org.recap.BaseTestCaseUT;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class BulkRequestFormUT extends BaseTestCaseUT {

    @Test
    public void testBulkRequestForm(){

        BulkRequestForm bulkRequestForm = new BulkRequestForm();

        bulkRequestForm.setRequestId(1);
        bulkRequestForm.setPatronBarcode("354676");
        bulkRequestForm.setPatronBarcodeInRequest("32452");
        bulkRequestForm.setPatronEmailAddress("test@gmail.com");
        bulkRequestForm.setRequestingInstitution("PUL");
        bulkRequestForm.setRequestType("RECALL");
        bulkRequestForm.setRequestNotes("testNotes");
        bulkRequestForm.setTotalRecordsCount("10");
        bulkRequestForm.setTotalPageCount(456);
        bulkRequestForm.setShowResults(Boolean.TRUE);
        bulkRequestForm.setRequestingInstitutions(Arrays.asList("PUL"));
        bulkRequestForm.setRequestTypes(Arrays.asList("RECALL"));
        bulkRequestForm.setDeliveryLocations(new ArrayList<>());
        bulkRequestForm.setRequestStatuses(Arrays.asList("Complete"));
        bulkRequestForm.resetPageNumber();
        bulkRequestForm.setOnChange("change");
        bulkRequestForm.setInstitutionList(Arrays.asList("PUL"));
        bulkRequestForm.setShowRequestErrorMsg(Boolean.TRUE);
        bulkRequestForm.setRequestingInstituionHidden("HD");
        bulkRequestForm.setSearchInstitutionHdn("HD");
        bulkRequestForm.setFile(new MockMultipartFile("test",new byte[1]));
        bulkRequestForm.setBulkSearchResultRows(Arrays.asList(new BulkSearchResultRow()));
        bulkRequestForm.setRequestIdSearch("1");
        bulkRequestForm.setRequestNameSearch("test");
        bulkRequestForm.setPatronBarcodeSearch("2521234");
        bulkRequestForm.setBulkRequestName("Test");
        bulkRequestForm.setDeliveryLocation("test");
        bulkRequestForm.setDeliveryLocationInRequest("test");
        bulkRequestForm.setDisableRequestingInstitution(false);
        bulkRequestForm.setDisableSearchInstitution(true);
        bulkRequestForm.setErrorMessage("ERROR");
        bulkRequestForm.setFileName("test");
        bulkRequestForm.setInstitution("NYPL");
        bulkRequestForm.setItemBarcode("123");
        bulkRequestForm.setItemOwningInstitution("NYPL");
        bulkRequestForm.setItemTitle("test");
        bulkRequestForm.setMessage("test");
        bulkRequestForm.setPageNumber(200);
        bulkRequestForm.setPageSize(20);
        bulkRequestForm.setStatus("SUCCESS");
        bulkRequestForm.setSubmitted(true);

        assertNotNull(bulkRequestForm.getMessage());
        assertNotNull(bulkRequestForm.getErrorMessage());
        assertNotNull(bulkRequestForm.getBulkRequestName());
        assertNotNull(bulkRequestForm.getDeliveryLocation());
        assertNotNull(bulkRequestForm.getDeliveryLocationInRequest());
        assertNotNull(bulkRequestForm.getDisableSearchInstitution());
        assertNotNull(bulkRequestForm.getFileName());
        assertNotNull(bulkRequestForm.getInstitution());
        assertNotNull(bulkRequestForm.getItemBarcode());
        assertNotNull(bulkRequestForm.getItemOwningInstitution());
        assertNotNull(bulkRequestForm.getItemTitle());
        assertNotNull(bulkRequestForm.getPageNumber());
        assertNotNull(bulkRequestForm.getPageSize());
        assertNotNull(bulkRequestForm.getStatus());
        assertNotNull(bulkRequestForm.getPatronBarcodeSearch());
        assertNotNull(bulkRequestForm.getRequestNameSearch());
        assertNotNull(bulkRequestForm.getRequestIdSearch());
        assertNotNull(bulkRequestForm.getBulkSearchResultRows());
        assertNotNull(bulkRequestForm.getFile());
        assertNotNull(bulkRequestForm.getSearchInstitutionHdn());
        assertNotNull(bulkRequestForm.getRequestingInstituionHidden());
        assertTrue(bulkRequestForm.isShowRequestErrorMsg());
        assertNotNull(bulkRequestForm.getInstitutionList());
        assertTrue(bulkRequestForm.isDisableRequestingInstitution());
        assertNotNull(bulkRequestForm.getOnChange());
        assertNotNull(bulkRequestForm.getRequestStatuses());
        assertNotNull(bulkRequestForm.getDeliveryLocations());
        assertNotNull(bulkRequestForm.getRequestTypes());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertTrue(bulkRequestForm.isShowResults());
        assertTrue(bulkRequestForm.isSubmitted());
        assertNotNull(bulkRequestForm.getTotalPageCount());
        assertNotNull(bulkRequestForm.getTotalRecordsCount());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());
        assertNotNull(bulkRequestForm.getRequestingInstitutions());

    }
}
