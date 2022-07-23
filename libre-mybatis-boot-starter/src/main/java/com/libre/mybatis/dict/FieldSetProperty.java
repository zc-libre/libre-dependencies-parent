package com.libre.mybatis.dict;

import com.libre.mybatis.annotation.FieldBind;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ZC
 * @date 2021/11/21 3:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldSetProperty {

	private String fieldName;

	private FieldBind fieldDict;
}
