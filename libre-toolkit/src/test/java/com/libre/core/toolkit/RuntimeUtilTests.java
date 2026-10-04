package com.libre.core.toolkit;

import org.junit.jupiter.api.Test;
import org.zclibre.toolkit.core.RuntimeUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 进程编号解析回归测试。
 *
 * @author Libre
 */
class RuntimeUtilTests {

	@Test
	void returnsCurrentProcessId() {
		assertEquals(ProcessHandle.current().pid(), RuntimeUtil.getPId());
	}

}
