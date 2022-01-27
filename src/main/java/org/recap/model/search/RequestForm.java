package org.recap.model.search;

import lombok.Data;

import lombok.EqualsAndHashCode;
import org.recap.model.jpa.OwnerCodeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 13/10/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RequestForm {

    private Integer requestId;
    private String patronBarcode;
    private String itemBarcode;
    private String status;
    private String deliveryLocation;
    private String patronBarcodeInRequest;
    private String itemBarcodeInRequest;
    private String deliveryLocationInRequest;
    private String itemTitle;
    private String itemOwningInstitution;
    private String storageLocation;
    private String patronEmailAddress;
    private String requestingInstitution;
    private String requestType;
    private String requestNotes;
    private String startPage;
    private String endPage;
    private String volumeNumber;
    private String issue;
    private String articleAuthor;
    private String articleTitle;
    private String message;
    private String errorMessage;
    private String totalRecordsCount = "0";
    private Integer pageNumber = 0;
    private Integer pageSize = 10;
    private Integer totalPageCount = 0;
    private boolean submitted = false;
    private boolean showResults = false;
    private List<String> requestingInstitutions = new ArrayList<>();
    private List<String> requestTypes = new ArrayList<>();
    private List<OwnerCodeEntity> deliveryLocations = new ArrayList<>();
    private List<SearchResultRow> searchResultRows = new ArrayList<>();
    private List<String> requestStatuses = new ArrayList<>();
    private List<String> institutionList = new ArrayList<>();
    private boolean disableRequestingInstitution = false;
    private String onChange;
    private String institution;
    private boolean showRequestErrorMsg;
    private String requestingInstituionHidden;
    private String itemBarcodeHidden;
    private Boolean disableSearchInstitution = false;
    private String searchInstitutionHdn;

    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }
}
