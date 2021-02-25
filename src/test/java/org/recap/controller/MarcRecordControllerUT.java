package org.recap.controller;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.model.search.BibliographicMarcForm;
import org.recap.util.MarcRecordViewUtil;

import static org.junit.Assert.assertNotNull;

public class MarcRecordControllerUT extends BaseTestCaseUT {

    @InjectMocks
    MarcRecordController marcRecordController;

    @Mock
    MarcRecordViewUtil marcRecordViewUtil;

    @Test
    public void openMarcRecord(){
        BibliographicMarcForm bibliographicMarcForm = new BibliographicMarcForm();
        Integer bibId = 1;
        Mockito.when(marcRecordViewUtil.buildBibliographicMarcForm(bibId, null, null)).thenReturn(bibliographicMarcForm);
        BibliographicMarcForm marcForm = marcRecordController.openMarcRecord(bibId);
        assertNotNull(marcForm);
    }
}
