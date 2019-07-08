package cn.stylefeng.guns.core.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * bean util
 * 
 * @author zcg
 */
@Slf4j
public class BeanUtil {

	static private Gson gson = new GsonBuilder()
			.disableHtmlEscaping().serializeNulls()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();

	static public Map<String, Object> toMap(Object obj) {
		return toMap(obj, null);
	}

	static public Map<String, Object> toMap(String key, Object obj) {
		Map<String, Object> empty = new HashMap<String, Object>();
		empty.put(key, obj);
		return empty;
	}

	static public Map<String, Object> toMap(Object obj, String... excludeFields) {
		Map<String, Object> empty = new HashMap<String, Object>();

		if (Objects.isNull(obj)) {
			return empty;
		}

		return beanToMap(obj, excludeFields);
	}

	/**
	 * 将 target目标的feild属性值赋值给original对象相同feild属性，前提是该属性值不为空
	 * 
	 * @param original
	 * @param target
	 * @return
	 */
	static public <T> T addFeildValueInOrginal(T original, T target) {
		if (Objects.isNull(original) || Objects.isNull(target)) {
			return original;
		}

		return addFeildValueInOrginal0(original, target, false);
	}

	/**
	 * 将 target目标的feild属性值替换掉original对象相同feild属性，前提是该属性值不为空
	 * 
	 * @param original
	 * @param target
	 * @return
	 */
	static public <T> T switchFeildValueInOrginal(T original, T target) {
		if (Objects.isNull(original) || Objects.isNull(target)) {
			return original;
		}

		return addFeildValueInOrginal0(original, target, true);
	}

	/**
	 * 将给定的对象转换为json字符串，转换失败返回null
	 * 
	 * @param obj
	 * @return
	 */
	static public String toJson(Object obj) {
		try {
			return gson.toJson(obj);
		} catch (Exception e) {
			log.warn("---- > switch object to json string is failed ,and that object is : " + obj.toString(), e);
		}
		return null;
	}

	static public JsonObject toJosnObject(String json) {
		return new JsonParser().parse(json).getAsJsonObject();
	}

	/**
	 * 将给定的json字符串转换为给定的class对象，失败返回null
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	static public <T> T toBean(String json, Class<? extends T> cls) {
		try {
			return gson.fromJson(json, cls);
		} catch (Exception e) {
			log.warn("---- > switch json string to bean is failed ,and that object class is : " + cls
					+ " ,and that json string is : " + json, e);
		}
		return null;
	}

	static public <T> T toBean(byte[] json, Class<? extends T> cls) {
		return toBean(new String(json), cls);
	}

	static public <T> T toBean(byte[] json, Charset charset, Class<? extends T> cls) {
		try {
			return toBean(new String(json, charset.name()), cls);
		} catch (UnsupportedEncodingException e) {
			log.warn(
					"---- > switch json string to bean is failed ,and that object class is : " + cls
							+ " ,and that json string is : " + json + " ;cause is is no given character : " + charset,
					e);
			return null;
		}
	}

	static private <T> T addFeildValueInOrginal0(T original, T target, boolean isSwitch) {
		Field[] originalFields = getDeclaredFields(original);
		Map<String, Object> targetFields = beanToMap(target, null);

		if (Objects.isNull(originalFields)) {
			return original;
		}

		for (int i = 0, len = originalFields.length; i < len; ++i) {
			Field f = originalFields[i];
			f.setAccessible(true);
			Object originalValue = null;
			try {
				originalValue = f.get(original);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}

			if (!isSwitch && !Objects.isNull(originalValue)) {
				continue;
			}

			String name = f.getName();
			Class<?> fieldType = f.getType();
			Object val = targetFields.get(name);
			if (val != null && (fieldType.isAssignableFrom(val.getClass())
					|| fieldType.getName().equals(val.getClass().getName()))) {
				try {
					f.set(original, val);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					//
				}
			}
		}
		return original;
	}

	private static Field[] getDeclaredFields(Object original) {
		return original.getClass().getDeclaredFields();
	}

	private static Map<String, Object> beanToMap(Object obj, String[] excludeFields) {
		Map<String, Object> result = new HashMap<String, Object>();

		Class<?> cls = obj.getClass();
		Field[] fields = cls.getDeclaredFields();

		if (Objects.isNull(fields) || fields.length == 0) {
			return result;
		}

		for (int i = 0, len = fields.length; i < len; ++i) {
			Field field = fields[i];
			// 打开私有访问
			field.setAccessible(true);
			// 获取属性
			String name = field.getName();

			// 排除属性
			if (valueInArrays(name, excludeFields)) {
				continue;
			}
			// 获取属性值
			Object value = null;
			try {
				value = field.get(obj);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}

			result.put(name, value);
		}
		return result;
	}

	private static boolean valueInArrays(String name, String[] excludeFields) {
		if (Objects.isNull(name) || Objects.isNull(excludeFields) || excludeFields.length == 0) {
			return false;
		}

		for (int i = 0, len = excludeFields.length; i < len; ++i) {
			if (name.equals(excludeFields[i])) {
				return true;
			}
		}

		return false;
	}

	public static byte[] toJsonAsBytes(Object obj) {
		String json = toJson(obj);
		return Objects.isNull(json) ? new byte[0] : json.getBytes();
	}

	public static JsonArray toJosnArray(String json) {
		return new JsonParser().parse(json).getAsJsonArray();
	}

	/**
	 * 给定对象所有字段是否为空
	 * 
	 * @param obj
	 * @return
	 */
	static public boolean isFeildNull(Object obj) {
		return isFeildNull(obj, "");
	}

	static public boolean isFeildNull(Object obj, String excludeField) {
		return isFeildNull(obj, new String[] { excludeField });
	}

	/**
	 * 给定的对象中，排除给定字段名称，其它字段是否都为空
	 * 
	 * @param obj
	 * @param excludeFields
	 * @return
	 */
	static public boolean isFeildNull(Object obj, String... excludeFields) {
		if (obj != null) {
			Map<String, Object> fields = beanToMap(obj, null);
			for (Map.Entry<String, Object> entry : fields.entrySet()) {
				System.out.println("key :" + entry.getKey() + " ;" + entry.getValue());
				if (!valueInArrays(entry.getKey(), excludeFields) && entry.getValue() != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 	将map中的double和float转换为整数
	 * @param map
	 */
	static public Map<String ,Object> elementToNumber(Map<String ,Object> map) {
		Map<String ,Object > result = new HashMap<String, Object>(map.size());
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value.getClass().isAssignableFrom(Double.class) ) {
				value = Math.round(Double.parseDouble(value + ""));
			}
			else if(value.getClass().isAssignableFrom(Float.class)) {
				value = Math.round(Float.parseFloat(value + ""));
			}
			result.put(key, value);
		}
		return result;
	}
	
	/**
	 * 	将map中的double和float转换为整数
	 * @param map
	 */
	static public List<Map<String ,Object>> elementToNumber(List<Map<String ,Object>> list) {
		List<Map<String ,Object>> result = new ArrayList<Map<String ,Object>>();
		for(int i = 0 ,len = list.size() ;i < len ;++i) {
			result.add(elementToNumber(list.get(i)));
		}
		return result;
	}
	
//	static public void main(String[] args) {
//	}
}
