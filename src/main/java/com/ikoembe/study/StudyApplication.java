package com.ikoembe.study;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StudyApplication {
	private static final String SECURITY_SCHEME_NAME = "Bearer oAuth Token";

	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}


	/**
	 * Open API Configuration Bean
	 *
	 * @param title
	 * @param version
	 * @param description
	 * @return
	 */
	@Bean
	public OpenAPI openApiConfiguration(
			@Value("${openapi.title}") final String title,
			@Value("${openapi.version}") final String version,
			@Value("${openapi.description}") final String description
	) {
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
				.components(
						new Components()
								.addSecuritySchemes(SECURITY_SCHEME_NAME,
										new SecurityScheme()
												.name(SECURITY_SCHEME_NAME)
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				)
				.info(new Info()
						.title(title)
						.version(version)
						.description(description)
						.termsOfService("Terms of service")
				);
	}

}
