package com.infra.gummadibuilt.common.correlation;

import com.google.common.collect.Lists;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

@Configuration
public class CorrelationIdConfig {

    @Bean
    public FilterRegistrationBean correlationFilterRegistration() {
        FilterRegistrationBean bean = new FilterRegistrationBean(
                new CorrelationIdFilter()
        );
        bean.setUrlPatterns(Lists.newArrayList("/*"));
        bean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}