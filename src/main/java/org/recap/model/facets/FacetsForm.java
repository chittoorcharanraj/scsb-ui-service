package org.recap.model.facets;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinakar on 03/05/21
 */

@Setter
@Getter
public class FacetsForm {
    List<String> institutionList = new ArrayList<>();
    List<String> storageLocationsList= new ArrayList<>();
}
