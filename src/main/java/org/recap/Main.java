package org.recap;

//import brave.sampler.Sampler;

import brave.sampler.Sampler;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.recap.filter.XSSFilter;
import org.recap.security.SessionFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.HashSet;
import java.util.Set;


/**
 * The type Main.
 */
@PropertySource("classpath:application.properties")
@SpringBootApplication(scanBasePackages = {"org.recap.controller", "org.recap.*"},exclude = {SecurityFilterAutoConfiguration.class})
public class Main {

    /**
     * The Tomcat max parameter count.
     */
    @Value("${" + PropertyKeyConstants.TOMCAT_MAX_PARAMETER_COUNT + "}")
    Integer tomcatMaxParameterCount;

    private AbstractApplicationContext applicationContext;

    private OAuth2SsoProperties oAuth2SsoProperties;

    /**
     * The Tomcat secure.
     */
    @Value("${" + PropertyKeyConstants.SERVER_SECURE + "}")
    boolean tomcatSecure;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Servlet container factory embedded servlet container factory.
     *
     * @return the embedded servlet container factory
     */
    @Bean
    public ServletWebServerFactory servletContainerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxParameterCount(tomcatMaxParameterCount);
            connector.setSecure(tomcatSecure);
        });
        return factory;
    }

    /**
     * Get filter registered bean filter registration bean.
     *
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean getFilterRegisteredBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.add("/*");
        filterRegistrationBean.setUrlPatterns(urlPatterns);
        filterRegistrationBean.setFilter(new SessionFilter());
        return filterRegistrationBean;
    }

    /**
     * Gets xss filter registered bean.
     *
     * @return the xss filter registered bean
     */
    @Bean
    public FilterRegistrationBean getXSSFilterRegisteredBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        Set<String> urlPatterns = new HashSet<>();
        urlPatterns.add("/*");
        filterRegistrationBean.setUrlPatterns(urlPatterns);
        filterRegistrationBean.setFilter(new XSSFilter());
        return filterRegistrationBean;
    }

    /**
     * Container customizer embedded servlet container customizer.
     *
     * @return the embedded servlet container customizer
     */

    @Bean
    public WebServerFactoryCustomizer containerCustomizer() {
        return new WebServerFactoryCustomizer() {
            @Override
            public void customize(WebServerFactory container) {
                if (container.getClass().isAssignableFrom(TomcatServletWebServerFactory.class)) {
                    TomcatServletWebServerFactory tomcatContainer = (TomcatServletWebServerFactory) container;
                    tomcatContainer.addContextCustomizers(new ContextSecurityCustomizer());
                }
            }
        };
    }

    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }

    private static class ContextSecurityCustomizer implements TomcatContextCustomizer {
        @Override
        public void customize(Context context) {
            SecurityConstraint constraint = new SecurityConstraint();
            SecurityCollection securityCollection = new SecurityCollection();
            securityCollection.setName("restricted_methods");
            securityCollection.addPattern("/*/**");
            securityCollection.addMethod(HttpMethod.OPTIONS.toString());
            securityCollection.addMethod(HttpMethod.HEAD.toString());
            securityCollection.addMethod(HttpMethod.PUT.toString());
            securityCollection.addMethod(HttpMethod.PATCH.toString());
            securityCollection.addMethod(HttpMethod.DELETE.toString());
            securityCollection.addMethod(HttpMethod.TRACE.toString());
            constraint.addCollection(securityCollection);
            constraint.setAuthConstraint(true);
            context.addConstraint(constraint);
        }
    }

    @Bean
    ClientRegistrationRepository getClientRegistrationRepository(){
        return new ClientRegistrationRepository() {
            @Override
            public ClientRegistration findByRegistrationId(String registrationId) {
                return null;
            }
        };
    }

    public AbstractApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    OAuth2SsoProperties getOAuth2SsoProperties() {
        return new OAuth2SsoProperties();
    }

 /*   @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }*/

}
