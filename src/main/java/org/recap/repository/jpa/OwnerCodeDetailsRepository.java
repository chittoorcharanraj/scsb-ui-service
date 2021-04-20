package org.recap.repository.jpa;


import org.recap.model.jpa.DeliveryCodeEntity;
import org.recap.model.jpa.OwnerCodeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * The interface Owner code details repository.
 */
public interface OwnerCodeDetailsRepository extends BaseRepository<OwnerCodeEntity> {

    /**
     * Find by owner code owner code entity.
     *
     * @param ownerCode the owner code
     * @return the owner code entity
     */
    OwnerCodeEntity findByOwnerCode(@Param("ownerCode") String ownerCode);

    /**
     * To get the customer code entity for the given description.
     *
     * @param description the description
     * @return the customer code entity
     */
    OwnerCodeEntity findByDescription(@Param("description") String description);

    /**
     * Find OwnerCodeEntity by owner code and institution Id.
     *
     * @param ownerCode the owner code
     * @param institutionId the institution Id
     * @return the owner code entity
     */
    OwnerCodeEntity findByOwnerCodeAndInstitutionId(String ownerCode, Integer institutionId);

    /**
     * Find by owner code owner code entity.
     *
     * @param ownerCode the owner code
     * @return the owner code entity
     */
    @Query(value = "select ownerCodeEntity from OwnerCodeEntity ownerCodeEntity inner join ownerCodeEntity.institutionEntity ie where ie.institutionCode = :institutionCode and ownerCodeEntity.ownerCode = :ownerCode ")
    OwnerCodeEntity findByOwnerCodeAndOwningInstitutionCode(@Param("ownerCode") String ownerCode, @Param("institutionCode") String institutionCode);

    /**
     * Find by owner code in list.
     *
     * @param ownerCodes the owner codes
     * @return the list
     */
    List<OwnerCodeEntity> findByOwnerCodeIn(List<String> ownerCodes);

/*
    @Query(value="select ownerCode from OwnerCodeEntity ownerCode where ownerCode.ownerCode =:ownerCode and ownerCode.recapDeliveryRestrictions LIKE ('%EDD%')")
   */
    @Query(value="select OC.* from OWNER_CODES_T OC join OWN_DELIVERY_MAPPING_T ODM on OC.OWNER_CODE_ID = ODM.OWNER_CODE_ID JOIN DELIVERY_CODES_T DC ON DC.DELIVERY_CODE_ID = ODM.DELIVERY_CODE_ID WHERE DC.DELIVERY_CODE = 'EDD' AND OC.OWNER_CODE=:ownerCode", nativeQuery = true)
    OwnerCodeEntity findByOwnerCodeAndRecapDeliveryRestrictionLikeEDD(@Param("ownerCode") String ownerCode);


    @Query(value="select * from DeliveryCodeEntity DC join OWN_DELIVERY_MAPPING_T ODM  on ON DC.id = ODM.DELIVERY_CODE_ID  JOIN OwnerCodeEntity ON OC.id = ODM.OWNER_CODE_ID  WHERE ODM.DEL_RESTRICT_TYPE_ID in(select DEL_RESTRICT_TYPE_ID from DELIVERY_RESTRICT_TYPE_T where DEL_RESTRICT_TYPE=:deliveryCodeType) AND OC.ownerCode=:ownerCode", nativeQuery = true)
    List<DeliveryCodeEntity> findByOwnerCodeWithDeliveryRestriction(@Param("ownerCode") String ownerCode, @Param("deliveryCodeType") String deliveryCodeType);


    @Query(value="select * from delivery_codes_t where delivery_code = :deliveryLocation and delivery_code_id in " +
            "(select delivery_code_id from OWN_DELIVERY_MAPPING_T where OWNER_CODE_ID =:ownerCodeId " +
            " and requesting_INST_ID = :institutionId ", nativeQuery = true)
    List<DeliveryCodeEntity> findByOwnerCodeAndRequestingInstitution(@Param("ownerCodeId") Integer ownerCodeId, @Param("institutionId") Integer institution, @Param("deliveryLocation") String deliveryLocation);

    @Query(value="SELECT * FROM DELIVERY_CODES_T WHERE DELIVERY_CODE_ID IN (SELECT DELIVERY_CODE_ID FROM OWN_DELIVERY_MAPPING_T WHERE OWNER_CODE_ID =:ownerCodeId)", nativeQuery = true)
    List<DeliveryCodeEntity> findAllDeliveryRestrictionsByOwnerCodeId(@Param("ownerCodeId") Integer ownerCodeId);

    @Query(value="SELECT * FROM DELIVERY_CODES_T WHERE DELIVERY_CODE_ID IN (SELECT DELIVERY_CODE_ID FROM OWN_DELIVERY_MAPPING_T WHERE OWNER_CODE_ID =:ownerCodeId AND REQUESTING_INST_ID =:requestingInstitutionId)", nativeQuery = true)
    List<DeliveryCodeEntity> findDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(@Param("ownerCodeId") Integer ownerCodeId, @Param("requestingInstitutionId") Integer requestingInstitutionId);

    @Query(value="select DC.* from DELIVERY_CODES_T DC where DC.DELIVERY_CODE_ID in (select ODM.DELIVERY_CODE_ID from OWN_DELIVERY_MAPPING_T ODM where ODM.OWNER_CODE_ID =:ownerCodeId and ODM.REQUESTING_INST_ID = :institutionId) AND DC.IMS_LOCATION_ID IS NULL", nativeQuery = true)
    List<Object[]> findInstitutionDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(@Param("ownerCodeId") Integer ownerCodeId, @Param("institutionId") Integer institutionId);

    @Query(value="SELECT DC.* FROM DELIVERY_CODES_T DC WHERE DC.DELIVERY_CODE_ID IN (SELECT ODM.DELIVERY_CODE_ID FROM OWN_DELIVERY_MAPPING_T ODM WHERE ODM.OWNER_CODE_ID =:ownerCodeId AND ODM.REQUESTING_INST_ID =:requestingInstitutionId) AND DC.IMS_LOCATION_ID =:imsLocationId", nativeQuery = true)
    List<Object[]> findImsLocationDeliveryRestrictionsByOwnerCodeIdAndRequestingInstId(@Param("ownerCodeId") Integer ownerCodeId, @Param("requestingInstitutionId") Integer requestingInstitutionId, @Param("imsLocationId") Integer imsLocationId);

    @Query(value="SELECT DC.* FROM DELIVERY_CODES_T DC WHERE DC.DELIVERY_CODE_ID IN (SELECT ODM.DELIVERY_CODE_ID FROM OWN_DELIVERY_MAPPING_T ODM WHERE ODM.OWNER_CODE_ID =:ownerCodeId AND ODM.DEL_RESTRICT_TYPE_ID = (SELECT DRT.DEL_RESTRICT_TYPE_ID FROM DEL_RESTRICT_TYPE_T DRT WHERE DRT.DEL_RESTRICT_TYPE =:deliveryRestrictType))", nativeQuery = true)
    List<Object[]> findDeliveryRestrictionsByOwnerCodeIdAndDeliveryRestrictType(@Param("ownerCodeId") Integer ownerCodeId, @Param("deliveryRestrictType") String deliveryRestrictType);







}
