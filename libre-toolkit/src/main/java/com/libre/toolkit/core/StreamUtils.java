package com.libre.toolkit.core;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author ZC
 * @date 2022/1/15 9:23
 */
@UtilityClass
public class StreamUtils {

	/**
	 * ignore
	 */
	@SafeVarargs
	public static <A, E> List<A> list(List<E> list, Function<E, A> sFunction, Consumer<E>... peeks) {
		return list(list, sFunction, false, peeks);
	}

	/**
	 * 对list进行map、peek操作
	 * @param list 数据
	 * @param sFunction 需要的列
	 * @param isParallel 是否并行流
	 * @param peeks 后续操作
	 * @return java.util.List<A>
	 * @since 2021/11/9 18:01
	 */
	@SafeVarargs
	public static <A, E> List<A> list(List<E> list, Function<E, A> sFunction, boolean isParallel,
			Consumer<E>... peeks) {
		return peekStream(list, isParallel, peeks).map(sFunction).collect(Collectors.toList());
	}

	/**
	 * ignore
	 */
	@SafeVarargs
	public static <K, T> Map<K, List<T>> listGroupBy(List<T> list, Function<T, K> sFunction, Consumer<T>... peeks) {
		return listGroupBy(list, sFunction, false, peeks);
	}

	/**
	 * ignore
	 */
	@SafeVarargs
	public static <K, T> Map<K, List<T>> listGroupBy(List<T> list, Function<T, K> sFunction, boolean isParallel,
			Consumer<T>... peeks) {
		return listGroupBy(list, sFunction, Collectors.toList(), isParallel, peeks);
	}

	/**
	 * ignore
	 */
	@SafeVarargs
	public static <T, K, D, A, M extends Map<K, D>> M listGroupBy(List<T> list, Function<T, K> sFunction,
			Collector<? super T, A, D> downstream, Consumer<T>... peeks) {
		return listGroupBy(list, sFunction, downstream, false, peeks);
	}

	/**
	 * 对list进行groupBy操作
	 * @param list 数据
	 * @param sFunction 分组的key，依据
	 * @param downstream 下游操作
	 * @param isParallel 是否并行流
	 * @param peeks 封装成map时可能需要的后续操作，不需要可以不传
	 * @param <T> 实体类型
	 * @param <K> 实体中的分组依据对应类型，也是Map中key的类型
	 * @param <D> 下游操作对应返回类型，也是Map中value的类型
	 * @param <A> 下游操作在进行中间操作时对应类型
	 * @param <M> 最后返回结果Map类型
	 * @return Map<实体中的属性, List < 实体>>
	 */
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public static <T, K, D, A, M extends Map<K, D>> M listGroupBy(List<T> list, Function<T, K> sFunction,
			Collector<? super T, A, D> downstream, boolean isParallel, Consumer<T>... peeks) {
		boolean hasFinished = downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH);
		return peekStream(list, isParallel, peeks).collect(new Collector<T, HashMap<K, A>, M>() {
			@Override
			public Supplier<HashMap<K, A>> supplier() {
				return HashMap::new;
			}

			@Override
			public BiConsumer<HashMap<K, A>, T> accumulator() {
				return (m, t) -> {
					// 只此一处，和原版groupingBy修改只此一处，成功在支持下游操作的情况下支持null值
					K key = Optional.ofNullable(t).map(sFunction).orElse(null);
					A container = m.computeIfAbsent(key, k -> downstream.supplier().get());
					downstream.accumulator().accept(container, t);
				};
			}

			@Override
			public BinaryOperator<HashMap<K, A>> combiner() {
				return (m1, m2) -> {
					for (Map.Entry<K, A> e : m2.entrySet()) {
						m1.merge(e.getKey(), e.getValue(), downstream.combiner());
					}
					return m1;
				};
			}

			@Override
			public Function<HashMap<K, A>, M> finisher() {
				return hasFinished ? i -> (M) i : intermediate -> {
					intermediate.replaceAll((k, v) -> (A) downstream.finisher().apply(v));
					@SuppressWarnings("unchecked")
					M castResult = (M) intermediate;
					return castResult;
				};
			}

			@Override
			public Set<Characteristics> characteristics() {
				return hasFinished ? Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH))
						: Collections.emptySet();
			}
		});
	}

	/**
	 * ignore
	 */
	@SafeVarargs
	public static <E, A, P> Map<A, P> map(List<E> list, Function<E, A> keyFunc, Function<E, P> valueFunc,
			Consumer<E>... peeks) {
		return map(list, keyFunc, valueFunc, false, peeks);
	}

	/**
	 * list转换为map
	 * @param <E> 实体类型
	 * @param <A> 实体中的属性类型
	 * @param <P> 实体中的属性类型
	 * @param list 数据
	 * @param keyFunc key
	 * @param isParallel 是否并行流
	 * @param peeks 封装成map时可能需要的后续操作，不需要可以不传
	 * @return Map<实体中的属性, 实体>
	 */
	@SafeVarargs
	public static <E, A, P> Map<A, P> map(List<E> list, Function<E, A> keyFunc, Function<E, P> valueFunc,
			boolean isParallel, Consumer<E>... peeks) {
		return peekStream(list, isParallel, peeks).collect(HashMap::new,
				(m, v) -> m.put(keyFunc.apply(v), valueFunc.apply(v)), HashMap::putAll);
	}

	/**
	 * 将list转为Stream流，然后再叠加peek操作
	 * @param list 数据
	 * @param isParallel 是否并行流
	 * @param peeks 叠加的peek操作
	 * @param <E> 数据元素类型
	 * @return 转换后的流
	 */
	@SafeVarargs
	public static <E> Stream<E> peekStream(List<E> list, boolean isParallel, Consumer<E>... peeks) {
		if (CollectionUtils.isEmpty(list)) {
			return Stream.empty();
		}
		return Stream.of(peeks)
			.reduce(StreamSupport.stream(list.spliterator(), isParallel), Stream::peek, Stream::concat);
	}

}
