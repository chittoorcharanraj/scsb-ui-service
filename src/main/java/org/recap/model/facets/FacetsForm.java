package org.recap.model.facets;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinakar on 03/05/21
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class FacetsForm {
    List<String> institutionList = new ArrayList<>();
    List<String> storageLocationsList= new ArrayList<>();
    List<String> cgdCodesList = new ArrayList<>();
}
