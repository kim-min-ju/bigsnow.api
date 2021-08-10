/*
 * Copyright (c) big snow Co.,Ltd. All rights reserved.
 */
package kr.co.bigsnow.api.util;

import java.util.Map;

import kr.co.bigsnow.api.domain.Meta;


public class CtrlUtil {

    public static void settingRst(Map<String, Object> mapReq, int code, String msg, String view) {
    	Meta meta = new Meta();
    	
		meta.setResult_code(code);
		meta.setResult_msg(msg);
		meta.setResult_view(view);

		mapReq.put("result", meta);
    			
    }
 
    
}
