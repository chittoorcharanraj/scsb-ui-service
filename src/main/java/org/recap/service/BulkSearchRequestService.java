package org.recap.service;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.jpa.BulkRequestItemEntity;
import org.recap.model.jpa.InstitutionEntity;
import org.recap.model.search.BulkRequestForm;
import org.recap.repository.jpa.BulkRequestDetailsRepository;
import org.recap.repository.jpa.ImsLocationDetailRepository;
import org.recap.repository.jpa.InstitutionDetailsRepository;
import org.recap.repository.jpa.ItemDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Created by akulak on 25/9/17.
 */
@Service
public class BulkSearchRequestService {

    @Autowired
    private InstitutionDetailsRepository institutionDetailsRepository;

    @Autowired
    private BulkRequestDetailsRepository bulkRequestDetailsRepository;

    @Autowired
    private ImsLocationDetailRepository imsLocationDetailRepository;

    public Page<BulkRequestItemEntity> processSearchRequest(BulkRequestForm bulkRequestForm) {
        String requestId = StringUtils.isNotBlank(bulkRequestForm.getRequestIdSearch()) ? bulkRequestForm.getRequestIdSearch().trim() : bulkRequestForm.getRequestIdSearch();
        Integer bulkRequestId = 0;
        if(StringUtils.isNotBlank(requestId)){bulkRequestId = Integer.valueOf(requestId);}
        String bulkRequestName = StringUtils.isNotBlank(bulkRequestForm.getRequestNameSearch()) ? bulkRequestForm.getRequestNameSearch().trim() : bulkRequestForm.getRequestNameSearch();
        String patronId = StringUtils.isNotBlank(bulkRequestForm.getPatronBarcodeSearch()) ? bulkRequestForm.getPatronBarcodeSearch().trim() : bulkRequestForm.getPatronBarcodeSearch();
        String institution = StringUtils.isNotBlank(bulkRequestForm.getInstitution()) ? bulkRequestForm.getInstitution().trim() : bulkRequestForm.getInstitution();
        Integer storageLcoation = imsLocationDetailRepository.findByImsLocationCode(bulkRequestForm.getStorageLocation()).getId();
        InstitutionEntity institutionEntity = institutionDetailsRepository.findByInstitutionCode(institution);
        Integer requestingInstitutionId = 0;
        if (institutionEntity != null){
            requestingInstitutionId = institutionEntity.getId();
        }
        Pageable pageable = PageRequest.of(bulkRequestForm.getPageNumber(), bulkRequestForm.getPageSize(), Sort.Direction.DESC,"id");

        if (StringUtils.isNotBlank(requestId) && StringUtils.isBlank(bulkRequestName) && StringUtils.isBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByIdAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestId,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByIdAndImsLocation(pageable,bulkRequestId,storageLcoation);
        } else if (StringUtils.isBlank(requestId) && StringUtils.isNotBlank(bulkRequestName) && StringUtils.isBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByBulkRequestNameAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestName,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByBulkRequestNameAndImsLocation(pageable,bulkRequestName,storageLcoation);
        } else if (StringUtils.isBlank(requestId) && StringUtils.isBlank(bulkRequestName) && StringUtils.isNotBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByPatronIdAndRequestingInstitutionIdAndImsLocation(pageable,patronId,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByPatronIdAndImsLocation(pageable,patronId,storageLcoation);
        } else if (StringUtils.isNotBlank(requestId) && StringUtils.isNotBlank(bulkRequestName) && StringUtils.isBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByIdAndBulkRequestNameAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestId,bulkRequestName,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByIdAndBulkRequestNameAndImsLocation(pageable,bulkRequestId,bulkRequestName,storageLcoation);
        } else if (StringUtils.isBlank(requestId) && StringUtils.isNotBlank(bulkRequestName) && StringUtils.isNotBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByBulkRequestNameAndPatronIdAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestName,patronId,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByBulkRequestNameAndPatronIdAndImsLocation(pageable,bulkRequestName,patronId,storageLcoation);
        } else if (StringUtils.isNotBlank(requestId) && StringUtils.isBlank(bulkRequestName) && StringUtils.isNotBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByIdAndPatronIdAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestId,patronId,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByIdAndPatronIdAndImsLocation(pageable,bulkRequestId,patronId,storageLcoation);
        } else if (StringUtils.isNotBlank(requestId) && StringUtils.isNotBlank(bulkRequestName) && StringUtils.isNotBlank(patronId)) {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByIdAndBulkRequestNameAndPatronIdAndRequestingInstitutionIdAndImsLocation(pageable,bulkRequestId,bulkRequestName,patronId,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByIdAndBulkRequestNameAndPatronIdAndImsLocation(pageable,bulkRequestId,bulkRequestName,patronId,storageLcoation);
        } else {
            return StringUtils.isNotBlank(institution) ? bulkRequestDetailsRepository.findByRequestingInstitutionIdAndImsLocation(pageable,requestingInstitutionId,storageLcoation) : bulkRequestDetailsRepository.findByImsLocation(pageable,storageLcoation);
        }
    }
}
