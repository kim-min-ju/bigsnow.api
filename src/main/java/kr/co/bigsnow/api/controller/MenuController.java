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

@Api(tags = {"2. Menu"})
@Slf4j
@RestController
 
@RequestMapping(path = "/menu", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenuController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;


	@PostMapping("/menuList")
    
    @Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리", description = "메뉴를 조회합니다.")
   
    Map<String, Object> menuList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		
    		lstRs         = dbSvc.dbList("menu.menuList" , mapReq);
    		int nRowCount = dbSvc.dbInt ("menu.menuCount", mapReq);
    		
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
	
	
	
	@PostMapping("/menuDetail")
    
    @Operation(summary = "메뉴 : 시스템 관리 > 메뉴 관리(메뉴 상세조회)", description = "메뉴의 상세 조회합니다.")
    @Parameters({
     	  @Parameter(name = "menu_id", required = true, description = "메뉴 ID", schema = @Schema(implementation = Integer.class))
    })    
   
    Map<String, Object> menuDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		
    		mapRlt.put("detail" , dbSvc.dbDetail("menu.menuDetail" , mapReq));
    		
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
    	
	 //----------------------------
    
                                                 

	@PostMapping("/insertMenu")
	@Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리[저장]", description = "메뉴를 등록합니다.")
	@Parameters({
	      @Parameter(name = "menu_nm", required = true, description = "메뉴명", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "menu_depth", required = true, description = "메뉴 Depth", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_url",  required = true, description = "메뉴 URL", schema = @Schema(implementation = String.class))  
	    , @Parameter(name = "etc"     ,  required = false, description = "비고", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "url_role",  required = false, description = "URL 규칙", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn"     , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "up_menu_id" , required = false, description = "상위메뉴 ID", schema = @Schema(implementation = String.class))
	     
	})   

	
    public Map<String, Object> insertMenu (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("menu.insertMenu", mapReq);
				
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
	    
    
	@PostMapping("/updateMenu")
	@Operation(summary = "메뉴 : 시스템 관리 > 메뉴관리[수정]", description = "메뉴를 등록합니다.")
	@Parameters({
		  @Parameter(name = "menu_id", required = true, description = "메뉴 ID", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_nm", required = true, description = "메뉴명", schema = @Schema(implementation = String.class))		
	    , @Parameter(name = "menu_depth", required = true, description = "메뉴 Depth", schema = @Schema(implementation = Integer.class))
	    , @Parameter(name = "menu_url",  required = true, description = "메뉴 URL", schema = @Schema(implementation = String.class))  
	    , @Parameter(name = "etc"     ,  required = false, description = "비고", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "url_role",  required = false, description = "URL 규칙", schema = @Schema(implementation = String.class))
	    , @Parameter(name = "use_yn"     , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
	     
	})   

    public Map<String, Object> updateMenu (HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
       
		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		Map<String, Object> mapRlt    = new HashMap();
		

		int rtnInt = 0;

		try {

				dbSvc.dbInsert("menu.updateMenu", mapReq);
				
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
