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

@Api(tags = {"1. Main"})
@Slf4j
@RestController
 
@RequestMapping(path = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;


	@PostMapping("/home")
    
    @Operation(summary = "메뉴 : 메인(홈) 페이지", description = "메인(홈) 페이지의 정보를 조회한다.")
   
    Map<String, Object> home(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	List<Map<String, Object>> lstRs = null;
    	Map<String, Object> mapRlt = new HashMap();
    	
    	Map<String, Object> mapParam = new HashMap();
 
    	try {
    		
    		if (!CommonUtil.isAdmin(CommonUtil.nvlMap(mapReq, "token_user_grp_cd"))) {
    			mapReq.put("cust_no" , CommonUtil.nvlMap(mapReq, "token_cust_no")   );
    		}    		
    		
    		mapRlt.put("todayLessonList",  dbSvc.dbList("lesson.mainTodayLessonList" , mapReq)); // 오늘의 수업 현황 (최근 5 건)   
    		
    		mapRlt.put("todayLessonStat",  dbSvc.dbList("lesson.mainTodayLessonStat" , mapReq)); // ㅁ 전체 수업 현황 (통계 수치-상위분류)
    		
    		mapParam.put("brd_kind" , "BKD001"); // 공지사항
    		mapRlt.put("noticeList"     ,  dbSvc.dbList("board.mainBoardList" , mapParam));
    		
    		
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
	
		        
}
