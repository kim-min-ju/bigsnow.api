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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;
	  
@Slf4j
@RestController

@RequestMapping(path = "/cust", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	  
    @GetMapping("/custSelectBoxList" )    
    @Operation(summary = "기관조회 ", description = "기관의 SelectBox용으로 조회합니다.")

    @Parameters({
		@Parameter(name = "cust_no", required = false, description = "로그인한 기관번호", schema = @Schema(implementation = String.class))
    })    
    
    Map<String, Object> custSelectBoxList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
 
    	try {
    		
    		// 관리자일경우 모든 내용이 나오도록 처리함
    		if (CommonUtil.isAdmin(CommonUtil.nvlMap(mapReq, "token_user_grp_cd"))) {
    			mapReq.remove("cust_no");
    		}
    		
    		lstRs         = dbSvc.dbList("cust.custSelectBoxList" , mapReq);
    		
    		mapRlt.put("list" , lstRs);
    		
    		if ( lstRs  != null )
    		{
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
      	
	
	
     @PostMapping("/custList" )    
     @Operation(summary = "메뉴: 시스템관리 > 기관", description = "각종 기관의 목록을 조회합니다.")

     @Parameters({
 		  @Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class))
 		, @Parameter(name = "page_now", required = false, description = "현재 페이지 번호(값을 넣지 않으면 전체 목록을 조회한다.)", schema = @Schema(implementation = Integer.class))
 		, @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
     })    
     
     Map<String, Object> custList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
     	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
     	
     	ObjectMapper mapper = new ObjectMapper();
     	
     	List<Map<String, Object>> lstRs = null;
     	Map<String, Object> mapRlt = new HashMap();
  
     	try {
     		
     		if ( !"".equals(CommonUtil.nvlMap(mapReq, "page_now"))) {
     		   CommonUtil.setPageParam(mapReq); // Paging 값 세팅
     		}   
     		
     		mapRlt.put("list" , dbSvc.dbList("cust.custList" , mapReq));
     		mapRlt.put("count", dbSvc.dbInt ("cust.custCount", mapReq));
     		

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
                  
     
	    @PostMapping("/custDetail")
	    @Operation(summary = "메뉴 : 시스템관리 > 기관 : 기관 상세", description = "기관의 상세정보를 조회한다.")
		@Parameters({
			@Parameter(name = "cust_no", required = true, description = "기관 번호", schema = @Schema(implementation = Integer.class))
		})
	    Map<String, Object> custDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
	    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
	    	
	    	Map<String, Object> mapRlt    = new HashMap();
	    	
	    	try {
	    	 
	    		mapRlt.put("detail" , dbSvc.dbDetail("cust.custDetail" , mapReq));
	    		
	    		mapRlt.put("cust_user" , dbSvc.dbDetail("cust.custUserDetail" , mapReq));
	    		
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
		                                

		
		@Operation(summary = "메뉴 : 시스템관리 > 기관 등록", description = "기관정보를 저장등록한다" )
		@Parameters({
			   @Parameter(name = "cust_nm"      , required = true, description = "기관명", schema = @Schema(implementation = String.class)) 
			,  @Parameter(name = "up_cust_no"     , required = false, description = "상위기관번호", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "tel_no"          , required = false, description = "전화번호", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "fax_no"         , required = false, description = "팩스번호", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "post_no"      , required = false, description = "우편번호", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "addr"         , required = false, description = "주소", schema = @Schema(implementation = String.class))
		    ,  @Parameter(name = "addr_detail"  , required = false, description = "상세 주소", schema = @Schema(implementation = String.class))
			,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
			   
		})	   
	 
	   @RequestMapping(value = "/insertCust", method = RequestMethod.POST ) 
	    public Map<String, Object> insertCust(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
	    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
	    	
	    	Map<String, Object> mapRlt    = new HashMap();
	    	
			try {

				dbSvc.dbInsert("cust.insertCust", mapReq);

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
	    
	  
		   @RequestMapping(value = "/updateCust", method = RequestMethod.POST ) 
		    public Map<String, Object> updateCust(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
		    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		    	
		    	Map<String, Object> mapRlt    = new HashMap();
		    	
				try {

					dbSvc.dbInsert("cust.insertCust", mapReq);

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
