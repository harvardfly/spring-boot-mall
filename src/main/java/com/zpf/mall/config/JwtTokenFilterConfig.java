package com.zpf.mall.config;

import com.zpf.mall.filter.JwtTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：jwt token过滤器的配置 它加载JwtTokenFilter，并指定需要加过滤器的url
 */
@Configuration
public class JwtTokenFilterConfig {

    @Bean
    public JwtTokenFilter tokenFilter() {
        return new JwtTokenFilter();
    }

    @Bean(name = "tokenFilterConf")
    public FilterRegistrationBean jwtTokenFilterConfig() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(tokenFilter());
        filterRegistrationBean.addUrlPatterns("/product/*");
        filterRegistrationBean.setName("tokenFilterConf");
        return filterRegistrationBean;
    }
}
