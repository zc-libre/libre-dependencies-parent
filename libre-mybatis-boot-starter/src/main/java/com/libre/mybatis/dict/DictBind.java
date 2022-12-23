package com.libre.mybatis.dict;

import com.libre.mybatis.annotation.FieldBind;
import org.apache.ibatis.reflection.MetaObject;

/**
 * @author ZC
 * @date 2021/11/21 3:20
 */
public interface DictBind {

	/**
	 * 绑定属性
	 * @param fieldBind 字典绑定注解
	 * @param fieldValue 字典值
	 * @param metaObject 元数据
	 */
	void setMetaObject(FieldBind fieldBind, Object fieldValue, MetaObject metaObject);

}
