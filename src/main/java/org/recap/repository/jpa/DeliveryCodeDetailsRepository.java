package org.recap.repository.jpa;

import org.recap.model.jpa.DeliveryCodeEntity;
import org.springframework.data.repository.query.Param;

/**
 * Created by rajeshbabuk on 20/Apr/2021
 */
public interface DeliveryCodeDetailsRepository extends BaseRepository<DeliveryCodeEntity> {

    /**
     * Find by delivery code delivery code entity.
     *
     * @param deliveryCode the delivery code
     * @param owningInstitutionId Instituion Id
     * @param active active flag
     * @return the delivery code entity
     */
    DeliveryCodeEntity findByDeliveryCodeAndOwningInstitutionIdAndActive(@Param("deliveryCode") String deliveryCode, @Param("owningInstitutionId") Integer owningInstitutionId, @Param("active") char active);
}
