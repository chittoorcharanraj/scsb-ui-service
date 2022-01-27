package org.recap.model.search;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.Date;

/**
 * Created by akulak on 25/9/17.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class BulkSearchResultRow {

    private Integer bulkRequestId;
    private String bulkRequestName;
    private String fileName;
    private String patronBarcode;
    private String requestingInstitution;
    private String deliveryLocation;
    private String createdBy;
    private String emailAddress;
    private Date createdDate;
    private String status;
    private String bulkRequestNotes;
    private String imsLocation;
}
