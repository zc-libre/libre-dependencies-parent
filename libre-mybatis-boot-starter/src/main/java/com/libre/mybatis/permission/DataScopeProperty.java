package com.libre.mybatis.permission;

import com.libre.mybatis.annotation.DataColumn;
import com.libre.mybatis.annotation.DataScope;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Libre
 */
@NoArgsConstructor
public class DataScopeProperty {

	public static final DataScopeProperty CHECK_INSTANCE = new DataScopeProperty();

	private String type;

	private List<DataColumnProperty> columns;

	public DataScopeProperty(DataScope dataScope) {
		this.type = dataScope.type();
		this.setColumns(dataScope.value());
	}

	public void setColumns(DataColumn[] dataScope) {
		if (!ObjectUtils.isEmpty(dataScope)) {
			this.columns = Arrays.stream(dataScope)
				.map((dataColumn) -> new DataColumnProperty(dataColumn.alias(), dataColumn.name()))
				.collect(Collectors.toList());
		}

	}

	public String getType() {
		return this.type;
	}

	public List<DataColumnProperty> getColumns() {
		return this.columns;
	}

	public void setType(String type) {
		this.type = type;
	}
}
