package fassto.fms.dynamicorderapi.global.feign;

import fassto.fms.dynamicorderapi.global.request.SlackMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack", url = "${fms.slack.api.url}")
public interface SlackFeignClient {

    @PostMapping("/services/T01HGS2J2QJ/B08L6215RHC/xBrCw6ugVXUxXmfTwH4sjN3R")
    void sendMessage(@RequestBody SlackMessage payload);
}
