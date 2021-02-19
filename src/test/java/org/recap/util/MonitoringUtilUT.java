package org.recap.util;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.recap.BaseTestCaseUT;
import org.recap.RecapConstants;
import org.recap.config.MonitoringConfiguration;
import org.recap.model.search.Monitoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class MonitoringUtilUT extends BaseTestCaseUT {

    @InjectMocks
    MonitoringUtil monitoringUtil;

    @Mock
    MonitoringConfiguration monitoringConfiguration;

    @Mock
    MonitoringConfiguration.Logging logging;

    @Test
    public void getMonitoringProjects(){
        Map<String, String> map = new HashMap<>();
        map.put(RecapConstants.SCSB_UI,"http://localhost:9080/test");
        map.put(RecapConstants.SCSB_AUTH,"http://localhost:9080/test");
        map.put(RecapConstants.SCSB_GATEWAY,"http://localhost:9080/test");
        map.put(RecapConstants.SCSB_DOC,"http://localhost:9080/test");
        map.put(RecapConstants.SCSB_CIRC,"http://localhost:9080/test");
        map.put(RecapConstants.SCSB_BATCH,"http://localhost:9080/test");
        Mockito.when(monitoringConfiguration.getUrl()).thenReturn(map);
        Mockito.when(monitoringConfiguration.getLogging()).thenReturn(logging);
        List<Monitoring> monitoringList = monitoringUtil.getMonitoringProjects();
        assertNotNull(monitoringList);
    }
}
