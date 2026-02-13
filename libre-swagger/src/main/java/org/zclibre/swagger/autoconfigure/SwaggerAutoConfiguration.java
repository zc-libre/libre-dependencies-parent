package org.zclibre.swagger.autoconfigure;

import java.util.Optional;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

/**
 * @author zhao.cheng
 * @date 2021/3/3 16:03
 */
@AutoConfiguration
@ConditionalOnClass(OpenAPI.class)
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnMissingClass("org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
@ConditionalOnProperty(prefix = "libre.swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public OpenAPI customOpenAPI(Environment environment, SwaggerProperties properties) {
		String appName = environment.getProperty("spring.application.name");
		OpenAPI openAPI = new OpenAPI().info(apiInfo(appName, properties));

		Components components = new Components();
		boolean hasSecurityScheme = false;

		// 如果开启 apiKey 认证
		if (Boolean.TRUE.equals(properties.getAuthorization().getEnabled())) {
			SwaggerProperties.Authorization authorization = properties.getAuthorization();
			components.addSecuritySchemes(authorization.getName(),
					new SecurityScheme().type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.HEADER)
						.name(authorization.getKeyName()));
			openAPI.addSecurityItem(new SecurityRequirement().addList(authorization.getName()));
			hasSecurityScheme = true;
		}

		// 如果开启 oauth2 认证
		if (Boolean.TRUE.equals(properties.getOauth2().getEnabled())) {
			SwaggerProperties.Oauth2 oauth2 = properties.getOauth2();
			SecurityScheme oauth2Scheme = new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
				.flows(buildOAuthFlows(oauth2));
			components.addSecuritySchemes(oauth2.getName(), oauth2Scheme);

			SecurityRequirement securityRequirement = new SecurityRequirement();
			securityRequirement.addList(oauth2.getName(),
					oauth2.getScopes().stream().map(SwaggerProperties.Scope::getScope).toList());
			openAPI.addSecurityItem(securityRequirement);
			hasSecurityScheme = true;
		}

		if (hasSecurityScheme) {
			openAPI.components(components);
		}

		return openAPI;
	}

	@Bean
	public OperationCustomizer libreGlobalHeaderCustomizer(SwaggerProperties properties) {
		return (operation, handlerMethod) -> {
			for (SwaggerProperties.Header header : properties.getHeaders()) {
				operation.addParametersItem(new Parameter().in("header")
					.name(header.getName())
					.description(header.getDescription())
					.required(header.isRequired()));
			}
			return operation;
		};
	}

	private OAuthFlows buildOAuthFlows(SwaggerProperties.Oauth2 oauth2) {
		Scopes scopes = new Scopes();
		oauth2.getScopes().forEach(s -> scopes.addString(s.getScope(), s.getDescription()));

		OAuthFlows flows = new OAuthFlows();
		switch (oauth2.getGrantType()) {
			case AUTHORIZATION_CODE ->
				flows.authorizationCode(new OAuthFlow().authorizationUrl(oauth2.getAuthorizeUrl())
					.tokenUrl(oauth2.getTokenUrl())
					.scopes(scopes));
			case CLIENT_CREDENTIALS ->
				flows.clientCredentials(new OAuthFlow().tokenUrl(oauth2.getTokenUrl()).scopes(scopes));
			case IMPLICIT -> flows.implicit(new OAuthFlow().authorizationUrl(oauth2.getAuthorizeUrl()).scopes(scopes));
			case PASSWORD -> flows.password(new OAuthFlow().tokenUrl(oauth2.getTokenUrl()).scopes(scopes));
		}
		return flows;
	}

	private Info apiInfo(@Nullable String appName, SwaggerProperties properties) {
		String defaultName = (appName == null ? "" : appName) + "服务";
		String title = Optional.ofNullable(properties.getTitle()).orElse(defaultName);
		String description = Optional.ofNullable(properties.getDescription()).orElse(defaultName);
		return new Info().title(title)
			.description(description)
			.version(properties.getVersion())
			.contact(new Contact().name(properties.getContactUser())
				.url(properties.getContactUrl())
				.email(properties.getContactEmail()));
	}

}
