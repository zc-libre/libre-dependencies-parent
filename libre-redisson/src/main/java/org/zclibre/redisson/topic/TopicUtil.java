package org.zclibre.redisson.topic;

import lombok.experimental.UtilityClass;

/**
 * 话题工具类
 *
 * @author Libre
 */
@UtilityClass
public class TopicUtil {

	private static final char BACK_SLASH = '\\';

	private static final char ASTERISK = '*';

	private static final char QUESTION_MARK = '?';

	private static final char RIGHT_SQ_BRACKET = ']';

	private static final char LEFT_SQ_BRACKET = '[';

	/**
	 * 判断是否为模糊话题，*、? 和 [...]
	 * @param name 话题名
	 * @return 是否模糊话题
	 */
	public static boolean isPattern(String name) {
		int length = name.length();
		boolean isRightSqBracket = false;
		// 倒序，因为表达式一般在尾部
		for (int i = length - 1; i > 0; i--) {
			char charAt = name.charAt(i);
			switch (charAt) {
				case ASTERISK:
				case QUESTION_MARK:
					if (isEscapeChars(name, i)) {
						break;
					}
					return true;
				case RIGHT_SQ_BRACKET:
					if (isEscapeChars(name, i)) {
						break;
					}
					isRightSqBracket = true;
					break;
				case LEFT_SQ_BRACKET:
					if (isEscapeChars(name, i)) {
						break;
					}
					if (isRightSqBracket) {
						return true;
					}
					break;
				default:
					break;
			}
		}
		return false;
	}

	/**
	 * 判断是否为转义字符
	 * @param name 话题名
	 * @param index 索引
	 * @return 是否为转义字符
	 */
	private static boolean isEscapeChars(String name, int index) {
		if (index < 1) {
			return false;
		}
		// 预读一位，判断是否为转义符 “/”
		char charAt = name.charAt(index - 1);
		return BACK_SLASH == charAt;
	}

}
