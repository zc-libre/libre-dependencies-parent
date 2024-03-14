package com.libre.security.service;

import com.libre.security.pojo.Oauth2ClientDetails;

import java.util.List;

public interface Oauth2ClientDetailsService {

	/**
	 * 通过clientId 查询客户端信息
	 * @param clientId 用户名
	 * @return R
	 */
	Oauth2ClientDetails getClientDetailsById(String clientId);

	/**
	 * 查询全部客户端
	 * @return R
	 */
	List<Oauth2ClientDetails> listClientDetails();

}
