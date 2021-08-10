package kr.co.bigsnow.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
 

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.pagination.PaginationInfo;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
	  
@Slf4j
@RestController
@RequestMapping(path = "/teacher", produces = MediaType.APPLICATION_JSON_VALUE)
public class TeacherController extends StandardController {
	
	/**
	 * 강사관리
	 */
	@Autowired
	protected PlatformTransactionManager txManager;
	
    @PostMapping("/teacherList")
    @Operation(summary = "메뉴 : 강사 관리 > 강사 목록", description = "강사 목록를 조회합니다.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = Integer.class) )
    	,	@Parameter(name = "user_nm", required = false, description = "강사명", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "hp_no"  , required = false, description = "연락처", schema = @Schema(implementation = String.class) )	
		,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
		,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))    		
    		
    })        
    
    Map<String, Object>  subjectList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
     		CommonUtil.setPageParam(mapReq); // Paging 값 세팅
    		
    		mapRlt.put("list" , dbSvc.dbList("teacher.teacherList" , mapReq));
    		mapRlt.put("count", dbSvc.dbInt ("teacher.teacherCount", mapReq));
    		
       		CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
    		
    		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
    			
    			super.errorLogWrite(request, mapReq, e1);
    			log.error(this.getClass().getName(), e1);
    			
    			
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    			
    		}  catch (DataAccessException e2) {
    			
    			
    			String sqlId 	= "";
    			String sqlMsg 	= e2.getMessage();
    			
    			if(sqlMsg.indexOf("/*") > 0) {
    				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
    				mapReq.put("sql_id", sqlId);
    			}
    			
    			super.errorLogWrite(request, mapReq, e2);
    			log.error(this.getClass().getName(), e2);
    			
    			
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    			
    		}  catch (Exception e) {
    			
    			log.error(e.getMessage());
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    		}
    	
        return mapRlt;
    }
    
    
    @PostMapping("/teacharDetail")
    @Operation(summary = "메뉴 : 강사 관리 > 강좌 상세", description = "강사의 상세정보를 조회합니다.")
	@Parameters({
		@Parameter(name = "user_no", required = true, description = "회원(강사)번호", schema = @Schema(implementation = Integer.class))
	})
    Map<String, Object> subjectDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    	 
    		mapRlt.put("detail" , dbSvc.dbDetail("teacher.tearcherDetail" , mapReq));
    		
    		CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
    		
    	 
    		
		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			super.errorLogWrite(request, mapReq, e1);
			log.error(this.getClass().getName(), e1);
			
			
			
			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
			
		}  catch (DataAccessException e2) {
			
			
			String sqlId 	= "";
			String sqlMsg 	= e2.getMessage();
			
			if(sqlMsg.indexOf("/*") > 0) {
				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
				mapReq.put("sql_id", sqlId);
			}
			
			super.errorLogWrite(request, mapReq, e2);
			log.error(this.getClass().getName(), e2);
			
			
			
			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
			
		}  catch (Exception e) {
			
			log.error(e.getMessage());
			
			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
		}
    	
        return mapRlt;
    }
    
    @PostMapping("/teacharDelete")
	 
	@Operation(summary = "메뉴 : 강사 관리 > 강사 상세 [강사 삭제]", description = "강사를 삭제한다.(use_yn 값을 N으로 세팅한다." )
	@Parameters({
		@Parameter(name = "user_no", required = true, description = "강사(회원) 번호", schema = @Schema(implementation = String.class))
	})
	
	public  Map<String, Object>  subjectDelete(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;

		try {
			
					if (!"".equals(CommonUtil.nvlMap(mapReq, "user_no"))) {
						
						deleteRow = dbSvc.dbUpdate("teacher.updateteacherUseYn", mapReq);
						CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
						
					} else {
						CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
					}

	  		
			}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
				
				super.errorLogWrite(request, mapReq, e1);
				log.error(this.getClass().getName(), e1);
				
				
				
				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
				
			}  catch (DataAccessException e2) {
				
				
				String sqlId 	= "";
				String sqlMsg 	= e2.getMessage();
				
				if(sqlMsg.indexOf("/*") > 0) {
					sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
					mapReq.put("sql_id", sqlId);
				}
				
				super.errorLogWrite(request, mapReq, e2);
				log.error(this.getClass().getName(), e2);
				
				
				
				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
				
			}  catch (Exception e) {
				
				log.error(e.getMessage());
				
				CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
			}
	    	
	        return mapRlt;
	    }
	    
	
	
	
    @PostMapping("/tearcherLessionPopList")
    @Operation(summary = "메뉴 : 강사 관리 > 강사 목록 > 강사일정 팝업", description = "강사 일정의 팝업을 조회한다.")
    @Parameters({
    	 	@Parameter(name = "user_no"  , required = false, description = "강사(회원)번호", schema = @Schema(implementation = Integer.class) )	
    		
    })        
    
    Map<String, Object>  tearcherLessionPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
     		CommonUtil.setPageParam(mapReq); // Paging 값 세팅
    		
    		mapRlt.put("list" , dbSvc.dbList("teacher.tearcherLessionList" , mapReq));
    		
       		CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
    		
    		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
    			
    			super.errorLogWrite(request, mapReq, e1);
    			log.error(this.getClass().getName(), e1);
    			
    			
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    			
    		}  catch (DataAccessException e2) {
    			
    			
    			String sqlId 	= "";
    			String sqlMsg 	= e2.getMessage();
    			
    			if(sqlMsg.indexOf("/*") > 0) {
    				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
    				mapReq.put("sql_id", sqlId);
    			}
    			
    			super.errorLogWrite(request, mapReq, e2);
    			log.error(this.getClass().getName(), e2);
    			
    			
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    			
    		}  catch (Exception e) {
    			
    			log.error(e.getMessage());
    			
    			CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
    		}
    	
        return mapRlt;
    }	
	
	     
    	
	
}
