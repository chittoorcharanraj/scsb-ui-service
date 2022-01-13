package org.recap.model.search;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.Comparator;

/**
 * Created by rajeshbabuk on 25/7/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BibDataField implements Comparable<BibDataField>{

    private String dataFieldTag;
    private char indicator1;
    private char indicator2;
    private String dataFieldValue;

    private static final Comparator<BibDataField> BIB_DATA_FIELD_COMPARATOR = Comparator.comparing(BibDataField::getDataFieldTag,Comparator.nullsFirst(String::compareTo)).thenComparing(BibDataField::getDataFieldValue,Comparator.nullsFirst(String::compareTo));

    @Override
    public int compareTo(BibDataField bibDataField) {
        return BIB_DATA_FIELD_COMPARATOR.compare(this,bibDataField);
    }

}
