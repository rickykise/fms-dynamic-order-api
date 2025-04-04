package fassto.fms.dynamicorderapi.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class ProfileApplicationRunner implements ApplicationRunner {
    private final EnvironmentComponent environmentComponent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        environmentComponent.showProject();
    }
}
