package fassto.fms.dynamicorderapi.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients("fassto.fms.dynamicorderapi.global.feign")
public class ConnectivityFeignConfig {
}
