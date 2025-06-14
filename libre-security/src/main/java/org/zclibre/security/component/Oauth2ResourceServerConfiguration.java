package org.zclibre.security.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * @author lengleng
 * @date 2022-06-04
 *
 * 资源服务器认证授权配置
 */
@Slf4j
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class Oauth2ResourceServerConfiguration {

	protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

	private final PermitAllUrlProperties permitAllUrl;

	private final Oauth2BearerTokenExtractor oauth2BearerTokenExtractor;

	private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		RequestMatcher[] permitAllMatchers = permitAllUrl.getUrls() // List<String>
			.stream()
			.map(url -> PathPatternRequestMatcher.withDefaults().matcher(url))
			.toArray(RequestMatcher[]::new);

		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(permitAllMatchers)
			.permitAll()
			.anyRequest()
			.authenticated())
			.oauth2ResourceServer(
					oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
						.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
						.bearerTokenResolver(oauth2BearerTokenExtractor))
			.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

}
