package org.recap.repository.jpa;

import org.recap.model.jpa.RequestItemEntity;
import org.recap.model.reports.TransactionReport;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

/**
 * Created by rajeshbabuk on 26/10/16.
 */
public interface RequestItemDetailsRepository extends BaseRepository<RequestItemEntity> {



    /**
     *To get the pageable request item entity for the given patron barcode, item barcode, status and institution.
     *
     * @param pageable      the pageable
     * @param patronBarcode the patron barcode
     * @param itemBarcode   the item barcode
     * @param status        the status
     * @param institutionId the institution id
     * @return the page
     */
    @Query(value = "select request from RequestItemEntity request inner join request.itemEntity item inner join request.requestStatusEntity status where ((request.patronId = :patronBarcode OR :patronBarcode = '') and (request.requestingInstitutionId = :institutionId) and (item.barcode = :itemBarcode OR :itemBarcode = '') and (status.requestStatusDescription = :status OR :status = '') and (item.imsLocationId = :imsLocationId OR :imsLocationId = 0)) OR ((request.patronId = :patronBarcode OR :patronBarcode = '') and (item.barcode = :itemBarcode OR :itemBarcode = '') and (request.requestingInstitutionId not in (:institutionId)) and (item.owningInstitutionId = :institutionId OR :institutionId = 0) and (status.requestStatusDescription = :status OR :status = '') and (item.imsLocationId = :imsLocationId OR :imsLocationId = 0))")
    Page<RequestItemEntity> findByPatronBarcodeAndItemBarcodeAndStatusAndInstitution(Pageable pageable, @Param("patronBarcode") String patronBarcode, @Param("itemBarcode") String itemBarcode, @Param("status") String status,@Param("imsLocationId") Integer imsLocationId,@Param("institutionId")Integer institutionId);
    /**
     * To get the pageable request item entity which has retrieval, recall and edd status for the given patron barcode, item barcode and institution.
     *
     * @param pageable      the pageable
     * @param patronBarcode the patron barcode
     * @param itemBarcode   the item barcode
     * @param institutionId the institution id
     * @return the page
     */
    @Query(value = "select request from RequestItemEntity request inner join request.itemEntity item inner join request.requestStatusEntity status where ((request.patronId = :patronBarcode OR :patronBarcode = '') and (request.requestingInstitutionId = :institutionId) and (item.barcode = :itemBarcode OR :itemBarcode = '') and (status.requestStatusCode in ('RETRIEVAL_ORDER_PLACED', 'RECALL_ORDER_PLACED', 'EDD_ORDER_PLACED')) and (item.imsLocationId = :imsLocationId)) OR ((request.patronId = :patronBarcode OR :patronBarcode = '') and (item.barcode = :itemBarcode OR :itemBarcode = '') and (request.requestingInstitutionId not in (:institutionId)) and (item.owningInstitutionId = :institutionId OR :institutionId = 0) and (status.requestStatusCode in ('RETRIEVAL_ORDER_PLACED', 'RECALL_ORDER_PLACED', 'EDD_ORDER_PLACED')) and (item.imsLocationId = :imsLocationId))")
    Page<RequestItemEntity> findByPatronBarcodeAndItemBarcodeAndActiveAndInstitution(Pageable pageable, @Param("patronBarcode") String patronBarcode, @Param("itemBarcode") String itemBarcode,@Param("imsLocationId") Integer imsLocationId,@Param("institutionId")Integer institutionId);

    /**
     * To get the request item entity for the given item barcode and status.
     *
     * @param itemBarcode       the item barcode
     * @param requestStatusCode the request status code
     * @return the request item entity
     * @throws IncorrectResultSizeDataAccessException the incorrect result size data access exception
     */
    @Query(value = "select requestItemEntity from RequestItemEntity requestItemEntity inner join requestItemEntity.itemEntity ie inner join requestItemEntity.requestStatusEntity as rse  where ie.barcode = :itemBarcode and rse.requestStatusCode= :requestStatusCode ")
    RequestItemEntity findByItemBarcodeAndRequestStaCode(@Param("itemBarcode") String itemBarcode, @Param("requestStatusCode") String requestStatusCode) throws IncorrectResultSizeDataAccessException;


