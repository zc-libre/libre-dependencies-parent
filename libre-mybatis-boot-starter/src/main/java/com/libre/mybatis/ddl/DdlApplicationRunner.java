package com.libre.mybatis.ddl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
public class DdlApplicationRunner implements ApplicationRunner {

	private final List<IDdl> ddlList;

	public DdlApplicationRunner(List<IDdl> ddlList) {
		this.ddlList = ddlList;
	}

	public void run(ApplicationArguments applicationArguments) throws Exception {
		log.debug("  ...  DDL start create  ...  ");
		if (CollectionUtils.isNotEmpty(this.ddlList)) {
			this.ddlList.forEach((ddl) -> ddl
					.runScript((dataSource) -> DdlRunner.runScript(ddl.getDdlGenerator(), dataSource, ddl.getSqlFiles(), true)));
		}

		log.debug("  ...  DDL end create  ...  ");
	}

}
