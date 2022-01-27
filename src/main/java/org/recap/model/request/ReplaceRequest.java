package org.recap.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * Created by rajeshbabuk on 15/11/17.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ReplaceRequest {

    private String replaceRequestByType;
    private String requestStatus;
    private String requestIds;
    private String startRequestId;
    private String endRequestId;
    private String fromDate;
    private String toDate;

}
