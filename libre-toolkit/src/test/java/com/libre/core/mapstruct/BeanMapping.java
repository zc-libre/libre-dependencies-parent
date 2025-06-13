package com.libre.core.mapstruct;

import com.libre.core.mapstruct.bean.SourceBean;
import com.libre.core.mapstruct.bean.TargetBean;
import org.zclibre.toolkit.mapstruct.BaseMapping;
import org.mapstruct.Mapper;

/**
 * @author ZC
 * @date 2021/12/20 2:43
 */
@Mapper
public interface BeanMapping extends BaseMapping<SourceBean, TargetBean> {

}
