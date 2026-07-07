package org.recap.model.dataexportinfo;

import org.junit.jupiter.api.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class S3RecentDataExportInfoListUT extends BaseTestCaseUT {

    @Test
    public void getS3RecentDataExportInfoList(){

        S3RecentDataExportInfoList s3RecentDataExportInfoList = new S3RecentDataExportInfoList();

        s3RecentDataExportInfoList.setRecentDataExportInfoList(Arrays.asList(new S3RecentDataExportInfo()));
        assertNotNull(s3RecentDataExportInfoList.getRecentDataExportInfoList());
    }
}
