package org.zclibre.boot.xss;

/**
 * xss 异常，校验模式抛出
 *
 * @author L.cm
 */
public interface XssException {

	/**
	 * 属性名，目前仅 body json 支持，form 表单不支持
	 * @return 属性名
	 */
	default String getName() {
		return null;
	}

	/**
	 * 输入的数据
	 * @return 数据
	 */
	String getInput();

	/**
	 * 获取异常的消息
	 * @return 消息
	 */
	String getMessage();

}
