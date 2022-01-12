package org.recap.repository.jpa;

import org.recap.model.jpa.ImsLocationEntity;

/**
 * @author dinakar on 30/04/21
 */
public interface ImsLocationDetailRepository extends BaseRepository<ImsLocationEntity> {
    /**
     *
     * @param imsLocationCode
     * @return ImsLocationEntity
     */
    ImsLocationEntity findByImsLocationCode(String imsLocationCode);

}
