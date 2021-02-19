package org.recap.controller;

import org.junit.Test;
import org.mockito.Mock;
import org.recap.model.search.SearchItemResultRow;
import org.recap.model.search.SearchRecordsRequest;
import org.recap.model.search.SearchResultRow;
import org.recap.util.CsvUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by premkb on 2/8/16.
 */
public class SearchRecordsControllerIT extends BaseControllerUT {

    @Autowired
    SearchRecordsController searchRecordsControllerWired;

    @Mock
    private CsvUtil csvUtil;

    @Mock
    public UserAuthUtil userAuthUtil;


    SearchRecordsRequest searchRecordsRequest;

    @Test
    public void exportRecords() throws Exception {
        try {
            byte[] fileContent = searchRecordsControllerWired.exportRecords(buildRequestWithResultRows());
            assertNotNull(fileContent);
        } catch (Exception e) {
        }

    }

    private SearchRecordsRequest buildRequestWithResultRows() {
        searchRecordsRequest = new SearchRecordsRequest();
        List<SearchResultRow> searchResultRows = new ArrayList<>();
        SearchResultRow searchResultRow1 = new SearchResultRow();
        searchResultRow1.setTitle("Title1");
        searchResultRow1.setAuthor("Author1");
        searchResultRow1.setPublisher("publisher1");
        searchResultRow1.setOwningInstitution("NYPL");
        searchResultRow1.setCollectionGroupDesignation("Shared");
        searchResultRow1.setSelected(true);

        SearchResultRow searchResultRow2 = new SearchResultRow();
        searchResultRow2.setTitle("Title2");
        searchResultRow2.setAuthor("Author2");
        searchResultRow2.setPublisher("publisher2");
        searchResultRow2.setOwningInstitution("NYPL");
        searchResultRow2.setCollectionGroupDesignation("Shared");
        searchResultRow2.setSelectAllItems(true);

        List<SearchItemResultRow> searchItemResultRows = new ArrayList<>();
        SearchItemResultRow searchItemResultRow1 = new SearchItemResultRow();
        searchItemResultRow1.setCallNumber("call number1");
        searchItemResultRow1.setChronologyAndEnum("Chn Enum1");
        searchItemResultRow1.setCustomerCode("Code 1");
        searchItemResultRow1.setBarcode("1");
        searchItemResultRow1.setUseRestriction("In Library use");
        searchItemResultRow1.setCollectionGroupDesignation("Shared");
        searchItemResultRow1.setAvailability("Available");
        searchItemResultRows.add(searchItemResultRow1);
        searchResultRow2.setSearchItemResultRows(searchItemResultRows);

        searchResultRows.add(searchResultRow1);
        searchResultRows.add(searchResultRow2);
        searchRecordsRequest.setSearchResultRows(searchResultRows);
        return searchRecordsRequest;
    }

}
