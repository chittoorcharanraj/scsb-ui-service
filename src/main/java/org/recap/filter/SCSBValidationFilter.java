package org.recap.filter;

import org.apache.http.HttpStatus;
import org.recap.ScsbConstants;
import org.recap.spring.ApplicationContextProvider;
import org.recap.util.UserAuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class SCSBValidationFilter implements Filter {
    public static final Logger LOGGER = LoggerFactory.getLogger(SCSBValidationFilter.class);

    UserAuthUtil userAuthUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        var user_authenticated = ScsbConstants.FALSE_STRING;
        try {
            HttpSession session = httpServletRequest.getSession(false);
            boolean authenticated = getUserAuthUtil().isAuthenticated(session, ScsbConstants.SCSB_SHIRO_SEARCH_URL);
            user_authenticated = (authenticated) ? ScsbConstants.TRUE_STRING : ScsbConstants.FALSE_STRING;
            httpServletResponse.setHeader(ScsbConstants.USER_AUTHENTICATED, user_authenticated);
        } catch (Exception e) {
            httpServletResponse.setHeader(ScsbConstants.USER_AUTHENTICATED, user_authenticated);
        }
        if (user_authenticated.equalsIgnoreCase(ScsbConstants.TRUE_STRING))
            chain.doFilter(request, response);
        else
            httpServletResponse.setStatus(HttpStatus.SC_OK);
    }

    public UserAuthUtil getUserAuthUtil() {
        if (userAuthUtil == null) {
            userAuthUtil = ApplicationContextProvider.getInstance().getApplicationContext().getBean(UserAuthUtil.class);
        }
        return userAuthUtil;
    }
}
