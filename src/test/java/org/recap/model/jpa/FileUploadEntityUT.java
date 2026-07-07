package org.recap.model.jpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({SpringExtension.class})
public class FileUploadEntityUT {

    @InjectMocks
    FileUploadEntity upload;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

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
