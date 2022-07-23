package com.libre.redis.serializer;

import com.libre.toolkit.core.ObjectUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * ProtoStuff 序列化
 *
 * @author Libre
 */
@Slf4j
public class ProtoStuffSerializer implements RedisSerializer<Object> {

	@SuppressWarnings("rawtypes")
	private final Schema<BytesWrapper> schema;

	public ProtoStuffSerializer() {
		this.schema = RuntimeSchema.getSchema(BytesWrapper.class);
		log.info("redis serializer-type: ProtoStuff");
	}

	@Override
	public byte[] serialize(Object object) throws SerializationException {
		if (object == null) {
			return null;
		}
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try {
			return ProtostuffIOUtil.toByteArray(new BytesWrapper<>(object), schema, buffer);
		}
		finally {
			buffer.clear();
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		if (ObjectUtil.isEmpty(bytes)) {
			return null;
		}
		BytesWrapper<Object> wrapper = new BytesWrapper<>();
		ProtostuffIOUtil.mergeFrom(bytes, wrapper, schema);
		return wrapper.getValue();
	}

}
