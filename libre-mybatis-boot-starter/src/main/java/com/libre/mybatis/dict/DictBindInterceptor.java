package com.libre.mybatis.dict;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;

import java.sql.Statement;

/**
 * @author ZC
 * @date 2021/11/21 3:23
 */
@Intercepts({@Signature(
	type = ResultSetHandler.class,
	method = "handleResultSets",
	args = {Statement.class}
)})
public class DictBindInterceptor implements Interceptor {

	private final DictBind dictBind;

	public DictBindInterceptor(DictBind dictBind) {
		this.dictBind = dictBind;
		DictBindContext.init(null != dictBind);
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		return DictBindProcessor.process(invocation, (metaObject, fieldSetProperty) -> {
			DictBindProcessor.dictBindValue(dictBind, metaObject, fieldSetProperty);
		});
	}

	@Override
	public Object plugin(Object target) {
		return target instanceof ResultSetHandler ? Plugin.wrap(target, this) : target;
	}
}
