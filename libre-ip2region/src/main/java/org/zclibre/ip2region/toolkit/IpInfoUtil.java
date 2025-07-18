package org.zclibre.ip2region.toolkit;

import org.zclibre.ip2region.core.IpInfo;
import org.zclibre.toolkit.core.StringPool;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * ip 信息详情
 *
 * @author L.cm
 */
public class IpInfoUtil {

	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\|");

	private static final Pattern DOT_PATTERN = Pattern.compile("\\.");

	private static final Pattern T_PATTERN = Pattern.compile("\\t");

	/**
	 * 获取 ip v4 part
	 * @return 是否 ipv4
	 */
	public static String[] getIpV4Part(String ip) {
		return DOT_PATTERN.split(ip);
	}

	/**
	 * 将 DataBlock 转化为 IpInfo
	 * @param region region
	 * @return IpInfo
	 */
	@Nullable
	public static IpInfo toIpInfo(@Nullable String region) {
		if (region == null) {
			return null;
		}
		IpInfo ipInfo = new IpInfo();
		String[] splitInfoArr = SPLIT_PATTERN.split(region);
		// 补齐5位
		if (splitInfoArr.length < 5) {
			splitInfoArr = Arrays.copyOf(splitInfoArr, 5);
		}
		ipInfo.setCountry(filterZero(splitInfoArr[0]));
		ipInfo.setRegion(filterZero(splitInfoArr[1]));
		ipInfo.setProvince(filterZero(splitInfoArr[2]));
		ipInfo.setCity(filterZero(splitInfoArr[3]));
		ipInfo.setIsp(filterZero(splitInfoArr[4]));
		return ipInfo;
	}

	/**
	 * 将 ipv6 地区转化为 IpInfo
	 * @param ipRecord ipRecord
	 * @return IpInfo
	 */
	@Nullable
	public static IpInfo toIpV6Info(String[] ipRecord) {
		IpInfo ipInfo = new IpInfo();
		String info1 = ipRecord[0];
		String[] splitInfoArr = T_PATTERN.split(info1);
		// 补齐5位
		if (splitInfoArr.length < 4) {
			splitInfoArr = Arrays.copyOf(splitInfoArr, 4);
		}
		ipInfo.setCountry(splitInfoArr[0]);
		ipInfo.setProvince(splitInfoArr[1]);
		ipInfo.setCity(splitInfoArr[2]);
		ipInfo.setRegion(splitInfoArr[3]);
		ipInfo.setIsp(ipRecord[1]);
		return ipInfo;
	}

	/**
	 * 数据过滤，因为 ip2Region 采用 0 填充的没有数据的字段
	 * @param info info
	 * @return info
	 */
	@Nullable
	private static String filterZero(@Nullable String info) {
		// null 或 0 返回 null
		if (info == null || StringPool.ZERO.equals(info)) {
			return null;
		}
		return info;
	}

	/**
	 * 读取 IpInfo
	 * @param ipInfo IpInfo
	 * @param function Function
	 * @return info
	 */
	@Nullable
	public static String readInfo(@Nullable IpInfo ipInfo, Function<IpInfo, String> function) {
		if (ipInfo == null) {
			return null;
		}
		return function.apply(ipInfo);
	}

}
