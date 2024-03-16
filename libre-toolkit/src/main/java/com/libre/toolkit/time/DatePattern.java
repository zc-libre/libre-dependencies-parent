package com.libre.toolkit.time;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author ZC
 * @date 2021/12/7 0:18
 */
public class DatePattern {

	public static final Pattern REGEX_NORM = Pattern
		.compile("\\d{4}-\\d{1,2}-\\d{1,2}(\\s\\d{1,2}:\\d{1,2}(:\\d{1,2})?)?(.\\d{1,3})?");

	// --------------------------------------------------------------------------------------------------------------------------------
	// Normal
	/**
	 * 年月格式：yyyy-MM
	 */
	public static final String NORM_MONTH_PATTERN = "yyyy-MM";

	/**
	 * 年月格式 ：yyyy-MM
	 */
	public static final DateTimeFormatter NORM_MONTH_FORMATTER = createFormatter(NORM_MONTH_PATTERN);

	/**
	 * 简单年月格式：yyyyMM
	 */
	public static final String SIMPLE_MONTH_PATTERN = "yyyyMM";

	/**
	 * 简单年月格式 ：yyyyMM
	 */
	public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN);

	/**
	 * 标准日期格式：yyyy-MM-dd
	 */
	public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * 标准日期格式 ：yyyy-MM-dd
	 */
	public static final DateTimeFormatter NORM_DATE_FORMATTER = createFormatter(NORM_DATE_PATTERN);

	/**
	 * 标准时间格式：HH:mm:ss
	 */
	public static final String NORM_TIME_PATTERN = "HH:mm:ss";

	/**
	 * 标准日期格式 ：HH:mm:ss
	 */
	public static final DateTimeFormatter NORM_TIME_FORMATTER = createFormatter(NORM_TIME_PATTERN);

	/**
	 * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
	 */
	public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

	/**
	 * 标准日期格式 ：yyyy-MM-dd HH:mm
	 */
	public static final DateTimeFormatter NORM_DATETIME_MINUTE_FORMATTER = createFormatter(
			NORM_DATETIME_MINUTE_PATTERN);

	/**
	 * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
	 */
	public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 标准日期时间格式，精确到秒 ：yyyy-MM-dd HH:mm:ss
	 */
	public static final DateTimeFormatter NORM_DATETIME_FORMATTER = createFormatter(NORM_DATETIME_PATTERN);

	/**
	 * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final String NORM_DATETIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 标准日期时间格式，精确到毫秒 ：yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final DateTimeFormatter NORM_DATETIME_MS_FORMATTER = createFormatter(NORM_DATETIME_MS_PATTERN);

	/**
	 * ISO8601日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss,SSS
	 */
	public static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

	/**
	 * 标准日期格式 ：yyyy-MM-dd HH:mm:ss,SSS
	 */
	public static final DateTimeFormatter ISO8601_FORMATTER = createFormatter(ISO8601_PATTERN);

	/**
	 * 标准日期格式：yyyy年MM月dd日
	 */
	public static final String CHINESE_DATE_PATTERN = "yyyy年MM月dd日";

	/**
	 * 标准日期格式 ：yyyy年MM月dd日
	 */
	public static final DateTimeFormatter CHINESE_DATE_FORMATTER = createFormatter(ISO8601_PATTERN);

	/**
	 * 标准日期格式：yyyy年MM月dd日 HH时mm分ss秒
	 */
	public static final String CHINESE_DATE_TIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒";

	/**
	 * 标准日期格式 ：yyyy年MM月dd日HH时mm分ss秒
	 */
	public static final DateTimeFormatter CHINESE_DATE_TIME_FORMATTER = createFormatter(CHINESE_DATE_TIME_PATTERN);

	// --------------------------------------------------------------------------------------------------------------------------------
	// Pure
	/**
	 * 标准日期格式：yyyyMMdd
	 */
	public static final String PURE_DATE_PATTERN = "yyyyMMdd";

	/**
	 * 标准日期格式 ：yyyyMMdd
	 */
	public static final DateTimeFormatter PURE_DATE_FORMATTER = createFormatter(PURE_DATE_PATTERN);

	/**
	 * 标准日期格式：HHmmss
	 */
	public static final String PURE_TIME_PATTERN = "HHmmss";

	/**
	 * 标准日期格式 ：HHmmss
	 */
	public static final DateTimeFormatter PURE_TIME_FORMATTER = createFormatter(PURE_TIME_PATTERN);

	/**
	 * 标准日期格式：yyyyMMddHHmmss
	 */
	public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";

	/**
	 * 标准日期格式 ：yyyyMMddHHmmss
	 */
	public static final DateTimeFormatter PURE_DATETIME_FORMATTER = createFormatter(PURE_DATETIME_PATTERN);

	/**
	 * 标准日期格式：yyyyMMddHHmmssSSS
	 */
	public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

	/**
	 * 标准日期格式 ：yyyyMMddHHmmssSSS
	 */
	public static final DateTimeFormatter PURE_DATETIME_MS_FORMATTER = createFormatter(PURE_DATETIME_MS_PATTERN);

	// --------------------------------------------------------------------------------------------------------------------------------
	// Others
	/**
	 * HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z
	 */
	public static final String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

	/**
	 * JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy
	 */
	public static final String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ss
	 */
	public static final String UTC_SIMPLE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS
	 */
	public static final String UTC_SIMPLE_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ss'Z'
	 */
	public static final String UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
	 */
	public static final String UTC_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 */
	public static final String UTC_MS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * UTC时间：yyyy-MM-dd'T'HH:mm:ssZ
	 */
	public static final String UTC_MS_WITH_ZONE_OFFSET_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	/**
	 * 创建并为 {@link DateTimeFormatter} 赋予默认时区和位置信息，默认值为系统默认值。
	 * @param pattern 日期格式
	 * @return {@link DateTimeFormatter}
	 * @since 5.7.5
	 */
	private static DateTimeFormatter createFormatter(String pattern) {
		return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).withZone(ZoneId.systemDefault());
	}

}
