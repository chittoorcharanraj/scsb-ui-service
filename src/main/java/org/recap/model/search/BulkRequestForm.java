package org.recap.model.search;

import lombok.Getter;
import lombok.Setter;
import org.recap.model.jpa.BulkCustomerCodeEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akulak on 19/9/17.
 */
@Setter
@Getter
public class BulkRequestForm {

    private Integer requestId;
    private String patronBarcode;
    private String itemBarcode;
    private String status;
    private String deliveryLocation;
    private String deliveryLocationInRequest;
    private String itemTitle;
    private String itemOwningInstitution;
    private String storageLocation;
    private String patronEmailAddress;
    private String requestingInstitution;
    private String requestType;
    private String requestNotes;
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
    private List<BulkCustomerCodeEntity> deliveryLocations = new ArrayList<>();
    private List<BulkSearchResultRow> bulkSearchResultRows = new ArrayList<>();
    private List<String> requestStatuses = new ArrayList<>();
    private List<String> institutionList = new ArrayList<>();
    private boolean disableRequestingInstitution = false;
    private String onChange;
    private boolean showRequestErrorMsg=false;
    private String requestingInstituionHidden;
    private Boolean disableSearchInstitution = false;
    private String searchInstitutionHdn;
    private MultipartFile file;

    //Search parameters
    private String requestIdSearch;
    private String requestNameSearch;
    private String patronBarcodeSearch;
    private String institution;

    //Request Parameters
    private String bulkRequestName;
    private String patronBarcodeInRequest;
    private String fileName;

}
