/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.libre.security.service;

import com.libre.security.pojo.Oauth2ClientDetails;

import java.util.List;

/**
 * @author lengleng
 * @date 2020/12/05
 */
public interface OAuth2ClientDetailsService {

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
