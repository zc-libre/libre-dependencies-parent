package com.libre.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.libre.toolkit.result.R;
import com.libre.toolkit.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author lengleng
 * @date 2019/2/1
 *
 * 客户端异常处理 AuthenticationException 不同细化异常处理
 */
@RequiredArgsConstructor
public class ResourceAuthExceptionEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	@SneakyThrows
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) {

		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		R<String> result = new R<>();
		result.setCode(ResultCode.CLIENT_UN_AUTHORIZED.getCode());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		if (authException != null) {
			result.setMsg(ResultCode.CLIENT_UN_AUTHORIZED.getMessage());
			result.setData(authException.getMessage());
		}

		// 针对令牌过期返回特殊的 424
		if (authException instanceof InvalidBearerTokenException) {
			response.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
			result.setMsg("token expire");
		}

		PrintWriter printWriter = response.getWriter();
		printWriter.append(objectMapper.writeValueAsString(result));
	}

}
