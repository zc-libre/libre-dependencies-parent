package com.libre.ip2region.impl;

import com.libre.ip2region.config.Ip2regionProperties;
import com.libre.ip2region.core.Ip2regionSearcher;
import com.libre.ip2region.core.IpInfo;
import com.libre.ip2region.core.Searcher;
import com.libre.ip2region.exception.Ip2regionException;
import com.libre.ip2region.toolkit.IpInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * ip2region 初始化
 *
 * @author dream.lu
 */
@RequiredArgsConstructor
public class Ip2regionSearcherImpl implements InitializingBean, DisposableBean, Ip2regionSearcher {

	private final ResourceLoader resourceLoader;

	private final Ip2regionProperties properties;

	private Searcher searcher;

	@Override
	public IpInfo memorySearch(long ip) {
		try {
			return IpInfoUtil.toIpInfo(searcher.search(ip));
		}
		catch (IOException e) {
			throw new Ip2regionException(e);
		}
	}

	@Override
	public IpInfo memorySearch(String ip) {
		try {
			return IpInfoUtil.toIpInfo(searcher.search(ip));
		}
		catch (IOException e) {
			throw new Ip2regionException(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Resource resource = resourceLoader.getResource(properties.getDbFileLocation());
		try (InputStream inputStream = resource.getInputStream()) {
			this.searcher = Searcher.newWithBuffer(StreamUtils.copyToByteArray(inputStream));
		}
	}

	@Override
	public void destroy() throws Exception {
		if (this.searcher != null) {
			this.searcher.close();
		}
	}

}
