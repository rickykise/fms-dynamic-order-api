package fassto.fms.dynamicorderapi.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private static final String SERVER_URL = "/";
    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public OpenAPI getOpenApi() {
        return new OpenAPI()
                .info(getApiInfo())
                .components(getComponents())
                .addServersItem(getServersItem())
                .security(Collections.singletonList(new SecurityRequirement().addList(AUTHORIZATION)))
                .tags(getTags());
    }

    private Info getApiInfo() {
        return new Info()
                .title("DYNAMIC ORDER API")
                .version("1.0.0")
                .description("DYNAMIC ORDER API");
    }

    private SecurityScheme apiKeySecuritySchema() {
        return new SecurityScheme()
                .name(AUTHORIZATION) // authorisation-token
                .description("인증 키를 입력하세요.")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.APIKEY);
    }

    private Components getComponents() {
        Components components = new Components()
                .addSecuritySchemes(AUTHORIZATION, apiKeySecuritySchema());

        return components;
    }

    private Server getServersItem() {
        return new Server().url(SERVER_URL);
    }

    private List<Tag> getTags() {
        return Arrays.asList(
                new Tag().name(SwaggerTags.CONFIG).description("고객사 CONFIG")
        );
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SwaggerTags {
        public static final String CONFIG = "고객사 CONFIG";
    }
}
