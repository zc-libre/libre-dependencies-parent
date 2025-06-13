package com.libre.mqtt;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * Retry template specifically designed for MQTT operations.
 *
 * <p>
 * This class provides a configured retry mechanism for MQTT connection and messaging
 * operations. It uses exponential backoff strategy to handle transient failures
 * gracefully and avoid overwhelming the MQTT broker.
 *
 * <p>
 * Features:
 * <ul>
 * <li>Exponential backoff with configurable multiplier</li>
 * <li>Maximum retry attempts configuration</li>
 * <li>Specific exception handling for MQTT-related errors</li>
 * <li>Comprehensive logging of retry attempts</li>
 * </ul>
 *
 * <p>
 * Example usage: <pre>
 * MqttRetryTemplate retryTemplate = new MqttRetryTemplate(
 *     3,                              // max attempts
 *     Duration.ofSeconds(1),          // initial delay
 *     Duration.ofSeconds(30),         // max delay
 *     2.0                             // multiplier
 * );
 *
 * String result = retryTemplate.execute(context -> {
 *     // MQTT operation that might fail
 *     return mqttClient.connect();
 * });
 * </pre>
 *
 * @author libre-mqtt
 * @since 1.0.0
 */
@Slf4j
@Getter
public class MqttRetryTemplate {

	private final RetryTemplate retryTemplate;

	private final int maxAttempts;

	private final Duration initialDelay;

	private final Duration maxDelay;

	private final double multiplier;

	/**
	 * Creates a new MQTT retry template with the specified configuration.
	 * @param maxAttempts the maximum number of retry attempts
	 * @param initialDelay the initial delay between retries
	 * @param maxDelay the maximum delay between retries
	 * @param multiplier the exponential backoff multiplier
	 */
	public MqttRetryTemplate(int maxAttempts, Duration initialDelay, Duration maxDelay, double multiplier) {
		Assert.isTrue(maxAttempts > 0, "Max attempts must be greater than 0");
		Assert.notNull(initialDelay, "Initial delay must not be null");
		Assert.notNull(maxDelay, "Max delay must not be null");
		Assert.isTrue(multiplier >= 1.0, "Multiplier must be >= 1.0");

		this.maxAttempts = maxAttempts;
		this.initialDelay = initialDelay;
		this.maxDelay = maxDelay;
		this.multiplier = multiplier;
		this.retryTemplate = createRetryTemplate();
	}

	/**
	 * Creates a retry template with default MQTT-friendly settings.
	 * @return a new MqttRetryTemplate with default configuration
	 */
	public static MqttRetryTemplate defaultTemplate() {
		return new MqttRetryTemplate(3, // 3 retry attempts
				Duration.ofSeconds(1), // 1 second initial delay
				Duration.ofSeconds(30), // 30 seconds max delay
				2.0 // exponential backoff multiplier
		);
	}

	/**
	 * Executes the given callback with retry logic.
	 * @param <T> the return type
	 * @param callback the operation to execute with retry
	 * @return the result of the callback execution
	 * @throws Exception if all retry attempts fail
	 */
	public <T> T execute(RetryCallback<T, Exception> callback) throws Exception {
		return retryTemplate.execute(callback, context -> {
			log.error("All retry attempts failed for MQTT operation after {} attempts", context.getRetryCount());
			throw new MqttException("MQTT operation failed after " + context.getRetryCount() + " attempts");
		});
	}

	/**
	 * Executes the given callback with retry logic and custom recovery.
	 * @param <T> the return type
	 * @param callback the operation to execute with retry
	 * @param recoveryCallback the recovery operation if all retries fail
	 * @return the result of the callback or recovery execution
	 * @throws Exception if both callback and recovery fail
	 */
	public <T> T execute(RetryCallback<T, Exception> callback,
			org.springframework.retry.RecoveryCallback<T> recoveryCallback) throws Exception {
		return retryTemplate.execute(callback, recoveryCallback);
	}

	/**
	 * Creates and configures the underlying RetryTemplate.
	 * @return a configured RetryTemplate instance
	 */
	private RetryTemplate createRetryTemplate() {
		return RetryTemplate.builder()
			.maxAttempts(maxAttempts)
			.exponentialBackoff(initialDelay.toMillis(), multiplier, maxDelay.toMillis())
			.retryOn(Exception.class)
			.withListener(new RetryListener() {
				@Override
				public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
					log.debug("Starting MQTT retry operation");
					return true;
				}

				@Override
				public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					log.warn("MQTT operation failed (attempt {}/{}): {}", context.getRetryCount(), maxAttempts,
							throwable.getMessage());
				}

				@Override
				public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
						Throwable throwable) {
					log.error("MQTT retry operation failed permanently after {} attempts", context.getRetryCount());
				}
			})
			.build();
	}

}
