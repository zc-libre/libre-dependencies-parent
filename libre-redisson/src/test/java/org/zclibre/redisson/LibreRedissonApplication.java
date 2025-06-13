package org.zclibre.redisson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author ZC
 * @date 2022/2/5 23:52
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LibreRedissonApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibreRedissonApplication.class, args);
	}

}
