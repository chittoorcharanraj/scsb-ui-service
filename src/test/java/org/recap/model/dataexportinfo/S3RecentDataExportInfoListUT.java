package org.recap.model.dataexportinfo;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class S3RecentDataExportInfoListUT extends BaseTestCaseUT {

    @Test
    public void getS3RecentDataExportInfoList(){

        S3RecentDataExportInfoList s3RecentDataExportInfoList = new S3RecentDataExportInfoList();

        s3RecentDataExportInfoList.setRecentDataExportInfoList(Arrays.asList(new S3RecentDataExportInfo()));
        assertNotNull(s3RecentDataExportInfoList.getRecentDataExportInfoList());
    }
}
