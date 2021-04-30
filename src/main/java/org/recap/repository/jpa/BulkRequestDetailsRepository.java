package org.recap.repository.jpa;

import org.recap.model.jpa.BulkRequestItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by akulak on 22/9/17.
 */

public interface BulkRequestDetailsRepository extends BaseRepository<BulkRequestItemEntity> {
    Page<BulkRequestItemEntity> findByIdAndRequestingInstitutionIdAndImsLocation(Pageable pageable, Integer id, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndImsLocation(Pageable pageable, Integer id,Integer imsLocation);

    Page<BulkRequestItemEntity> findByBulkRequestNameAndRequestingInstitutionIdAndImsLocation(Pageable pageable, String bulkRequestName, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByBulkRequestNameAndImsLocation(Pageable pageable, String bulkRequestName,Integer imsLocation);

    Page<BulkRequestItemEntity> findByPatronIdAndRequestingInstitutionIdAndImsLocation(Pageable pageable, String patronId, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByPatronIdAndImsLocation(Pageable pageable, String patronId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndBulkRequestNameAndRequestingInstitutionIdAndImsLocation(Pageable pageable, Integer id, String bulkRequestName, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndBulkRequestNameAndImsLocation(Pageable pageable, Integer id, String bulkRequestName,Integer imsLocation);

    Page<BulkRequestItemEntity> findByBulkRequestNameAndPatronIdAndRequestingInstitutionIdAndImsLocation(Pageable pageable, String bulkRequestName, String patronId, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByBulkRequestNameAndPatronIdAndImsLocation(Pageable pageable, String bulkRequestName, String patronId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndPatronIdAndRequestingInstitutionIdAndImsLocation(Pageable pageable, Integer id, String patronId, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndPatronIdAndImsLocation(Pageable pageable, Integer id, String patronId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndBulkRequestNameAndPatronIdAndRequestingInstitutionIdAndImsLocation(Pageable pageable, Integer id, String bulkRequestName, String patronId, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByIdAndBulkRequestNameAndPatronIdAndImsLocation(Pageable pageable, Integer id, String bulkRequestName, String patronId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByRequestingInstitutionIdAndImsLocation(Pageable pageable, Integer requestingInstitutionId,Integer imsLocation);

    Page<BulkRequestItemEntity> findByImsLocation(Pageable pageable,Integer imsLocation);

}
