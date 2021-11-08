package org.recap.model.dataexportinfo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
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
