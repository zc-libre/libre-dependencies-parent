package org.zclibre.oss;

import org.junit.jupiter.api.Test;
import org.zclibre.oss.config.OssProperties;
import org.zclibre.oss.support.OssTemplate;

import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 SDK 客户端初始化、签名和资源释放，不访问外部对象存储。
 *
 * @author Libre
 */
class OssSdkCompatibilityTests {

	@Test
	void createsClientsAndSignsGetAndPutRequests() {
		OssProperties properties = new OssProperties();
		properties.setEndpoint("http://127.0.0.1:9000");
		properties.setAccessKey("compatibility-test");
		properties.setSecretKey("compatibility-test-secret");
		properties.setRegion("us-east-1");
		OssTemplate template = new OssTemplate(properties);
		try {
			template.afterPropertiesSet();
			assertNotNull(template.getS3Client());
			assertNotNull(template.getS3AsyncClient());
			assertNotNull(template.getTransferManager());
			Duration expires = Duration.ofMinutes(5);
			assertSignedUrl(template.getObjectURL("test-bucket", "test.txt", expires));
			assertSignedUrl(template.getPutObjectURL("test-bucket", "test.txt", expires));
		}
		finally {
			template.destroy();
		}
	}

	private static void assertSignedUrl(String value) {
		URI url = URI.create(value);
		assertEquals("/test-bucket/test.txt", url.getPath());
		assertTrue(url.getQuery().contains("X-Amz-Algorithm=AWS4-HMAC-SHA256"));
		assertTrue(url.getQuery().contains("X-Amz-Expires=300"));
		assertTrue(url.getQuery().contains("X-Amz-Signature="));
	}

}
