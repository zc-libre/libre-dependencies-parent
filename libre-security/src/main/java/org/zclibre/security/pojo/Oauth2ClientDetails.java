package org.zclibre.security.pojo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 客户端信息
 * </p>
 *
 * @author lengleng
 * @since 2019/2/1
 */
@Data
public class Oauth2ClientDetails implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Long id;

	/**
	 * 客户端ID
	 */
	private String clientId;

	/**
	 * 客户端密钥
	 */
	private String clientSecret;

	/**
	 * 资源ID
	 */
	private String resourceIds;

	/**
	 * 作用域
	 */
	@NotBlank(message = "scope 不能为空")
	private String scope;

	/**
	 * 授权方式[A,B,C]
	 */
	private String authorizedGrantTypes;

	/**
	 * 回调地址
	 */
	private String webServerRedirectUri;

	/**
	 * 权限
	 */
	private String authorities;

	/**
	 * 请求令牌有效时间
	 */
	private Integer accessTokenValidity;

	/**
	 * 刷新令牌有效时间
	 */
	private Integer refreshTokenValidity;

	/**
	 * 扩展信息
	 */
	private String additionalInformation;

	/**
	 * 是否自动放行
	 */
	private String autoapprove;

}
