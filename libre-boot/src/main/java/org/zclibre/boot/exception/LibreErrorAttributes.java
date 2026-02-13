package org.zclibre.boot.exception;

import com.google.common.collect.Maps;
import org.zclibre.toolkit.core.StringUtil;
import org.zclibre.toolkit.result.R;
import org.zclibre.toolkit.result.ResultCode;
import jakarta.servlet.RequestDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
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
		// 请求地址
		String requestUrl = this.getAttr(webRequest, RequestDispatcher.ERROR_REQUEST_URI);
		if (StringUtil.isBlank(requestUrl)) {
			requestUrl = this.getAttr(webRequest, RequestDispatcher.FORWARD_REQUEST_URI);
		}
		// status code
		Integer status = this.getAttr(webRequest, RequestDispatcher.ERROR_STATUS_CODE);
		// error
		Throwable error = getError(webRequest);
		log.error("URL:{} error status:{}", requestUrl, status, error);
		R<Object> result;
		if (error instanceof BusinessException serviceException) {
			result = serviceException.getResult();
			result = Optional.ofNullable(result).orElse(R.fail(ResultCode.FAILURE));
		}
		else {
			result = R.fail(ResultCode.FAILURE, "System error status:" + status);
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put("code", result.getCode());
		map.put("msg", result.getMsg());
		map.put("success", result.isSuccess());
		map.put("data", result.getData());
		return map;

	}

	private <T> T getAttr(WebRequest webRequest, String name) {
		return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}

}
