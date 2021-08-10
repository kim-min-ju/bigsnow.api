package kr.co.bigsnow.api.controller;


import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.pagination.PaginationInfo;
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.db.DbService;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.ExcelHeader;
import kr.co.bigsnow.core.util.FileManager;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
	  
@Slf4j
@RestController


@RequestMapping(path = "/commmon" )
 
public class CommonController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

 
	@Autowired
	protected CommonService commonService;	
 	
  
  

     @PostMapping( value="/fileUpload") /* , consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } */
     
     @Operation(summary = "공통 : 파일 업로드", description = "첨부파일을 업로드 후 DB에 등록한 파일 번호를 반환한다.")
     @Parameters({
     	  @Parameter(name = "up_file", required = true,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )
     })          
     
     // , RequestEntity<Map<String, Object>> requestEntity 
     Map<String, Object>  fileUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException 
     {
    	 
    	 Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
    	 
     	 Map<String, Object> mapRlt    = new HashMap();
     	
     	 try {
     		    
     		   FileManager fileMgr = new FileManager(request, dbSvc);
     		   
			   // mapReq.put("rel_key", CommonUtil.nvlMap(mapReq, "auth_idx"));
			   // mapReq.put("rel_tbl", "TB_PROD"); // 테이블 명
			   fileMgr.fileDbSave(mapReq);     		   
  
			   
			   mapRlt.put("file_no", CommonUtil.nvlMap(mapReq, "file_no" ));
			   
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

 