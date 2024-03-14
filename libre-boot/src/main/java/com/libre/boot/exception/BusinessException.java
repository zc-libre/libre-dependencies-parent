package com.libre.boot.exception;

import com.libre.toolkit.result.IResultCode;
import com.libre.toolkit.result.R;
import org.springframework.lang.Nullable;

/**
 * 业务异常
 *
 * @author Libre
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 2359767895161832954L;

	@Nullable
	private final R<?> result;

	public BusinessException(R<?> result) {
		super(result.getMsg());
		this.result = result;
	}

	public BusinessException(IResultCode rCode) {
		this(rCode, rCode.getMessage());
	}

	public BusinessException(IResultCode rCode, String message) {
		super(message);
		this.result = R.fail(rCode, message);
	}

	public BusinessException(String message) {
		super(message);
		this.result = null;
	}

	public BusinessException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
		doFillInStackTrace();
		this.result = null;
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public <T> R<T> getResult() {
		return (R<T>) result;
	}

	/**
	 * 提高性能
	 * @return Throwable
	 */
	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

	public void doFillInStackTrace() {
		super.fillInStackTrace();
	}

}
