package by.panic.entomus.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public GroupedOpenApi appApi() {
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("CryptoSystem API")
                        .description("API taking its roots from cryptomus api")
                        .version("v1.0.0"));
//                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
    }
}
