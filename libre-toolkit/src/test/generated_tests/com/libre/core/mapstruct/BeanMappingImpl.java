package com.libre.core.mapstruct;

import com.libre.core.mapstruct.bean.SourceBean;
import com.libre.core.mapstruct.bean.TargetBean;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-03-27T14:23:42+0800",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 1.8.0_212 (Oracle Corporation)"
)
@Component
public class BeanMappingImpl implements BeanMapping {

    @Override
    public TargetBean sourceToTarget(SourceBean arg0) {
        if ( arg0 == null ) {
            return null;
        }

        TargetBean targetBean = new TargetBean();

        targetBean.setName( arg0.getName() );
        targetBean.setAge( arg0.getAge() );

        return targetBean;
    }

    @Override
    public SourceBean targetToSource(TargetBean arg0) {
        if ( arg0 == null ) {
            return null;
        }

        SourceBean sourceBean = new SourceBean();

        sourceBean.setName( arg0.getName() );
        sourceBean.setAge( arg0.getAge() );

        return sourceBean;
    }

    @Override
    public List<TargetBean> sourceToTarget(List<SourceBean> arg0) {
        if ( arg0 == null ) {
            return null;
        }

        List<TargetBean> list = new ArrayList<TargetBean>( arg0.size() );
        for ( SourceBean sourceBean : arg0 ) {
            list.add( sourceToTarget( sourceBean ) );
        }

        return list;
    }

    @Override
    public List<SourceBean> targetToSource(List<TargetBean> arg0) {
        if ( arg0 == null ) {
            return null;
        }

        List<SourceBean> list = new ArrayList<SourceBean>( arg0.size() );
        for ( TargetBean targetBean : arg0 ) {
            list.add( targetToSource( targetBean ) );
        }

        return list;
    }

    @Override
    public List<TargetBean> sourceToTarget(Stream<SourceBean> arg0) {
        if ( arg0 == null ) {
            return null;
        }

        return arg0.map( sourceBean -> sourceToTarget( sourceBean ) )
        .collect( Collectors.toCollection( ArrayList<TargetBean>::new ) );
    }

    @Override
    public List<SourceBean> targetToSource(Stream<TargetBean> arg0) {
        if ( arg0 == null ) {
            return null;
        }

        return arg0.map( targetBean -> targetToSource( targetBean ) )
        .collect( Collectors.toCollection( ArrayList<SourceBean>::new ) );
    }
}
