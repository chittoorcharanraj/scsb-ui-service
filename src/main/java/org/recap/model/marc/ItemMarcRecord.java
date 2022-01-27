package org.recap.model.marc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.marc4j.marc.Record;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ItemMarcRecord {
    private Record itemRecord;

}
