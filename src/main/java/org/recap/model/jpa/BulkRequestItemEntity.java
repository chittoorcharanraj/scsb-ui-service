package org.recap.model.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;


import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by akulak on 21/9/17.
 */
@Entity
@Table(name = "bulk_request_item_t", catalog = "")
@AttributeOverride(name = "id", column = @Column(name = "BULK_REQUEST_ID"))
@Data
@EqualsAndHashCode(callSuper = false)
public class BulkRequestItemEntity extends BulkRequestItemAbstractEntity {

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "bulk_request_t",
            joinColumns = @JoinColumn(name = "BULK_REQUEST_ID"),
            inverseJoinColumns = @JoinColumn(name = "REQUEST_ID"))
    private List<RequestItemEntity> requestItemEntities;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IMS_LOCATION_ID", referencedColumnName = "IMS_LOCATION_ID", insertable = false, updatable = false)
    private ImsLocationEntity imsLocationEntity;
/*
    @Column(
            name = "IMS_LOCATION_ID"
    )
    private Integer imsLocationCode;*/
}
