package com.libre.mqtt;

import java.util.List;

/**
 * MQTT操作接口
 *
 * <p>提供MQTT主题管理和消息发送的核心功能，包括：
 * <ul>
 *   <li>主题的订阅和取消订阅</li>
 *   <li>主题列表的查询</li>
 *   <li>MQTT消息的发送</li>
 * </ul>
 *
 * @author libre
 * @since 1.0.0
 */
public interface MqttOptions {

	/**
	 * 添加MQTT主题订阅
	 *
	 * <p>使用默认的QoS级别（通常为0）订阅指定的主题
	 *
	 * @param topic 要订阅的主题名称，不能为null或空字符串
	 * @throws IllegalArgumentException 如果topic为null或空字符串
	 */
	void addTopic(String topic);

	/**
	 * 添加MQTT主题订阅并指定QoS级别
	 *
	 * <p>订阅指定的主题并设置服务质量等级
	 *
	 * @param topic 要订阅的主题名称，不能为null或空字符串
	 * @param qos 服务质量等级，有效值为0、1、2
	 *            <ul>
	 *              <li>0 - 最多一次传递（At most once）</li>
	 *              <li>1 - 至少一次传递（At least once）</li>
	 *              <li>2 - 恰好一次传递（Exactly once）</li>
	 *            </ul>
	 * @throws IllegalArgumentException 如果topic为null或空字符串，或qos不在有效范围内
	 */
	void addTopic(String topic, int qos);

	/**
	 * 移除MQTT主题订阅
	 *
	 * <p>取消订阅指定的主题，如果主题不存在则忽略
	 *
	 * @param topic 要取消订阅的主题名称，不能为null或空字符串
	 * @throws IllegalArgumentException 如果topic为null或空字符串
	 */
	void removeTopic(String topic);

	/**
	 * 获取当前已订阅的主题列表
	 *
	 * <p>返回当前实例已订阅的所有主题名称列表
	 *
	 * @return 已订阅的主题名称列表，如果没有订阅任何主题则返回空列表，不会返回null
	 */
	List<String> listTopics();

	/**
	 * 转换并发送MQTT消息
	 *
	 * <p>将消息对象转换为MQTT协议格式并发送到指定的主题
	 *
	 * @param mqttMessage 要发送的MQTT消息对象，包含主题、负载和其他元数据
	 * @throws IllegalArgumentException 如果mqttMessage为null
	 * @throws RuntimeException 如果消息发送失败
	 */
	void convertAndSend(MqttMessage mqttMessage);

}
