package kr.co.bigsnow.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.db.DbService;
import kr.co.bigsnow.core.util.CommonUtil;
import kr.co.bigsnow.core.util.CoreConst;
import kr.co.bigsnow.core.util.ExcelHeader;
import kr.co.bigsnow.core.util.FileManager;

 
 

@Service("commonService")
public class CommonService extends StandardController{
	
//	@Resource(name = "dbSvc")
//	protected  DbService dbSvc; 
 
		 
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
	 
	
	public Map excelUploadProc(HttpServletRequest request, HttpServletResponse response, Map mapReq, FileManager fileMgr) {

		Map<String, Object> mapResult = new HashMap<String, Object>();

		ExcelHeader excelHeader = new ExcelHeader();

		String strFlag = CommonUtil.nvlMap(mapReq, "iflag");

		int nResult = 1;

		try {

			String strExcelHDId = CommonUtil.nvlMap(mapReq, "excel_header_id").toUpperCase();
			
			Map mapExcelInfo = excelHeader.getHeader(strExcelHDId);

			List lstFile = (List) mapReq.get(CoreConst.MAP_UPFILE_KEY);

			//StringBuffer sb = new StringBuffer(); // fileMgr.getMsgBuffer(); // 오류 메시지
			
			if (lstFile != null && !lstFile.isEmpty()) 
			{
				
				for (int nLoop = 0; nLoop < lstFile.size(); nLoop++) 
				{
					
					Map mapFile = (Map) lstFile.get(nLoop);
					List lstRs = fileMgr.excelFileToList(CommonUtil.nvlMap(mapFile, "phy_file_nm"), mapExcelInfo);

					StringBuffer sb = fileMgr.getMsgBuffer(); // 오류 메시지
					
					List lstProcRlt = new ArrayList();
					
					if (lstRs != null && !lstRs.isEmpty()) 
					{

						mapResult.put("total_cnt", CommonUtil.getComma(String.valueOf(lstRs.size())));

						if (sb.length() > 0) {

							setDbProcMsg(lstProcRlt,  "0" , "Error:" + sb.toString() );
							
							mapResult.put("errorRs", lstProcRlt);
							mapResult.put("msg", "오류가 발생하여 모두 저장하지 않았습니다. 처음부터 다시 엑셀을 올려 주세요");
							nResult = -1;

						} else {
							
							
							if ( strExcelHDId.equals(CoreConst.EXCEL_HD_TEACHER) ) {
								if (!proCheckTeacher(request, mapReq, mapExcelInfo, lstRs, lstProcRlt)) { // 엑셀 저장전 선처리를 한다.
									if (lstProcRlt.size() > 0) {
										mapResult.put("errorRs", lstProcRlt);
										mapResult.put("msg", "DB에 저장중에 오류가 발생하였습니다.");

										nResult = -1;									
									}
									continue;
								}
								
							} else	if (  strExcelHDId.equals(CoreConst.EXCEL_HD_STUDENT)  ) {
									if (!proCheckStudent(request, mapReq, mapExcelInfo, lstRs, lstProcRlt)) { // 엑셀 저장전 선처리를 한다.
										if (lstProcRlt.size() > 0) {
											mapResult.put("errorRs", lstProcRlt);
											mapResult.put("msg", "DB에 저장중에 오류가 발생하였습니다.");

											nResult = -1;									
										}
										continue;
									}
										
								
							} else if ( strExcelHDId.equals(CoreConst.EXCEL_HD_LESSON_STUDENT) ) {
								if (!proCheckLessionStudent(request, mapReq, mapExcelInfo, lstRs, lstProcRlt)) { // 엑셀 저장전 선처리를 한다.
									if (lstProcRlt.size() > 0) {
										mapResult.put("errorRs", lstProcRlt);
										mapResult.put("msg", "DB에 저장중에 오류가 발생하였습니다.");

										nResult = -1;									
									}
									continue;
								}
							} else if ( strExcelHDId.equals(CoreConst.EXCEL_HD_SUBJECT) ) {
								if (!proCheckSubjectStudent(request, mapReq, mapExcelInfo, lstRs, lstProcRlt)) { // 엑셀 저장전 선처리를 한다.
									if (lstProcRlt.size() > 0) {
										mapResult.put("errorRs", lstProcRlt);
										mapResult.put("msg", "DB에 저장중에 오류가 발생하였습니다.");

										nResult = -1;									
									}
									continue;
								}
							} 
														
							
							this.excelUploadWork(request, mapReq, mapExcelInfo, lstRs, lstProcRlt);
							
							if (lstProcRlt.size() > 0) {
								mapResult.put("errorRs", lstProcRlt);
								mapResult.put("msg", "DB에 저장중에 오류가 발생하였습니다.");

								nResult = -1;
							} else {
								mapResult.put("msg", "엑셀 파일을 정상적으로 업로드 하였습니다.");
								nResult = 1;
							}
							
						}

					} else {
						mapResult.put("msg", "엑셀파일에서 읽은 데이터 건수가  0건으로 처리하지 못하였습니다.");
					}
				}

			} else {
				mapResult.put("msg", "서버에서 업로드된 파일을 읽지 못하였습니다.");
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
	
	// sosunj
	public boolean proCheckSubjectStudent(HttpServletRequest request, Map mapReq, Map mapExcelInfo, List lstRs, List lstProcRlt ) throws Exception {
		
	//	StringBuffer sBuf = new StringBuffer();
		String       strSplitChar = String.valueOf( (char )2);
		boolean      bFlag = true;
		
		
		if ( lstRs == null || lstRs.isEmpty())
			return false;
		
		String strXpathInsert = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_INSERT); // 저장할 XPath
		String strXpathUpdate = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_UPDATE); // 수정할 XPath
		String strXpathDetail = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_DETAIL); // 조회할 XPath
		
		int nDetail = 0;
		
		for(int nIdx=0; nIdx < lstRs.size(); nIdx++)
		{
		    Map mapRs = (Map)lstRs.get(nIdx);
		    
		    Map mapDetailRs = new HashMap();
		    
			try {
			 
				 // 기관명이 존재하는지 확인
				 List lstCustRs = dbSvc.dbList("custNameList", mapRs );
				 
				 if ( lstCustRs == null || lstCustRs.isEmpty()) {
					 
				    setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 존재하지 않습니다."  );
					bFlag = false;
					continue;			
					
				 } else {
					 
					 if ( lstCustRs.size() > 1) {
						 
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 여러건이 존재합니다."  );
						bFlag = false;
						continue;
						
					 } else {
						 mapRs.put("cust_no",  CommonUtil.nvlMap((Map)lstCustRs.get(0), "cust_no"));
					 }
				 }				
				
				  System.out.println("===========================>" + mapRs.toString());						
				
				  if ( !proCodeRead(lstProcRlt,  mapRs, "sbj_grp_cd"   , "SGC", "sbj_grp_nm" )) continue; // 과목그룹 코드 조회
				  if ( !proCodeRead(lstProcRlt,  mapRs, "sbj_cls_cd"   , "SCC", "sbj_cls_nm" )) continue; // 분류 코드 조회				
				  if ( !proCodeRead(lstProcRlt,  mapRs, "class_tgt_cd" , "CTC", "class_tgt_nm" )) continue; // 수강대상 코드 조회
				  if ( !proCodeRead(lstProcRlt,  mapRs, "prog_state_cd", "PSC", "prog_state_nm" )) continue; // 상태 코드 조회
				 
				  System.out.println("===========================>END ");	
				  
				 // 데이터가 존재하는지 확인
				  mapDetailRs = dbSvc.dbDetail(strXpathDetail, mapRs);  // 회원ID가 중복이 되는지 확인
				  
				  if ( !"".equals(strXpathDetail)) {
						String strDupChk = CommonUtil.nvlMap(mapReq, CoreConst.INPUT_DUPCHECK);
						
						if ( "Y".equals(strDupChk) && mapDetailRs != null )
						{
							setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:[" + CommonUtil.nvlMap(mapRs, "cust_nm") + "][" + CommonUtil.nvlMap(mapRs, "sbj_grp_nm") +"][" + CommonUtil.nvlMap(mapRs, "sbj_nm") +"] DB에 동일 데이터가 이미 존재합니다." );
							bFlag = false;
							continue;
						}
				  } 
					 

				 
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String strError = e.toString();
				
				String strErrNm = "java.sql.SQLDataException:";
				
				try {
					strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
					strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
				} catch (Exception e1) {
					 
					e1.printStackTrace();
					strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
				}
				
				
				setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
				
				bFlag = false;
			}
			 
			
		}
		
	 
		return bFlag;
	}		
	   

