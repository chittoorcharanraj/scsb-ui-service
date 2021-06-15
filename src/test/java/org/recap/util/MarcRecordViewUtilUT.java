package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.ScsbCommonConstants;
import org.recap.model.jpa.*;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.BibliographicDetailsRepository;
import org.recap.repository.jpa.OwnerCodeDetailsRepository;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by rajeshbabuk on 17/10/16.
 */
public class MarcRecordViewUtilUT extends BaseTestCaseUT {

    @InjectMocks
    MarcRecordViewUtil mockMarcRecordViewUtil;

    @Mock
    OwnerCodeDetailsRepository ownerCodeDetailsRepository;

    @Mock
    BibliographicDetailsRepository bibliographicDetailsRepository;

    @Test
    public void buildBibliographicMarcForm() throws Exception {
        Integer bibId = 1;
        Integer itemId = 1;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(getBibEntityWithHoldingsAndItem());
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemEntities(any(), any(), any())).thenReturn(Arrays.asList(getBibEntityWithHoldingsAndItem().getItemEntities().get(0)));
        BibliographicMarcForm bibliographicMarcForm =mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }
    @Test
    public void buildBibliographicMarcFormEmptyItemEntity() throws Exception {
        Integer bibId = 1;
        Integer itemId = 1;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(getBibEntityWithHoldingsAndItem());
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemEntities(any(), any(), any())).thenReturn(Collections.EMPTY_LIST);
        BibliographicMarcForm bibliographicMarcForm =mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }
    @Test
    public void buildBibliographicMarcFormWithoutInstitutionEntity() throws Exception {
        Integer bibId = 1;
        Integer itemId = 1;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        userDetailsForm.setSuperAdmin(true);
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem();
        bibliographicEntity.setInstitutionEntity(null);
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(bibliographicEntity);
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemEntities(any(), any(), any())).thenReturn(Collections.EMPTY_LIST);
        BibliographicMarcForm bibliographicMarcForm =mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }

    @Test
    public void buildBibliographicMarcFormWithoutItemId() throws Exception {
        Integer bibId = 1;
        Integer itemId = 0;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(getBibEntityWithHoldingsAndItem());
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemEntities(any(), any(), any())).thenReturn(Arrays.asList(getBibEntityWithHoldingsAndItem().getItemEntities().get(0)));
        BibliographicMarcForm bibliographicMarcForm = mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }

    @Test
    public void buildBibliographicMarcFormWithoutBibliographicEntity() throws Exception {
        Integer bibId = 1;
        Integer itemId = 0;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(null);
        BibliographicMarcForm bibliographicMarcForm = mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }

    @Test
    public void buildBibliographicMarcFormItemNotAvailable() throws Exception {
        Integer bibId = 1;
        Integer itemId = 1;
        UserDetailsForm userDetailsForm = getUserDetailsForm();
        ItemEntity itemEntity= getBibEntityWithHoldingsAndItem().getItemEntities().get(0);
        itemEntity.getItemStatusEntity().setStatusCode(ScsbCommonConstants.NOT_AVAILABLE);
        Mockito.when(bibliographicDetailsRepository.findByIdAndCatalogingStatusAndIsDeletedFalse(bibId, ScsbCommonConstants.COMPLETE_STATUS)).thenReturn(getBibEntityWithHoldingsAndItem());
        Mockito.when(bibliographicDetailsRepository.getNonDeletedItemEntities(any(), any(), any())).thenReturn(Arrays.asList(itemEntity));
        BibliographicMarcForm bibliographicMarcForm = mockMarcRecordViewUtil.buildBibliographicMarcForm(bibId,itemId,userDetailsForm);
        assertNotNull(bibliographicMarcForm);
    }

    @Test
    public void getDeliveryLocationsList() {
        Object[] objects = {1, "PA", "test", "test", 1, 1, 1};
        List<Object[]> deliveryCodeObjects = new ArrayList<>();
        deliveryCodeObjects.add(objects);
        Mockito.when(ownerCodeDetailsRepository.findByOwnerCodeAndInstitutionId(any(),any())).thenReturn(getOwnerCodeEntity());
        Mockito.when(ownerCodeDetailsRepository.findDeliveryRestrictionsByOwnerCodeIdAndDeliveryRestrictType(any(),any())).thenReturn(deliveryCodeObjects);
        mockMarcRecordViewUtil.getDeliveryLocationsList("PA", 1);
    }

    private OwnerCodeEntity getOwnerCodeEntity() {
        OwnerCodeEntity ownerCodeEntity = new OwnerCodeEntity();
        ownerCodeEntity.setId(1);
        ownerCodeEntity.setOwnerCode("PA");
        ownerCodeEntity.setDescription("test");
        ownerCodeEntity.setInstitutionId(1);
        return ownerCodeEntity;
    }

    private UserDetailsForm getUserDetailsForm() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setSuperAdmin(false);
        userDetailsForm.setLoginInstitutionId(2);
        return userDetailsForm;
    }

    public BibliographicEntity getBibEntityWithHoldingsAndItem() throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);
        bibliographicEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1);
        itemEntity.setLastUpdatedDate(new Date());
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode("010203");
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        itemEntity.setDeleted(false);
        itemEntity.setCatalogingStatus(ScsbCommonConstants.COMPLETE_STATUS);
        itemEntity.setImsLocationId(1);
        CollectionGroupEntity collectionGroupEntity = new CollectionGroupEntity();
        collectionGroupEntity.setCollectionGroupCode(ScsbCommonConstants.SHARED_CGD);
        ItemStatusEntity itemStatusEntity = new ItemStatusEntity();
        itemStatusEntity.setId(1);
        itemStatusEntity.setStatusCode(ScsbCommonConstants.AVAILABLE);
        ImsLocationEntity imsLocationEntity = new ImsLocationEntity();
        imsLocationEntity.setId(1);
        imsLocationEntity.setImsLocationCode("HD");
        imsLocationEntity.setActive(true);
        itemEntity.setItemStatusEntity(itemStatusEntity);
        itemEntity.setCollectionGroupEntity(collectionGroupEntity);
        itemEntity.setImsLocationEntity(imsLocationEntity);

        InstitutionEntity institutionEntity = new InstitutionEntity();
        institutionEntity.setId(1);
        institutionEntity.setInstitutionName("UC");
        institutionEntity.setInstitutionName("UC");

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setInstitutionEntity(institutionEntity);
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    public File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    public File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }
}
