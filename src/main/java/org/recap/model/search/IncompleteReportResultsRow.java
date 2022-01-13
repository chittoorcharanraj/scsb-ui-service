package org.recap.model.search;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Created by akulak on 8/3/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class IncompleteReportResultsRow {
    private String author;
    private String createdDate;
    private String title;
    private String owningInstitution;
    private String customerCode;
    private String barcode;

}
