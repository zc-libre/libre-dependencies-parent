package com.libre.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: Libre
 * @Date: 2022/6/12 5:37 PM
 */
public class LibreResourceServerConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
//				.oauth2ResourceServer(
//						oauth2 -> oauth2.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
//								.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
//								.bearerTokenResolver(pigBearerTokenExtractor))
//				.headers().frameOptions().disable().and().csrf().disable();
		return http.build();
	}

}
