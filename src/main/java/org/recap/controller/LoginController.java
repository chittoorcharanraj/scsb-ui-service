package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.recap.PropertyKeyConstants;
import org.recap.ScsbCommonConstants;
import org.recap.ScsbConstants;
import org.recap.repository.jpa.UserDetailsRepository;
import org.recap.security.UserInstitutionCache;
import org.recap.util.HelperUtil;
import org.recap.util.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;


/**
 * Created by dinakar on 23/12/20.
 */
@Slf4j
@Controller
public class LoginController extends AbstractController {





    @Autowired
    private UserInstitutionCache userInstitutionCache;

    @Autowired
    private PropertyUtil propertyUtil;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    /**
     * Return either login or search view. Returns search view if user authenticated. If not it will return login view.
     *
     * @param request the request
     * @return the string
     */
    @GetMapping(value = "/")
    public String loginScreen(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (null != auth && !HelperUtil.isAnonymousUser(auth)) {
            return ScsbConstants.REDIRECT_SEARCH;
        }
        return ScsbConstants.FORWARD_INDEX;
    }

    /**
     * Return home view.
     *
     * @param request the request
     * @return the string
     */
    @GetMapping(value = "/home")
    public String home(HttpServletRequest request) {
        return ScsbConstants.FORWARD_INDEX;
    }

    /**
     * Perform the SCSB authentication and authorization after user authenticated from partners IMS
     *
     * @param request the request
     * @return the view name
     */
    @GetMapping(value = "/login-scsb")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = processSessionFixation(request);
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String institutionFromRequest = request.getParameter("institution");
            String authType = propertyUtil.getPropertyByInstitutionAndKey(
                    institutionFromRequest, PropertyKeyConstants.ILS.ILS_AUTH_TYPE);

