package org.recap.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.model.jpa.FileUploadEntity;
import org.recap.repository.jpa.FileUploadRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static junit.framework.Assert.assertNotNull;
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class FileUploadServiceUT {

    @InjectMocks
    FileUploadService fileUploadService;

    @Mock
    FileUploadEntity fileUpload;

    @Mock
    FileUploadRepository fileUploadRepository;


    @Test
    public void testuploadFile() {
        Mockito.when(fileUploadRepository.saveAndFlush(Mockito.any())).thenReturn(fileUpload);
        FileUploadEntity upload=fileUploadService.uploadFile(fileUpload);
        assertNotNull(upload);
    }

    @Test
    public void getFileUploadEntity() {
        Mockito.when(fileUploadRepository.findByInstitutionName(Mockito.any())).thenReturn(fileUpload);
        FileUploadEntity upload=fileUploadService.getFileUploadEntity("HUL");
        assertNotNull(upload);
    }

    @Test
    public void updateFileUploadEntity() {
        Mockito.when(fileUploadRepository.updateFileUploadEntity(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(1);
        int upload=fileUploadService.updateFileUploadEntity("HUL","test","onBoardstatus",new Date(),"onBoardcomments");
        assertNotNull(upload);
    }

}
