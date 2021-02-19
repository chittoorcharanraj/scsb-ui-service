package org.recap.service;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.jpa.RequestStatusEntity;
import org.recap.model.search.BulkRequestForm;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:application.properties")
public class BulkRequestServiceUT {

    @InjectMocks
    BulkRequestService bulkRequestService;

    @Mock
    BulkRequestForm bulkRequestForm;

    @Mock
    BulkSearchRequestService bulkSearchRequestService;

    @Mock
    BulkRequestItemEntity bulkRequestItemEntity;

    @Mock
    InstitutionDetailsRepository institutionDetailsRepository;

    @Mock
    SecurityUtil securityUtil;

    @Mock
    BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Mock
    RequestItemEntity requestItemEntity;

    @Mock
    RequestStatusEntity requestStatusEntity;

    @Test
    public void testSaveUpdatedRequestStatus() throws Exception {
        List<RequestItemEntity> requestItemEntities=new ArrayList<>();
        requestItemEntities.add(requestItemEntity);
        Mockito.when(bulkRequestItemEntity.getRequestItemEntities()).thenReturn(requestItemEntities);
        Mockito.when(requestItemEntity.getRequestStatusEntity()).thenReturn(requestStatusEntity);
        Mockito.when(bulkRequestItemEntity.getBulkRequestStatus()).thenReturn("PROCESSED");
        Mockito.when(requestStatusEntity.getRequestStatusCode()).thenReturn("EXCEPTION");
        Mockito.when(bulkRequestDetailsRepository.findById(1)).thenReturn(Optional.of(bulkRequestItemEntity));
        File bibContentFile = getBibContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        Mockito.when(bulkRequestItemEntity.getBulkRequestFileData()).thenReturn(sourceBibContent.getBytes(StandardCharsets.UTF_8));
        BulkRequestItemEntity request=bulkRequestService.saveUpadatedRequestStatus(1);
        assertNotNull(request);
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = null;
        resource = getClass().getResource("BulkRequest.csv");
        return new File(resource.toURI());
    }

    @Test
    public void testProcessOnPageSizeChange(){
        Page<BulkRequestItemEntity> bulkRequestItemEntities= PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(Mockito.any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(2l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList=new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult=bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }

    @Test
    public void testProcessOnPageSizeChangeResultsNotFound(){
        Page<BulkRequestItemEntity> bulkRequestItemEntities= PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(Mockito.any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(0l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList=new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult=bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }

    @Test
    public void testProcessOnPageSizeChangeException(){
        Page<BulkRequestItemEntity> bulkRequestItemEntities= PowerMockito.mock(Page.class);
        Mockito.when(bulkSearchRequestService.processSearchRequest(Mockito.any())).thenReturn(bulkRequestItemEntities);
        Mockito.when(bulkRequestItemEntities.getTotalElements()).thenReturn(2l);
        List<BulkRequestItemEntity> bulkRequestItemEntityList=new ArrayList<>();
        bulkRequestItemEntityList.add(bulkRequestItemEntity);
        Mockito.when(bulkRequestItemEntities.getContent()).thenReturn(bulkRequestItemEntityList);
        Mockito.when(bulkRequestForm.getPageNumber()).thenReturn(2);
        Mockito.when(bulkRequestForm.getPageSize()).thenReturn(2);
        Mockito.when(bulkRequestItemEntity.getEmailId()).thenReturn("test@gmail.com");
        Mockito.when(securityUtil.getDecryptedValue(Mockito.any())).thenThrow(NullPointerException.class);
        Mockito.when(bulkRequestForm.getTotalRecordsCount()).thenReturn("2");
        BulkRequestForm bulkRequestFormResult=bulkRequestService.processOnPageSizeChange(bulkRequestForm);
        assertNotNull(bulkRequestFormResult);
    }
}