            if (StringUtils.equals(authType, ScsbConstants.AUTH_TYPE_OAUTH)) {
//                if (auth == null || HelperUtil.isAnonymousUser(auth)) {
//                    return "redirect:/oauth2/authorization/nypl";
//                }
                if (auth instanceof OAuth2AuthenticationToken oauth2Token) {
                    OAuth2User principal = oauth2Token.getPrincipal();
                    Map<String, Object> attrs = principal.getAttributes();

                    String username = firstNonBlank(
                            asString(attrs.get("preferred_username")),
                            asString(attrs.get("sub")),
                            asString(attrs.get("email")),
                            asString(attrs.get("upn")),
                            auth.getName()
                    );

                    if (StringUtils.isNotBlank(username)) {
                        Cookie cookieUserName = new Cookie(ScsbConstants.USER_NAME, username);
                        cookieUserName.setHttpOnly(true);
                        cookieUserName.setSecure(true);
                        HelperUtil.setCookieProperties(cookieUserName);
                        response.addCookie(cookieUserName);
                        UsernamePasswordToken token = new UsernamePasswordToken(
                                username + ScsbConstants.TOKEN_SPLITER + institutionFromRequest,
                                "", true
                        );

                        Map<String, Object> resultMap = getUserAuthUtil().doAuthentication(token);

                        if (userHasRoles(resultMap)) {
                            if (!(Boolean) resultMap.get(ScsbConstants.IS_USER_AUTHENTICATED)) {
                                String errorMessage = (String) resultMap.get(ScsbConstants.USER_AUTH_ERRORMSG);
                                log.error("User: {}, {} {}", token.getUsername(), ScsbCommonConstants.LOG_ERROR, errorMessage);
                                return ScsbConstants.REDIRECT_USER;
                            }
                        } else {
                            return ScsbConstants.REDIRECT_USER;
                        }

                        setSessionValues(session, resultMap, token);
                        return ScsbConstants.REDIRECT_SEARCH;
                    } else {
                        log.error("OAuth2 login succeeded but username not found in NYPL attributes.");
                        return ScsbConstants.REDIRECT_USER;
                    }
                }
            }
            return ScsbConstants.REDIRECT_USER;

        } catch (Exception exception) {
            log.error(ScsbCommonConstants.LOG_ERROR, exception);
            log.error("Exception occurred in authentication : {}", exception.getLocalizedMessage());
            return ScsbConstants.REDIRECT_HOME;
        }
    }
    private boolean userHasRoles(Map<String, Object> resultMap) {
        return (Boolean) resultMap.get(ScsbConstants.SEARCH_PRIVILEGE);
    }

    private HttpSession processSessionFixation(HttpServletRequest request) {

        String requestedSessionId = request.getSession().getId();

        String institutionCode = userInstitutionCache.getInstitutionForRequestSessionId(requestedSessionId);

        userInstitutionCache.removeSessionId(requestedSessionId);

        request.getSession(false).invalidate();
        HttpSession session = request.getSession(true);

        userInstitutionCache.addRequestSessionId(session.getId(), institutionCode);

        return session;
    }


    /**
     *
     * @param request HttpServletRequest
     * @return return redirect URL
     */
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        log.info("Subject Logged out");
        HttpSession session = null;
        try {
            session = request.getSession(false);
            getUserAuthUtil().authorizedUser(ScsbConstants.SCSB_SHIRO_LOGOUT_URL, (UsernamePasswordToken) session.getAttribute(ScsbConstants.USER_TOKEN));
        } finally {
            if (session != null) {
                session.invalidate();
            }
        }
        return ScsbConstants.REDIRECT_HOME;
    }

    private void setValuesInSession(HttpSession session, Map<String, Object> authMap) {
        session.setAttribute(ScsbConstants.USER_NAME, authMap.get(ScsbConstants.USER_NAME));
        session.setAttribute(ScsbConstants.USER_DESC, userDetailsRepository.findByLoginId(authMap.get(ScsbConstants.USER_NAME).toString()).getUserDescription());
        session.setAttribute(ScsbConstants.USER_ID, authMap.get(ScsbConstants.USER_ID));
        session.setAttribute(ScsbConstants.USER_INSTITUTION, authMap.get(ScsbConstants.USER_INSTITUTION));
        session.setAttribute(ScsbConstants.SUPER_ADMIN_USER, authMap.get(ScsbConstants.SUPER_ADMIN_USER));
        session.setAttribute(ScsbConstants.USER_ADMINISTRATOR, authMap.get(ScsbConstants.USER_ADMINISTRATOR));
        session.setAttribute(ScsbConstants.REPOSITORY, authMap.get(ScsbConstants.REPOSITORY));
        session.setAttribute(ScsbConstants.REQUEST_PRIVILEGE, authMap.get(ScsbConstants.REQUEST_PRIVILEGE));
        session.setAttribute(ScsbConstants.COLLECTION_PRIVILEGE, authMap.get(ScsbConstants.COLLECTION_PRIVILEGE));
        session.setAttribute(ScsbConstants.REPORTS_PRIVILEGE, authMap.get(ScsbConstants.REPORTS_PRIVILEGE));
        session.setAttribute(ScsbConstants.SEARCH_PRIVILEGE, authMap.get(ScsbConstants.SEARCH_PRIVILEGE));
        session.setAttribute(ScsbConstants.USER_ROLE_PRIVILEGE, authMap.get(ScsbConstants.USER_ROLE_PRIVILEGE));
        session.setAttribute(ScsbConstants.REQUEST_ALL_PRIVILEGE, authMap.get(ScsbConstants.REQUEST_ALL_PRIVILEGE));
        session.setAttribute(ScsbConstants.REQUEST_ITEM_PRIVILEGE, authMap.get(ScsbConstants.REQUEST_ITEM_PRIVILEGE));
        session.setAttribute(ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE, authMap.get(ScsbConstants.BARCODE_RESTRICTED_PRIVILEGE));
        session.setAttribute(ScsbConstants.DEACCESSION_PRIVILEGE, authMap.get(ScsbConstants.DEACCESSION_PRIVILEGE));
        session.setAttribute(ScsbCommonConstants.BULK_REQUEST_PRIVILEGE, authMap.get(ScsbCommonConstants.BULK_REQUEST_PRIVILEGE));
        session.setAttribute(ScsbCommonConstants.RESUBMIT_REQUEST_PRIVILEGE, authMap.get(ScsbCommonConstants.RESUBMIT_REQUEST_PRIVILEGE));
        session.setAttribute(ScsbConstants.MONITORING, authMap.get(ScsbConstants.MONITORING));
        session.setAttribute(ScsbConstants.LOGGING, authMap.get(ScsbConstants.LOGGING));
        session.setAttribute(ScsbConstants.REQUESTLOG, authMap.get(ScsbConstants.REQUESTLOG));
        session.setAttribute(ScsbConstants.DATA_EXPORT, authMap.get(ScsbConstants.DATA_EXPORT));
        Object isSuperAdmin = session.getAttribute(ScsbConstants.SUPER_ADMIN_USER);
        if ((boolean) isSuperAdmin) {
            session.setAttribute(ScsbConstants.ROLE_FOR_SUPER_ADMIN, true);
        } else {
            session.setAttribute(ScsbConstants.ROLE_FOR_SUPER_ADMIN, false);
        }
    }

    private void setSessionValues(HttpSession session, Map<String, Object> resultMap, UsernamePasswordToken token) {
        session.setAttribute(ScsbConstants.USER_TOKEN, token);
        session.setAttribute(ScsbConstants.USER_AUTH, resultMap);
        setValuesInSession(session, resultMap);
    }
    private String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private String firstNonBlank(String... vals) {
        for (String v : vals) if (StringUtils.isNotBlank(v)) return v;
        return null;
    }
}