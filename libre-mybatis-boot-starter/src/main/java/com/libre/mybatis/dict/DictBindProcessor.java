package com.libre.mybatis.dict;

import com.libre.mybatis.annotation.FieldBind;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author ZC
 * @date 2021/11/21 3:48
 */
public class DictBindProcessor {

	public static List<Field> getFieldList(Class<?> clazz) {
		if (null == clazz) {
			return null;
		}
		else {
			Field[] fields = clazz.getDeclaredFields();
			List<Field> fieldList = Arrays.stream(fields).filter((field) -> !Modifier.isStatic(field.getModifiers()))
					.filter((field) -> !Modifier.isTransient(field.getModifiers())).collect(Collectors.toList());
			Class<?> superclass = clazz.getSuperclass();
			if (!superclass.equals(Object.class)) {
				List<Field> list = getFieldList(superclass);
				for (Field field : list) {
					if (fieldList.stream().noneMatch((f) -> f.getName().equals(field.getName()))) {
						fieldList.add(field);
					}
				}

			}
			return fieldList;
		}
	}

	public static void dictBindValue(DictBind dictBind, MetaObject metaObject, FieldSetProperty fieldSetProperty) {
		String fieldName = fieldSetProperty.getFieldName();
		Object metaObjectValue = metaObject.getValue(fieldName);
		if (null != metaObjectValue) {
			boolean isBind = true;
			if (null != dictBind) {
				FieldBind fieldBind = fieldSetProperty.getFieldDict();
				if (null != fieldBind) {
					isBind = false;
					dictBind.setMetaObject(fieldBind, metaObjectValue, metaObject);
				}
			}
			if (isBind) {
				metaObject.setValue(fieldName, metaObjectValue);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object process(Invocation invocation, BiConsumer<MetaObject, FieldSetProperty> biConsumer)
			throws Throwable {
		List list = (List) invocation.proceed();
		if (!list.isEmpty()) {
			DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
			Field field = defaultResultSetHandler.getClass().getDeclaredField("mappedStatement");
			field.setAccessible(true);
			MappedStatement mappedStatement = (MappedStatement) field.get(defaultResultSetHandler);
			Configuration configuration = mappedStatement.getConfiguration();

			for (Object dictBind : list) {
				if (null != dictBind && !DictBindContext.isBind(configuration, dictBind, biConsumer)) {
					break;
				}
			}

		}
		return list;
	}

}