    /**
     * To get the count of edd, recall and retrieval request counts for the given institution.
     *
     * @param fromDate         the from date
     * @param toDate           the to date
     * @param itemOwningInstId the item owning inst id
     * @param requestTypeCode  the request type code
     * @return the bd hold recall retrieval request counts
     */
    @Query(value = "SELECT COUNT(*) FROM REQUEST_ITEM_T,ITEM_T WHERE REQUEST_ITEM_T.ITEM_ID=ITEM_T.ITEM_ID " +
            "AND REQUEST_ITEM_T.CREATED_DATE >= :fromDate AND REQUEST_ITEM_T.CREATED_DATE <= :toDate " +
            "AND REQUEST_ITEM_T.REQUEST_TYPE_ID IN (SELECT REQUEST_TYPE_ID FROM REQUEST_TYPE_T WHERE REQUEST_TYPE_CODE IN (:requestTypeCodes)) " +
            "AND REQUEST_ITEM_T.REQUEST_STATUS_ID IN (SELECT REQUEST_STATUS_ID FROM REQUEST_ITEM_STATUS_T WHERE REQUEST_STATUS_CODE IN (:requestStatusCodes)) " +
            "AND ITEM_T.OWNING_INST_ID = :itemOwningInstId", nativeQuery = true)
    long getEDDRecallRetrievalRequestCounts(@Param("fromDate") Date fromDate,
                                            @Param("toDate") Date toDate,
                                            @Param("itemOwningInstId") int itemOwningInstId,
                                            @Param("requestStatusCodes") List<String> requestTypeCode,
                                            @Param("requestTypeCodes") List<String> requestTypeCodes);

    /**
     * To get the count of physical and edd requests for all the institutions.
     *
     * @param fromDate           the from date
     * @param toDate             the to date
     * @param itemOwningInstId   the item owning inst id
     * @param collectionGroupIds the collection group ids
     * @param requestStatusCodes   the request status codes
     * @return the physical and edd counts
     */
    @Query(value = "SELECT COUNT(*) FROM REQUEST_ITEM_T,ITEM_T WHERE REQUEST_ITEM_T.ITEM_ID=ITEM_T.ITEM_ID " +
            "AND REQUEST_ITEM_T.CREATED_DATE >= :fromDate AND REQUEST_ITEM_T.CREATED_DATE <= :toDate " +
            "AND REQUEST_ITEM_T.REQUESTING_INST_ID NOT IN (:requestInstIds) " +
            "AND REQUEST_ITEM_T.REQUEST_TYPE_ID IN (SELECT REQUEST_TYPE_ID FROM REQUEST_TYPE_T WHERE REQUEST_TYPE_CODE IN (:requestTypeCodes)) " +
            "AND REQUEST_ITEM_T.REQUEST_STATUS_ID IN (SELECT REQUEST_STATUS_ID FROM REQUEST_ITEM_STATUS_T WHERE REQUEST_STATUS_CODE IN (:requestStatusCodes)) " +
            "AND ITEM_T.COLLECTION_GROUP_ID IN (:collectionGroupIds) " +
            "AND ITEM_T.OWNING_INST_ID IN (:itemOwningInstId)", nativeQuery = true)
    long getPhysicalAndEDDCounts(@Param("fromDate") Date fromDate,
                                 @Param("toDate") Date toDate,
                                 @Param("itemOwningInstId") List<Integer> itemOwningInstId,
                                 @Param("collectionGroupIds") List<Integer> collectionGroupIds,
                                 @Param("requestInstIds") List<Integer> requestInstIds,
                                 @Param("requestStatusCodes") List<String> requestStatusCodes,
                                 @Param("requestTypeCodes") List<String> requestTypeCodes);

    /**
     * To get the list of  request item entities for the given list of request id.
     *
     * @param id the request id
     * @return the list
     */
    List<RequestItemEntity> findByIdIn(List<Integer> id);

    /**
     *
     * @param status
     * @param institutionId
     * @return the list of RequestItemEntity
     */
    @Query(value = "select request from RequestItemEntity request inner join  request.requestStatusEntity status inner join request.itemEntity item where (request.requestingInstitutionId = :institutionId and status.requestStatusDescription = :status and request.lastUpdatedDate BETWEEN :fromDate AND :toDate)  OR (request.requestingInstitutionId not in (:institutionId) and item.owningInstitutionId = :institutionId and status.requestStatusDescription in (:status) and request.lastUpdatedDate BETWEEN :fromDate AND :toDate)")
    List<RequestItemEntity> findByStatusAndInstitutionAndAll(@Param("status") String status,@Param("institutionId")Integer institutionId,@Param("fromDate")Date fromDate,@Param("toDate")Date toDate);

