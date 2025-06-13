package com.libre.security.service;

import com.libre.security.constant.SecurityConstants;
import com.libre.security.pojo.Oauth2ClientDetails;
import org.zclibre.toolkit.core.StringPool;
import org.zclibre.toolkit.core.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * 查询客户端相关信息实现
 *
 * @author lengleng
 * @date 2022/5/29
 */
@RequiredArgsConstructor
public class Oauth2RegisteredClientRepository implements RegisteredClientRepository {

	/**
	 * 刷新令牌有效期默认 30 天
	 */
	private final static int refreshTokenValiditySeconds = 60 * 60 * 24 * 30;

	/**
	 * 请求令牌有效期默认 12 小时
	 */
	private final static int accessTokenValiditySeconds = 60 * 60 * 12;

	private final Oauth2ClientDetailsService clientDetailsService;

	/**
	 * Saves the registered client.
	 *
	 * <p>
	 * IMPORTANT: Sensitive information should be encoded externally from the
	 * implementation, e.g. {@link RegisteredClient#getClientSecret()}
	 * @param registeredClient the {@link RegisteredClient}
	 */
	@Override
	public void save(RegisteredClient registeredClient) {
	}

	/**
	 * Returns the registered client identified by the provided {@code id}, or
	 * {@code null} if not found.
	 * @param id the registration identifier
	 * @return the {@link RegisteredClient} if found, otherwise {@code null}
	 */
	@Override
	public RegisteredClient findById(String id) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the registered client identified by the provided {@code clientId}, or
	 * {@code null} if not found.
	 * @param clientId the client identifier
	 * @return the {@link RegisteredClient} if found, otherwise {@code null}
	 */

	/**
	 * 重写原生方法支持redis缓存
	 * @param clientId
	 * @return
	 */
	@Override
	@SneakyThrows
	@Cacheable(value = "oauth2_client", key = "#clientId", unless = "#result == null")
	public RegisteredClient findByClientId(String clientId) {

		Oauth2ClientDetails clientDetails = Optional.ofNullable(clientDetailsService.getClientDetailsById(clientId))
			.orElseThrow(() -> new OAuth2AuthorizationCodeRequestAuthenticationException(
					new OAuth2Error("客户端查询异常，请检查数据库链接"), null));

		RegisteredClient.Builder builder = RegisteredClient.withId(clientDetails.getClientId())
			.clientId(clientDetails.getClientId())
			.clientSecret(SecurityConstants.NOOP + clientDetails.getClientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

		String authorizedGrantTypeStr = clientDetails.getAuthorizedGrantTypes();
		Set<String> authorizedGrantTypes = StringUtils.commaDelimitedListToSet(authorizedGrantTypeStr);
		for (String authorizedGrantType : authorizedGrantTypes) {
			builder.authorizationGrantType(new AuthorizationGrantType(authorizedGrantType));

		}
		// 回调地址
		Optional.ofNullable(clientDetails.getWebServerRedirectUri())
			.ifPresent(redirectUri -> Arrays.stream(redirectUri.split(StringPool.COMMA))
				.filter(StringUtil::isNotBlank)
				.forEach(builder::redirectUri));

		// scope
		Optional.ofNullable(clientDetails.getScope())
			.ifPresent(scope -> Arrays.stream(scope.split(StringPool.COMMA))
				.filter(StringUtil::isNotBlank)
				.forEach(builder::scope));

		return builder
			.tokenSettings(TokenSettings.builder()
				.accessTokenFormat(OAuth2TokenFormat.REFERENCE)
				.accessTokenTimeToLive(Duration.ofSeconds(
						Optional.ofNullable(clientDetails.getAccessTokenValidity()).orElse(accessTokenValiditySeconds)))
				.refreshTokenTimeToLive(Duration.ofSeconds(Optional.ofNullable(clientDetails.getRefreshTokenValidity())
					.orElse(refreshTokenValiditySeconds)))
				.build())
			.clientSettings(ClientSettings.builder()
				.requireAuthorizationConsent(!BooleanUtils.toBoolean(clientDetails.getAutoapprove()))
				.build())
			.build();
	}

}
