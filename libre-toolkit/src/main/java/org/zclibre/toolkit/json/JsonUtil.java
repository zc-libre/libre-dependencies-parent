package org.zclibre.toolkit.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.zclibre.toolkit.core.Exceptions;
import org.zclibre.toolkit.core.ObjectUtil;
import org.zclibre.toolkit.core.StringUtil;
import org.zclibre.toolkit.time.DatePattern;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * json 工具类
 *
 * @author Libre
 */
@UtilityClass
public class JsonUtil {

	/**
	 * 将对象序列化成json字符串
	 * @param object javaBean
	 * @return jsonString json字符串
	 */

	public static String toJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return getInstance().writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将对象序列化成json字符串
	 * @param object javaBean
	 * @return jsonString json字符串
	 */

	public static String toPrettyJson(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return getInstance().writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将对象序列化成 json byte 数组
	 * @param object javaBean
	 * @return jsonString json字符串
	 */
	public static byte[] toJsonAsBytes(Object object) {
		if (object == null) {
			return new byte[0];
		}
		try {
			return getInstance().writeValueAsBytes(object);
		}
		catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 * @param jsonString jsonString
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(String jsonString) {
		try {
			return getInstance().readTree(Objects.requireNonNull(jsonString, "jsonString is null"));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将InputStream转成 JsonNode
	 * @param in InputStream
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(InputStream in) {
		try {
			return getInstance().readTree(Objects.requireNonNull(in, "InputStream in is null"));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将java.io.Reader转成 JsonNode
	 * @param reader java.io.Reader
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(Reader reader) {
		try {
			return getInstance().readTree(Objects.requireNonNull(reader, "Reader in is null"));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 * @param content content
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(byte[] content) {
		try {
			return getInstance().readTree(Objects.requireNonNull(content, "byte[] content is null"));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json字符串转成 JsonNode
	 * @param jsonParser JsonParser
	 * @return jsonString json字符串
	 */
	public static JsonNode readTree(JsonParser jsonParser) {
		try {
			return getInstance().readTree(Objects.requireNonNull(jsonParser, "jsonParser is null"));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json byte 数组反序列化成对象
	 * @param content json bytes
	 * @param valueType class
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(byte[] content, Class<T> valueType) {
		if (ObjectUtil.isEmpty(content)) {
			return null;
		}
		try {
			return getInstance().readValue(content, valueType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param jsonString jsonString
	 * @param valueType class
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(String jsonString, Class<T> valueType) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, valueType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param in InputStream
	 * @param valueType class
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(InputStream in, Class<T> valueType) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, valueType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将java.io.Reader反序列化成对象
	 * @param reader java.io.Reader
	 * @param valueType class
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(Reader reader, Class<T> valueType) {
		if (reader == null) {
			return null;
		}
		try {
			return getInstance().readValue(reader, valueType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param content bytes
	 * @param typeReference 泛型类型
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(byte[] content, TypeReference<T> typeReference) {
		if (ObjectUtil.isEmpty(content)) {
			return null;
		}
		try {
			return getInstance().readValue(content, typeReference);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param jsonString jsonString
	 * @param typeReference 泛型类型
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(String jsonString, TypeReference<T> typeReference) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, typeReference);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param in InputStream
	 * @param typeReference 泛型类型
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(InputStream in, TypeReference<T> typeReference) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, typeReference);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将java.io.Reader反序列化成对象
	 * @param reader java.io.Reader
	 * @param typeReference 泛型类型
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(Reader reader, TypeReference<T> typeReference) {
		if (reader == null) {
			return null;
		}
		try {
			return getInstance().readValue(reader, typeReference);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param content bytes
	 * @param javaType JavaType
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(byte[] content, JavaType javaType) {
		if (ObjectUtil.isEmpty(content)) {
			return null;
		}
		try {
			return getInstance().readValue(content, javaType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param jsonString jsonString
	 * @param javaType JavaType
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(String jsonString, JavaType javaType) {
		if (StringUtil.isBlank(jsonString)) {
			return null;
		}
		try {
			return getInstance().readValue(jsonString, javaType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将json反序列化成对象
	 * @param in InputStream
	 * @param javaType JavaType
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(InputStream in, JavaType javaType) {
		if (in == null) {
			return null;
		}
		try {
			return getInstance().readValue(in, javaType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 将java.io.Reader反序列化成对象
	 * @param reader java.io.Reader
	 * @param javaType JavaType
	 * @param <T> T 泛型标记
	 * @return Bean
	 */

	public static <T> T readValue(Reader reader, JavaType javaType) {
		if (reader == null) {
			return null;
		}
		try {
			return getInstance().readValue(reader, javaType);
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * clazz 获取 JavaType
	 * @param clazz Class
	 * @return MapType
	 */
	public static JavaType getType(Class<?> clazz) {
		return getInstance().getTypeFactory().constructType(clazz);
	}

	/**
	 * 封装 map type，keyClass String
	 * @param valueClass value 类型
	 * @return MapType
	 */
	public static MapType getMapType(Class<?> valueClass) {
		return getMapType(String.class, valueClass);
	}

	/**
	 * 封装 map type
	 * @param keyClass key 类型
	 * @param valueClass value 类型
	 * @return MapType
	 */
	public static MapType getMapType(Class<?> keyClass, Class<?> valueClass) {
		return getInstance().getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
	}

	/**
	 * 封装 map type
	 * @param elementClass 集合值类型
	 * @return CollectionLikeType
	 */
	public static CollectionLikeType getListType(Class<?> elementClass) {
		return getInstance().getTypeFactory().constructCollectionLikeType(List.class, elementClass);
	}

	/**
	 * 封装参数化类型
	 *
	 * <p>
	 * 例如： Map.class, String.class, String.class 对应 Map[String, String]
	 * </p>
	 * @param parametrized 泛型参数化
	 * @param parameterClasses 泛型参数类型
	 * @return JavaType
	 */
	public static JavaType getParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
		return getInstance().getTypeFactory().constructParametricType(parametrized, parameterClasses);
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param elementClass elementClass
	 * @param <T> 泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(byte[] content, Class<T> elementClass) {
		if (ObjectUtil.isEmpty(content)) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param content InputStream
	 * @param elementClass elementClass
	 * @param <T> 泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(InputStream content, Class<T> elementClass) {
		if (content == null) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param reader java.io.Reader
	 * @param elementClass elementClass
	 * @param <T> 泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(Reader reader, Class<T> elementClass) {
		if (reader == null) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(reader, getListType(elementClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param elementClass elementClass
	 * @param <T> 泛型
	 * @return 集合
	 */
	public static <T> List<T> readList(String content, Class<T> elementClass) {
		if (StringUtil.isBlank(content)) {
			return Collections.emptyList();
		}
		try {
			return getInstance().readValue(content, getListType(elementClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @return 集合
	 */
	public static Map<String, Object> readMap(byte[] content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 * @param content InputStream
	 * @return 集合
	 */
	public static Map<String, Object> readMap(InputStream content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 * @param reader java.io.Reader
	 * @return 集合
	 */
	public static Map<String, Object> readMap(Reader reader) {
		return readMap(reader, Object.class);
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @return 集合
	 */
	public static Map<String, Object> readMap(String content) {
		return readMap(content, Object.class);
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param valueClass 值类型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(byte[] content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 * @param content InputStream
	 * @param valueClass 值类型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(InputStream content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 * @param reader java.io.Reader
	 * @param valueClass 值类型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(Reader reader, Class<?> valueClass) {
		return readMap(reader, String.class, valueClass);
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param valueClass 值类型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <V> Map<String, V> readMap(String content, Class<?> valueClass) {
		return readMap(content, String.class, valueClass);
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param keyClass key类型
	 * @param valueClass 值类型
	 * @param <K> 泛型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(byte[] content, Class<?> keyClass, Class<?> valueClass) {
		if (ObjectUtil.isEmpty(content)) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param content InputStream
	 * @param keyClass key类型
	 * @param valueClass 值类型
	 * @param <K> 泛型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(InputStream content, Class<?> keyClass, Class<?> valueClass) {
		if (content == null) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param reader java.io.Reader
	 * @param keyClass key类型
	 * @param valueClass 值类型
	 * @param <K> 泛型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(Reader reader, Class<?> keyClass, Class<?> valueClass) {
		if (reader == null) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(reader, getMapType(keyClass, valueClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 读取集合
	 * @param content bytes
	 * @param keyClass key类型
	 * @param valueClass 值类型
	 * @param <K> 泛型
	 * @param <V> 泛型
	 * @return 集合
	 */
	public static <K, V> Map<K, V> readMap(String content, Class<?> keyClass, Class<?> valueClass) {
		if (StringUtil.isBlank(content)) {
			return Collections.emptyMap();
		}
		try {
			return getInstance().readValue(content, getMapType(keyClass, valueClass));
		}
		catch (IOException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * jackson 的类型转换
	 * @param fromValue 来源对象
	 * @param toValueType 转换的类型
	 * @param <T> 泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
		return getInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * jackson 的类型转换
	 * @param fromValue 来源对象
	 * @param toValueType 转换的类型
	 * @param <T> 泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, JavaType toValueType) {
		return getInstance().convertValue(fromValue, toValueType);
	}

	/**
	 * jackson 的类型转换
	 * @param fromValue 来源对象
	 * @param toValueTypeRef 泛型类型
	 * @param <T> 泛型标记
	 * @return 转换结果
	 */
	public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
		return getInstance().convertValue(fromValue, toValueTypeRef);
	}

	/**
	 * tree 转对象
	 * @param treeNode TreeNode
	 * @param valueType valueType
	 * @param <T> 泛型标记
	 * @return 转换结果
	 */
	public static <T> T treeToValue(TreeNode treeNode, Class<T> valueType) {
		try {
			return getInstance().treeToValue(treeNode, valueType);
		}
		catch (JsonProcessingException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 对象转 tree
	 * @param fromValue fromValue
	 * @param <T> 泛型标记
	 * @return 转换结果
	 */
	public static <T extends JsonNode> T valueToTree(Object fromValue) {
		return getInstance().valueToTree(fromValue);
	}

	/**
	 * 判断是否可以序列化
	 * @param value 对象
	 * @return 是否可以序列化
	 */
	public static boolean canSerialize(Object value) {
		if (value == null) {
			return true;
		}
		return getInstance().canSerialize(value.getClass());
	}

	/**
	 * 判断是否可以反序列化
	 * @param type JavaType
	 * @return 是否可以反序列化
	 */
	public static boolean canDeserialize(JavaType type) {
		return getInstance().canDeserialize(type);
	}

	/**
	 * 检验 json 格式
	 * @param jsonString json 字符串
	 * @return 是否成功
	 */
	public static boolean isValidJson(String jsonString) {
		return isValidJson(mapper -> {
			try {
				mapper.readTree(jsonString);
			}
			catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 检验 json 格式
	 * @param content json byte array
	 * @return 是否成功
	 */
	public static boolean isValidJson(byte[] content) {
		return isValidJson(mapper -> {
			try {
				mapper.readTree(content);
			}
			catch (IOException e) {
				throw Exceptions.unchecked(e);
			}
		});
	}

	/**
	 * 检验 json 格式
	 * @param input json input stream
	 * @return 是否成功
	 */
	public static boolean isValidJson(InputStream input) {
		return isValidJson(mapper -> {
			try {
				mapper.readTree(input);
			}
			catch (IOException e) {
				throw Exceptions.unchecked(e);
			}
		});
	}

	/**
	 * 检验 json 格式
	 * @param reader java.io.Reader
	 * @return 是否成功
	 */
	public static boolean isValidJson(Reader reader) {
		return isValidJson(mapper -> {
			try {
				mapper.readTree(reader);
			}
			catch (IOException e) {
				throw Exceptions.unchecked(e);
			}
		});
	}

	/**
	 * 检验 json 格式
	 * @param jsonParser json parser
	 * @return 是否成功
	 */
	public static boolean isValidJson(JsonParser jsonParser) {
		return isValidJson(mapper -> {
			try {
				mapper.readTree(jsonParser);
			}
			catch (IOException e) {
				throw Exceptions.unchecked(e);
			}
		});
	}

	/**
	 * 检验 json 格式
	 * @param consumer ObjectMapper consumer
	 * @return 是否成功
	 */
	public static boolean isValidJson(Consumer<ObjectMapper> consumer) {
		ObjectMapper mapper = getInstance().copy();
		mapper.enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
		mapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
		try {
			consumer.accept(mapper);
			return true;
		}
		catch (Throwable e) {
			return false;
		}
	}

	/**
	 * 创建 ObjectNode
	 * @return ObjectNode
	 */
	public static ObjectNode createObjectNode() {
		return getInstance().createObjectNode();
	}

	/**
	 * 创建 ArrayNode
	 * @return ArrayNode
	 */
	public static ArrayNode createArrayNode() {
		return getInstance().createArrayNode();
	}

	/**
	 * 获取 ObjectMapper 实例
	 * @return ObjectMapper
	 */
	public static ObjectMapper getInstance() {
		return JacksonHolder.INSTANCE;
	}

	private static class JacksonHolder {

		private static final ObjectMapper INSTANCE = new JacksonObjectMapper();

	}

	private static class JacksonObjectMapper extends ObjectMapper {

		@Serial
		private static final long serialVersionUID = 4288193147502386170L;

		private static final Locale CHINA = Locale.CHINA;

		JacksonObjectMapper() {
			super(jsonFactory());
			super.setLocale(CHINA);
			super.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, CHINA));
			// 单引号
			super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			// 忽略json字符串中不识别的属性
			super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			// 忽略无法转换的对象
			super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			super.setTimeZone(TimeZone.getDefault());
			super.registerModule(new JavaTimeModule());
			super.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			super.findAndRegisterModules();
		}

		JacksonObjectMapper(ObjectMapper src) {
			super(src);
		}

		private static JsonFactory jsonFactory() {
			return JsonFactory.builder()
				// 可解析反斜杠引用的所有字符
				.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
				// 允许JSON字符串包含非引号控制字符（值小于32的ASCII字符，包含制表符和换行符）
				.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
				.build();
		}

		@Override
		public ObjectMapper copy() {
			return new JacksonObjectMapper(this);
		}

	}

}
