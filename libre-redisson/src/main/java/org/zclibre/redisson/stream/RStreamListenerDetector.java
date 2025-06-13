package org.zclibre.redisson.stream;

import org.zclibre.redisson.common.RModule;
import org.zclibre.redisson.common.RedisNameResolver;
import org.zclibre.toolkit.core.CharPool;
import org.zclibre.toolkit.core.INetUtil;
import org.zclibre.toolkit.core.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.ElementsSubscribeService;
import org.redisson.Redisson;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamGroup;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.redisson.client.RedisException;
import org.redisson.connection.ConnectionManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Redisson 监听器
 *
 * @author Libre
 */
@Slf4j
public class RStreamListenerDetector implements BeanPostProcessor {

	private final RedissonClient client;

	private final RedisNameResolver resolver;

	private final String groupId;

	private final String hostIp;

	private final ConnectionManager connectionManager;

	public RStreamListenerDetector(RedissonClient client, RedisNameResolver resolver, String groupId) {
		this.client = client;
		this.resolver = resolver;
		this.groupId = groupId;
		this.hostIp = INetUtil.getHostIp();
		this.connectionManager = ((Redisson) client).getCommandExecutor().getConnectionManager();
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			RStreamListener listener = AnnotationUtils.findAnnotation(method, RStreamListener.class);
			if (listener != null) {
				String name = listener.value();
				Assert.hasText(name, "@RStreamListener value must not be empty.");
				log.info("Found @RStreamListener on bean:{} method:{}", beanName, method);
				// 校验 method，method 入参数大于等于1
				int paramCount = method.getParameterCount();
				if (paramCount > 1) {
					throw new IllegalArgumentException(
							"@RStreamListener on method " + method + " parameter count must less or equal to 1.");
				}
				// 处理名称，统一前缀，避免冲突
				String streamName = resolver.resolve(RModule.Stream, name);
				RStream<Object, Object> stream = client.getStream(streamName);
				// 创建 group
				createGroupIfNeed(stream, groupId);
				addListener(bean, method, paramCount, stream, groupId);
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

	private static void createGroupIfNeed(RStream<Object, Object> stream, final String groupName) {
		boolean created = false;
		// list group 没有时会报错：RedisException: ERR no such key.
		try {
			created = stream.listGroups().stream().map(StreamGroup::getName).anyMatch(name -> name.equals(groupName));
		}
		catch (RedisException e) {
			log.warn(e.getMessage());
		}
		// 创建 group，多次创建会报错：BUSYGROUP Consumer Group name already exists.
		if (!created) {
			try {
				stream.createGroup(StreamCreateGroupArgs.name(groupName));
			}
			catch (RedisException e) {
				log.warn(e.getMessage());
			}
		}
	}

	private void addListener(Object bean, Method method, int paramCount, RStream<Object, Object> stream,
			String groupId) {
		// 消费者应该为 groupId + ip + pid 比较合适
		String consumerName = groupId + CharPool.COLON + hostIp + CharPool.AT + RuntimeUtil.getPId();
		ElementsSubscribeService subscribeService = connectionManager.getServiceManager().getElementsSubscribeService();
		// 阻塞1秒，一次响应一条，多条时 ack 相应会有问题
		subscribeService.subscribeOnElements(
				() -> stream.readGroupAsync(groupId, consumerName, StreamReadGroupArgs.neverDelivered()),
				new RStreamConsumer(stream, groupId, bean, method, paramCount));
	}

}
