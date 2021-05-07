package org.recap.model.search;

import lombok.Getter;
import lombok.Setter;
import org.recap.ScsbCommonConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 6/7/16.
 */
@Setter
@Getter
public class SearchRecordsRequest implements Serializable {

    private String fieldValue = "";

    private String fieldName;

    private List<String> owningInstitutions = new ArrayList<>();

    private List<String> collectionGroupDesignations = new ArrayList<>();

    private List<String> availability = new ArrayList<>();

    private List<String> materialTypes = new ArrayList<>();

    private List<String> useRestrictions = new ArrayList<>();

    private List<SearchResultRow> searchResultRows = new ArrayList<>();

    private Integer totalPageCount = 0;

    private String totalBibRecordsCount = "0";

    private String totalItemRecordsCount = "0";

    private String totalRecordsCount = "0";

    private Integer pageNumber = 0;

    private Integer pageSize = 10;

    private boolean showResults = false;

    private boolean selectAll = false;

    private boolean selectAllFacets = false;

    private boolean showTotalCount = false;

    private Integer index;

    private String errorMessage;

    private boolean isDeleted = false;

    private String catalogingStatus;

    private List<String> imsDepositoryCodes = new ArrayList<>();

    /**
     * Instantiates a new Search records request.
     */
    public SearchRecordsRequest() {
        this.setFieldName("");
        this.setFieldValue("");
        this.setSelectAllFacets(true);
        this.setDeleted(false);
        this.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);

        this.getCollectionGroupDesignations().add("Shared");
        this.getCollectionGroupDesignations().add("Private");
        this.getCollectionGroupDesignations().add("Open");

        this.getAvailability().add("Available");
        this.getAvailability().add("NotAvailable");

        this.getMaterialTypes().add("Monograph");
        this.getMaterialTypes().add("Serial");
        this.getMaterialTypes().add("Other");

        this.getUseRestrictions().add("NoRestrictions");
        this.getUseRestrictions().add("InLibraryUse");
        this.getUseRestrictions().add("SupervisedUse");

        this.setPageNumber(0);
        this.setPageSize(10);
        this.setShowResults(false);
    }
    /**
     * Reset page number.
     */
    public void resetPageNumber() {
        this.pageNumber = 0;
    }
    public void reset() {
        this.totalBibRecordsCount = String.valueOf(0);
        this.totalItemRecordsCount = String.valueOf(0);
        this.totalRecordsCount = String.valueOf(0);
        this.showTotalCount = false;
        this.errorMessage = null;
    }
}
