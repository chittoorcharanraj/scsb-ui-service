package org.recap.model.search;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeshbabuk on 12/10/16.
 */
@Setter
@Getter
public class CollectionForm extends BibliographicMarcForm {

    private String itemBarcodes;
    private boolean showResults = false;
    private boolean selectAll = false;
    private String errorMessage;
    private String barcodesNotFoundErrorMessage;
    private String ignoredBarcodesErrorMessage;
    @Getter(AccessLevel.NONE) private List<SearchResultRow> searchResultRows = new ArrayList<>();
    private boolean showModal = false;

    /**
     * Gets search result rows.
     *
     * @return the search result rows
     */
    public List<SearchResultRow> getSearchResultRows() {
        if (null == searchResultRows) {
            searchResultRows = new ArrayList<>();
        }
        return searchResultRows;
    }
}
