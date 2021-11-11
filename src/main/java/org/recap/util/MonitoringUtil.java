package org.recap.util;

import org.recap.ScsbConstants;
import org.recap.config.MonitoringConfiguration;
import org.recap.model.search.Monitoring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonitoringUtil {

    @Autowired
    private MonitoringConfiguration monitoringConfiguration;

    /**
     * This will return all monitoring and logging related url's for all projects from properties
     * @return
     */
    public List<Monitoring> getMonitoringProjects() {
        List<Monitoring> projects = new ArrayList<>();
        projects.add(build(ScsbConstants.SCSB_UI));
        projects.add(build(ScsbConstants.SCSB_AUTH));
        projects.add(build(ScsbConstants.SCSB_GATEWAY));
        projects.add(build(ScsbConstants.SCSB_DOC));
        projects.add(build(ScsbConstants.SCSB_CIRC));
        projects.add(build(ScsbConstants.SCSB_BATCH));
        return projects;
    }

    private Monitoring build(String key) {
        Monitoring monitoring = new Monitoring();
        //monitoring.setProjectName(key);
        monitoring.setMonitoringUrl(monitoringConfiguration.getUrl().get(key));
        monitoring.setLoggingUrl(monitoringConfiguration.getLogging().getUrl().get(key));
        return monitoring;
    }
}
