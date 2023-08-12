package com.libre.ip2region.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * mica-ip2region native 支持
 *
 * @author L.cm
 */
class Ip2regionRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.resources().registerPattern("ip2region/ip2region.xdb");
	}

}
