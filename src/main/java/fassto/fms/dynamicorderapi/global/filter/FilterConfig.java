package fassto.fms.dynamicorderapi.global.filter;

import fassto.fms.dynamicorderapi.global.config.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilter() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthorizationFilter(securityProperties));
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }
}
