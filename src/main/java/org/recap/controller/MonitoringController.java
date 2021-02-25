package org.recap.controller;

import org.recap.RecapConstants;
import org.recap.security.UserManagementService;
import org.recap.util.MonitoringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class MonitoringController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);

    @Value("${monitoring.embed.ui.url}")
    private String scsbURL;

    @Value("${monitoring.embed.docker.url}")
    private String dockerURL;

    @Value("${monitoring.embed.aws.url}")
    private String awsURL;

    @Value("${logging.ui.url}")
    private String loggingURL;

    @Value("${logging.embed.ui.url}")
    private String embedLogURL;


    @Autowired
    private MonitoringUtil monitoringUtil;

    /**
     * Display All Monitoring url's
     *
     * @return the boolean
     */
    @GetMapping("/monitoring")
    public boolean monitoring(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_MONITORING_URL);
        if (authenticated) {
            return true;
        }
        return UserManagementService.unAuthorizedUser(session, "Monitoring", logger);
    }

    @GetMapping("/logging")
    public boolean logging(HttpServletRequest request) {
        boolean authenticated = getUserAuthUtil().isAuthenticated(request, RecapConstants.SCSB_SHIRO_LOGGING_URL);
        HttpSession session = request.getSession(false);
        if (authenticated) {
            return true;
        }
        return UserManagementService.unAuthorizedUser(session, "Logging", logger);
    }
    @GetMapping("/properties")
    public Map<String,String> properties(){
        logger.info("monitoring properties are loaded");
        Map<String,String> prop = new HashMap<>();
        prop.put("scsbURL",scsbURL);
        prop.put("dockerURL",dockerURL);
        prop.put("awsURL",awsURL);
        prop.put("loggingURL",loggingURL);
        prop.put("embedLogURL",embedLogURL);
        return prop;
    }
}