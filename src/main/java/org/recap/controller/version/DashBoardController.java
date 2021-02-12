package org.recap.controller.version;

import org.recap.controller.RecapController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dinakarn on 27/01/21.
 */
@RestController
@RequestMapping("/validation")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class DashBoardController extends RecapController {

    private static final Logger logger = LoggerFactory.getLogger(DashBoardController.class);

    @Value("${version.number}")
    private String versionNumberService;

    /**
     * Gets the version number for scsb application which is configured in external application properties file.
     *
     * @return the version number service
     */
    @GetMapping("/getVersionNumberService")
    public String getVersionNumberService() {
        return versionNumberService;
    }

    /**
     * Sets version number service.
     *
     * @param versionNumberService the version number service
     */
    public void setVersionNumberService(String versionNumberService) {
        this.versionNumberService = versionNumberService;
    }
}
