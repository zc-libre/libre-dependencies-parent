package org.zclibre.toolkit.constant;

/**
 * @author zhao.cheng
 * @Date 2021/2/18
 */
public interface LibreConstants {

	String SPRING_APP_NAME_KEY = "spring.application.name";

	String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";

	/**
	 * contentType
	 */
	String CONTENT_TYPE_NAME = "Content-type";

	/**
	 * 顶级父节点id
	 */
	Long TOP_PARENT_ID = 0L;

	/**
	 * 默认为空消息
	 */
	String DEFAULT_NULL_MESSAGE = "暂无承载数据";

	/**
	 * 默认成功消息
	 */
	String DEFAULT_SUCCESS_MESSAGE = "操作成功";

	/**
	 * 默认失败消息
	 */
	String DEFAULT_FAILURE_MESSAGE = "操作失败";

	/**
	 * 默认未授权消息
	 */
	String DEFAULT_UNAUTHORIZED_MESSAGE = "签名认证失败";

}
