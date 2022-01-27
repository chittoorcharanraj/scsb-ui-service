package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.util.MarcRecordViewUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by rajeshbabuk on 22/7/16.
 */
@Slf4j
@RestController
@RequestMapping("/openMarcRecordByBibId")
public class MarcRecordController {

    @Autowired
    private MarcRecordViewUtil marcRecordViewUtil;

    /**
     * Renders the marc record view UI page for the scsb application.
     *
     * @param bibId the bib id
     * @return the string
     */
    @GetMapping("")
    public BibliographicMarcForm openMarcRecord(@RequestParam("bibId") Integer bibId) {
        log.info("openMarcRecord --> Called");
        BibliographicMarcForm bibliographicMarcForm = marcRecordViewUtil.buildBibliographicMarcForm(bibId, null, null);
        return bibliographicMarcForm;
    }

}
