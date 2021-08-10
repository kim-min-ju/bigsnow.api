package kr.co.bigsnow.core.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.api.jwt.JwtUserDetailsService;
import kr.co.bigsnow.core.db.DbService ;

import lombok.extern.slf4j.Slf4j;

 
@Slf4j
@SuppressWarnings("unchecked")
public class StandardController {


	/**
	 * DB 서비스
	 */
	@Autowired
	protected DbService dbSvc;


	@Autowired
	private JwtUserDetailsService userDetailsService;
	
	public Map<String,Object> setRequestMap(HttpServletRequest  request, HttpServletResponse response) {
	    Map<String,Object> superMapReq = setRequestMap(request, response, null, -1);
		return superMapReq;
	}
	 
	public Map<String, Object> setRequestMap(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) {
		// TODO Auto-generated method stub
		Map<String,Object> superMapReq = setRequestMap(request, response, requestEntity, -1);

		return superMapReq;
	}

	 
	public Map<String, Object> setRequestMap(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String,Object>> requestEntity, int intOption) {
		Map<String, Object> superMapReq 	= null;
		Map<String, Object> mapCode			= null;

        log.debug(" >>>>>>>>>>>>>> start = " + getMethodName() + " == " + superMapReq);

        try {
        	if(requestEntity != null) {
        		
        		if( requestEntity.getMethod() == HttpMethod.GET  ) {
        			superMapReq = getParam(request, requestEntity);
        		} else if( requestEntity.getMethod() == HttpMethod.DELETE  ) {
        			superMapReq = getParam(request, requestEntity);
        		} else {
        			superMapReq = postParam(requestEntity);
        		}
        		
        	} 
        	
        	
        	if ( superMapReq == null ) {
        		superMapReq = new HashMap<String, Object>();
        	}

        	//--------------------------- JWT의 토큰 정보 Map에 담기 시작 ------------------------------------
        	getJwtTokenValue(  request, superMapReq);
        	//--------------------------- JWT의 토큰 정보 Map에 담기 종료 ------------------------------------            
           
        	superMapReq.put("ip_addr",  CommonUtil.getClientIP(request));
 
        	log.info("\n--------------------------------------[superMapReq]-----------------------------------------------------------");
        	log.info("\n superMapReq =========== > " + superMapReq.toString() );
        	log.info("\n--------------------------------------[superMapReq]-----------------------------------------------------------");
        	
        	log.info("\n\n Referer =========== > " + request.getHeader("REFERER") );

		} catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			log.error(this.getClass().getName(), e1);

			errorLogWrite(request, superMapReq, e1);

		} catch (DataAccessException e2) {
			

			String sqlId 	= "";
			String sqlMsg 	= e2.getMessage();

			if(sqlMsg.indexOf("/*") > 0) {
				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
				superMapReq.put("sql_id", sqlId);
			}

			log.error(this.getClass().getName(), e2);

			errorLogWrite(request, superMapReq, e2);

		} catch (Exception e) {
			log.error(this.getClass().getName(), e);
			errorLogWrite(request, superMapReq, e);
		}
        log.debug(" >>>>>>>>>>>>>> end = " + getMethodName() + " == " + superMapReq);
        return superMapReq;
	}

	public Map getJwtTokenValue (  HttpServletRequest  request, Map superMapReq ) {
		
        try {
        	 
        	//--------------------------- JWT의 토큰 정보 Map에 담기 시작 ------------------------------------
    		final String requestTokenHeader = request.getHeader("Authorization");

            Map<String, Object>  mapToken = userDetailsService.verifyJWT(requestTokenHeader);       	
        	
            superMapReq.put("token_user_no", CommonUtil.nvlMap(mapToken, "user_no"));
            superMapReq.put("token_user_id", CommonUtil.nvlMap(mapToken, "user_id"));
            superMapReq.put("token_user_nm", CommonUtil.nvlMap(mapToken, "user_nm"));
            superMapReq.put("token_cust_no", CommonUtil.nvlMap(mapToken, "cust_no"));
            superMapReq.put("token_user_grp_cd", CommonUtil.nvlMap(mapToken, "user_grp_cd"));
            
            superMapReq.put("reg_user_no"  , CommonUtil.nvlMap(mapToken, "user_no"));
        	//--------------------------- JWT의 토큰 정보 Map에 담기 종료 ------------------------------------            
  
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			log.error(this.getClass().getName(), e1);

			errorLogWrite(request, superMapReq, e1);


		} catch (Exception e) {
			log.error(this.getClass().getName(), e);
			errorLogWrite(request, superMapReq, e);
		}
        log.debug(" >>>>>>>>>>>>>> end = " + getMethodName() + " == " + superMapReq);
        return superMapReq;
		
	}
	
	
	public String getMethodName() {
		String result = null;

		final Throwable t = new Throwable();
		if (t != null) {
			final StackTraceElement[] stes = t.getStackTrace();
			if (stes != null && stes.length > 0) {
				result = stes[3].getClassName().concat(".")
						.concat(stes[3].getMethodName());
			}
		}

		return result;
	}

	protected void errorLogWrite(HttpServletRequest  request, Map<String, Object> mapReq, Exception e) {
		Exception se = null;

		String strXPath = "errorLog.insertErrorLog";
		Map paramMap 	= new HashMap<String, Object>();

		int errCode 	= -1;
		try {

			if (e instanceof DataAccessException) {
		       se = (SQLException) ((DataAccessException) e).getRootCause();
		       errCode = ((SQLException) se).getErrorCode();
			}

			paramMap.put("menu_url"			, request.getRequestURL().toString());
			paramMap.put("param_data"		, mapReq.toString());
			paramMap.put("sql_id"			, CommonUtil.nvlMap(mapReq, "sql_id"));
			paramMap.put("simple_msg"		, se.getClass().getName());
			paramMap.put("detail_msg"		, e.getMessage());
			paramMap.put("proc_complet_yn"	, "N");
			paramMap.put("etc"				, errCode);
			paramMap.put("user_id"			, CommonUtil.nvlMap(mapReq, "user_id"));

			//dbSvc.dbUpdate(strXPath, paramMap);

		} catch ( Exception err) {
			log.error(e.toString());
		}

	}

	 
	protected Map<String, Object> getParam(HttpServletRequest  request, RequestEntity<Map<String,Object>> requestEntity) {
		Map<String, Object> data = CommonUtil.getRequestMap(request);
		getHeader(data, requestEntity);
		
		return data;
	}
	
 
	protected Map<String, Object> postParam(RequestEntity<Map<String,Object>> requestEntity) {
		Map<String, Object> data = requestEntity.getBody();
        
        if(data == null) {
            data = new HashMap<String, Object>();
        }
        
		getHeader(data, requestEntity);
		
		return data;
	}
	
	 
	protected Map<String, Object> getHeader(Map<String, Object> data, RequestEntity<Map<String,Object>> requestEntity) {

		if(( (Map)requestEntity.getHeaders()).containsKey("Authorization")) {
			data.put("Authorization", ( (Map)requestEntity.getHeaders()).get("Authorization") );
		}
		
		return data;
	}

	protected void errorGoPage(HttpServletRequest  request, HttpServletResponse response) {

	}

}
