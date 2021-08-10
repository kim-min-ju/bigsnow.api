package kr.co.bigsnow.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.CaseUtils;

class SnakeToCamelWithRuleConverter {

	private final String jsonString;

	private final Map<String, Object> map;

	private final Map<String, String> ruleSet;

    SnakeToCamelWithRuleConverter(String jsonString, Map<String, String> ruleSet) {
        this.jsonString = jsonString;
        this.map = null;
        this.ruleSet = ruleSet;
    }

    SnakeToCamelWithRuleConverter(Map<String, Object> map, Map<String, String> ruleSet) {
        this.jsonString = null;
        this.map = map;
        this.ruleSet = ruleSet;
    }

    Map<String, Object> convert() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});

        return convertMap(map);
    }

    Map<String, Object> convertMap() {
        if (map != null) {
            return convertMap(map);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Object convertValue(Object obj) {
        if (obj instanceof Map) {
            return convertMap((Map<String, Object>) obj);
        }
        else if (obj instanceof List) {
            return convertList((List<Object>) obj);
        }
        else {
            return obj;
        }
    }

    private Map<String, Object> convertMap(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.put(mapKey(key), convertValue(value));
        }
        return result;
    }

    private List<Object> convertList(List<Object> list) {
        List<Object> result = new ArrayList<>();
        for (Object obj : list) {
            result.add(convertValue(obj));
        }
        return result;
    }

    private String mapKey(String key) {
        if (ruleSet != null && this.ruleSet.containsKey(key)) key = this.ruleSet.get(key);
        return CaseUtils.toCamelCase(key, false, '_');
    }
}
