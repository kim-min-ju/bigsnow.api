package kr.co.bigsnow.core.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/******************************************************************
 * <pre>
 * kr.co.ezpmp.core.util
 * ㄴ
 *  </pre>
 * @author   : Lim Jae Sung
 * @version  : 1.0
 * @since 2021/02/16
 * @see <b>Copyright (C) by OSC Company All right reserved.</b>
 *******************************************************************/
public class JsonUtils {

    private static ObjectMapper defaultMapper;

    static {
        defaultMapper = new ObjectMapper();
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return defaultMapper.writeValueAsString(o);
    }

    public static <T> T toObject(String jsonString) throws IOException {
        return defaultMapper.readValue(jsonString, new TypeReference<T>() {});
    }

    public static Map<String, Object> convertSnakeToCamel(String jsonString) throws IOException {
        return (new SnakeToCamelWithRuleConverter(jsonString, null)).convert();
    }

    public static Map<String, Object> convertSnakeToCamel(String jsonString, Map<String, String> ruleSet) throws IOException {
        return (new SnakeToCamelWithRuleConverter(jsonString, ruleSet)).convert();
    }

    public static Map<String, Object> convertSnakeToCamel(Map<String, Object> map) {
        return (new SnakeToCamelWithRuleConverter(map, null)).convertMap();
    }

    public static Map<String, Object> convertSnakeToCamel(Map<String, Object> map, Map<String, String> ruleSet) {
        return (new SnakeToCamelWithRuleConverter(map, ruleSet)).convertMap();
    }

}
