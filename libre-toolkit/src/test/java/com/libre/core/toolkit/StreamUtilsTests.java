package com.libre.core.toolkit;

import com.google.common.collect.Lists;
import org.zclibre.toolkit.core.StreamUtils;
import com.libre.core.pojo.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author ZC
 * @date 2022/1/15 9:28
 */
public class StreamUtilsTests {

	private List<Entity> list;

	@BeforeEach
	void init() {
		list = Lists.newArrayList();
		for (long i = 0; i < 2; i++) {
			Entity entity = new Entity();
			entity.setId(i);
			entity.setName(i + "");
			list.add(entity);
		}
	}

	@Test
	void list() {
		List<Long> ids = StreamUtils.list(list, Entity::getId);
		Assertions.assertEquals(Arrays.asList(0L, 1L), ids);
	}

}
