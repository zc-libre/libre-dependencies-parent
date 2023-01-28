package com.libre.security.service;

import com.libre.security.pojo.OAuth2User;
import org.springframework.core.Ordered;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author lengleng
 * @date 2021/12/21
 */
public interface OAuth2UserDetailsService extends UserDetailsService, Ordered {

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	default boolean support(String clientId, String grantType) {
		return true;
	}

	/**
	 * 排序值 默认取最大的
	 * @return 排序值
	 */
	default int getOrder() {
		return Integer.MIN_VALUE;
	}


	/**
	 * 通过用户实体查询
	 * @param oAuth2User {@link OAuth2User}
	 * @return {@link UserDetails}
	 */
	default UserDetails loadUserByUser(OAuth2User oAuth2User) {
		return this.loadUserByUsername(oAuth2User.getUsername());
	}



}
