package com.libre.toolkit.time;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhao.cheng
 * @date 2021/2/4 15:30
 */
@UtilityClass
public class LocalDateTimeUtils {

	/**
	 * {@link Instant}转{@link LocalDateTime}，使用默认时区
	 *
	 * @param instant {@link Instant}
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime of(Instant instant) {
		return of(instant, ZoneId.systemDefault());
	}


	/**
	 * {@link Instant}转{@link LocalDateTime}
	 *
	 * @param instant {@link Instant}
	 * @param zoneId  时区
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime of(Instant instant, ZoneId zoneId) {
		if (null == instant) {
			return null;
		}
		if (null == zoneId) {
			zoneId = ZoneId.systemDefault();
		}
		return LocalDateTime.ofInstant(instant, zoneId);
	}

	/**
	 * {@link Date}转{@link LocalDateTime}，使用默认时区
	 *
	 * @param date Date对象
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime of(Date date) {
		if (null == date) {
			return null;
		}
		return of(date.toInstant());
	}

	/**
	 * 获取当前月份的第一天
	 *
	 * @return 当前月份的第一天
	 */
	public static LocalDateTime beginOfMouth() {
		return beginOfMouth(LocalDateTime.now());
	}

	/**
	 * 获取当前月份的最后一天
	 *
	 * @return 当前月份的最后一天
	 */
	public static LocalDateTime endOfMouth() {
		return endOfMouth(LocalDateTime.now());
	}


	/**
	 * 获取指定月份的第一天
	 *
	 * @return 指定月份的第一天
	 */
	public static LocalDateTime beginOfMouth(LocalDateTime localDateTime) {
		return adjustInto(localDateTime, TemporalAdjusters.firstDayOfMonth(), LocalTime.MIN);
	}

	/**
	 * 获取指定月份的最后一天
	 *
	 * @return 指定月份的最后一天
	 */
	public static LocalDateTime endOfMouth(LocalDateTime localDateTime) {
		return adjustInto(localDateTime, TemporalAdjusters.firstDayOfMonth(), LocalTime.MAX);
	}


	public static LocalDateTime adjustInto(LocalDateTime dateTime, TemporalAdjuster temporalAdjuster, LocalTime time) {
		Objects.requireNonNull(dateTime);
		Objects.requireNonNull(temporalAdjuster);
		Objects.requireNonNull(time);
		return LocalDateTime.of(LocalDate.from(dateTime.with(temporalAdjuster)), time);
	}

	/**
	 * 判断是否是周末
	 *
	 * @param localDateTime /
	 * @return /
	 */
	public static boolean isWeekend(LocalDateTime localDateTime) {
		return isWeekend(localDateTime.toLocalDate());
	}

	/**
	 * 是否为周末（周六或周日）
	 *
	 * @param localDate 判定的日期{@link LocalDate}
	 * @return 是否为周末（周六或周日）
	 * @since 5.7.6
	 */
	public static boolean isWeekend(LocalDate localDate) {
		final DayOfWeek dayOfWeek = localDate.getDayOfWeek();
		return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
	}

	/**
	 * 获取指定时间的周一
	 *
	 * @param time /
	 * @return /
	 */
	public static LocalDateTime beginOfWeek(LocalDateTime time) {
		return beginOfWeek(time.toLocalDate());
	}

	/**
	 * 获取指定时间的周末
	 *
	 * @param time /
	 * @return /
	 */
	public static LocalDateTime endOfWeek(LocalDateTime time) {
		return endOfWeek(time.toLocalDate());
	}

	/**
	 * 获取指定时间的周一
	 *
	 * @param date /
	 * @return /
	 */
	public static LocalDateTime beginOfWeek(LocalDate date) {
		return LocalDateTime.of(date, LocalTime.MIN).with(DayOfWeek.MONDAY);
	}

	/**
	 * 获取指定时间的周末
	 *
	 * @param date {@link LocalDate}
	 * @return {@link LocalDateTime}
	 */
	public static LocalDateTime endOfWeek(LocalDate date) {
		return LocalDateTime.of(date, LocalTime.MAX).with(DayOfWeek.SUNDAY);
	}

	/**
	 * 获得指定日期是所在年份的第几周，如：
	 * <ul>
	 *     <li>如果一年的第一天是星期一，则第一周从第一天开始，没有零周</li>
	 *     <li>如果一年的第二天是星期一，则第一周从第二天开始，而第一天在零周</li>
	 *     <li>如果一年中的第4天是星期一，则第1周从第4周开始，第1至第3周在零周开始</li>
	 *     <li>如果一年中的第5天是星期一，则第二周从第5周开始，第1至第4周在第1周</li>
	 * </ul>
	 *
	 * @param date 日期（{@link LocalDate} 或者 {@link LocalDateTime}等）
	 * @return 所在年的第几周
	 * @since 5.7.21
	 */
	public static int weekOfYear(TemporalAccessor date) {
		Objects.requireNonNull(date, "date must not be null");
		return date.get(WeekFields.ISO.weekOfYear());
	}

}
