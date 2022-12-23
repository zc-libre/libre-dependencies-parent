package com.libre.mybatis.permission;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author Libre
 * @date 2021/11/4 23:03
 */
@Slf4j
public abstract class AbstractDataScopeProvider implements IDataScopeProvider {

	@Override
	public void sqlRender(Object[] parameterObjects, MappedStatement mappedStatement, SqlCommandType sqlCommandType)
			throws Exception {

		DataScopeProperty dataScopeProperty = DataScopePropertyHandler.getDataScopeProperty(mappedStatement.getId());

		if (!ObjectUtils.isEmpty(dataScopeProperty)) {
			if (sqlCommandType == SqlCommandType.INSERT) {
				this.processInsert(parameterObjects, mappedStatement, dataScopeProperty);
			}
			else if (sqlCommandType == SqlCommandType.UPDATE) {
				this.processUpdate(parameterObjects, mappedStatement, dataScopeProperty);
			}
			else if (sqlCommandType == SqlCommandType.DELETE) {
				this.processDelete(parameterObjects, mappedStatement, dataScopeProperty);
			}
			else if (sqlCommandType == SqlCommandType.SELECT) {
				this.processSelect(parameterObjects, mappedStatement, dataScopeProperty);
			}
		}

	}

	public void processInsert(Object[] parameterObjects, MappedStatement mappedStatement,
			DataScopeProperty dataScopeProperty) {
	}

	public void processUpdate(Object[] parameterObjects, MappedStatement mappedStatement,
			DataScopeProperty dataScopeProperty) {
	}

	public void processDelete(Object[] parameterObjects, MappedStatement mappedStatement,
			DataScopeProperty dataScopeProperty) {
	}

	public void processSelect(Object[] parameterObjects, MappedStatement mappedStatement,
			DataScopeProperty dataScopeProperty) {
		processStatements(parameterObjects, mappedStatement, (statement, argSeq) -> {
			this.processSelect((Select) statement, argSeq, parameterObjects, dataScopeProperty);
		});
	}

	public void processSelect(Select select, int var2, Object[] parameterObjects, DataScopeProperty dataScopeProperty) {
		SelectBody selectBody = select.getSelectBody();
		if (selectBody instanceof PlainSelect) {
			this.setWhere((PlainSelect) selectBody, parameterObjects, dataScopeProperty);
		}
		else if (selectBody instanceof SetOperationList) {
			SetOperationList setOperationList = (SetOperationList) selectBody;
			List<SelectBody> selectBodyList = setOperationList.getSelects();
			selectBodyList.forEach((s) -> this.setWhere((PlainSelect) s, parameterObjects, dataScopeProperty));
		}

	}

	/**
	 * 查询条件设置
	 * @param plainSelect /
	 * @param parameterObjects /
	 * @param dataScopeProperty /
	 */
	public abstract void setWhere(PlainSelect plainSelect, Object[] parameterObjects,
			DataScopeProperty dataScopeProperty);

	public static void processStatements(Object[] parameterObjects, MappedStatement mappedStatement,
			StatementProcessor statementProcessor) {
		DataScopePropertyHandler.processStatements(parameterObjects, mappedStatement, statementProcessor);
	}

}
