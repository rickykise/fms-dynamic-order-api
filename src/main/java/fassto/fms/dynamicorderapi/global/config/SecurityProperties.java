package fassto.fms.dynamicorderapi.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "security.authorization")
public class SecurityProperties {

    private String header;
    private String expectedValue;
}
