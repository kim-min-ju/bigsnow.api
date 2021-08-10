/*
 * Copyright (c) 2008   All rights reserved.
 */
package kr.co.bigsnow.core.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Class Summary. <br>
 * Class Description.
 * @since 1.00
 * @version 1.00 - 2008. 06. 06
 * @author KTL
 * @see
 */
public class ExcelHeader {
    /**
     * Constructor Summary. <br>
     * Constructor Description.
     * @since 1.00
     * @see
     */
    public ExcelHeader() { };

    /**
     * Throws : IOException <br>
     * Parameters : String StrDelimittoken : (ex) "/" , ".", "-" , String rtnFormmat <br>
     * Return Value : String <br>
     * 내용 : 오늘 날짜 값 가져오기 <br>
     */
    public Map getHeader(String strGbn) {

    	Map mapExcelInfo = new HashMap();

        try {
			
			if ( CoreConst.EXCEL_HD_TEACHER.equals(strGbn) || CoreConst.EXCEL_HD_STUDENT.equals(strGbn) ) // 강사  / 회원
			{
				
				String[][] arrData =   { 
						 {"A",  "cust_nm"            , CoreConst.EXCEL_FMT_STRING,  "Y"} //    기관명           
						,{"B",  "user_nm"            , CoreConst.EXCEL_FMT_STRING,  "Y"} //    회원명           
						,{"C",  "birthday"           , CoreConst.EXCEL_FMT_STRING,  "N"} //    생년월일         
						,{"D",  "gender_gbn"         , CoreConst.EXCEL_FMT_STRING,  "N"} //    성별             
						,{"E",  "user_passwd"        , CoreConst.EXCEL_FMT_STRING,  "N"} //    비밀번호         
						,{"F",  "email"              , CoreConst.EXCEL_FMT_STRING,  "N"} //    이메일           
						,{"G",  "tel_no"             , CoreConst.EXCEL_FMT_STRING,  "N"} //    전화번호         
						,{"H",  "hp_no"              , CoreConst.EXCEL_FMT_STRING,  "Y"} //    휴대전화번호     
						,{"I",  "post_no"            , CoreConst.EXCEL_FMT_STRING,  "N"} //    우편번호         
						,{"J",  "addr"               , CoreConst.EXCEL_FMT_STRING,  "N"} //    주소             
						,{"K",  "addr_detail"        , CoreConst.EXCEL_FMT_STRING,  "N"} //    상세주소         
						,{"L",  "intro"              , CoreConst.EXCEL_FMT_STRING,  "N"} //    소개             
						,{"M",  "passbook_no"        , CoreConst.EXCEL_FMT_STRING,  "N"} //    통장번호   
						
				};
				
				mapExcelInfo.put(CoreConst.EXCEL_FIELD_INFO,  arrData );

				mapExcelInfo.put(CoreConst.EXCEL_SKIP_HEADER ,  1);   // Header skip 라인 수
				mapExcelInfo.put(CoreConst.EXCEL_EOF         ,  "A");  // 엑셀의 마지막 라인에 도달 했을 경우
				
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_INSERT ,  "user.insertUser");   // 저장할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_UPDATE ,  "user.updateUser");   // 수정할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_DETAIL ,  "user.userIdDetail");   // 조회할 XPath
				
				mapExcelInfo.put(CoreConst.EXCEL_TEMPLATE_FILE, "");
				
				
				mapExcelInfo.put(CoreConst.EXCEL_EXT_MAPKEY, "user_grp_cd");
				
				
				
			}	else if ( CoreConst.EXCEL_HD_LESSON_STUDENT.equals(strGbn)) { // 수업 관리 > 수강생 등록
				
				// 엑셀 COL명, Map에 담길 Key 값, 데이터 형식, 필수여부(Y/N)
				String[][] arrData =   { 
						
						 {"A",  "cust_nm"    , CoreConst.EXCEL_FMT_STRING,  "Y"} //    기관명                   
						,{"B",  "lec_nm"     , CoreConst.EXCEL_FMT_STRING,  "Y"} //    강좌명          
						,{"C",  "hp_no"      , CoreConst.EXCEL_FMT_STRING,  "N"} //    수강생전화번호         
						,{"D",  "user_nm"    , CoreConst.EXCEL_FMT_STRING,  "N"} //    수강생명             
					
				};
				
				mapExcelInfo.put(CoreConst.EXCEL_FIELD_INFO,  arrData );
				// mapExcelInfo.put(CoreConst.EXCEL_INIT_VALUE,  arrInitVal );
				
				mapExcelInfo.put(CoreConst.EXCEL_SKIP_HEADER ,  1);   // Header skip 라인 수
				mapExcelInfo.put(CoreConst.EXCEL_EOF         ,  "A");  // 엑셀의 마지막 라인에 도달 했을 경우
				
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_INSERT ,  "lesson.insertLectureapp");   // 저장할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_UPDATE ,  "");   // 수정할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_DETAIL ,  "lesson.lessonStudentDupChk");   // 조회할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_TEMPLATE_FILE, "");
				
			}	else if ( CoreConst.EXCEL_HD_SUBJECT.equals(strGbn)) { 
				
				// 엑셀 COL명, Map에 담길 Key 값, 데이터 형식, 필수여부(Y/N)
				String[][] arrData =   { 
						
						 {"A",  "cust_nm"        , CoreConst.EXCEL_FMT_STRING,  "Y"} //    기관명           
						,{"B",  "sbj_grp_nm"     , CoreConst.EXCEL_FMT_STRING,  "Y"} //    과목그룹          
						,{"C",  "sbj_nm"         , CoreConst.EXCEL_FMT_STRING,  "Y"} //    과목          
						,{"D",  "sbj_cls_nm"     , CoreConst.EXCEL_FMT_STRING,  "N"} //    분류         
						,{"E",  "class_tgt_nm"   , CoreConst.EXCEL_FMT_STRING,  "N"} //    수강대상             
						,{"F",  "prog_state_nm"  , CoreConst.EXCEL_FMT_STRING,  "Y"} //    상태
						,{"G",  "sbj_dsc"        , CoreConst.EXCEL_FMT_STRING,  "N"} //    강좌설명
					
				};
				
				mapExcelInfo.put(CoreConst.EXCEL_FIELD_INFO,  arrData );
				// mapExcelInfo.put(CoreConst.EXCEL_INIT_VALUE,  arrInitVal );
				
				mapExcelInfo.put(CoreConst.EXCEL_SKIP_HEADER ,  1);   // Header skip 라인 수
				mapExcelInfo.put(CoreConst.EXCEL_EOF         ,  "A");  // 엑셀의 마지막 라인에 도달 했을 경우
				
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_INSERT ,  "lecture.insertSubject");   // 저장할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_UPDATE ,  "");   // 수정할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_XPATH_DETAIL ,  "lecture.SubjectDupChk");   // 조회할 XPath
				mapExcelInfo.put(CoreConst.EXCEL_TEMPLATE_FILE, "");
				
			}
        } catch (Exception e) {
            
        }

        return mapExcelInfo;
    }
    
}