    /**
     *
     * @param status
     * @param institutionId
     * @return the list of RequestItemEntity
     */
    @Query(value = "select request from RequestItemEntity request inner join  request.requestStatusEntity status inner join request.itemEntity item where (request.requestingInstitutionId = :institutionId and status.requestStatusDescription = :status and request.lastUpdatedDate BETWEEN :fromDate AND :toDate)  OR (request.requestingInstitutionId not in (:institutionId) and item.owningInstitutionId = :institutionId and status.requestStatusDescription in (:status) and request.lastUpdatedDate BETWEEN :fromDate AND :toDate)")
    Page<RequestItemEntity> findByStatusAndInstitutionAndDateRange(Pageable pageable,@Param("status") String status,@Param("institutionId")Integer institutionId,@Param("fromDate")Date fromDate,@Param("toDate")Date toDate);

    /**
     *
     * @param owningInsts
     * @param requestingInsts
     * @param typeOfUses
     * @param fromDate
     * @param toDate
     * @return List of COUNTS
     */
    @Query(value = "SELECT REQUEST_TYPE_T.REQUEST_TYPE_CODE, REQUEST_ITEM_T.REQUESTING_INST_ID, ITEM_T.OWNING_INST_ID, ITEM_T.COLLECTION_GROUP_ID, COUNT(*) " +
            "            FROM REQUEST_ITEM_T" +
            "            INNER JOIN ITEM_T ON REQUEST_ITEM_T.ITEM_ID = ITEM_T.ITEM_ID" +
            "            INNER JOIN REQUEST_TYPE_T ON REQUEST_ITEM_T.REQUEST_TYPE_ID = REQUEST_TYPE_T.REQUEST_TYPE_ID" +
            "            WHERE ITEM_T.OWNING_INST_ID IN" +
            "                (:owningInsts)" +
            "            AND REQUEST_ITEM_T.REQUESTING_INST_ID IN" +
            "                (:requestingInsts)" +
            "            AND REQUEST_ITEM_T.REQUEST_TYPE_ID" +
            "                IN (:typeOfUses)" +
            "            AND REQUEST_ITEM_T.CREATED_DATE >= :fromDate AND REQUEST_ITEM_T.CREATED_DATE <= :toDate" +
            "            GROUP BY REQUEST_TYPE_T.REQUEST_TYPE_DESC,ITEM_T.OWNING_INST_ID, REQUEST_ITEM_T.REQUESTING_INST_ID, ITEM_T.COLLECTION_GROUP_ID" +
            "            ORDER BY REQUEST_TYPE_T.REQUEST_TYPE_ID,ITEM_T.OWNING_INST_ID,REQUESTING_INST_ID,ITEM_T.COLLECTION_GROUP_ID",nativeQuery = true)
    List<Object[]> pullTransactionReportCount(  @Param("owningInsts") List<Integer> owningInsts,
                                                @Param("requestingInsts") List<Integer> requestingInsts,
                                                @Param("typeOfUses") List<Integer> typeOfUses,
                                                @Param("fromDate") Date fromDate,
                                                @Param("toDate") Date toDate);

