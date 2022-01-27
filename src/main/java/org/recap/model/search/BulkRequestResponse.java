package org.recap.model.search;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by rajeshbabuk on 11/10/17.
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class BulkRequestResponse {

    private Integer bulkRequestId;
    private String screenMessage;
    private boolean success;

}
