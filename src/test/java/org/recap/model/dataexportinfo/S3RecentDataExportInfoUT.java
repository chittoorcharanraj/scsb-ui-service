package org.recap.model.dataexportinfo;

import org.junit.Test;
import org.recap.BaseTestCaseUT;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class S3RecentDataExportInfoUT extends BaseTestCaseUT {

    @Test
    public void getS3RecentDataExportInfo(){
        S3RecentDataExportInfo s3RecentDataExportInfo = new S3RecentDataExportInfo();
        s3RecentDataExportInfo.setBibDataFormat("XML");
        s3RecentDataExportInfo.setInstitution("PUL");
        s3RecentDataExportInfo.setBibCount("10");
        s3RecentDataExportInfo.setGcd("GCD");
        s3RecentDataExportInfo.setItemCount("223");
        s3RecentDataExportInfo.setKeyName("testKey");
        s3RecentDataExportInfo.setKeyLastModified(new Date());
        s3RecentDataExportInfo.setKeySize(2L);

        assertNotNull(s3RecentDataExportInfo.getBibCount());
        assertNotNull(s3RecentDataExportInfo.getBibDataFormat());
        assertNotNull(s3RecentDataExportInfo.getGcd());
        assertNotNull(s3RecentDataExportInfo.getInstitution());
        assertNotNull(s3RecentDataExportInfo.getItemCount());
        assertNotNull(s3RecentDataExportInfo.getKeyName());
        assertNotNull(s3RecentDataExportInfo.getKeyLastModified());
        assertNotNull(s3RecentDataExportInfo.getKeySize());
    }
}
