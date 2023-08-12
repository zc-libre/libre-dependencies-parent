package com.libre.security.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author lengleng
 * @date 2022-06-04
 * <p>
 * 资源服务器认证授权配置
 */
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class ResourceServerConfiguration {

	protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

	private final PermitAllUrlProperties permitAllUrl;

	private final OAuth2BearerTokenExtractor OAuth2BearerTokenExtractor;

	private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				.requestMatchers(permitAllUrl.getUrls().toArray(new String[0]))
				.permitAll()
				.anyRequest()
				.authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
				.authenticationEntryPoint(resourceAuthExceptionEntryPoint).bearerTokenResolver(OAuth2BearerTokenExtractor))
			.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

}
