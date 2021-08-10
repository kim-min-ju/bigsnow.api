package kr.co.bigsnow.api.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;
	  
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

 
// @Api(tags = {"1. User"})
@Slf4j
@RestController
@RequestMapping(path = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
public class CodeController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;


	@PostMapping("/codeReprList")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리(왼쪽 목록)", description = "대표 공통코드를 조회합니다.")
   
    Map<String, Object> codeReprList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		lstRs         = dbSvc.dbList("code.codeReprList" , mapReq);
    		int nRowCount = dbSvc.dbInt ("code.codeReprCount", mapReq);
    		
    		mapRlt.put("list" , lstRs);
    		mapRlt.put("count", nRowCount);
    		
    		CtrlUtil.settingRst(mapReq, 200, "success", "Y");
    		
		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			super.errorLogWrite(request, mapReq, e1);
			log.error(this.getClass().getName(), e1);
			
			
			
		}  catch (DataAccessException e2) {
			
			
			String sqlId 	= "";
			String sqlMsg 	= e2.getMessage();
			
			if(sqlMsg.indexOf("/*") > 0) {
				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
				mapReq.put("sql_id", sqlId);
			}
			
			super.errorLogWrite(request, mapReq, e2);
			log.error(this.getClass().getName(), e2);
			
			
			
		}  catch (Exception e) {
			
			log.error(e.getMessage());
		}
    	
        return mapRlt;
    }	
	
	 
    
                                                 

	@PostMapping("/insertRepr")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(대표코드 등록)", description = "대표코드를 등록합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "repr_nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc"   , required = false, description = "설명", schema = @Schema(implementation = String.class))
	     
	})   

	
    public Map<String, Object> insertRepr (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("code.insertRepr", mapReq);
				
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
	    
    

	@PostMapping("/updateRepr")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(대표코드 수정)", description = "공통코드를 수정합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "repr_nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc", required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	     
	})   

	
    public Map<String, Object> updateRepr (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("code.updateRepr", mapReq);
				
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
	
	
	
	@PostMapping("/reprDetail")
    
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리 ( 대표코드 상세조회)", description = "대표 코드를 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
    })    
   
    Map<String, Object> reprDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		
    		mapRlt.put("detail" , dbSvc.dbDetail("code.reprDetail" , mapReq));
    		
    		CtrlUtil.settingRst(mapReq, 200, "success", "Y");
    		
		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			super.errorLogWrite(request, mapReq, e1);
			log.error(this.getClass().getName(), e1);
			
			
			
		}  catch (DataAccessException e2) {
			
			
			String sqlId 	= "";
			String sqlMsg 	= e2.getMessage();
			
			if(sqlMsg.indexOf("/*") > 0) {
				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
				mapReq.put("sql_id", sqlId);
			}
			
			super.errorLogWrite(request, mapReq, e2);
			log.error(this.getClass().getName(), e2);
			
			
			
		}  catch (Exception e) {
			
			log.error(e.getMessage());
		}
    	
        return mapRlt;
    }	    
    
    
    
	@PostMapping("/insertCode")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(코드 등록)", description = "공통코드를 등록합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc", required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "val1", required = true, description = "코드값1", schema = @Schema(implementation = String.class))  
	     
	})   

	
    public Map<String, Object> insertCode (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("code.insertCode", mapReq);
				
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
	    
    

	@PostMapping("/updateCode")
	@Operation(summary = "메뉴 : 시스템 관리 > 코드관리 팝업(코드 수정)", description = "공통코드를 수정합니다.")
	@Parameters({
	      @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "nm", required = true, description = "코드명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "dsc", required = true, description = "설명", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn", required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "val1", required = true, description = "값1", schema = @Schema(implementation = String.class))  
	     
	})   

	
    public Map<String, Object> updateCode (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("code.updateCode", mapReq);
				
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
	
	
	
	@PostMapping("/codeDetail")
    
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리 ( 코드 상세조회)", description = "공통코드를 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))
    	, @Parameter(name = "cd", required = true, description = "코드", schema = @Schema(implementation = String.class))
    })    
   
    Map<String, Object> codeDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		
    		mapRlt.put("detail" , dbSvc.dbDetail("code.codeDetail" , mapReq));
    		
    		CtrlUtil.settingRst(mapReq, 200, "success", "Y");
    		
		}  catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
			
			super.errorLogWrite(request, mapReq, e1);
			log.error(this.getClass().getName(), e1);
			
			
			
		}  catch (DataAccessException e2) {
			
			
			String sqlId 	= "";
			String sqlMsg 	= e2.getMessage();
			
			if(sqlMsg.indexOf("/*") > 0) {
				sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
				mapReq.put("sql_id", sqlId);
			}
			
			super.errorLogWrite(request, mapReq, e2);
			log.error(this.getClass().getName(), e2);
			
			
			
		}  catch (Exception e) {
			
			log.error(e.getMessage());
		}
    	
        return mapRlt;
    }	


	//---------------------------------------
	

    @GetMapping("/codeList")
    @Operation(summary = "메뉴 : 시스템 관리 > 코드관리(오른쪽 코드목록)", description = "코드 목록을 조회합니다.")
	@Parameters({
		@Parameter(name = "repr_cd", required = true, description = "대표코드", schema = @Schema(implementation = String.class))	
	})
    Map<String, Object> codeList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    	 
    		
    		mapRlt.put("list" , dbSvc.dbList("code.codeList" , mapReq));
    		
    		
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
    	
	
	
	//---------------------------------------
	
	
	
	 
}
