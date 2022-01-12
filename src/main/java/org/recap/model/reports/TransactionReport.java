package org.recap.model.reports;

import lombok.Data;

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
    private String imsLocation;
    private String callNumber;
    private String customerCode;
    private String stopCode;

    public TransactionReport(String requestType, String requestingInst, String owningInst,  String cgd, Long count) {
        this.requestType = requestType;
        this.owningInst = owningInst;
        this.requestingInst = requestingInst;
        this.cgd = cgd;
        this.count = count;
    }

    public TransactionReport(String requestType, String requestingInst, String owningInst,  String cgd, String itemBarcode, String createdDate, String requestStatus,String imsLocation,String callNumber,String customerCode,String stopCode) {
        this.requestType = requestType;
        this.requestingInst = requestingInst;
        this.owningInst = owningInst;
        this.cgd = cgd;
        this.itemBarcode = itemBarcode;
        this.createdDate = createdDate;
        this.requestStatus = requestStatus;
        this.imsLocation = imsLocation;
        this.callNumber = callNumber;
        this.customerCode = customerCode;
        this.stopCode = stopCode;
    }
}
