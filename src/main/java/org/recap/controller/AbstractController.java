package org.recap.controller;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.recap.PropertyKeyConstants;
import org.recap.service.RestHeaderService;
import org.recap.util.RequestServiceUtil;
import org.recap.util.UserAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.annotation.CheckForNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class AbstractController {

    @Value("${" + PropertyKeyConstants.SCSB_GATEWAY_URL + "}")
    private String scsbUrl;

    @Value("${" + PropertyKeyConstants.SCSB_AUTH_URL + "}")
    private String scsbShiro;

    @Autowired
    private RestHeaderService restHeaderService;

    @Autowired
    private RequestServiceUtil requestServiceUtil;

    @Autowired
    private UserAuthUtil userAuthUtil;

    /**
     * Get rest template rest template.
     *
     * @return the rest template
     */
    @CheckForNull
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    /**
     * Gets scsb shiro.
     *
     * @return the scsb shiro
     */
    public String getScsbShiro() {
        return scsbShiro;
    }

    /**
     * Gets scsb url.
     *
     * @return the scsb url
     */
    public String getScsbUrl() {
        return scsbUrl;
    }


}
