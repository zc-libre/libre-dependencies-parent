package com.libre.toolkit.core;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author ZC
 * @date 2021/12/6 2:58
 */
@UtilityClass
public class StringUtil extends StringUtils {

	/**
	 * 格式化字段串
	 * @param message 需要转换的字符串
	 * @param arguments 需要替换的变量
	 * @return 转换后的字符串
	 */
	public static String format(String message, Object... arguments) {
		// message 为 null 返回空字符串
		if (message == null) {
			return StringPool.EMPTY;
		}
		// 参数为 null 或者为空
		if (arguments == null || arguments.length == 0) {
			return message;
		}

		StringBuilder sb = new StringBuilder((int) (message.length() * 1.5));
		int cursor = 0;
		int index = 0;
		int argsLength = arguments.length;
		for (int start, end; (start = message.indexOf(CharPool.LEFT_BRACE, cursor)) != -1
				&& (end = message.indexOf(CharPool.RIGHT_BRACE, start)) != -1 && index < argsLength;) {
			sb.append(message, cursor, start);
			sb.append(arguments[index]);
			cursor = end + 1;
			index++;
		}
		sb.append(message.substring(cursor));
		return sb.toString();
	}

	/**
	 * 首字母变小写
	 * @param str 字符串
	 * @return {@link String}
	 */
	public static String firstCharToLower(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= CharPool.UPPER_A && firstChar <= CharPool.UPPER_Z) {
			char[] arr = str.toCharArray();
			arr[0] += (CharPool.LOWER_A - CharPool.UPPER_A);
			return new String(arr);
		}
		return str;
	}

}
