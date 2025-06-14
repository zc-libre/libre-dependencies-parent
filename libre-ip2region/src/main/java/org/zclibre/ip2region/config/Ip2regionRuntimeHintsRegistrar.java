package org.zclibre.ip2region.config;

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
		// matches all the files in "ip2region" directory where is under resource
		// directory
		// and its child directories at any depth
		hints.resources().registerPattern("ip2region/*");
	}

}
