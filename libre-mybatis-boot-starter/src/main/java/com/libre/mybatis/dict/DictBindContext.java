package com.libre.mybatis.dict;

import com.libre.mybatis.annotation.FieldBind;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;

/**
 * @author ZC
 * @date 2021/11/21 3:43
 */
public class DictBindContext {

	private static boolean hasDictBind = false;

	private static Map<Class<?>, List<FieldSetProperty>> classFieldSetListMap;

	private static Set<Class<?>> notDictBindClassSet;

	public static void init(boolean hasDictBind) {
		DictBindContext.hasDictBind = hasDictBind;
		classFieldSetListMap = new ConcurrentHashMap<>(16);
		notDictBindClassSet = new CopyOnWriteArraySet<>();
	}

	public static List<FieldSetProperty> getFieldSetProperties(Class<?> dictBindClass) {
		if (notDictBindClassSet.contains(dictBindClass)) {
			return null;
		}
		else {
			List<FieldSetProperty> fieldSetPropertyList = classFieldSetListMap.get(dictBindClass);
			if (null == fieldSetPropertyList) {
				if (dictBindClass.isAssignableFrom(HashMap.class)) {
					notDictBindClassSet.add(dictBindClass);
				}
				else {
					fieldSetPropertyList = new ArrayList<>();
					List<Field> fieldList = DictBindProcessor.getFieldList(dictBindClass);
					for (Field field : fieldList) {
						FieldBind fieldBind = null;
						if (hasDictBind) {
							fieldBind = field.getAnnotation(FieldBind.class);
						}
						if (null == field) {
							continue;
						}
						fieldSetPropertyList.add(new FieldSetProperty(field.getName(), fieldBind));
					}
					if (fieldSetPropertyList.isEmpty()) {
						notDictBindClassSet.add(dictBindClass);
					}
					else {
						classFieldSetListMap.put(dictBindClass, fieldSetPropertyList);
					}
				}
			}

			return fieldSetPropertyList;
		}
	}

	public static boolean isBind(Configuration configuration, Object result,
			BiConsumer<MetaObject, FieldSetProperty> biConsumer) {
		List<FieldSetProperty> fieldSetProperties = getFieldSetProperties(result.getClass());
		if (!CollectionUtils.isEmpty(fieldSetProperties)) {
			MetaObject metaObject = configuration.newMetaObject(result);
			fieldSetProperties.parallelStream().forEach((fieldSetProperty) -> {
				biConsumer.accept(metaObject, fieldSetProperty);
			});
			return true;
		}
		else {
			return false;
		}
	}

}
