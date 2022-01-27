package org.recap.model.dataexportinfo;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author dinakar on 20/01/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DataExportResponse {
    private String message;
    private String errorMessage;
}
