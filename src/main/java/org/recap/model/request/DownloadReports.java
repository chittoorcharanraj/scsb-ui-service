package org.recap.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class DownloadReports {
    private byte [] content;
    private  String fileName;
}
