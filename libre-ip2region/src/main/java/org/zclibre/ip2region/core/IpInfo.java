package org.zclibre.ip2region.core;

import org.zclibre.toolkit.core.StringPool;
import org.zclibre.toolkit.core.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * ip 信息详情
 *
 * @author L.cm
 */
@Data
public class IpInfo implements Serializable {

	/**
	 * 国家
	 */
	private String country;

	/**
	 * 区域
	 */
	private String region;

	/**
	 * 省
	 */
	private String province;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 运营商
	 */
	private String isp;

	/**
	 * 拼接完整的地址
	 * @return address
	 */
	public String getAddress() {
		Set<String> regionSet = new LinkedHashSet<>();
		regionSet.add(country);
		regionSet.add(region);
		regionSet.add(province);
		regionSet.add(city);
		regionSet.removeIf(Objects::isNull);
		return StringUtil.join(regionSet, StringPool.EMPTY);
	}

	/**
	 * 拼接完整的地址
	 * @return address
	 */
	public String getAddressAndIsp() {
		Set<String> regionSet = new LinkedHashSet<>();
		regionSet.add(country);
		regionSet.add(region);
		regionSet.add(province);
		regionSet.add(city);
		regionSet.add(isp);
		regionSet.removeIf(Objects::isNull);
		return StringUtil.join(regionSet, StringPool.SPACE);
	}

}
