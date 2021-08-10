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
import kr.co.bigsnow.api.service.CommonService;
import kr.co.bigsnow.api.util.CtrlUtil;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.ExcelHeader;
import kr.co.bigsnow.core.util.FileManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/lesson", produces = MediaType.APPLICATION_JSON_VALUE)
public class LessonController extends StandardController {

    /**
     * TransactionManager
     */
    @Autowired
    protected PlatformTransactionManager txManager;

    @Autowired
    protected CommonService commonService;


    @PostMapping("/insertAttendance")
    @Operation(summary = "메뉴: 홈 > 화상 출석 등록 ", description = "화상 교육에 대한 출석을 등록한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "les_no", required = true, description = "수강번호", schema = @Schema(implementation = Integer.class))

    })


    public Map<String, Object> insertAttendance(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();


        int rtnInt = 0;

        try {


            if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_app_no"))) {

                int nCnt = dbSvc.dbCount("lesson.userAttendanceExistCount", mapReq);

                if (nCnt == 0) {

                    mapReq.put("att_yn", "Y");
                    dbSvc.dbInsert("lesson.insertAttendance", mapReq);
                }

                dbSvc.dbUpdate("lesson.updateLectureAppAtt", mapReq);
            }

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/myLessionVideoList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 화상채팅방의 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class))

    })
    Map<String, Object> myLessionVideoList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {


            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            // mapReq = getJwtTokenValue (  request, mapReq );

//            List lstRs = dbSvc.dbList("lesson.mngLessionVideoList", mapReq);
            List lstRs = dbSvc.dbList("lesson.myLessionVideoList", mapReq);

            mapRlt.put("list", lstRs);

            if (lstRs == null) {
                mapRlt.put("count", "0");
            } else {
                mapRlt.put("count", lstRs.size());
            }


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/myLessionList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 수업 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class))

    })
    Map<String, Object> myLessionList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {


            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            // mapReq = getJwtTokenValue (  request, mapReq );

            List lstRs = dbSvc.dbList("lesson.myLessionList", mapReq);

            mapRlt.put("list", lstRs);

            if (lstRs == null) {
                mapRlt.put("count", "0");
            } else {
                mapRlt.put("count", lstRs.size());
            }


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }

    @PostMapping("/myAttendList")
    @Operation(summary = "메뉴: 홈/강의실/강의실 목록", description = "로그인한 수강생의 수업 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = false, description = "로그인한 수강생 번호", schema = @Schema(implementation = Integer.class))

    })
    Map<String, Object> myAttendList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {


            mapReq.put("cust_no", CommonUtil.nvlMap(mapReq, "token_cust_no"));

            // mapReq = getJwtTokenValue (  request, mapReq );

            List lstRs = dbSvc.dbList("lesson.myAttendList", mapReq);

            mapRlt.put("list", lstRs);

            if (lstRs == null) {
                mapRlt.put("count", "0");
            } else {
                mapRlt.put("count", lstRs.size());
            }


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lessonList")
    @Operation(summary = "메뉴:수업관리 > 수업", description = "수업 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "cust_nm", required = false, description = "기관명", schema = @Schema(implementation = String.class))
            , @Parameter(name = "cust_no", required = false, description = "기관번호", schema = @Schema(implementation = String.class))
            , @Parameter(name = "lec_nm", required = false, description = "강좌명", schema = @Schema(implementation = String.class))
            , @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    Map<String, Object> lessonList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list", dbSvc.dbList("lesson.lessonList", mapReq));
            mapRlt.put("count", dbSvc.dbInt("lesson.lessonCount", mapReq));

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lessonDetailAndList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 수업상세 리스트[강좌 상세]", description = "수업상세의 상단에 있는 상세정보를 조회한다.")
    @Parameters({
            @Parameter(name = "lec_no", required = true, description = "강좌 번호", schema = @Schema(implementation = String.class))
            , @Parameter(name = "page_now", required = false, description = "현재 페이지 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "page_row_count", required = false, description = "한 페이지당 보여 줄 Row 건수", schema = @Schema(implementation = Integer.class))
    })
    Map<String, Object> subjectDetail(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonDetail", mapReq));

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list", dbSvc.dbList("lesson.lessonDetailAndList", mapReq));
            mapRlt.put("count", dbSvc.dbInt("lesson.lessonDetailAndCount", mapReq));


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping(path = "/lessonVideoDataFileInsert")
    @Operation(summary = "메뉴: 수업 관리 > 영상/자료 관리를 등록한다.", description = "영상자료를 insert하기 전에 기존 영상주소를 삭제한 후 신규로 insert합니다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "ttl", required = true, description = "영상제목", schema = @Schema(implementation = String[].class))
            , @Parameter(name = "video_url", required = true, description = "Video Url", schema = @Schema(implementation = String[].class))
            , @Parameter(name = "up_file", required = true, description = "첨부파일의 input tag를 [up_file]로 한다.")

    })
    public Map<String, Object> lessonVideoDataFileInsert(HttpServletRequest request, HttpServletResponse response, MultipartHttpServletRequest requestFile) throws JsonProcessingException {

        Map<String, Object> mapReq = CommonUtil.getRequestFileMap(request, "/upload/file/", false); // requestEntity
        Map<String, Object> mapRlt = new HashMap();

        try {


            FileManager fileMgr = new FileManager(request, dbSvc);

            String[] arrTtl = CommonUtil.getMapValArray(mapReq, "ttl");
            String[] arrVideoUrl = CommonUtil.getMapValArray(mapReq, "video_url");

            Map mapParam = new HashMap();

            mapParam.put("les_no", CommonUtil.nvlMap(mapReq, "les_no"));

            if (arrTtl != null && !"".equals(arrTtl[0])) {

                dbSvc.dbDelete("lesson.deleteAllVideoData", mapParam);

                for (int nLoop = 0; nLoop < arrTtl.length; nLoop++) {
                    mapParam.put("ttl", arrTtl[nLoop]);
                    mapParam.put("video_url", arrVideoUrl[nLoop]);

                    dbSvc.dbInsert("lesson.insertVideoData", mapParam);

                }
            }


            List fileList = (List) mapReq.get(CoreConst.MAP_UPFILE_KEY);

            if (fileList != null && !fileList.isEmpty()) {
                mapReq.put("ref_pk", CommonUtil.nvlMap(mapReq, "les_no"));  // 테이블 키
                mapReq.put("ref_nm", "TB_VIDEODATA");  // 테이블 명

                fileMgr.fileDbSave(mapReq);
            }

            // 파일처리가 필요함

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lessonVideoList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 영상/자료 관리의 정보를 조회", description = "영상/자료를 조회한다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class))
    })
    Map<String, Object> lessonVideoList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            mapRlt.put("video", dbSvc.dbList("lesson.lessonVideoData", mapReq));
            mapRlt.put("file", dbSvc.dbList("lesson.lessonVideoFile", mapReq));


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lessonAttendList")
    @Operation(summary = "메뉴: 수업관리 > 수업 > 출석부 팝업", description = "출석부를 출력하기 위한 수강생 정보를 표시한다.")
    @Parameters({
            @Parameter(name = "les_no", required = true, description = "수강 번호", schema = @Schema(implementation = Integer.class))
    })
    Map<String, Object> lessonAttendList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonAttendDetail", mapReq));

            List lstRs = dbSvc.dbList("lesson.lessonAttendUserList", mapReq);

            // mapRlt.put("user_list" , lstRs);

            if (lstRs != null) {

                String strUserNo = "";

                for (int nLoop = 0; nLoop < lstRs.size(); nLoop++) {
                    Map mapRs = (Map) lstRs.get(nLoop);

                    if (!"".equals(strUserNo)) strUserNo += ",";

                    strUserNo += CommonUtil.nvlMap(mapRs, "std_user_no");
                }

                mapReq.put("arr_user_no", strUserNo.split(","));

                mapRlt.put("list", makeLessonAttendDateList(dbSvc.dbList("lesson.lessonAttendDateList", mapReq)));
            }

            //		 	mapRlt.put("list" , dbSvc.dbList("lesson.lessonAttendDateUserList" , mapReq));


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    List makeLessonAttendDateList(List lstData) {

        if (lstData == null || lstData.isEmpty()) {
            return null;
        }


        try {


            for (int nLoop = 0; nLoop < lstData.size(); nLoop++) {
                Map mapRs = (Map) lstData.get(nLoop);

                int nInwon = CommonUtil.nvlMapInt(mapRs, "tot_inwon");

                List lstVal = new ArrayList();

                for (int nIdx = 0; nIdx < nInwon; nIdx++) {
                    Map mapVal = new HashMap();

                    mapVal.put("user_nm", CommonUtil.nvlMap(mapRs, "user_nm_" + nIdx));
                    mapVal.put("user_att", CommonUtil.nvlMap(mapRs, "user_att_" + nIdx));

                    mapRs.remove("user_nm_" + nIdx);
                    mapRs.remove("user_att_" + nIdx);

                    lstVal.add(mapVal);

                }

                mapRs.put("list", lstVal);


            }


        } catch (Exception e) {

        }

        return lstData;
    }


    @PostMapping("/lessonStudentList")
    @Operation(summary = "메뉴: 수업관리 > 수강생", description = "수강생 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "cust_no", required = false, description = "기관 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "cust_nm", required = false, description = "기관 명", schema = @Schema(implementation = String.class))

            , @Parameter(name = "lec_no", required = false, description = "강좌 번호", schema = @Schema(implementation = String.class))

            , @Parameter(name = "lec_nm", required = false, description = "강좌 명", schema = @Schema(implementation = String.class))
            , @Parameter(name = "lec_year", required = false, description = "강좌 년도", schema = @Schema(implementation = String.class))
            , @Parameter(name = "std_user_nm", required = false, description = "수강생명", schema = @Schema(implementation = String.class))

    })
    Map<String, Object> lessonStudentList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            CommonUtil.setPageParam(mapReq); // Paging 값 세팅

            mapRlt.put("list", dbSvc.dbList("lesson.lessonStudentList", mapReq));
            mapRlt.put("count", dbSvc.dbCount("lesson.lessonStudentCount", mapReq));

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lessonStudentFeedbackList")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업", description = "수강생 목록에서 피드백 팝업의 목록을 조회한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = false, description = "수강신청 번호", schema = @Schema(implementation = Integer.class))

    })
    Map<String, Object> lessonStudentFeedbackList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            List lstRs = dbSvc.dbList("lesson.lessonStudentFeedbackList", mapReq);

            mapRlt.put("list", lstRs);

            if (lstRs == null) {
                mapRlt.put("count", "0");
            } else {
                mapRlt.put("count", lstRs.size());
            }


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/insertFeedback")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업(피드백 등록) ", description = "피드백을 저장한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "ctnt", required = true, description = "피드백 내용", schema = @Schema(implementation = String.class))

    })


    public Map<String, Object> insertFeedback(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();


        int rtnInt = 0;

        try {

            dbSvc.dbInsert("lesson.insertFeedback", mapReq);

            dbSvc.dbInsert("lesson.updateFeedbackCount", mapReq);

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/deleteFeedback")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 피드백 팝업(피드백 삭제) ", description = "피드백을 삭제한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청 번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "fb_no", required = true, description = "피드백 번호", schema = @Schema(implementation = Integer.class))

    })

    public Map<String, Object> deleteFeedback(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();


        int rtnInt = 0;

        try {

            dbSvc.dbInsert("lesson.deleteFeedback", mapReq);
            dbSvc.dbInsert("lesson.updateFeedbackCount", mapReq);

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/insertStudent")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 등록 팝업(수강생 등록) ", description = "수강생 저장한다.")
    @Parameters({
            @Parameter(name = "std_user_no", required = true, description = "수강생 번호(tb_user에서 조회한다.)", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "lec_no", required = true, description = "강좌번호", schema = @Schema(implementation = Integer.class))

    })


    public Map<String, Object> insertStudent(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();


        int rtnInt = 0;

        try {
            List lstRs = dbSvc.dbList("lesson.lessonStudentDupChk", mapReq);

            if (lstRs == null || lstRs.isEmpty()) {

                dbSvc.dbInsert("lesson.insertLectureapp", mapReq);
                dbSvc.dbUpdate("lecture.updateLectureInwonCount", mapReq);

                CtrlUtil.settingRst(mapRlt, 200, "정상적으로 저장되었습니다.", "Y");
            } else {
                CtrlUtil.settingRst(mapRlt, 400, "이미 등록된 수강생입니다.", "N");
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }

    @PostMapping("/insertStudentExcelUpload")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 등록 팝업(엑셀 등록) ", description = "엑셀 업로드를 통해 수강생 저장한다.")
    @Parameters({
            @Parameter(name = "up_file", required = true, description = "업로드 파일명", schema = @Schema(implementation = String.class))
    })


    public Map<String, Object> insertStudentExcelUpload(HttpServletRequest request, HttpServletResponse response, MultipartHttpServletRequest requestFile) throws JsonProcessingException {

        Map<String, Object> mapReq = CommonUtil.getRequestFileMap(request, "/upload/excel/", false); // requestEntity

        Map<String, Object> mapRlt = new HashMap();

        try {

            // 기관명, 강좌번호, 강좌명, 수강생번호, 수강생명

            FileManager fileMgr = new FileManager(request, dbSvc);

            mapReq.put(CoreConst.INPUT_DUPCHECK, "Y");
            mapReq.put("excel_header_id", CoreConst.EXCEL_HD_LESSON_STUDENT);

            mapRlt = commonService.excelUploadProc(request, response, mapReq, fileMgr);

            insertStudentExcelUploadInwon(request, response, mapReq, fileMgr);


            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }

    // 강의의 인원수를 업데이트 한다.
    public Map insertStudentExcelUploadInwon(HttpServletRequest request, HttpServletResponse response, Map mapReq, FileManager fileMgr) {

        Map<String, Object> mapResult = new HashMap<String, Object>();

        ExcelHeader excelHeader = new ExcelHeader();

        String strFlag = CommonUtil.nvlMap(mapReq, "iflag");

        int nResult = 1;

        try {

            String strExcelHDId = CommonUtil.nvlMap(mapReq, "excel_header_id").toUpperCase();

            Map mapExcelInfo = excelHeader.getHeader(strExcelHDId);

            List lstFile = (List) mapReq.get(CoreConst.MAP_UPFILE_KEY);

            //StringBuffer sb = new StringBuffer(); // fileMgr.getMsgBuffer(); // 오류 메시지

            if (lstFile != null && !lstFile.isEmpty()) {

                for (int nLoop = 0; nLoop < lstFile.size(); nLoop++) {

                    Map mapFile = (Map) lstFile.get(nLoop);
                    List lstRs = fileMgr.excelFileToList(CommonUtil.nvlMap(mapFile, "phy_file_nm"), mapExcelInfo);

                    if (lstRs != null && !lstRs.isEmpty()) {

                        for (int nIdx = 0; nIdx < lstRs.size(); nIdx++) {
                            Map mapRs = (Map) lstRs.get(nIdx);
                            dbSvc.dbUpdate("lecture.updateLectureInwonCount", mapRs);
                        }
                    }
                }
            }

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {
            super.errorLogWrite(request, mapReq, e1);

            nResult = -100;

        } catch (DataAccessException e2) {
            super.errorLogWrite(request, mapReq, e2);
            nResult = -101;

        } catch (Exception e) {
            super.errorLogWrite(request, mapReq, e);
            nResult = -102;
        }

        if (nResult < 0) {
            mapResult.put("msg", "데이터를 저장하는데 실패하였습니다. 관리자에게 문의하시길 바랍니다.");
            mapResult.put("status", "N");

        } else {
            mapResult.put("status", "Y");
        }

        return mapResult;
    }


    @PostMapping("/lessonStudentDetail")
    @Operation(summary = "메뉴: 수업관리 > 수강생 : 수강생 상세", description = "수강생 상세정보를 조회한다.")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강신청번호", schema = @Schema(implementation = Integer.class))
            , @Parameter(name = "lec_no", required = true, description = "강좌번호", schema = @Schema(implementation = Integer.class))
    })
    Map<String, Object> lessonStudentDetail(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        Map<String, Object> mapRlt = new HashMap();

        try {

            mapRlt.put("detail", dbSvc.dbDetail("lesson.lessonLectureAppDetail", mapReq));
            mapRlt.put("list", dbSvc.dbList("lesson.lessonAttendanceDetailList", mapReq));

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @GetMapping("/lectureStudentList")
    @Operation(summary = "메뉴 >수업관리 > 수강생 등록 팝업", description = "기관에 해당하는 강좌와 수강생 목록을 조회")

    @Parameters({
            @Parameter(name = "cust_no", required = true, description = "선택한 기관번호", schema = @Schema(implementation = String.class))
    })
    Map<String, Object> lectureStudentList(HttpServletRequest request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {
        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);

        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> lstRs = null;
        Map<String, Object> mapRlt = new HashMap();

        try {

            mapRlt.put("lecture", dbSvc.dbList("lecture.lectureList", mapReq));  // 강좌 목록

            mapReq.put("user_gbn_cd", "UGP001"); // 수강생
            mapRlt.put("student", dbSvc.dbList("user.userList", mapReq));

            CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


    @PostMapping("/lectureappDelete")
    @Operation(summary = "메뉴 : 수강생을 삭제한다.", description = "수강생 삭제한다.(use_yn 값을 N으로 세팅한다.) ")
    @Parameters({
            @Parameter(name = "lec_app_no", required = true, description = "수강 등록번호", schema = @Schema(implementation = Integer.class))
    })

    public Map<String, Object> lectureappDelete(HttpServletRequest request, HttpServletResponse response,
                                                RequestEntity<Map<String, Object>> requestEntity) throws JsonProcessingException {

        Map<String, Object> mapReq = super.setRequestMap(request, response, requestEntity);
        Map<String, Object> mapRlt = new HashMap();

        int deleteRow = 0;

        try {

            if (!"".equals(CommonUtil.nvlMap(mapReq, "lec_app_no"))) {

                deleteRow = dbSvc.dbUpdate("lesson.updateLectureAppUseYn", mapReq);
                CtrlUtil.settingRst(mapRlt, 200, "success", "Y");

            } else {
                CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
            }


        } catch (NullPointerException | ArrayIndexOutOfBoundsException e1) {

            super.errorLogWrite(request, mapReq, e1);
            log.error(this.getClass().getName(), e1);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (DataAccessException e2) {


            String sqlId = "";
            String sqlMsg = e2.getMessage();

            if (sqlMsg.indexOf("/*") > 0) {
                sqlId = sqlMsg.substring(sqlMsg.indexOf("/*") + 2, sqlMsg.indexOf("*/") - 1);
                mapReq.put("sql_id", sqlId);
            }

            super.errorLogWrite(request, mapReq, e2);
            log.error(this.getClass().getName(), e2);


            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");

        } catch (Exception e) {

            log.error(e.getMessage());

            CtrlUtil.settingRst(mapRlt, 500, "fail", "N");
        }

        return mapRlt;
    }


}