    /**
     *
     * @param owningInsts
     * @param requestingInsts
     * @param typeOfUses
     * @param fromDate
     * @param toDate
     * @param cgdType
     * @return the list of Objects with pagination
     */
    @Query(value = "SELECT REQUEST_TYPE_T.REQUEST_TYPE_CODE, REQUEST_ITEM_T.REQUESTING_INST_ID, ITEM_T.OWNING_INST_ID, ITEM_T.COLLECTION_GROUP_ID, ITEM_T.BARCODE, REQUEST_ITEM_T.CREATED_DATE,REQUEST_ITEM_STATUS_T.REQUEST_STATUS_CODE" +
            " FROM  REQUEST_ITEM_T" +
            "    INNER JOIN ITEM_T ON REQUEST_ITEM_T.ITEM_ID = ITEM_T.ITEM_ID " +
            "    INNER JOIN REQUEST_ITEM_STATUS_T ON REQUEST_ITEM_T.REQUEST_STATUS_ID =  REQUEST_ITEM_STATUS_T.REQUEST_STATUS_ID" +
            "    INNER JOIN REQUEST_TYPE_T ON REQUEST_ITEM_T.REQUEST_TYPE_ID = REQUEST_TYPE_T.REQUEST_TYPE_ID " +
            "WHERE ITEM_T.OWNING_INST_ID IN" +
            "    (:owningInsts)" +
            "    AND REQUEST_ITEM_T.REQUESTING_INST_ID IN" +
            "    (:requestingInsts)" +
            "    AND REQUEST_ITEM_T.REQUEST_TYPE_ID" +
            "    IN (:typeOfUses)" +
            "    AND REQUEST_ITEM_T.CREATED_DATE >= :fromDate AND REQUEST_ITEM_T.CREATED_DATE <= :toDate " +
            "    AND ITEM_T.COLLECTION_GROUP_ID  IN (:cgdType)" +
            "ORDER BY ITEM_T.OWNING_INST_ID, REQUEST_ITEM_T.REQUESTING_INST_ID, REQUEST_ITEM_T.REQUEST_STATUS_ID,ITEM_T.COLLECTION_GROUP_ID DESC ",nativeQuery = true)
    List<Object[]> findTransactionReportsByOwnAndReqInstWithStatus(Pageable pageable,
                                                 @Param("owningInsts") List<Integer> owningInsts,
                                                 @Param("requestingInsts") List<Integer> requestingInsts,
                                                 @Param("typeOfUses") List<Integer> typeOfUses,
                                                 @Param("fromDate") Date fromDate,
                                                 @Param("toDate") Date toDate,
                                                 @Param("cgdType") List<Integer> cgdType);

    /**
     *
     * @param owningInsts
     * @param requestingInsts
     * @param typeOfUses
     * @param fromDate
     * @param toDate
     * @param cgdType
     * @return the list of Objects
     */
    @Query(value = "SELECT REQUEST_TYPE_T.REQUEST_TYPE_CODE, REQUEST_ITEM_T.REQUESTING_INST_ID, ITEM_T.OWNING_INST_ID, ITEM_T.COLLECTION_GROUP_ID, ITEM_T.BARCODE, REQUEST_ITEM_T.CREATED_DATE,REQUEST_ITEM_STATUS_T.REQUEST_STATUS_CODE" +
            " FROM  REQUEST_ITEM_T" +
            "    INNER JOIN ITEM_T ON REQUEST_ITEM_T.ITEM_ID = ITEM_T.ITEM_ID " +
            "    INNER JOIN REQUEST_ITEM_STATUS_T ON REQUEST_ITEM_T.REQUEST_STATUS_ID =  REQUEST_ITEM_STATUS_T.REQUEST_STATUS_ID" +
            "    INNER JOIN REQUEST_TYPE_T ON REQUEST_ITEM_T.REQUEST_TYPE_ID = REQUEST_TYPE_T.REQUEST_TYPE_ID " +
            "WHERE ITEM_T.OWNING_INST_ID IN" +
            "    (:owningInsts)" +
            "    AND REQUEST_ITEM_T.REQUESTING_INST_ID IN" +
            "    (:requestingInsts)" +
            "    AND REQUEST_ITEM_T.REQUEST_TYPE_ID" +
            "    IN (:typeOfUses)" +
            "    AND REQUEST_ITEM_T.CREATED_DATE >= :fromDate AND REQUEST_ITEM_T.CREATED_DATE <= :toDate " +
            "    AND ITEM_T.COLLECTION_GROUP_ID IN (:cgdType)" +
            "ORDER BY ITEM_T.OWNING_INST_ID, REQUEST_ITEM_T.REQUESTING_INST_ID, REQUEST_ITEM_T.REQUEST_STATUS_ID,ITEM_T.COLLECTION_GROUP_ID DESC ",nativeQuery = true)
    List<Object[]> findTransactionReportsByOwnAndReqInstWithStatusExport(
                                                        @Param("owningInsts") List<Integer> owningInsts,
                                                        @Param("requestingInsts") List<Integer> requestingInsts,
                                                        @Param("typeOfUses") List<Integer> typeOfUses,
                                                        @Param("fromDate") Date fromDate,
                                                        @Param("toDate") Date toDate,
                                                        @Param("cgdType") List<Integer> cgdType);

}
