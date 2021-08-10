package kr.co.bigsnow.core.util;

import java.util.Map;

import kr.co.bigsnow.core.entity.Result;


public class CtrlUtil {

    public static void settingRst(Map<String, Object> mapReq, int code, String msg, String view) {
    	Result result = new Result();
    	
    	result.setResult_code(code);
    	result.setResult_msg(msg);
    	result.setResult_view(view);

		mapReq.put("result", result);
    			
    }

}
