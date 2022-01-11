package org.recap.model.reports;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinakar on 11/04/21
 */
@Setter
@Getter
public class TransactionReports {
   private Integer totalRecordsCount = 0;
   private Integer pageNumber = 0;
   private Integer pageSize = 10;
   private Integer totalPageCount = 0;
   private String message;
   private List<TransactionReport> transactionReportList = new ArrayList<>();
   private List<String> owningInsts;
   private List<String> requestingInsts;
   private List<String> typeOfUses;
   private String fromDate;
   private String toDate;
   private String trasactionCallType;
   private String cgdType;
}
