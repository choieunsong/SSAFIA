package s05.p12a104.mafia.config;

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * API 문서 관련 swagger2 설정 정의.
 */
@Configuration
public class SwaggerConfig {

  public static final String SECURITY_SCHEMA_NAME = "JWT";
  
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).select()
        .apis(RequestHandlerSelectors.any()).paths(PathSelectors.ant("/api/**")).build()
        .securityContexts(newArrayList(securityContext())).securitySchemes(newArrayList(apiKey()));
  }

  private ApiKey apiKey() {
    return new ApiKey(SECURITY_SCHEMA_NAME, "Authorization", "header");
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder().securityReferences(defaultAuth()).build();
  }


  private List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope =
        new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return newArrayList(new SecurityReference(SECURITY_SCHEMA_NAME, authorizationScopes));
  }

}