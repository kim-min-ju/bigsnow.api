package kr.co.bigsnow.api.controller;

import java.io.File;
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
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;
	  
@Slf4j
@RestController
@RequestMapping(path = "/lecture", produces = MediaType.APPLICATION_JSON_VALUE)
public class LectureController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	@Autowired
	protected CommonService commonService;		
	 
	
    @GetMapping("/subjectSelectBoxList")
    
    @Operation(summary = "?????? SelectBox ??????", description = "?????? SelectBox ??????")
    @Parameters({
    		@Parameter(name = "sbj_grp_cd", required = false, description = "?????? ????????????", schema = @Schema(implementation = String.class) )

    })        
    
    Map<String, Object>  subjectSelectBoxList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
     	//	CommonUtil.setPageParam(mapReq); // Paging ??? ??????
    		
     		List lstRs = dbSvc.dbList("lecture.subjectSelectBoxList" , mapReq);
     		
     		
     		if ( lstRs != null ) {
    		   mapRlt.put("list" ,    lstRs );
    		   mapRlt.put("count" , lstRs.size() );
     		}   


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
    	
	
	
    @PostMapping("/subjectList")
    @Operation(summary = "?????? : ?????? ?????? > ?????? ??????", description = "?????? ????????? ???????????????.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "?????????", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "????????????", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "lec_nm" , required = false, description = "?????????", schema = @Schema(implementation = String.class) )
	    ,   @Parameter(name = "page_now", required = false, description = "?????? ????????? ??????", schema = @Schema(implementation = Integer.class))
	    ,   @Parameter(name = "page_row_count", required = false, description = "??? ???????????? ?????? ??? Row ??????", schema = @Schema(implementation = Integer.class))    		
    })        
    
    Map<String, Object>  subjectList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
     		CommonUtil.setPageParam(mapReq); // Paging ??? ??????
    		
    		mapRlt.put("list" , dbSvc.dbList("lecture.subjectList" , mapReq));
    		mapRlt.put("count", dbSvc.dbInt ("lecture.subjectCount", mapReq));
    		
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
    
    
    @PostMapping("/subjectDetail")
    @Operation(summary = "?????? : ?????? ?????? > ?????? ??????", description = "????????? ??????????????? ???????????????.")
	@Parameters({
		@Parameter(name = "sbj_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class))
	})
    Map<String, Object> subjectDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    	 
    		mapRlt.put("detail" , dbSvc.dbDetail("lecture.subjectDetail" , mapReq));
    		
    		mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "sbj_no") );
    		mapReq.put("ref_nm", "TB_SUBJECT" );
    		mapReq.put("file_gbn" , "data"       );
    		
    		mapRlt.put("fileList" , dbSvc.dbList("common.fileList" , mapReq));
    		
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
    

    @PostMapping("/subjectDelete")
	@Operation(summary = "?????? : ?????? ?????? > ?????? [?????? ??????]", description = "????????? ????????????.(use_yn ?????? N?????? ????????????.) " )
	@Parameters({
		@Parameter(name = "sbj_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class))
	})
	
	public  Map<String, Object>  subjectDelete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;

		try {
			
					if (!"".equals(CommonUtil.nvlMap(mapReq, "sbj_no"))) {
						
						deleteRow = dbSvc.dbUpdate("lecture.updateSubjectUseYn", mapReq);
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
	    
	
		@Operation(summary = "????????? ????????????.", description = "????????? ????????????" )
		@Parameters({
			   @Parameter(name = "cust_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class)) 
			,  @Parameter(name = "sbj_grp_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "sbj_nm", required = true, description = "?????????", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "class_tgt_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "prog_state_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "sbj_dsc", required = true, description = "????????????", schema = @Schema(implementation = String.class))
			   
			,  @Parameter(name = "sbj_cls_cd", required = true, description = "????????????", schema = @Schema(implementation = String.class))   
			   
			,  @Parameter(name = "main_img", required = false, description = "[?????? ?????????] file??? Input ????????? ????????? main_img ??? ????????? ?????????[?????? <input name='main_img' type='file'>", schema = @Schema(implementation = File.class))   
			,  @Parameter(name = "main_img", required = false, description = "[?????????????????????] file??? Input ????????? ????????? data ??? ????????? ?????????[?????? <input name='data' type='file'>", schema = @Schema(implementation = File.class))
			   
			   
			   
		})	   
	
	   @RequestMapping(value = "/insertSubject", method = RequestMethod.POST ) 
	    public Map<String, Object> insertSubject(HttpServletRequest request,HttpServletResponse response,MultipartHttpServletRequest requestFile) throws Exception{
	       
			// Map<String, Object> mapReq 		= super.setRequestMap(requestFile, response); // requestEntity
			Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
			
			Map<String, Object> mapRlt    = new HashMap();
			
			FileManager fileMgr = new FileManager(request, dbSvc);
			
  
			int rtnInt = 0;

			try {
				
				 dbSvc.dbInsert("lecture.insertSubject", mapReq);
 
			     mapReq.put("ref_pk",  CommonUtil.nvlMap(mapReq, "sbj_no"));  // ????????? ???
			     mapReq.put("ref_nm",  "TB_SUBJECT");  // ????????? ???
				   
			     fileMgr.fileDbSave(mapReq);	 
	 
		  		
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
	
	   
		@Operation(summary = "????????? ????????????.", description = "????????? ????????????" )
		@Parameters({
			  @Parameter(name = "sbj_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "cust_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class)) 
			, @Parameter(name = "sbj_grp_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "sbj_nm", required = true, description = "?????????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "class_tgt_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "prog_state_cd", required = true, description = "??????????????????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "sbj_dsc", required = true, description = "????????????", schema = @Schema(implementation = String.class))
			, @Parameter(name = "sbj_cls_cd", required = true, description = "????????????", schema = @Schema(implementation = String.class))
			  
			,  @Parameter(name = "main_img", required = false, description = "[?????? ?????????] file??? Input ????????? ????????? main_img ??? ????????? ?????????[?????? <input name='main_img' type='file'>", schema = @Schema(implementation = File.class))   
			,  @Parameter(name = "main_img", required = false, description = "[?????????????????????] file??? Input ????????? ????????? data ??? ????????? ?????????[?????? <input name='data' type='file'>", schema = @Schema(implementation = File.class))
			  
			  
		})	   
	   
	   @RequestMapping(value = "/updateSubject", method = RequestMethod.POST) 
	    public Map<String, Object> updateSubject(HttpServletRequest request,HttpServletResponse response,MultipartHttpServletRequest requestFile) throws Exception{
	       
			Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
			
			Map<String, Object> mapRlt    = new HashMap();
			
			FileManager fileMgr = new FileManager(request, dbSvc);
			
 
			int rtnInt = 0;

			try {

				
				 dbSvc.dbUpdate("lecture.updateSubject", mapReq);
 
			     mapReq.put("ref_pk",  CommonUtil.nvlMap(mapReq, "sbj_no"));  // ????????? ???
			     mapReq.put("ref_nm",  "TB_SUBJECT");  // ????????? ???
				   
			     fileMgr.fileDbSave(mapReq);	 			 
				
		  		
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
	
 
    @PostMapping("/lectureList")
    @Operation(summary = "?????? > ?????? ?????? > ?????? ?????? ??????", description = "???????????? ????????? ???????????????.")
    @Parameters({
    		@Parameter(name = "cust_nm", required = false, description = "?????????", schema = @Schema(implementation = String.class) )
    	,   @Parameter(name = "cust_no", required = false, description = "????????????", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "lec_nm" , required = false, description = "?????????", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "sbj_nm" , required = false, description = "?????????", schema = @Schema(implementation = String.class) )
    	,	@Parameter(name = "prog_state_cd" , required = false, description = "????????????", schema = @Schema(implementation = String.class) )
    		
		,   @Parameter(name = "page_now", required = false, description = "?????? ????????? ??????", schema = @Schema(implementation = Integer.class))
		,   @Parameter(name = "page_row_count", required = false, description = "??? ???????????? ?????? ??? Row ??????", schema = @Schema(implementation = Integer.class))    		
    		
    })        
    
    
    Map<String, Object>  lectureList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
     		CommonUtil.setPageParam(mapReq); // Paging ??? ??????
    		
    		mapRlt.put("list" , dbSvc.dbList("lecture.lectureList" , mapReq));
    		mapRlt.put("count", dbSvc.dbInt ("lecture.lectureCount", mapReq));
    		
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
    
    
    
    
    
    

    @PostMapping("/subjectPopList")
    @Operation(summary = "?????? > ?????? ?????? > ????????????[??????]", description = "?????? ????????? ????????? ??? ????????? ???????????? ????????????.")
    @Parameters({
    		@Parameter(name = "sbj_nm" , required = false, description = "?????????", schema = @Schema(implementation = String.class) )
    		
    })        
    
    
    Map<String, Object>  subjectPopList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
    		
    		if (!CommonUtil.isAdmin(CommonUtil.nvlMap(mapReq, "token_user_grp_cd"))) {
    			mapReq.put("cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );
    		}
    		
    		mapRlt.put("list" , dbSvc.dbList("lecture.subjectList" , mapReq));
    		
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
    
        
    
    
    
    @Operation(summary = "?????? > ?????? ?????? > ???????????? ??????", description = "??????????????? ??????????????? ???????????????.")
    @PostMapping("/lectureDetail")
	@Parameters({
		@Parameter(name = "lec_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class))
	})    
    
    Map<String, Object> lectureDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    	 
    		mapRlt.put("detail" , dbSvc.dbDetail("lecture.lectureDetail" , mapReq));
    		
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
    
           
	@Operation(summary = "?????? > ?????? ?????? > ???????????? ??????", description = "?????? ????????? ????????????.(use_yn ?????? N?????? ????????????." )
	@Parameters({
		@Parameter(name = "lec_no", required = true, description = "?????? ??????", schema = @Schema(implementation = String.class))
	})
	
	@RequestMapping(value = "/deleteLecture", method = RequestMethod.PUT)
	public  Map<String, Object>  delete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;

		try {
			
			
					if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_no"))) {
						deleteRow = dbSvc.dbUpdate("lecture.updateLectureUseYn", mapReq);
						CtrlUtil.settingRst(mapRlt, 200, "success", "Y");
					} else {
						CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
					}

					// ?????? ?????? ??? ??????
					dbSvc.dbInsert("lesson.deleteLession", mapReq);
					dbSvc.dbInsert("lesson.insertLession", mapReq);

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
	    
	@Operation(summary = "?????? > ?????? ?????? > ?????? ????????? ????????????.", description = "??????????????? ????????? ????????????." )
	@Parameters({
		    @Parameter(name = "sbj_no",required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_nm",required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "teacher_no", required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "capa_num",   required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_year",   required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_degree", required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_state_cd",  required = false, description="??????????????????(PSC)"  , schema = @Schema(implementation = String.class))   
		  , @Parameter(name = "lec_fr_dt",  required = false, description="???????????????   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_dt",  required = false, description="???????????????   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_mon_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_tue_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_wed_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_thu_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fri_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sat_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sun_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_tm",  required = false, description="?????????????????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_tm",  required = false, description="?????????????????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_dsc",required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "materials",  required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_room",   required = false, description="????????? ??????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "fee",   required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "tot_round",  required = false, description="??? ?????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_round", required = false, description="?????? ??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_inwon" ,  required = false, description="???????????????"  , schema = @Schema(implementation = String.class))  
	})	  	
	
	@RequestMapping(value = "/insertLecture", method = RequestMethod.POST) 
    public Map<String, Object> insertLecture(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
  	 Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);		
	
	  
			Map<String, Object> mapRlt  = new HashMap();
		 
  
			int rtnInt = 0;

			try {

				ObjectMapper mapper = new ObjectMapper();
				
				mapReq.put("lec_fr_dt", CommonUtil.removeDateFormat(mapReq, "lec_fr_dt") );
				mapReq.put("lec_to_dt", CommonUtil.removeDateFormat(mapReq, "lec_to_dt") );
				mapReq.put("fee"      , CommonUtil.removeComma(mapReq, "fee") );
				
				dbSvc.dbInsert("lecture.insertLecture", mapReq);

				// ?????? ?????? ??? ??????
				mapper = new ObjectMapper();
				mapReq.put("lec_no", mapReq.get("returnKey") );
				dbSvc.dbInsert("lesson.deleteLession", mapReq);
				dbSvc.dbInsert("lesson.insertLession", mapReq);
  
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
	
    

	@Operation(summary = "?????? > ?????? ?????? > ?????? ????????? ????????????.", description = "??????????????? ????????? ????????????." )
	@Parameters({
		    @Parameter(name = "lec_no",required = true, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "sbj_no",required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_nm",required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "teacher_no", required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "capa_num",   required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_year",   required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_degree", required = false, description="??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_state_cd",  required = false, description="??????????????????(PSC)"  , schema = @Schema(implementation = String.class))   
		  , @Parameter(name = "lec_fr_dt",  required = false, description="???????????????   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_dt",  required = false, description="???????????????   "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_mon_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_tue_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_wed_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_thu_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fri_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sat_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_sun_yn", required = false, description="????????????-???  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_fr_tm",  required = false, description="?????????????????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_to_tm",  required = false, description="?????????????????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_dsc",required = false, description="????????????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "materials",  required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_room",   required = false, description="????????? ??????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "fee",   required = false, description="?????????  "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "tot_round",  required = false, description="??? ?????? "  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "prog_round", required = false, description="?????? ??????"  , schema = @Schema(implementation = String.class))
		  , @Parameter(name = "lec_inwon" ,  required = false, description="???????????????"  , schema = @Schema(implementation = String.class))  
	})	  	
	
	 @RequestMapping(value = "/updateLecture", method = RequestMethod.POST) 
      public Map<String, Object> updateLecture(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	 Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);	
	
	   
			Map<String, Object> mapRlt    = new HashMap();
		 

			try {

				
				mapReq.put("lec_fr_dt", CommonUtil.removeDateFormat(mapReq, "lec_fr_dt") );
				mapReq.put("lec_to_dt", CommonUtil.removeDateFormat(mapReq, "lec_to_dt") );
				mapReq.put("fee"      , CommonUtil.removeComma(mapReq, "fee") );
				
				dbSvc.dbInsert("lecture.updateLecture", mapReq);

				// ?????? ?????? ??? ??????
				dbSvc.dbInsert("lesson.deleteLession", mapReq);
				dbSvc.dbInsert("lesson.insertLession", mapReq);
 
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
	
   
	 
    @GetMapping("/lectureTeacherList")
    @Operation(summary = "??????????????? ???????????? ?????? ??? ?????????????????? ??????", description = "??????????????? ???????????? ???????????? ?????? ????????????")
    @Parameters({
    		@Parameter(name = "user_no", required = false, description = "????????????", schema = @Schema(implementation = Integer.class) )
    })        
    
    
    Map<String, Object>  lectureTeacherSchedultList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
            List lstRs = dbSvc.dbList("lecture.lectureTeacherScheduleList" , mapReq);
    		
    		mapRlt.put("list" , lstRs);
    		
    	    if ( lstRs != null ) {	
    		   mapRlt.put("count", lstRs.size());
    	    }
    		
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
    
    	
	@PostMapping("/insertSubjectExcelUpload")	
	@Operation(summary = "??????: ???????????? > ???????????? ?????? : ????????? ????????? ??????(?????? ??????) ", description = "?????? ???????????? ?????? ????????? ????????????.")
	@Parameters({
		   @Parameter(name = "up_file", required = true, description = "????????? ?????????", schema = @Schema(implementation = String.class)) 
	})	   

       
	public    Map<String, Object>  insertSubjectExcelUpload (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile )  throws JsonProcessingException 
	{
		    
	   	 Map<String, Object> mapReq 	= CommonUtil.getRequestFileMap(request, "/upload/excel/", false); // requestEntity
		 
	 	 Map<String, Object> mapRlt    = new HashMap();
	 	
	 	 try {
	 		    
	 		   FileManager fileMgr = new FileManager(request, dbSvc);
 
	 		   mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
	 		   mapReq.put("excel_header_id", CoreConst.EXCEL_HD_SUBJECT );
	 		   
	 		   mapRlt = commonService.excelUploadProc( request,  response, mapReq, fileMgr );
	 		   
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
