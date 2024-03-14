package com.libre.mybatis.permission;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Libre
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataColumnProperty {

	/**
	 * 表别名
	 */
	private String alias;

	/**
	 * 字段名
	 */
	private String name;

	public String getAliasDotName() {
		return StringUtils.isBlank(this.alias) ? this.name : this.alias + StringPool.DOT + this.name;
	}

}
