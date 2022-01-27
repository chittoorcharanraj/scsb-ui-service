package org.recap.model.dataexportinfo;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class S3RecentDataExportInfo {

    private String institution;
    private String bibDataFormat;
    private String keyName;
    private String gcd;
    private String bibCount;
    private String itemCount;
    private long keySize;
    private Date keyLastModified;
}
