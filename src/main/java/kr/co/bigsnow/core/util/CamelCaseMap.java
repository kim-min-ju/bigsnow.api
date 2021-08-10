package kr.co.bigsnow.core.util;

import java.util.HashMap;

import org.springframework.jdbc.support.JdbcUtils;

public class CamelCaseMap extends HashMap<String, Object> {
	
	@Override
	public Object put(String key, Object value) {
		return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(key), value);
	}

}
