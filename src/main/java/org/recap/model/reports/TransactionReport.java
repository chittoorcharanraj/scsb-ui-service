package org.recap.model.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author dinakar on 11/04/21
 */
@Data
public class TransactionReport {
    private String requestType;
    private String owningInst;
    private String requestingInst;
    private String cgd;
    private Long count;
    private String itemBarcode;
    private String createdDate;
    private String requestStatus;

    public TransactionReport(String requestType, String owningInst, String requestingInst, String cgd, Long count) {
        this.requestType = requestType;
        this.owningInst = owningInst;
        this.requestingInst = requestingInst;
        this.cgd = cgd;
        this.count = count;
    }

    public TransactionReport(String requestType, String owningInst, String requestingInst, String cgd, String itemBarcode, String createdDate, String requestStatus) {
        this.requestType = requestType;
        this.owningInst = owningInst;
        this.requestingInst = requestingInst;
        this.cgd = cgd;
        this.itemBarcode = itemBarcode;
        this.createdDate = createdDate;
        this.requestStatus = requestStatus;
    }
}
