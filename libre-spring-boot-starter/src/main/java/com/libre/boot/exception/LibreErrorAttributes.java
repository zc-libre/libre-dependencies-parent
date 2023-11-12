package com.libre.boot.exception;

import com.google.common.collect.Maps;
import com.libre.toolkit.result.R;
import com.libre.toolkit.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Optional;

/**
 * @author zhao.cheng
 * @date 2021/4/19 13:48
 */
@Slf4j
public class LibreErrorAttributes extends DefaultErrorAttributes {

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
		String requestUrl = this.getAttr(webRequest, "javax.servlet.error.request_uri");
		Integer status = this.getAttr(webRequest, "javax.servlet.error.status_code");
		Throwable error = getError(webRequest);
		log.error("URL:{} error status:{}", requestUrl, status, error);
		R<Object> result;
		if (error instanceof BusinessException) {
			result = ((BusinessException) error).getResult();
			result = Optional.ofNullable(result).orElse(R.fail(ResultCode.FAILURE));
		}
		else {
			result = R.fail(ResultCode.FAILURE, "System error status:" + status);
		}

		Map<String,  Object> map = Maps.newHashMap();
		map.put("code", result.getCode());
		map.put("msg", result.getMsg());
		map.put("success", result.isSuccess());
		map.put("data", result.getData());
		return map;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private <T> T getAttr(WebRequest webRequest, String name) {
		return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

}