	public boolean proCodeRead( List lstProcRlt,  Map mapRs, String strField, String strReprCd, String strNm ) throws Exception {
		
		boolean bFlag = true;
		
		try {
			
			  String strVal = CommonUtil.nvlMap(mapRs, strNm);
			
		
			  if ( "".equals(strVal)) 
				  return true;
			
			  Map mapParam = new HashMap();
			  
			  mapParam.put("repr_cd", strReprCd);
			  mapParam.put("nm"     , strVal );
	
			  
	  
			  
			  List lstCode = dbSvc.dbList("code.codeNameList", mapParam);
			  
			  if ( lstCode == null || lstCode.isEmpty()) {
				  setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:[" + strReprCd + "][" + strVal + "]에 해당하는 공통코드가 존재하지 않습니다. 관리자에게 문의하여 주세요");
				  return false;
			  }
			  
			  if ( lstCode.size() > 1 ) {
				  setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:[" + strReprCd + "][" + strVal + "]에 해당하는 공통코드가 여러건이 존재합니다. 관리자에게 문의하여 주세요");
				  return false;				  
			  }

			  Map mapData = (Map)lstCode.get(0);			  
			  mapRs.put(strField, CommonUtil.nvlMap(mapData, "cd"));
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			String strError = e.toString();
			
			String strErrNm = "java.sql.SQLDataException:";
			
			try {
				strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
				strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
			} catch (Exception e1) {
				 
				e1.printStackTrace();
				strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
			}
			
			
			setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
			
			bFlag = false;
		}
		
		return bFlag;
	}
	
	
	public boolean proCheckTeacher(HttpServletRequest request, Map mapReq, Map mapExcelInfo, List lstRs, List lstProcRlt ) throws Exception {
		
	//	StringBuffer sBuf = new StringBuffer();
		String       strSplitChar = String.valueOf( (char )2);
		boolean      bFlag = true;
		
		
		if ( lstRs == null || lstRs.isEmpty())
			return false;
		
		String strXpathInsert = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_INSERT); // 저장할 XPath
		String strXpathUpdate = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_UPDATE); // 수정할 XPath
		String strXpathDetail = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_DETAIL); // 조회할 XPath
		
		int nDetail = 0;
		
		for(int nIdx=0; nIdx < lstRs.size(); nIdx++)
		{
		    Map mapRs = (Map)lstRs.get(nIdx);
		    
		    Map mapDetailRs = new HashMap();
		    
			try {

				
				 // User Id를 핸드폰 번호로 생성한다. 
				 String strHpNo = CommonUtil.nvlMap(mapRs, "hp_no");  
				 
				 strHpNo = strHpNo.replaceAll("-", "");
				 
				 if ( "".equals(strHpNo) || strHpNo.length() < 11 ) 
				 {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:핸드폰 번호[" + CommonUtil.nvlMap(mapRs, "hp_no") + "]가 잘못 되었습니다." );
						bFlag = false;
						continue;
				 }
				 
				 mapRs.put("user_id", strHpNo.substring(3));
				 
				 // 데이터가 존재하는지 확인
				  mapDetailRs = dbSvc.dbDetail("user.userIdDetail", mapRs);  // 회원ID가 중복이 되는지 확인
				  
				  if ( !"".equals(strXpathDetail)) {
						String strDupChk = CommonUtil.nvlMap(mapReq, CoreConst.INPUT_DUPCHECK);
						
						if ( "Y".equals(strDupChk) && mapDetailRs != null )
						{
							setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:강사ID는 핸드폰번호 8자리로 생성됩니다. [" + CommonUtil.nvlMap(mapRs, "user_id") + "] DB에 동일 데이터가 이미 존재합니다." );
							bFlag = false;
							continue;
						}
				  } 
					
				 // 기관명이 존재하는지 확인
				 List lstCustRs = dbSvc.dbList("custNameList", mapRs );
				 
				 if ( lstCustRs == null || lstCustRs.isEmpty()) {
					 
				    setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 존재하지 않습니다."  );
					bFlag = false;
					continue;			
					
				 } else {
					 
					 if ( lstCustRs.size() > 1) {
						 
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 여러건이 존재합니다."  );
						bFlag = false;
						continue;
						
					 } else {
						 mapRs.put("cust_no",  CommonUtil.nvlMap((Map)lstCustRs.get(0), "cust_no"));
						 mapRs.put("user_grp_cd", "UGP002");  // 회원구분코드
					 }
				 }
				 
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String strError = e.toString();
				
				String strErrNm = "java.sql.SQLDataException:";
				
				try {
					strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
					strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
				} catch (Exception e1) {
					 
					e1.printStackTrace();
					strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
				}
				
				
				setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
				
				bFlag = false;
			}
			 
			
		}
		
	 
		return bFlag;
	}
		   
	
	public boolean proCheckStudent(HttpServletRequest request, Map mapReq, Map mapExcelInfo, List lstRs, List lstProcRlt ) throws Exception {
		
	//	StringBuffer sBuf = new StringBuffer();
		String       strSplitChar = String.valueOf( (char )2);
		boolean      bFlag = true;
		
		
		if ( lstRs == null || lstRs.isEmpty())
			return false;
		
		String strXpathInsert = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_INSERT); // 저장할 XPath
		String strXpathUpdate = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_UPDATE); // 수정할 XPath
		String strXpathDetail = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_DETAIL); // 조회할 XPath
		
		int nDetail = 0;
		
		Map mapUserDup = new HashMap();
		
		for(int nIdx=0; nIdx < lstRs.size(); nIdx++)
		{
		    Map mapRs = (Map)lstRs.get(nIdx);
		    
		    Map mapDetailRs = new HashMap();
		    
			try {

				 // User Id를 핸드폰 번호로 생성한다. 
				 String strHpNo = CommonUtil.nvlMap(mapRs, "hp_no");  
				 
				 strHpNo = strHpNo.replaceAll("-", "");
				 
				 if ( "".equals(strHpNo) || strHpNo.length() < 11 ) 
				 {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:핸드폰 번호[" + CommonUtil.nvlMap(mapRs, "hp_no") + "]가 잘못 되었습니다." );
						bFlag = false;
						continue;
				 }
				
				 String strUserId = strHpNo.substring(3);
				 
				 
				 if ( strUserId.equals( CommonUtil.nvlMap(mapUserDup, "user_id")) ) 
				 {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:엑셀에 동일한 핸드폰 번호[" + CommonUtil.nvlMap(mapRs, "hp_no") + "]가 여러건이 존재합니다." );
						
						mapRs.put("excel_col", "H열");
						
						bFlag = false;
						continue;
				 }
				 
				 mapRs.put("user_id"     , strUserId);
				 mapUserDup.put("user_id", strUserId);
				
				 // 데이터가 존재하는지 확인
				  mapDetailRs = dbSvc.dbDetail("user.userIdDetail", mapRs);  // 회원ID가 중복이 되는지 확인
				  
				  if ( !"".equals(strXpathDetail)) {
						String strDupChk = CommonUtil.nvlMap(mapReq, CoreConst.INPUT_DUPCHECK);
						
						if ( "Y".equals(strDupChk) && mapDetailRs != null )
						{
							setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:회원ID는 핸드폰번호 8자리로 생성됩니다. [" + CommonUtil.nvlMap(mapRs, "user_id") + "] DB에 동일 데이터가 이미 존재합니다." );
							bFlag = false;
							continue;
						}
				  } 
					
				 // 기관명이 존재하는지 확인
				 List lstCustRs = dbSvc.dbList("custNameList", mapRs );
				 
				 if ( lstCustRs == null || lstCustRs.isEmpty()) {
					 
				    setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 존재하지 않습니다."  );
					bFlag = false;
					continue;			
					
				 } else {
					 
					 if ( lstCustRs.size() > 1) {
						 
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 여러건이 존재합니다."  );
						bFlag = false;
						continue;
						
					 } else {
						 mapRs.put("cust_no",  CommonUtil.nvlMap((Map)lstCustRs.get(0), "cust_no"));
						 mapRs.put("user_grp_cd", "UGP001");  // 회원구분코드
					 }
				 }
				 
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String strError = e.toString();
				
				String strErrNm = "java.sql.SQLDataException:";
				
				try {
					strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
					strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
				} catch (Exception e1) {
					 
					e1.printStackTrace();
					strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
				}
				
				
				setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
				
				bFlag = false;
			}
			 
			
		}
		
	 
		return bFlag;
	}	
	
	
	public boolean proCheckLessionStudent(HttpServletRequest request, Map mapReq, Map mapExcelInfo, List lstRs, List lstProcRlt ) throws Exception {
		
	//	StringBuffer sBuf = new StringBuffer();
		String       strSplitChar = String.valueOf( (char )2);
		boolean      bFlag = true;
		
		
		if ( lstRs == null || lstRs.isEmpty())
			return false;
		
		String strXpathInsert = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_INSERT); // 저장할 XPath
		String strXpathUpdate = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_UPDATE); // 수정할 XPath
		String strXpathDetail = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_DETAIL); // 조회할 XPath
		
		int nDetail = 0;
		
		for(int nIdx=0; nIdx < lstRs.size(); nIdx++)
		{
		    Map mapRs = (Map)lstRs.get(nIdx);
		    
		    Map mapDetailRs = new HashMap();
		    
			try {
				
				 // 기관명이 존재하는지 확인
				 List lstCustRs = dbSvc.dbList("cust.custNameList", mapRs );
				 
				 if ( lstCustRs == null || lstCustRs.isEmpty()) {
					 
				    setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 존재하지 않습니다."  );
					bFlag = false;
					continue;			
					
				 } else {
					 
					 if ( lstCustRs.size() > 1) {
						 
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:기관명[" + CommonUtil.nvlMap(mapRs, "cust_nm")  +"]이 DB에 여러건이 존재합니다."  );
						bFlag = false;
						continue;
						
					 } else {
						 mapRs.put("cust_no",  CommonUtil.nvlMap((Map)lstCustRs.get(0), "cust_no"));
					 }
				 }				  
		
				 
				  Map mapParam = dbSvc.dbDetail("lecture.lectureNameSearch", mapRs);  // 강좌번호가 있는지  TB_LECTURE
				  
				  int nCnt = CommonUtil.nvlMapInt(mapParam, "cnt");
				  
				  if ( mapParam == null || mapParam.isEmpty() || nCnt == 0  ) {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:강좌명[" + CommonUtil.nvlMap(mapRs, "lec_nm") + "]이 DB에 존재하지 않습니다." );
						bFlag = false;
						continue; 
				  }
				  
				  if ( nCnt > 1 ) {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:강좌명[" + CommonUtil.nvlMap(mapRs, "lec_nm") + "]이 DB에 여러건이 존재합니다." );
						bFlag = false;
						continue; 
				  }
				  
				  mapRs.put("lec_no", CommonUtil.nvlMap(mapParam, "lec_no"));
				 
				  List lstUser = dbSvc.dbList("user.userNameHpList", mapRs);  // 수강생이 있는지 점검
				  
				  if ( lstUser == null || lstUser.isEmpty()) {
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:수강생 [" + CommonUtil.nvlMap(mapRs, "user_no") + ", " +  CommonUtil.nvlMap(mapRs, "hp_no")  +"] DB에 존재하지 않습니다." );
						bFlag = false;
						continue; 
				  } else if ( lstUser.size() > 1 ) {
							setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:수강생 [" + CommonUtil.nvlMap(mapRs, "user_no") + ", " +  CommonUtil.nvlMap(mapRs, "hp_no")  +"] DB에 존재하지 않습니다." );
							bFlag = false;
							continue; 
				  }
				  
				  mapRs.put("std_user_no", CommonUtil.nvlMap( ( Map)lstUser.get(0), "user_no"));
				  
				  // 데이터가 존재하는지 확인
				  Map mapDup  = dbSvc.dbDetail("lesson.lessonStudentDupChk", mapRs );
				  
				  if ( !"".equals(strXpathDetail)) {
						String strDupChk = CommonUtil.nvlMap(mapReq, CoreConst.INPUT_DUPCHECK);
						
						if ( "Y".equals(strDupChk) && mapDup != null  )
						{
							setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:강좌 정보에 이미 존재합니다. [" + CommonUtil.nvlMap(mapRs, "user_nm") + "]" );
							bFlag = false;
							continue;
						}
				  } 
					

				 
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String strError = e.toString();
				
				String strErrNm = "java.sql.SQLDataException:";
				
				try {
					strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
					strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
				} catch (Exception e1) {
					 
					e1.printStackTrace();
					strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
				}
				
				
				setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
				
				bFlag = false;
			}
			 
			
		}
		
	 
		return bFlag;
	}	
	
	
	public void setDbProcMsg(List lstProcRlt, String strRow, String strMsg)
	{
		
		Map mapRs = new HashMap();
		
		mapRs.put("excel_row",  strRow);
		mapRs.put("msg"      ,  strMsg);
		
		lstProcRlt.add(mapRs);
		
		
	}
		   
	public void excelUploadWork(HttpServletRequest request, Map mapReq, Map mapExcelInfo, List lstRs, List lstProcRlt ) throws Exception {
		
		StringBuffer sBuf = new StringBuffer();
		String       strSplitChar = String.valueOf( (char )2);
		
		if ( lstRs == null )
			return;
		
		String strXpathInsert = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_INSERT); // 저장할 XPath
		String strXpathUpdate = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_UPDATE); // 수정할 XPath
		String strXpathDetail = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_XPATH_DETAIL); // 조회할 XPath
		
		String strMapKey      = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_EXT_MAPKEY); // 외부 ExcelMap Key
		
		
		if ( lstRs == null || lstRs.isEmpty())
			return;
		
		int nDetail = 0;
		 
		for(int nIdx=0; nIdx < lstRs.size(); nIdx++)
		{
		    Map mapRs = (Map)lstRs.get(nIdx);
		    
		    Map mapDetailRs = new HashMap();
		    
			try {
				
				mapRs.put("reg_ip" ,  CommonUtil.getClientIP(request)); 
				
				//------------------------------- mapReq의 값을 mapRs에 옮긴다.
				if ( !"".equals(strMapKey)) {
					
					String[] arrMapKey = strMapKey.split(",");
					
					if ( arrMapKey.length <= 1 && !"".equals(arrMapKey[0]) ) 
					{
					    for ( int nLoop =0; nLoop < arrMapKey.length; nLoop++ )
					    {
					    	mapRs.put(arrMapKey[ nLoop] ,  CommonUtil.nvlMap(mapReq, arrMapKey[ nLoop])); 
					    }
					}
					
				}
				
				//-----------------------------------------------------------------------------
				
				if ( !"".equals(strXpathDetail)) {
					mapDetailRs = dbSvc.dbDetail(strXpathDetail, mapRs);   	
					
					String strDupChk = CommonUtil.nvlMap(mapReq, CoreConst.INPUT_DUPCHECK);
					
					if ( "Y".equals(strDupChk) && mapDetailRs != null )
					{
						setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:DB에 동일 데이터가 이미 존재합니다."  );
						continue;
					}
				} 
				
				if ( "".equals(strXpathDetail) ||  mapDetailRs == null || mapDetailRs.isEmpty()  ) {
					dbSvc.dbInsert(strXpathInsert, mapRs);   
				} else {
					dbSvc.dbUpdate(strXpathUpdate, mapRs);
				}
			} catch (Exception e) {
				
				e.printStackTrace();
				
				String strError = e.toString();
				
				String strErrNm = "java.sql.SQLDataException:";
				
				try {
					strError = strError.substring(strError.indexOf(strErrNm) + strErrNm.length() );
					strError = strError.substring(0, strError.indexOf( System.getProperty("line.separator") ));
				} catch (Exception e1) {
					 
					e1.printStackTrace();
					strError = "DB저장시 오류가 발생하여 저장하지 못하였습니다.";
				}
				
				setDbProcMsg(lstProcRlt,  CommonUtil.nvlMap(mapRs, "excel_row") , "Error:" + strError);
			}
			 
			
		}
		
	  
	}

 
	 
	 
	
}

