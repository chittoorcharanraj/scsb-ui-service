package org.recap.model.jpa;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.recap.BaseTestCase;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.TestCase.assertNotNull;

public class FileUploadEntityUT extends BaseTestCase {

    @InjectMocks
    FileUploadEntity upload;

    @Test
    public void testFileUploadEntity(){
        upload.setNewInstitutionname("HD");
        upload.setInstType("auth");
        upload.setOnBoardstatus("yes");
        upload.setCreatedBy("test1");
        upload.setUpdatedBy("update");
        upload.setOnBoardcomments("test2");

        Date date = Calendar.getInstance().getTime();
        upload.setCreatedDate(date);
        upload.setUpdatedDate(date);

        byte[] bytes = {23, 35, 89};
        upload.setOnBoardxmlfile(bytes);

        assertNotNull(upload.getNewInstitutionname());
        assertNotNull(upload.getInstType());
        assertNotNull(upload.getOnBoardstatus());
        assertNotNull(upload.getCreatedBy());
        assertNotNull(upload.getUpdatedBy());
        assertNotNull(upload.getOnBoardcomments());
        assertNotNull(upload.getCreatedDate());
        assertNotNull(upload.getUpdatedDate());
        assertNotNull(upload.getOnBoardxmlfile());

    }
}
