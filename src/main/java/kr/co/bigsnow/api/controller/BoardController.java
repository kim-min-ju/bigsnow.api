package kr.co.bigsnow.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
	  
@Slf4j
@RestController
@RequestMapping(path = "/board", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoardController extends StandardController {
	
	/**
	 * TransactionManager
	 */
	@Autowired
	protected PlatformTransactionManager txManager;

	
    @PostMapping("/noticeList")
    
    @Operation(summary = "메뉴:설정관리 > 공지사항 > 목록", description = "공지사항을 목록을 조회한다.")
    @Parameters({
					@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
			    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
			    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })        
    
    Map<String, Object>  noticeList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
   
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    		
    	mapReq.put("brd_kind", "BKD001");
    	
    	mapRlt = boardList( request, response, mapReq ); 
    	
        return mapRlt;
    }
    
    
    @PostMapping("/faqList")
    
    @Operation(summary = "메뉴:설정관리 > 자주하는 질문 > 목록", description = "자주하는 질문을 목록을 조회한다.")
    @Parameters({
					@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
			    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
			    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })        
    
    Map<String, Object>  faqList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
   
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    		
    	mapReq.put("brd_kind", "BKD002");
    	
    	mapRlt = boardList( request, response, mapReq ); 
    	
        return mapRlt;
    }    
    
    
    
    @PostMapping("/dataList")
    
    @Operation(summary = "메뉴:설정관리 > 자료실 > 목록", description = "자료실 목록을 조회한다.")
    @Parameters({
					@Parameter(name = "ttl", required = false, description = "제목", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_fr_dt", required = false, description = "등록일 기준(조회 시작일)", schema = @Schema(implementation = String.class) )
				,   @Parameter(name = "reg_to_dt", required = false, description = "등록일 기준(조회 종료일)", schema = @Schema(implementation = String.class) )
			    ,   @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
			    ,   @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })        
    
    Map<String, Object>  dataList(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException 
    {
   
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    		
    	mapReq.put("brd_kind", "BKD003");
    	
    	return boardList( request, response, mapReq ); 
    	
    }      
    
    
    Map<String, Object>  boardList(HttpServletRequest  request, HttpServletResponse response, Map mapReq)  throws JsonProcessingException 
    {
   
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    		
    		mapRlt.put("reg_fr_dt", CommonUtil.removeDateFormat(mapReq, "reg_fr_dt"));
    		mapRlt.put("reg_to_dt", CommonUtil.removeDateFormat(mapReq, "reg_to_dt"));
    		
     		CommonUtil.setPageParam(mapReq); // Paging 값 세팅
    		
    		mapRlt.put("list" , dbSvc.dbList("board.boardList" , mapReq));
    		mapRlt.put("count", dbSvc.dbInt ("board.boardCount", mapReq));
    		
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
        
    
    @PostMapping("/boardDetail")
    @Operation(summary = "메뉴 : 설정관리 > 모든 게시판 > 상세", description = "모든 게시판의 상세 내용을 조회한다.")
	@Parameters({
		@Parameter(name = "brd_reg_no", required = true, description = "게시판 번호", schema = @Schema(implementation = Integer.class))
	})
    Map<String, Object> subjectDetail(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity)  throws JsonProcessingException {
    	Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
    	
    	Map<String, Object> mapRlt    = new HashMap();
    	
    	try {
    	 
    		log.error(this.getClass().getName(), "===========================================");
    		log.error(this.getClass().getName(), mapReq.toString());
    		log.error(this.getClass().getName(), "===========================================");
    		
    		mapRlt.put("detail" , dbSvc.dbDetail("board.boardDetail" , mapReq));
    		
    		mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no") );
    		mapReq.put("ref_nm", "TB_BOARD" );
    		
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
    

	@RequestMapping(value = "/boardDelete", method = RequestMethod.POST)
	@Operation(summary = "메뉴 : 설정 관리 > 게시판 삭제", description = "게시판을 삭제한다.(use_yn 값을 N으로 세팅한다.)" )
	@Parameters({
		@Parameter(name = "brd_reg_no", required = true, description = "게시판번호 번호", schema = @Schema(implementation = String.class))
	})
	
	public  Map<String, Object>  subjectDelete(HttpServletRequest request, HttpServletResponse response,
			RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

		Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
    	Map<String, Object> mapRlt    = new HashMap();

		int deleteRow = 0;

		try {
			
					if (!"".equals(CommonUtil.nvlMap(mapReq, "brd_reg_no"))) {
						
						deleteRow = dbSvc.dbUpdate("board.updateBoardUseYn", mapReq);
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
    

    
	
	@Operation(summary = "게시판을 등록한다.", description = "게시판을 등록한다" )
	@Parameters({
		   @Parameter(name = "up_reg_no"    , required = true, description = "상위게시판 번호[상위게시판번호가 없는 경우 빈 값으로 보내주세요", schema = @Schema(implementation = String.class)) 
		,  @Parameter(name = "brd_kind"     , required = true, description = "게시판종류[공지사항:BKD001, FAQ:BKD002, 자료실:BKD003 ]", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ttl"          , required = true, description = "제목", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ctnt"         , required = true, description = "내용", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "top_notice_yn", required = true, description = "상단공지여부", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
		
		,  @Parameter(name = "up_file"      , required = false,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )	   
		   
	})	   
 
   @RequestMapping(value = "/insertBoard", method = RequestMethod.POST ) 
    public Map<String, Object> insertBoard (HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile ) throws Exception{
	    
	    Map<String, Object> mapReq 	  = CommonUtil.getRequestFileMap(request, "/upload/board/", false);  
		
		Map<String, Object> mapUp 	  = new HashMap();
		Map<String, Object> mapRlt    = new HashMap();
		
		Map<String, Object> subMap 		= null;
		
		boolean fileFlag 				= false;
		

		int rtnInt = 0;

		try {

			    FileManager fileMgr = new FileManager(request, dbSvc);
			
				dbSvc.dbInsert("board.insertBoard", mapReq);
				
				//----------------------- 파일저장 ----------------------
				mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no"));
				mapReq.put("ref_nm", "TB_BOARD"); // 테이블 명
				fileMgr.fileDbSave(mapReq);     					
				//----------------------- 파일저장 ----------------------
				
				String strBrdRegNo = CommonUtil.nvlMap(mapReq, "brd_reg_no");
				String strUpRegNo  = CommonUtil.nvlMap(mapReq, "up_reg_no");				 
				
				if (!"".equals(strUpRegNo) && !"0".equals(strUpRegNo) ) {
					Map mapParam = new HashMap();
				
					mapParam.put("brd_reg_no", strUpRegNo);
					mapUp = dbSvc.dbDetail("board.boardDetail", mapParam);
					
					mapReq.put("depth"     , ( CommonUtil.nvlMapInt(mapUp, "depth") + 1 )  );
					mapReq.put("top_reg_no", ( CommonUtil.nvlMap(mapUp, "top_reg_no")  )  );
					
					mapReq.put("all_reg_no", ( CommonUtil.nvlMap(mapUp, "all_reg_no") + strBrdRegNo + ","  )  );
					
				} else {
					
					mapReq.put("depth", "1" );
					mapReq.put("top_reg_no", strBrdRegNo  );
					mapReq.put("all_reg_no", strBrdRegNo + "," );
				}
				
				dbSvc.dbUpdate("board.insertAfterBoardUpdate", mapReq);
				
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
	
	
	@Operation(summary = "게시판을 수정한다.", description = "게시판을 수정한다" )
	@Parameters({
		   @Parameter(name = "brd_reg_no"   , required = true, description = "게시판 번호", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ttl"          , required = true, description = "제목", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "ctnt"         , required = true, description = "내용", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "top_notice_yn", required = true, description = "상단공지여부", schema = @Schema(implementation = String.class))
		,  @Parameter(name = "use_yn"       , required = true, description = "사용여부", schema = @Schema(implementation = String.class))
		
	    ,  @Parameter(name = "up_file"      , required = false,  description = "첨부 파일", schema = @Schema(implementation = File.class)  )		   
	})	
   @RequestMapping(value = "/updateBoard", method = RequestMethod.POST ) 
    public Map<String, Object> insertUpdate( HttpServletRequest  request, HttpServletResponse response , MultipartHttpServletRequest requestFile) throws Exception{
	       
	    Map<String, Object> mapReq 	  = CommonUtil.getRequestFileMap(request, "/upload/board/", false);  
		
		Map<String, Object> mapRlt    = new HashMap();
		

		try {

		       FileManager fileMgr = new FileManager(request, dbSvc);
			
				dbSvc.dbInsert("board.updateBoard", mapReq);

				//----------------------- 파일저장 ----------------------
				mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "brd_reg_no"));
				mapReq.put("ref_nm", "TB_BOARD"); // 테이블 명
				fileMgr.fileDbSave(mapReq);     					
				//----------------------- 파일저장 ----------------------
				
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
