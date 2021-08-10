package kr.co.bigsnow.core.util;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.NumberUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UrlPathHelper;
 

/**	
 * Class Summary. <br>
 * Class Description.
 * @version 1.00 - 2008. 06. 06
 * @author 정소선
 * @see
 */
public class CommonUtil {
    private static Random random =  new Random();

 
    public static boolean isAdmin( String strUserGrpCd )
    {
      
		if ( "UGP004".equals(strUserGrpCd) ||  "UGP006".equals(strUserGrpCd)) {
			return true;
		} else {
			return false;
		}
    
    }
    /**
	 * <pre>
	 *  HttpServletRequest의 getParameterMap을 이용하여 Request의 파라미터를 추출한다.
	 * </pre>
	 *
	 * @param request : HttpServletRequest
	 * @return Map<String, Object>
	 * @Method Name : getRequestMap
	 */

	@SuppressWarnings({"rawtypes"})
	public static Map<String, Object> getRequestMap(HttpServletRequest request) {
		Map<String, Object> mapReq = new HashMap<String, Object>();

		return getRequestMap(request, "/upload/file", false);
	}

	/**
	 * <pre>
	 *  HttpServletRequest의 getParameterMap을 이용하여 Request의 파라미터를 추출한다.
	 * </pre>
	 *
	 * @param request		 : HttpServletRequest
	 * @param strUploadFolder : 파일 업로드 폴더
	 * @return Map<String, Object>
	 * @Method Name : getRequestMap
	 */

	@SuppressWarnings({"rawtypes"})
	public static Map<String, Object> getRequestMap(HttpServletRequest request, String strUploadFolder) {
		Map<String, Object> mapReq = new HashMap<String, Object>();

		return getRequestMap(request, strUploadFolder, false);
	}

	/**
	 * <pre>
	 *  HttpServletRequest의 getParameterMap을 이용하여 Request의 파라미터를 추출한다.
	 * </pre>
	 *
	 * @param request		 : HttpServletRequest
	 * @param strUploadFolder : 파일 업로드 폴더
	 * @param bFileEncryption : 업로드 파일을 암호화해서 저장할 것인지 여부
	 * @return Map<String, Object>
	 * @Method Name : getRequestMap
	 */

	@SuppressWarnings({"rawtypes"})
	public static Map<String, Object> getRequestMap(HttpServletRequest request, String strUploadFolder, boolean bFileEncryption) {
		Map<String, Object> mapReq = new HashMap<String, Object>();

		try {
			String strContentType = nvl(request.getHeader("Content-Type"));
			boolean isFileUp = (strContentType.indexOf("multipart/form-data") > -1) ? true : false;  // 첨부파일이 존재하는지 확인

			if (isFileUp) { // 파일형식인 경우 ( enctype multipart/form-data )
				mapReq = getRequestFileMap(request, strUploadFolder, bFileEncryption);
			} else {
				mapReq = getRequestNoneFileMap(request);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}

		return mapReq;
	}

	/**
	 * <pre>
	 *  파일이 추가되지 않은 Request의 파라미터를 추출한다.
	 * </pre>
	 *
	 * @param request
	 * @return Map<String, Object>
	 * @Method Name : getRequestNoneFileMap
	 */

	@SuppressWarnings({"rawtypes"})
	public static Map<String, Object> getRequestNoneFileMap(HttpServletRequest request) {
		Map<String, Object> mapReq = new HashMap<String, Object>();

		try {
			Map<String, String[]> mapParam = request.getParameterMap();

			if (mapParam == null) {
				return null;
			}

			Iterator it = mapParam.keySet().iterator();
			Object paramKey = null;
			String[] paramValue = null;

			while (it.hasNext()) {
				paramKey = it.next();
				paramValue = (String[]) mapParam.get(paramKey);

				String strKey = paramKey.toString();

				if (paramValue.length > 1) {
					String[] arrVal = request.getParameterValues(paramKey.toString());

					for (int nLoop = 0; nLoop < arrVal.length; nLoop++) {
						arrVal[nLoop] = removeXSS(arrVal[nLoop]);
					}

					mapReq.put(strKey, arrVal);
				} else {
					mapReq.put(strKey, (paramValue[0] == null) ? "" : removeXSS(paramValue[0].trim()));
				}
			}
		} catch (Exception e) {
			return null;
		}

		return mapReq;
	}

	/**
	 * <pre>
	 *  파일이 추가된 Request의 파라미터를 추출한다.
	 * </pre>
	 *
	 * @param request
	 * @param strUploadFolder
	 * @param bFileEncryption
	 * @return Map<String, Object>
	 * @Method Name : getRequestFileMap
	 */

	public static Map<String, Object> getRequestFileMap(HttpServletRequest request, String strUploadFolder, boolean bFileEncryption) {

		FileManager fileMgr = new FileManager(request);
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map mapReq = new HashMap();

		try {

			Map mapParam = multipartRequest.getParameterMap();

			if (mapParam == null)
				return null;

			Iterator it = mapParam.keySet().iterator();
			Object paramKey = null;
			String[] paramValue = null;
			String strKey;
			String[] arrVal;

			while (it.hasNext()) {
				paramKey = it.next();
				paramValue = (String[]) mapParam.get(paramKey);

				//strKey = paramKey.toString().toLowerCase();
				strKey = paramKey.toString();

				if (paramValue.length > 1) {
					arrVal = multipartRequest.getParameterValues(paramKey.toString());

					for (int nLoop = 0; nLoop < arrVal.length; nLoop++) {
						arrVal[nLoop] = CommonUtil.removeXSS(arrVal[nLoop]);
					}

					mapReq.put(strKey, arrVal);
				} else {
					mapReq.put(strKey, (paramValue[0] == null) ? "" : CommonUtil.removeXSS(paramValue[0].trim()));
				}
			}

				/*				
				map.put("ip_addr", multipartRequest.getRemoteAddr());
				map.put("user_id", SessionUtil.getUserId());
				map.put("user_nm", SessionUtil.getUserNm());
	  			map.put("user_gbn_cd", SessionUtil.getSessionAttribute(multipartRequest, "user_gbn_cd"));
	  			map.put("team_leader_yn", SessionUtil.getSessionAttribute(multipartRequest, "team_leader_yn"));				
				*/

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		// 파일정보를 Map에 기록
		mapReq.put(CoreConst.MAP_UPFILE_KEY, fileMgr.upfileWrite(multipartRequest, strUploadFolder, bFileEncryption));

		return mapReq;
	}
    
    
    //--------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Parameters : String day, String delim <br>
	 * Return Value : String <br>
	 * 내용 : Date Format 변경. <br>
	 */
	public static String removeDateFormat(Map mapReq, String strMapKey) {

		String strDate = nvlMap(mapReq, strMapKey);

		strDate = removeDateFormat(strDate);

		mapReq.put(strMapKey, strDate);

		return strDate; // getDateString((Date)objDay, "yyyy년 MM월 dd일 HH:mm");
	}

	/**
	 * Parameters : String day, String delim <br>
	 * Return Value : String <br>
	 * 내용 : Date Format 변경. <br>
	 */
	public static String removeDateFormat(String strDate) {

		if (strDate == null)
			return "";

		strDate = strDate.replaceAll("\\.", "");
		strDate = strDate.replace("-", "");
		strDate = strDate.replace(" ", "");
		strDate = strDate.replace(":", "");

		return strDate; // getDateString((Date)objDay, "yyyy년 MM월 dd일 HH:mm");
	}


	public static String removeComma(Map mapReq, String strMapKey) {

		String strData = nvlMap(mapReq, strMapKey);

		strData = removeComma(strData);
		mapReq.put(strMapKey, strData);

		return strData;

	}

	public static String removeComma(String strData) {

		if (strData == null || "".equals(strData))
			return strData;

		strData = strData.replaceAll(",", "");

		return strData;

	}
    
    
    
	/**
	 * Method Summary. <br>
	 * 한 페이지당 표시할 파라미터를 정의한다..
	 *
	 * @param HashMap mapReq 파라미터 객체
	 * @param int	 nRowCount 한 페이지당 표시할 건수
	 * @return void
	 * @throws @since 1.00
	 * @see
	 */

	public static void setPageParam(Map mapReq) {
		
		int nPageRow = CommonUtil.nvlInt(CommonUtil.nvlMap(mapReq, "page_row_count"), 10);
		
		setPageParam(mapReq, nPageRow);
	}

	public static void setPageParam(Map mapReq, int nPageRowCount) {

		if (mapReq == null)
			return;

		try {
			int nCurrPage = getPageNow(mapReq, "page_now");

			if (nCurrPage <= 0) {
				nCurrPage = 1;

			}
			mapReq.put("page_now", nCurrPage);

			int nStartRow  = (nCurrPage - 1) * nPageRowCount + 1;
			int nEndRow    = nCurrPage * nPageRowCount;

			mapReq.put("start_row"    , (nStartRow - 1));
			//mapReq.put("end_row", nEndRow);
			mapReq.put("end_row"      , nPageRowCount);
			mapReq.put("page_row_count", nPageRowCount);

		} catch (Exception e) {

		}
	}
    

	/**
	 * Method Summary. <br>
	 * 현재 페이지 값을 조회한다. method.
	 *
	 * @param HashMap mapReq 파라미터 객체
	 * @param String  strMapKey HashMap의 키
	 * @return int 현재 페이지 값
	 * @throws @since 1.00
	 * @see
	 */
	public static int getPageNow(Map mapReq, String strMapKey) {
		int nPage = 1;

		if (mapReq == null)
			return nPage;

		try {
			if (nvlMap(mapReq, strMapKey) == null) {
				mapReq.put(strMapKey, 1);
			}
			nPage = Integer.parseInt(nvlMap(mapReq, strMapKey, "1"));
		} catch (Exception e) {
			nPage = 1;
		}

		return nPage;
	}
	
	
    
    /**
     * <pre>
     * 배열 의 해당값 포함여부 (객체의 equals 메소드를 사용하여 비교)
     * </pre>
     *
     * @param array 배열
     * @param value 값
     * @return 배열 의 해당값 포함여부
     */
    public static boolean arrayContains(Object[] array, Object value) {
        boolean isContains = false;

        if (isArray(array)) {
            for (Object obj : array) {
                isContains = obj.equals(value);

                if (isContains) {
                    break;
                }
            }
        }

        return isContains;
    }

    /**
     * DB에 들어간 Html 문자열을 Html Tag 형태로 변환해 준다. <br>
     * Method Description.
     * @param str 변환할 대상 문자열
     * @return 변환완료된 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String convertDBToHtml( String str ) {
        String strTemp = "";

        if( str == null ) {
        	str = "";
        }

        strTemp = str.replaceAll( "&lt;", "<" );
        strTemp = strTemp.replaceAll( "&gt;", ">" );
        strTemp = strTemp.replaceAll( "&nbsp;", " " );
        strTemp = strTemp.replaceAll( "<p> </p>", "<p>&nbsp;</p>" );
        strTemp = strTemp.replaceAll( "<div> </div>", "<div>&nbsp;</div>" );
        strTemp = strTemp.replaceAll( "&quot;", "\"" );
        strTemp = strTemp.replaceAll( "&amp;", "&" );

        return strTemp;
    };

    public static String convertHtmlTags(Object obj) {
    	if ( obj == null ) {
    		return "";
    	}

    	return convertHtmlTags(obj.toString());
    }

    public static String convertHtmlTags(String s) {
        s = s.replaceAll("<[^>]*>", "");    // 정규식 태그삭제
        s = s.replaceAll("\r\n", " ");      // 엔터제거

        return s;
    }

    /**
     * 자바스크립트에 "", 또는 ''안에 들어갈 문자 생성 " : \" ' : \' carrige return : \n \ : \\
     * @param pm_sSrc 수정을 원하는 문자열
     * @return 특수문자가 Replace된 문자열
     */
    public static String convertSpChar(String pm_sSrc) {
        if (pm_sSrc == null) {
            return "";
        }

        StringBuffer lm_sBuffer = new StringBuffer();
        char[] charArray = pm_sSrc.toCharArray();

        for (int i = 0; i < charArray.length; i++) {

            if (charArray[i] == '\"') {
                lm_sBuffer.append("\\\"");
            } else if (charArray[i] == '\'') {
                lm_sBuffer.append("\\\'");
            } else if (charArray[i] == '\n') {
                lm_sBuffer.append("\\n");
            } else if (charArray[i] == '\\') {
                lm_sBuffer.append("\\\\");
            } else {
                lm_sBuffer.append(charArray[i]);
            }
        }

        return lm_sBuffer.toString();
    }

    /**
     * HTML BODY, TD등에 텍스트로 보여줄 문자 생성 value값으로 들어갈 경우 value="str" 이런 형태이어야 한다. value='str', value=str
     * 이런건 예외상황이 발생할 수 있다. < : &lt; > : &gt; & : &amp; space : &nbsp; " : &quot;
     * @param pm_sSrc 수정을 원하는 문자열
     * @return 특수문자가 Replace된 문자열
     */
    public static String convertSpMark(String pm_sSrc) {
        if (pm_sSrc == null) {
            return "";
        }

        StringBuffer lm_sBuffer = new StringBuffer();
        char[] charArray = pm_sSrc.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '<') {
                lm_sBuffer.append("&lt;");
            } else if (charArray[i] == '>') {
                lm_sBuffer.append("&gt;");
            } else if(charArray[i] == '&') {
                lm_sBuffer.append("&amp;");
            } else if(charArray[i] == ' ') {
                lm_sBuffer.append("&nbsp;");
            } else if (charArray[i] == '\"') {
                lm_sBuffer.append("&quot;");
            } else {
                lm_sBuffer.append(charArray[i]);
            }
        }

        return lm_sBuffer.toString();
    }

    /**
     * str 문자열 중에서 오른쪽에 delChar와 같은 문자를 삭제하여 반환한다.
     * @param str 입력문자열
     * @param delChar 삭제할 문자
     * @return 오른쪽부분이 delChar가 삭제 처리된 문자열
     */
    public static String delRightChar(String str, char delChar) {
        String value = str;

        while (value.length() > 0) {
            int i = value.length() - 1;
            if (value.charAt(i) == delChar) {
                value = value.substring(0, i);
            } else
                break;
        }

        return value;
    }

    /**
     * double을 소수점이하 자리수를 원하는 형태로 변환(String)으로 반환
     * @param d
     * @param i - 소숫점이하 자리수
     * @return
     */
    public static String doubleStr(double d, int i) {
        DecimalFormat decimalformat = null;
        String szForm = "######0";
        String s = "";

        if (i > 0) {
            for (int z = 0; z < i; z++) {
                if (z == 0) {
                    szForm += ".0";
                } else {
                    szForm += "0";
                }
            }
        }

        decimalformat = new DecimalFormat(szForm);

        s = decimalformat.format(d);
        return s;
    }

    /**
     * double을 원하는 형태의 포맷으로 변환(String)으로 반환 i == 1 : 소숫점이하 한자리 i == 2 : 컴마로 구분된 숫자 i == 3 : 붙어있는숫자
     * @param d
     * @param i
     * @return
     */
    public static String doubleToStr(double d, int i) {
        DecimalFormat decimalformat = null;
        String s = "";

        if (i == 1) {
            decimalformat = new DecimalFormat("######0.#");
        } else if (i == 2) {
            decimalformat = new DecimalFormat("#,###,##0");
        } else if (i == 3) {
            decimalformat = new DecimalFormat("######0");
        } else {
            decimalformat = new DecimalFormat("#,###,##0.#####");
        }

        s = decimalformat.format(d);
        return s;
    }

    /**
     * 문자열을 디코딩한다. <br>
     * 기존 워크넷에서 사용중이며 주민등록번호와 같은 정보를 get 방식으로 url 호출시 사용된다.
     * @param strIn 디코딩할 문자열
     * @return String 디코딩된 문자열
     * @since 1.00
     * @see
     */
    public static String DSDecode(String strIn) {
        String retStr = "";

        for (int i = 0; i < (strIn.length()); i++) {
            retStr += (char) ((int) (strIn.charAt(i)) - (i % 2) - 1);
        }

        if (retStr.length() < 2 || !retStr.substring(retStr.length() - 6).equals("PASSWD")) {
            retStr = "";
        } else {
            retStr = retStr.substring(0, retStr.length() - 6);

        }

        return retStr;
    }

    /**
     * 문자열을 인코딩한다. <br>
     * 기존 워크넷에서 사용중이며 주민등록번호와 같은 정보를 get 방식으로 url 호출시 사용된다.
     * @param strIn 인코딩할 문자열
     * @return String 인코딩된 문자열
     * @since 1.00
     * @see
     */
    public static String DSEncode(String strIn) {
        strIn = strIn + "PASSWD";

        String	retStr = "";

        for (int i = 0; i < strIn.length(); i++) {
            retStr += (char) ((int) strIn.charAt(i) + (i % 2) + 1);
        }

        return retStr;
    }

    public static String getArrayValueComma(int[] rsArr) throws Exception {
        StringBuffer sb = new StringBuffer();

        if (rsArr == null)
            return "";
        for (int iLoop = 0; iLoop < rsArr.length; iLoop++) {
            sb.append((iLoop == 0) ? String.valueOf(rsArr[iLoop]) : "," + String.valueOf(rsArr[iLoop]));
        }
        
        return sb.toString();
    }

    public static String getArrayValueComma(String[] rsArr) throws Exception {
        return getArrayValueComma(rsArr, 0, 0);
    }

    public static String getArrayValueComma(String[] rsArr, int iStartPos, int iMax) throws Exception {
        StringBuffer sb = new StringBuffer();
     
        if (rsArr == null)
            return "";
        if (iMax == 0)
            iMax = rsArr.length;
        for (int iLoop = iStartPos; iLoop < rsArr.length; iLoop++) {
            sb.append((sb.length() == 0) ? String.valueOf(rsArr[iLoop]) : "," + String.valueOf(rsArr[iLoop]));
        }
       
        return sb.toString();
    }

    /**
     * <pre>
     *
     *   스트링내에 포함된 모든 특정 String을 다른 String으로 치환하는 메소드.
     *
     * </pre>
     *
     * @param str 전체 String
     * @param index_str 치환대상이 되는 특정 String
     * @param new_str 특정 String을 치환할 새로운 String
     * @return 특정 String이 다른 String으로 바뀌어진 String
     */
    public static String getChangeString(String str, String index_str, String new_str) {
        String temp = "";
        if (str != null && str.indexOf(index_str) != -1) {
            while (str.indexOf(index_str) != -1) {
                temp = temp + str.substring(0, str.indexOf(index_str)) + new_str;
                str = str.substring(str.indexOf(index_str) + index_str.length());
            }
            temp = temp + str;
        } else {
            temp = str;
        }

        return temp;
    }

    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @return String
     */
    public static String getComma(String strVal) {
    	strVal = nvl(strVal, "0");

        DecimalFormat formatter = new DecimalFormat("#,##0");

        return formatter.format(Integer.parseInt(strVal));
    }

    /**
     * Method Summary. <br>
     * LIST에서 특정값만 추출해 Map으로 담는다
     * @param listRow List 공통코드 쿼리 list
     * @param strClsCd 코드값
     * @return List
     * @throws name description
     * @since 1.00
     * @see
     */
    public static List<Map<String, Object>> getCommonCode( List<Map<String, Object>> listRow, String strClsCd ) {
    	List<Map<String, Object>> listResult 	= new ArrayList<Map<String, Object>>();
    	boolean keyCheck 		= false;

    	String strKey 			= "";			//map 키를 담는다
    	String strValue 		= "";			//map value값을 담는다
    	String strInclsCd		= "";

    	try {
    		Iterator<Map<String, Object>> iterator 	= listRow.iterator();
    		Map<String, Object> whileMap;
    		strClsCd 				= nvl(strClsCd);

    		Iterator<String> it;
    		Map<String, Object> mapResult;

    		while (iterator.hasNext()) {
    			whileMap = iterator.next();

    			// 요청키가 맞으면 루프를 빠져나온다.
    			if(keyCheck && !strClsCd.equals(String.valueOf(whileMap.get(strInclsCd)))) {
    				break;
	            }

            	it			= whileMap.keySet().iterator();
            	mapResult 	= new HashMap<String, Object>();

            	while (it.hasNext()) {
            		strKey 		= String.valueOf(it.next());
            		strValue	= String.valueOf(whileMap.get(strKey) != null ? whileMap.get(strKey) : "");		//value는 항상 null체크를 해줘야한다.
            		mapResult.put(strKey, strValue);	//모든 값을 map에 담는다.

            		// 해당키에 값이 맞는지 검사한다.
            		if(mapResult.get(strKey) != null && strClsCd.equals(mapResult.get(strKey))) {
            			strInclsCd = strKey;
            			keyCheck = true;
            		}
            	}

            	if(keyCheck) {
            		listResult.add(mapResult);
            	}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return listResult;
     }

    @SuppressWarnings("rawtypes")
	public static String getCommonCodeLabel(List resultMapList, String searchCode, String codeFiled, String labelFiled) {
        if (resultMapList != null) {
            Iterator iterator = resultMapList.iterator();

            while (iterator.hasNext()) {
                Map resultMap	= (Map) iterator.next();
                String strCode	= nvl(resultMap.get(codeFiled));

                if (strCode.equals(searchCode)) {
                    return nvl(resultMap.get(labelFiled));
                }
            }
        }

        return "";
    }

    /**
     * Method Summary. <br>
     * List에서 strField(테이블 필드명) 기준으로  Map에 담는다
     * @param listRow List 공통코드 쿼리 list
     * @param strField DB필드명
     * @return Map
     * @throws name description
     * @since 1.00
     * @see
     */
    public static Map getCommonCodeMap( List listRow, String strField ) {
    	Map			mapResult 	= new HashMap<String, List>();
  	   	List<Map>	listResult	= new ArrayList<Map>();

  	   	String strInclsCd		= "";
  	   	boolean keyCheck 		= false;

  	   	try {
  	   		Iterator<Map> iterator = listRow.iterator();
  	   		Map whileMap;

  	   		while (iterator.hasNext()) {
	           	whileMap = iterator.next();

	           	// 키가 바꼇을때 list를 map에 담는다
	           	if(keyCheck && !strInclsCd.equals(String.valueOf(whileMap.get(strField)))) {
	           		mapResult.put(strInclsCd, listResult);
	           		listResult 	= new ArrayList<Map>();
	           	}

	           	//map를 그대로 list에 담는다
	           	listResult.add(whileMap);

	           	strInclsCd = String.valueOf(whileMap.get(strField));
	           	keyCheck = true;
	        }

  	   		mapResult.put(strInclsCd, listResult);
  	   	} catch (Exception e) {
  	   		e.printStackTrace();
  	   	}

  	   	return mapResult;
     }

    public static String getConv(Object objKey, String strVal) {
        // UtilEncoder utilEncoder = new UtilEncoder();
        if (objKey != null && !objKey.equals("null")) {
            /*
             * String strKey = objKey.toString().trim(); strKey = utilEncoder.toKorean(strKey);
             */

            String strKey = objKey.toString().trim();

            if (!"".equals(strKey))
                return strKey;
        }

        return strVal;
    }

    /**
     * <pre>
     *
     *   CSV 파일을 등록시 &quot; &tilde; &quot; 사이의 콤마(,)를 치환하는 메소드.
     *
     * </pre>
     *
     * @param strBody 전체 String
     * @return 특정 String이 다른 String으로 바뀌어진 String
     */
    public static String getCSVChangeString(String strBody) {
        String temp = "";
        String sRetVal = "";
        String index_str = "\"";
        int iStartPos = 0;
        int iEndPos = 0;

        iStartPos = strBody.indexOf(index_str);

        if (iStartPos != -1) {
            sRetVal = strBody.substring(0, iStartPos);

            while (iStartPos != -1) {
                iEndPos = indexOfaA(strBody, index_str, iStartPos + 1);

                temp = strBody.substring(iStartPos + 1, iEndPos);
                sRetVal = sRetVal + getChangeString(temp, ",", "^COMMA^");
                strBody = strBody.substring(iEndPos + 1);
                iStartPos = strBody.indexOf(index_str);

                // 다음 index_str 사이의 문자열을 붙인다.
                if (iStartPos > 0) {
                    sRetVal += strBody.substring(0, (iStartPos));
                }
            }

            sRetVal += strBody;
        } else {
            sRetVal = strBody;
        }

        return sRetVal.replaceAll("\"", "").trim();
    }

    /**
     * Position Data를 얻기위해 입력된 String을 Delimeter로 토크나이징 하여 토크나이징된 토큰들을 int 배열로 반환한다.
     * @param pm_sString 토크나이징되는 문자열
     * @param pm_sDelimeter 문자열를 분리하는 delimeter 문자
     * @return 토크나이징된 Position Data토큰들의 int 갯수를 알려준다
     * @see java.util.StringTokenizer
     */
    public static int getDelimeterCount(String pm_sString, String pm_sDelimeter) {
        if (pm_sString == null) {
            return 0;
        }

        StringTokenizer lm_oTokenizer = new StringTokenizer(pm_sString, pm_sDelimeter);
        return lm_oTokenizer.countTokens();
    }

    public static String getDomainName(HttpServletRequest request) {
	   String strUrl = request.getRequestURL().toString();

    	try {
    		int nPos = strUrl.indexOf("://") + 3;

    		strUrl = strUrl.substring(nPos);
    		strUrl = strUrl.substring(0, strUrl.indexOf("/"));

    		if (strUrl.indexOf(":") > -1) {
    			strUrl = strUrl.substring(0, strUrl.indexOf(":"));
    		}
    	} catch (Exception e) {
    		
    		e.printStackTrace();
    	}

    	return "http://" + strUrl;
	}

	/**
	 * MD5암호화. <br>
	 * 암호화된 문자열을 얻는다.
	 * @param str 암호화할 문자열
	 * @return 암호화된 문자열
	 * @throws name description
	 * @since 1.00
	 * @see
	 */
	public static String getEncryptString(String str) {
		java.security.MessageDigest md5 = null;

		try {
            md5 = java.security.MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            return "";
        }

        String eip;
        byte[] bip;
        String temp = "";
        String tst	= str;

        bip = md5.digest(tst.getBytes());

        for (int i = 0; i < bip.length; i++) {
        	eip = "" + Integer.toHexString((int) bip[i] & 0x000000ff);

            if (eip.length() < 2) {
                eip = "0" + eip;
            }

            temp = temp + eip;
	    }

		return temp;
	}

    public static String getFileExt(String strFileName) {
        if (strFileName != null && !"".equals(strFileName)) {
            return strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()); // 파일확장빼기
        } else {
            return "";
        }

    }

    public static List getFileGbnList(List fileList) {
    	return getFileGbnList(fileList, "");
    }

    public static List getFileGbnList(List fileList, String strCompareGbn) {
        List valLst = new ArrayList<Map>();

    	if(fileList != null && fileList.size() > 0) {
        	for( int iLoop = 0; iLoop < fileList.size(); iLoop++ ) {
    			Map fileMap = ( Map ) fileList.get( iLoop );

    			String strFileGbn = nvl(fileMap.get("FILE_GBN"),"");

    			if (strCompareGbn.equals(strFileGbn) ||  "".equals(strCompareGbn) ) {
            		Map rowMap  = new HashMap<String, String>();

            		Iterator iter = fileMap.keySet().iterator();

            		while( iter.hasNext()) {
	            		String key    = (String)iter.next();
	            		String value  = (String)fileMap.get( key );

	            	    //System.out.println( key +": " + value );
	            	    rowMap.put(key, value);
	            	    valLst.add(rowMap);
            	   }
    			}
        	} // for( int iLoop = 0; iLoop < fileList.size(); iLoop++ )
    	} // if(fileList != null && fileList.size() > 0)

		return valLst;
	}

    public static String getFileName(Object strFileName) {

        if (strFileName != null && !"".equals(strFileName)) {
            return getFileName(strFileName.toString());
        } else {
            return "";
        }
    }

    public static String getFileName(String strFileName) {

        if (strFileName != null && !"".equals(strFileName)) {
            return strFileName.substring(strFileName.lastIndexOf("/") + 1, strFileName.length());
        } else {
            return "";
        }

    }

    /**
     * Method Summary. <br>
     * File Text Read
     * @param imgPath 이미지 경로
     * @return imgSize 이미지 사이즈 배열
     * @throws IOException 
     * @throws Exception ex
     * @since 1.00
     * @see
     */
    public static String getFileRead(String strFile) {
    	String strContext="";
    	Reader reader = null;
    	BufferedReader fin = null;
       
    	try {
        	reader = new InputStreamReader(  new FileInputStream(strFile),"UTF-8");
        	fin = new BufferedReader(reader);

        	String fileContent;

        	while ((fileContent = fin.readLine())!=null) {
        			strContext +=  fileContent + "\n";
            }

        	//Remember to call close.
        	//calling close on a BufferedReader/BufferedWriter
        	// will automatically call close on its underlying stream
        	//fin.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }finally {
        	try {
				fin.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}

        return strContext;
    }

    public static String getFileSize(Object obj) {
    	String strVal = "";

    	if ( obj == null ) {
    		return "";
    	}

    	Long nSize = Long.parseLong(obj.toString());

    	nSize /= 1000; // Byte를 KB로 환산

    	if (nSize < 1)
    		nSize = 1L;

    	if ( nSize < 1000 ) {
    		strVal = String.format("%dKB", nSize);
    	} else {
    		strVal = String.format("%.2fMB", nSize/1000.0);
    	}

    	return strVal;
    }

    /**
     * Method Summary. <br>
     * Method Description.
     * @param name description
     * @return description
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String getFormParm(HttpServletRequest request) {
        return getFormParm(request, "");
    }

    /**
     * Hidden 파라미터 생성. <br>
     * html hidden 속성의 파라미터 생성 method.
     * @param request HttpServletRequest
     * @param notParam 제외 파라미터
     * @return retQueryString
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getFormParm(HttpServletRequest request, String notParam) {
        String	retQueryString	= "";

        Map<String, String[]> parameter	= request.getParameterMap();
        Iterator	it			= parameter.keySet().iterator();
        Object		paramKey	= null;
        String[]	paramValue	= null;

        while (it.hasNext()) {
            paramKey = it.next();

            if (paramKey.equals(notParam)) {
                continue;
            }

            paramValue = (String[]) parameter.get(paramKey);

            for (int i = 0; i < paramValue.length; i++) {
                retQueryString += "<input name=\"" + paramKey + "\" type=hidden value=\"" + removeXSS(paramValue[i]) + "\" >  \n";
            }
        }

        return retQueryString;
    }

    /**
     * Hidden 파라미터 생성. <br>
     * html hidden 속성의 파라미터 생성 method.
     * @param request HttpServletRequest
     * @param notParam 제외 파라미터
     * @return retQueryString
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getFormParm(Map<String, Object> reqMap, String[] notParam) {
        String retQueryString = "";

        Map<String, Object> parameter = reqMap;
        Iterator it = parameter.keySet().iterator();
        Object paramKey = null;
        String[] paramValue = null;

        while (it.hasNext()) {
            paramKey		= it.next();
            paramValue		=  parameter.get(paramKey).toString().split(",");

            for (int i = 0; i < paramValue.length; i++) {
                for (int j = 0; j < notParam.length; j++) {
                    if (paramKey.equals(notParam[j])) {
                        continue;
                    }
                }

                retQueryString += "<input name=\"" + paramKey + "\" type='hidden' value=\"" + removeXSS(paramValue[i]) + "\" >  \n";
            }
        }

        return retQueryString;
    }

    /**
     * Method Summary. <br>
     * Front 페이지 네비게이션을 만드는 함수
     * @param functionNm 호출할 자바스크립트 function 명(ex: javascript:goList)
     * @param totCnt 게시물 총 갯수
     * @param pageNow 현재 페이지 번호
     * @param pagePerBlock 한 블럭에 표시할 페이지 번호 갯수
     * @param numPerPage 한 페이지당 게시물 라인 수
     * @return pageNavi 페이지 네비게이션 라인 문자열
     * @throw Exception
     * @since 1.00
     * @see
     */
    public static String getFrontPageNavi(String functionNm, int totCnt, int pageNow, int pagePerBlock, int numPerPage, String contextPath) {
        String	rtnNavi	= "";
        String	strUrl	= "";
        int		iNext;
        int		iPrev;

        // 총 페이지 수
        int totalPage = (int) Math.ceil(totCnt / (numPerPage * 1d));
        // 현재 페이지가 속한 블럭 번호
        int currBlock = (int) Math.ceil(pageNow / (pagePerBlock * 1d));
        // 총 블럭 갯수
        int totalBlock = (int) Math.ceil(totalPage / (pagePerBlock * 1d));
        // 현재 블록의 시작페이
        int startPage = (currBlock - 1) * pagePerBlock + 1;
        // 현재 블록의 마지막 페이지
        int endPage = startPage + pagePerBlock - 1;

        if (endPage > totalPage){
            endPage = totalPage;
        }

    	strUrl =  functionNm ;

        if (currBlock > 1) {
            iPrev = (currBlock - 1) * pagePerBlock;
            rtnNavi += "<a class='direction prev' href='" + strUrl + "(" + iPrev +")'><img src='"+contextPath+"/images/bt_awPre.gif'  alt='이전페이지' /></a>\n";
        }

        if (endPage == 0) {
            rtnNavi += "<strong>1</strong>";
        } else {
            // 현재 블록 생성
            String firstClass = "";

            for (int i = startPage; i <= endPage; i++) {
                if (i == pageNow) {
                	rtnNavi += "<strong>" + i + "</strong>\n";
                } else {
                    if( i == startPage ) {
                        firstClass = "class='first'";
                    } else {
                        firstClass = "";
                    }

                    rtnNavi += "<a href='" + strUrl + "(" + i + ")'" + firstClass + ">" + i + "</a>\n";
                }
            } // for (int i = startPage; i <= endPage; i++)
        } // if (endPage == 0)

        if (currBlock < totalBlock) {
            iNext = (currBlock * pagePerBlock) + 1;
            rtnNavi += "<a class='direction next' href='" + strUrl + "(" + iNext +")'><img src='"+contextPath+"/images/bt_awNext.gif' alt='다음페이지' /></a>\n";
        }

        return rtnNavi;
    }

    public static int[] getImageResize(HttpServletRequest req, String strImg, int nFixWidth, int nFixHeight) {
		int nRate     = 0;
		int nArrSize[] = {0,0};
		InputStream is = null;
		
		try {
	    	if ( strImg == null || "".equals(strImg) ) {
	    		return nArrSize;
	    	}

	        // URL url = new URL(strUrl);
	        // Image image = ImageIO.read(url);
	    	// Read from an input stream
	    	@SuppressWarnings("deprecation")
			String strPath = req.getRealPath("/")   + strImg;

	    	is = new BufferedInputStream( new FileInputStream(strPath));
	    	Image  image = ImageIO.read(is);

	    	int nWidth  = image.getWidth(null);
	        int nHeight = image.getHeight(null);

	        /////System.out.println("Original width = " + nWidth + ", height = " + nHeight);

	        if ( nWidth >= nFixWidth ) {	// 원하는 길이보다 넓이가 넓은 경우
	        	nRate   = ( nFixWidth * 100 ) /  nWidth  ;
	        	nWidth  = ( nWidth    *  nRate ) / 100 ;
	        	nHeight = ( nHeight   *  nRate ) / 100 ;
	        }

	        if ( nHeight >= nFixHeight ) {	// 원하는 길이보다 넓이가 넓은 경우
	        	nRate   = ( nFixHeight * 100)  / nHeight  ;
	        	nWidth  = ( nWidth     * nRate ) / 100 ;
	        	nHeight = ( nHeight    * nRate ) / 100 ;
	        }

	        /////System.out.println("Resize width = " + nWidth + ", height = " + nHeight);

	    	nArrSize[0] = nWidth;
	    	nArrSize[1] = nHeight;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }finally {
	    	try {
				is.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	    }

		return nArrSize;
	}

    public static String getImageResizeStr(HttpServletRequest req, String strImg, int nFixWidth, int nFixHeight) {
		String strImgWH = "";

		try {
		    int nArrSize[] = getImageResize(req, strImg, nFixWidth, nFixHeight);

		    if ( nArrSize[0] > 0 && nArrSize[1] > 0) {
		    	strImgWH = " width = '" + nArrSize[0] + "' height='" + nArrSize[1] + "'";
			}
		} catch ( Exception e) {
			e.printStackTrace();
		}

		return strImgWH;
	}

    @SuppressWarnings("rawtypes")
	public static String getInStr(List lstGroupMin, String strField) {
    	String strVal = "";

    	for(int nLoop=0; nLoop < lstGroupMin.size(); nLoop++) {
    		Map  dbRow = (Map) lstGroupMin.get(nLoop);

    		String strMap  = CommonUtil.getMapVal(dbRow, strField);

    		if (!"".equals(strMap)) {
	    		if (!"".equals(strVal)) strVal += ",";
	    		strVal += CommonUtil.getMapVal(dbRow, strField);
    		}
    	}

    	return strVal;
    }

    /**
	 * Method Summary. <br>
	 * List에 담겨있는 Map을 추출한다
	 * @param List list
	 * @param String strCompare 비교값
	 * @return String
	 * @throws name description
	 * @since 1.00
	 * @see
	*/
    public static Map<String, Object> getListMap( List<Map<String, Object>> list, String strCompare, String fieldNm ) {
    	Map<String, Object> mapVal = null;

    	try {
    		Iterator<Map<String, Object>> it = list.iterator();
    		Map<String, Object> whileMap;

    		while (it.hasNext()) {
    			whileMap = (Map<String, Object>) it.next();

    			if(strCompare.equals( String.valueOf(whileMap.get(fieldNm)) )) {
    				mapVal = whileMap;
    				break;
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return mapVal;
    }

    public static String[] getListToArray(List<Map<String, Object>> rsList, String strField) throws Exception {
    	return getListToArray(rsList, strField, 0, 0);
    }

    @SuppressWarnings("rawtypes")
	public static String[] getListToArray(List<Map<String, Object>> rsList, String strField, int iStartPos, int iEndPos) throws Exception{
        if (rsList == null)
            return null;

        if (iEndPos > rsList.size())
            iEndPos = rsList.size();
        if (iEndPos <= 0)
            iEndPos = rsList.size();

        String[] rsArr = new String[iEndPos - iStartPos];

        int iCnt = 0;


        for (int iLoop = 0; iLoop < iEndPos; iLoop++) {

            if (iLoop < iStartPos)
                continue;

            Map dbRow = (Map) rsList.get(iLoop);
            rsArr[iCnt++] = nvl(dbRow.get(strField));
        }
       
        return rsArr;
    }

    @SuppressWarnings("rawtypes")
	public static int[] getListToIntArray(List rsList, String strField) {
        return getListToIntArray(rsList, strField, 0, 0);
    }

    @SuppressWarnings("rawtypes")
	public static int[] getListToIntArray(List rsList, String strField, int iStartPos, int iEndPos) {
        if (rsList == null) {
            return null;
        }

        if (iEndPos > rsList.size()) {
            iEndPos = rsList.size();
        }

        if (iEndPos <= 0) {
            iEndPos = rsList.size();
        }

        int[] rsArr = new int[iEndPos - iStartPos];

        int iCnt = 0;

        try {
            for (int iLoop = 0; iLoop < iEndPos; iLoop++) {
                if (iLoop < iStartPos) {
                    continue;
                }

                Map dbRow = (Map) rsList.get(iLoop);
                rsArr[iCnt++] = Integer.parseInt(CommonUtil.getConv(dbRow.get(strField), "0"));
            }
        } catch (Exception e) {
            //System.out.println("Error ==> getListToArray() " + e.toString());
        }

        return rsArr;
    }

    @SuppressWarnings("rawtypes")
	public static float[] getListToIntArrayFloat(List rsList, String strField) {
        return getListToIntArrayFloat(rsList, strField, 0, 0);
    }

    @SuppressWarnings("rawtypes")
	public static float[] getListToIntArrayFloat(List rsList, String strField, int iStartPos, int iEndPos) {
        if (rsList == null) {
            return null;
        }

        if (iEndPos > rsList.size()) {
            iEndPos = rsList.size();
        }

        if (iEndPos <= 0) {
            iEndPos = rsList.size();
        }

        float[] rsArr = new float[iEndPos - iStartPos];

        int iCnt = 0;

        try {
            for (int iLoop = 0; iLoop < iEndPos; iLoop++) {
                if (iLoop < iStartPos) {
                    continue;
                }

                Map dbRow = (Map) rsList.get(iLoop);
                rsArr[iCnt++] = Float.parseFloat(CommonUtil.getConv(dbRow.get(strField), "0.0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rsArr;
    }

    @SuppressWarnings({ "rawtypes" })
	public static String getListValueComma(List rsList, String strField) {
        String strRetVal = "";

        try {
            if (rsList != null && rsList.size() > 0) {
                for (int iLoop = 0; iLoop < rsList.size(); iLoop++) {
                    Map dbRow = (Map) rsList.get(iLoop);
                    strRetVal += ("".equals(strRetVal)) ? nvl(dbRow.get(strField)) : "," + nvl(dbRow.get(strField));
                }
            }
        } catch (Exception e) {
            //System.out.println("Error ==> getListValueComma() " + e.toString());
        }

        return strRetVal;
    }




    /**
     * Method Summary. <br>
     * 공통코드를 checkbox로 변환하는 method.
     * Map 디폴트 키값 COMN_CD, COMN_NM
     * input 디폴트 name checkBtn
     * @param listRow List 공통코드 쿼리 list
     * @param strCompare 비교 값 다중체크일땐 String[], 단일체크 String
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
     public static String getCheckBox( List listRow, Object strCompare) {
	        return getCheckBox(listRow, strCompare, "COMN_CD", "COMN_NM", "checkBtn", "");
     }

     /**
      * Method Summary. <br>
      * 공통코드를 checkbox로 변환하는 method.
      * input 디폴트 name checkBtn
      * @param listRow List 공통코드 쿼리 list
      * @param strCompare 비교 값 다중체크일땐 String[], 단일체크 String
      * @param strCode MapCode 명칭
      * @param strCodeNm MapcodeNm 명칭
      * @return selectbox String
      * @throws name description
      * @since 1.00
      * @see
      */
      public static String getCheckBox( List listRow, Object strCompare, String strCode, String strCodeNm) {
 	        return getCheckBox(listRow, strCompare, strCode, strCodeNm, "checkBtn", "");
      }

    /**
     * Method Summary. <br>
     * 공통코드를 checkbox로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param strCompare 비교 값 다중체크일땐 String[], 단일체크 String
     * @param strCode MapCode 명칭
     * @param strCodeNm MapcodeNm 명칭
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
     public static String getCheckBox( List listRow, Object strCompare, String strCode, String strCodeNm, String strHtmlNm, String strCssNm ) {
  	   return getCheckBox(listRow, strCompare, strCode, strCodeNm, strHtmlNm, strCssNm, "");
     }

    /**
     * Method Summary. <br>
     * 공통코드를 checkbox로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param strCompare 비교 값 다중체크일땐 String[], 단일체크 String
     * @param strCode MapCode 명칭
     * @param strCodeNm MapcodeNm 명칭
     * @param parentTag <>를 제외한 부모태그명
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
     public static String getCheckBox( List listRow, Object strCompare, String strCode, String strCodeNm, String strHtmlNm, String strCssNm, String parentTag) {

	        StringBuffer sbReturn = new StringBuffer();
	        boolean objArr       = false;

	        String strCodeCd 	= "";			//value값을 담는다.
          String strName 		= "";			//name값을 담는다.
          String strSelected 	= "";
          String[] arrCompare = null;			//다중체크 배열

	        try {
	            if(strCompare instanceof String[]) {
	                objArr 		= true;
	                arrCompare 	= (String[]) strCompare;
	            }

	            Iterator iterator 	= listRow.iterator();
	            strCompare 			= getNullTrans(strCompare, "");

	            Map resultMap;

	            while (iterator.hasNext()) {
	            	resultMap 	= (Map) iterator.next();
	                strCodeCd 		= String.valueOf(resultMap.get(strCode));			//value값을 담는다.
	                strName 		= String.valueOf(resultMap.get(strCodeNm));			//name값을 담는다.

	                if (!"".equals(parentTag))
	                {
	                	sbReturn.append("<").append(parentTag).append(">");
	                }

	                //다중체크일때 배열로 들로온 값만큼 체크 해준다.
	                if(objArr) {
	                    for(int iLoop=0; iLoop<arrCompare.length; iLoop++) {
	                        //비교해서 체크해준다.
	                        if(strCodeCd.equals(arrCompare[iLoop])) {
	                            strSelected =  " checked ";
	                            break;
	                        } else {
	                            strSelected = " ";
	                        }
	                    }
	                } else {
	                	strSelected = strCodeCd.equals(String.valueOf(strCompare)) ? " checked " : " ";	//비교해서 체크해준다.
	                }

	                //strVal += " <input class='" + strCssNm + "' type='checkbox' name='" + strHtmlNm + "' id='" + strHtmlNm + "' value=\'" + strCodeCd + "\'" + strSelected + " />" + strName + "&nbsp;&nbsp; \n";
	                sbReturn
	                	.append(" <input class='").append(strCssNm).append("' type='checkbox' name='").append(strHtmlNm).append("' id='").append(strHtmlNm).append("_")
	                	.append(strCodeCd).append("' value=\'").append(strCodeCd).append("\'").append(strSelected).append(" />").append("<label for='").append(strHtmlNm).append("_").append(strCodeCd).append("'>").append(strName).append("</label>&nbsp;&nbsp;\n");

	                if (!"".equals(parentTag))
	                {
	                	sbReturn.append("</").append(parentTag).append(">");
	                }
	            }

	        } catch (Exception e) {

	        }

	        return sbReturn.toString();
     }
    /**
     * Method Summary. <br>
     * 공통코드를 checkbox로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param strCompare String selected index
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getMakeCodeName(List listRow, String strCompare) throws Exception {
        String strVal = "";

        Iterator iterator = listRow.iterator();
        strCompare = nvl(strCompare,"").trim();

        while (iterator.hasNext()) {
            Map resultMap = (Map) iterator.next();
            String strCode = resultMap.get("CD").toString();
            String strName = (String) resultMap.get("NM").toString();

            if ( strCompare.indexOf(strCode) >= 0 ) {
               if ( !"".equals(strVal))
            	strVal += " / ";

                strVal += strName;
            }
        }
         

        return strVal;
    }

    /**
     * Method Summary. <br>
     * 공통코드를 selectbox로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param strCompare String selected index
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getMakeSelectBox( List listRow, String strCompare ) throws Exception{
        String strVal = "";
    
        Iterator iterator = listRow.iterator();
        strCompare = nvl( strCompare, "" );

        while ( iterator.hasNext() ) {
            Map		resultMap	= (Map) iterator.next();
            String	strCode		= resultMap.get("comm_cd").toString();
            String	strName		= (String) resultMap.get("cd_nm").toString();
            String	strSelected	= strCode.equals( strCompare ) ? " selected " : " ";

            strVal += " <option value=\'" + strCode + "\'" + strSelected + ">" + strName + "</option>\n";
        }

         return strVal;
     }

    /**
     * Method Summary. <br>
     * 쿼리 결과를를 selectbox로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param sCd String 코드(option value)
     * @param sNm String 코드명(option name)
     * @param strCompare String selected index
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getMakeSelectBox(List listRow, String sCd, String sNm, String strCompare) throws Exception {
        String strVal = "";

        if( listRow == null || listRow.isEmpty() ) {
        	return strVal;
        }

        Iterator iterator = listRow.iterator();
        strCompare = nvl( strCompare, "" );

        while (iterator.hasNext()) {
            Map resultMap = (Map) iterator.next();

            String strCode = resultMap.get(sCd).toString();
            String strName = (String) resultMap.get(sNm).toString();
            String strSelected = strCode.equals(strCompare) ? " selected " : "";

            strVal += " <option value=\'" + strCode + "\'" + strSelected + ">" + strName + "</option>\n";
        }
      
        return strVal;
    }

    /**
     * Method Summary. <br>
     * 쿼리 결과를를 radio버튼으로 변환하는 method.
     * @param listRow List 공통코드 쿼리 list
     * @param rId 라디오 버튼 ID
     * @param rCd String 코드(option value)
     * @param rNm String 코드명(option name)
     * @param strCompare String selected index
     * @return selectbox String
     * @throws name description
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
    public static String getMakeRadioBox(List listRow, String rId, String rCd, String rNm, String strCompare) throws Exception{
    	String strVal = "";

    		if( listRow == null || listRow.isEmpty() ) {
    			return strVal;
    		}

    		Iterator iterator = listRow.iterator();
    		strCompare = nvl( strCompare, "" );

    		while (iterator.hasNext()) {
    			Map resultMap = (Map) iterator.next();

    			String strCode = resultMap.get(rCd).toString();
    			String strName = (String) resultMap.get(rNm).toString();
    			String strSelected = strCode.equals(strCompare) ? " checked " : "";

    			strVal += " <label><input type=\"radio\" id=\""+ rId +"\" name=\""+ rId +"\" value=\'" + strCode + "\' " + strSelected + ">"+ strName +"</label>\n";
    		}

    	return strVal;
    }

    @SuppressWarnings("rawtypes")
	public static String getMapVal(Map map, String strKey) {
        return getMapVal(map, strKey, "");
    }

    @SuppressWarnings("rawtypes")
	public static String getMapVal(Map map, String strKey, String strVal) {
        String strMapVal = strVal;

        if (map == null || "".equals(strKey))
        	return strVal;

        if (map.get(strKey) != null && map.get(strKey) != "")
            strMapVal = map.get(strKey).toString().trim();

        return strMapVal;
    }

    /**
     * Method Summary. <br>
     * Request의 필드값을 배열로 넘긴다. method
     * @param List 파일목록
     * @param int  파일갯수
     * @return String
     * @throws
     * @since 1.00
     * @see
     */
    public static String[] getMapValArray(Map<String, Object> mapReq, String strField) {
	   Object objVal    = mapReq.get(strField);

	   if ( objVal instanceof String[] ) {
		  return  (String[])mapReq.get(strField);
	   } else {
		  return new String[] {nvl(mapReq.get(strField))};
	   }
	}

    /**
     * <pre>
     * 모바일 통신사 식별번호 리스트 생성
     * </pre>
     * @author 양준성
     * @return List
     */
    public static List<Map<String, String>> getMobileIdentityList() {
    	List<Map<String, String>> list = new ArrayList<Map<String,String>>();
 	   	Map<String, String> map = null;

 	   	for (String id : new String[]{"010", "011", "016", "017", "018", "019"}) {
 	   		map = new HashMap<String, String>();
 	   		map.put("code", id);
 	   		map.put("value", id);

 	   		list.add(map);
 	   	}

 	   	return list;
	}

    /**
     * Method Summary. <br>
     * 원하는 월 selectBox로 변환하는 method
     * @param nStart
     * @param nEnd
     * @param nComp
     * @return strSel
     */
    public static String getMonthSelectBox(int nStart, int nEnd, String nComp){
    	String strSel = "";

    	for(int i=nStart; i <= nEnd; i++) {
    		String suffix = String.format("%02d", i);

    		String strSelected = suffix.equals(nComp) ? " selected " : "";
    		strSel += " <option value=\'" + suffix + "\'" + strSelected + ">" + i + "</option>\n";
    	}

    	return strSel;
    }
    
    /**
     * Method Summary. <br>
     * 원하는 월 selectBox로 변환하는 method (**월 포멧) 
     * @param nStart
     * @param nEnd
     * @param nComp
     * @return strSel
     */
    public static String getMonthTextSelectBox(int nStart, int nEnd, String nComp){
    	String strSel = "";

    	for(int i=nStart; i <= nEnd; i++) {
    		String suffix = String.format("%02d", i);
    		
    		String strSelected = suffix.equals(nComp) ? " selected " : "";
    		String strMonth	   = addZero(i, 2);
    		
    		if(!"".equals(strMonth))
    			strSel += " <option value=\'" + suffix + "\'" + strSelected + ">" + strMonth + "월</option>\n";
    	}

    	return strSel;
    }
    
    /**
	 * <pre>
	 * 맵안에 숫자를 담은 리스트 생성
	 * </pre>
	 * @author 양준성
	 * @param start
	 * @param end
	 * @return List
	 */
	public static List<Map<String, Integer>> getNumericalProgressionList(int start, int end, int increase) {
		List<Map<String, Integer>> list = new ArrayList<Map<String,Integer>>((Math.abs(end-start)+1) / Math.abs(increase));
		Map<String, Integer> map = null;

		int i = start;

		while ((i >= start && end >= i) || (i >= end && start >= i)) {
			map = new HashMap<String, Integer>();
			map.put("code", i);
			map.put("value", i);

			list.add(map);

			i += increase;
		}

		return list;
	}

    public static List<Map<String, String>> getNumericalProgressionListWithLpad(int start, int end, int increase) {
		List<Map<String, Integer>>	list	= getNumericalProgressionList(start, end, increase);
		List<Map<String, String>>	mapList	= new ArrayList<Map<String,String>>(list.size());

		int maxLength = Integer.toString(Math.abs(start)).length() > Integer.toString(Math.abs(end)).length() ?
							Integer.toString(Math.abs(start)).length() : Integer.toString(Math.abs(end)).length();

		Map<String, String> strMap = null;

		for( Map<String, Integer> map : list ) {
			strMap = new HashMap<String, String>();
			strMap.put("code", String.format("%0" + maxLength + "d", map.get("code")));
			strMap.put("value", String.format("%0" + maxLength + "d", map.get("value")));
			mapList.add(strMap);
		}

		return mapList;
	}

    @SuppressWarnings("rawtypes")
	public static String getOneFileName(List fileList, String strCompareGbn) {
        String strFileName = "";

        if(fileList != null && fileList.size() > 0) {
           for( int iLoop = 0; iLoop < fileList.size(); iLoop++ ) {
               Map fileMap = ( Map ) fileList.get( iLoop );

               String strFileGbn = nvl(fileMap.get("FILE_GBN"),"");

               if (strCompareGbn.equals(strFileGbn) ||  "".equals(strCompareGbn) ) {
            	   strFileName = nvl(fileMap.get("FILE_NM"));
            	   break;
               }
           }
        }

        return strFileName;
    }

    /**
    * 전화번호 "-" 표시
    * @param objPhone
 	* @return getPhoneFormat
 	*/
    public static String getPhoneFormat(Object objPhone) {
    	String phone = objPhone.toString();

        return getPhoneFormat(phone, "-");
    }

    /**
	 * 전화번호 7자리, 8자리 구분에 따른 "-" 표시
	 * @param objPhone
	 * @param delim
	 * @return tmp
	 */
    public static String getPhoneFormat(Object objPhone, String delim) {
    	String	tmp   	= "";
    	String	phone	= objPhone.toString();
    	int		iDayLen = phone.length();

    	if (iDayLen < 4) {
    		tmp = phone;
    	} else if(iDayLen < 8){
    		tmp = phone.substring(0, 3) + delim + phone.substring(3, 7);
    	} else if (iDayLen < 9){
    		tmp = phone.substring(0, 4) + delim + phone.substring(4, 8);
    	}

    	return tmp;
    }

    public static int getRandomInt(int limit) {
        int number = random.nextInt();
        number = (number >>> 16) & 0xffff;
        number /= (0xffff / limit);

        return number;
    }

    public static String getRealContent(String source) {
        int start = source.indexOf("<DIV");

        if (start < 0) {
            return source;
        }

        int realStart = source.substring(start, source.length()).indexOf(">") + start + 1;
        int end = source.indexOf("</DIV");
        return source.substring(realStart, end);
    }

    public static String getReplaceToHtml(Object objKey) {
        String strKey = getConv(objKey, "&nbsp;");

        strKey = strKey.replace("\r\n", "<br/>");
        strKey = strKey.replace("\n", "<br/>");

        return strKey;
    }

    /**
     * Throws :<br>
     * Parameters : HttpServletRequest request <br>
     * Return Value : String <br>
     * 내용 : getRequestInputString 이용하여 파라메터를 리턴함 <br>
     */
    public static String getRequestInputString(HttpServletRequest request) {
        return getRequestInputString(request, "");
    }

    @SuppressWarnings("rawtypes")
	public static String getRequestInputString(HttpServletRequest request, String notParam) {
        String		retQueryString	= "";

        Map			parameter	= request.getParameterMap();
        Iterator	it			= parameter.keySet().iterator();
        Object		paramKey	= null;
        String[]	paramValue	= null;

        while (it.hasNext()) {
            paramKey = it.next();

            if (paramKey.equals(notParam)) {
                continue;
            }

            paramValue = (String[]) parameter.get(paramKey);

            for (int i = 0; i < paramValue.length; i++) {
                retQueryString += "<input name=\"" + paramKey + "\" type=hidden value=\"" + paramValue[i] + "\" >  \n";
            }
        }

        return retQueryString;
    }
 

    /**
     * Method Summary. <br>
     * 서블릿 리퀘스트를 Map으로 변환하여 리턴.
     * @param  Map parameter <br>
     * @param  String[] notParam <br>
     * @return 파라미터 String
     * @throws e Exception
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getRequestQueryMap(Map parameter, String[] arrParam, boolean isParam ) {
        String		retQueryString	= "";
        Iterator	it				= parameter.keySet().iterator();
        Object		paramKey		= null;
        String[]	paramValue		= null;

        while (it.hasNext()) {
            paramKey	= it.next();
            paramValue	=  parameter.get(paramKey).toString().split(",");

            boolean bParam = isParam == true ? false : true;

            for (int i = 0; i < paramValue.length; i++) {
                for (int j = 0; j < arrParam.length; j++) {
                    if (paramKey.equals(arrParam[j])) {
                        bParam = isParam;
                    }
                }

                if(bParam) {
                    if( !retQueryString.equals( "" ) ) {
                    	retQueryString += "&";
                    }

                    retQueryString +=  paramKey + "=" + parameter.get(paramKey);
                }
            } // for (int i = 0; i < paramValue.length; i++)
        } // while (it.hasNext())

        return retQueryString;
    }

    /**
     * Throws :<br>
     * Parameters : HttpServletRequest request <br>
     * Return Value : String <br>
     * 내용 : HttpServletRequest를 이용하여 파라메터를 리턴함 <br>
     */
    @SuppressWarnings("rawtypes")
	public static String getRequestQueryString(HttpServletRequest request) {
        String		retQueryString	= "";

        Map<String, String[]> parameter	= request.getParameterMap();
        Iterator	it			= parameter.keySet().iterator();
        Object		paramKey	= null;
        String[]	paramValue	= null;

        while (it.hasNext()) {
            paramKey = it.next();

            paramValue = (String[]) parameter.get(paramKey);

            for (int i = 0; i < paramValue.length; i++) {
                if (retQueryString.length() > 0) {
                    retQueryString = retQueryString + "&";
                }

                retQueryString = retQueryString + paramKey + "=" + paramValue[i];
            }
        } // while (it.hasNext())

        return retQueryString;
    }

    /**
     * Throws :<br>
     * Parameters : HttpServletRequest request <br>
     * Parameters : String[] notParam 제외 파라미터 Return Value : String <br>
     * 내용 : HttpServletRequest를 이용하여 파라메터를 리턴함 <br>
     */
    @SuppressWarnings("rawtypes")
	public static String getRequestQueryString(HttpServletRequest request, String[] notParam) {
        String retQueryString = "";

        Map			parameter	= request.getParameterMap();
        Iterator	it			= parameter.keySet().iterator();
        Object		paramKey	= null;
        String[]	paramValue	= null;

        while (it.hasNext()) {
            paramKey = it.next();

            paramValue = (String[]) parameter.get(paramKey);

            boolean bParam = true;

            for (int i = 0; i < paramValue.length; i++) {
                for (int j = 0; j < notParam.length; j++) {
                    if (paramKey.equals(notParam[j])) {
                        bParam = false;
                    }
                }

                if(bParam) {
                    retQueryString += "&" + paramKey + "=" + paramValue[i];
                }
            }
        } // while (it.hasNext())

        return retQueryString;
    }


    /**
     * Throws :<br>
     * Parameters : HttpServletRequest request <br>
     * Return Value : String <br>
     * 내용 : HttpServletRequest를 이용하여 파라메터를 리턴함 <br>
     */
    @SuppressWarnings("rawtypes")
	public static String getRequestQueryString8859(HttpServletRequest request) {
        HashMap		map				= new HashMap();
        String		retQueryString	= "";

        Map			parameter	= request.getParameterMap();
        Iterator	it			= parameter.keySet().iterator();
        Object		paramKey	= null;
        String[]	paramValue	= null;

        while (it.hasNext()) {
            paramKey = it.next();
            paramValue = (String[]) parameter.get(paramKey);

            for (int i = 0; i < paramValue.length; i++) {
                if (retQueryString.length() > 0) {
                    retQueryString = retQueryString + "&";
                }

                try {
                    retQueryString = retQueryString + paramKey + "=" + URLEncoder.encode(paramValue[i], "8859_1");
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } // while (it.hasNext())

        return retQueryString;
    }

    public static String getRight(String str, int len) {
        if (str.length() < len)
            return "";

        return str.substring(str.length() - len);
    }

    public static String getSelBoxRepeat(int nStart, int nEnd, int nComp, int nSize)
    {
        return getSelBoxRepeat(nStart, nEnd, String.valueOf(nComp), nSize);
    }

    public static String getSelBoxRepeat(int nStart, int nEnd, Object objComp, int nSize)
    {
        StringBuffer strBuf = new StringBuffer();
        String strValue = new String();
        String strComp  = "";

        if ( objComp != null)
            strComp = objComp.toString();

        String strFormat = "%0" + String.valueOf(nSize) + "d";

        for (int nLoop = nStart; nLoop <= nEnd; nLoop ++ )
        {
           strBuf.append("<option value='");
           if ( nSize > 0 )
               strValue = String.format(strFormat, nLoop);
           else
               strValue = String.valueOf(nLoop);
           strBuf.append(strValue + "' " );
           if ( String.valueOf(nLoop).equals(strComp))
               strBuf.append(" selected " );
           strBuf.append(" >"  + String.valueOf(nLoop) + " </option> " + "\n");
        }


        return strBuf.toString();
    }

    public static String getSelBoxRepeat(int nStart, int nEnd, String strComp, int nSize) {
        return getSelBoxRepeat(nStart, nEnd, strComp, nSize);
    }

    /**
	* Method Summary. <br>
	* 공통코드를 selectbox로 변환하는 method.
	* @param listRow List 공통코드 쿼리 list
	* @param strCompare String selected index
	* @param strCode code 명칭
	* @param strCodeNm codeNm 명칭
	* @return selectbox String
	* @throws name description
	* @since 1.00
	* @see
	*/
	public static String getSelectBox( List listRow, String strCompare, String strCode, String strCodeNm) {
		return getSelectBox(listRow, strCompare, strCode, strCodeNm, true);
	}

    /**
	 * Method Summary. <br>
	 * 공통코드를 selectbox로 변환하는 method.
	 * @param listRow List 공통코드 쿼리 list
	 * @param strCompare String selected index
	 * @param strCode code 명칭
	 * @param strCodeNm codeNm 명칭
	 * @param isDefOption 기본 옵션 여부
	 * @return selectbox String
	 * @throws name description
	 * @since 1.00
	 * @see
	 */
	 public static String getSelectBox( List listRow, String strCompare, String strCode, String strCodeNm, boolean isDefOption) {
		 StringBuilder strVal = new StringBuilder();

		 try {
			 Iterator iterator 	= listRow.iterator();
			 strCompare 		= nvl(strCompare);

			 Map resultMap;
			 String strCodeCd;
			 String strName;
			 String strSelected;

	         while (iterator.hasNext()) {
	        	 resultMap 		= (Map) iterator.next();
	        	 strCodeCd 		= String.valueOf(resultMap.get(strCode));
	        	 strName 		= String.valueOf(resultMap.get(strCodeNm));
	        	 strSelected 	= strCodeCd.equals(strCompare) ? " selected " : " ";

	        	 strVal.append(" <option value=\'").append(strCodeCd).append("\'").append(strSelected).append(">").append(strName).append("</option>\n");
	         }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return strVal.toString();
	 }

    /**
	  * Method Summary. <br>
	  * 검색용 공통코드를 selectbox로 변환하는 method.
	  * @param listRow List 공통코드 쿼리 list
	  * @param strCompare String selected index
	  * @param strCode code 명칭
	  * @param strCodeNm codeNm 명칭
	  * @param strFlag flag 명칭
	  * @return selectbox String
	  * @throws name description
	  * @since 1.00
	  * @see
	  */
	 public static String getSelectBox( List listRow, String strCompare, String strCode, String strCodeNm, String strFlag) {
		 StringBuilder strVal = new StringBuilder(" <option value='").append(strFlag).append("'>전체</option>\n");

		 try {
			 Iterator iterator   = listRow.iterator();
			 strCompare          = nvl(strCompare);

			 Map resultMap;
			 String strCodeCd;
			 String strName;
			 String strSelected;

			 while (iterator.hasNext()) {
				 resultMap	= (Map) iterator.next();
				 strCodeCd	= String.valueOf(resultMap.get(strCode));
				 strName	= String.valueOf(resultMap.get(strCodeNm));

				 strSelected  = strCodeCd.equals(strCompare) ? " selected " : " ";

				 strVal.append(" <option value=\'").append(strCodeCd).append("\'").append(strSelected).append(">").append(strName).append("</option>\n");
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return strVal.toString();
	 }

    /**
     * request 객체 정보를 기반으로 context Root 전까지의 URL 정보를 반환한다.
     * @param request HttpServletRequest 객체
     * @return URL 정보 (예. http://www.abc.com:7001)
     * @since 1.00
     * @see
     */
    public static String getServletUrl(HttpServletRequest request) {
        String sServerName = request.getServerName();
        int iServerPort = request.getServerPort();
        String sScheme = request.getScheme();

        String sServerUrl = sScheme + "://" + sServerName + (iServerPort == 80 ? "" : ":" + iServerPort);
        return sServerUrl;
    }

    /**
     * Method Summary. <br>
     * 원하는 년도 selectBox로 변환하는 method
     * @param nStart
     * @param nEnd
     * @param nComp
     * @return strSel
     */
    public static String getStatsYearSelectBox(int nStart, int nEnd, String nComp){
    	String strSel ="";

    	for(int i=nStart; i<=nEnd; i++){
    		String strSelected = Integer.toString(i).equals(nComp) ? " selected " : "";
    		strSel += " <option value=\'" + i + "\'" + strSelected + ">" + i + "</option>\n";
    	}

    	return strSel;
    }
    
    /**
     * Method Summary. <br>
     * 원하는 년도 selectBox로 변환하는 method (****년)
     * @param nStart
     * @param nEnd
     * @param nComp
     * @return strSel
     */
    public static String getStatsYearTextSelectBox(int nStart, int nEnd, String nComp){
    	String strSel ="";

    	for(int i=nStart; i<=nEnd; i++){
    		String strSelected = Integer.toString(i).equals(nComp) ? " selected " : "";
    		strSel += " <option value=\'" + i + "\'" + strSelected + ">" + i + "년</option>\n";
    	}

    	return strSel;
    }

    /**
     * <pre>
     *  Html 화면 구성에 주소  암호화.
     * </pre>
     *
     * @param inputString 변환할 String 값
     * @return 서울시 강남구 도곡2동 999-99 -> 서울시 강남구 ***동 ***-**
     */
    public static String getStrAddr(String inputString) {
    	if(inputString == null) {
    		return "";
    	}

    	// 서울시 강남구 도곡2동 999-99 -> 서울시 강남구 ***동 ***-**
		String[] arrayAddr = inputString.split(" ");
		StringBuffer sb = new StringBuffer();

		if(arrayAddr != null && arrayAddr.length > 0) {
			String strAddr = "";
			int idx = 0;
			int hideLen = 0;
			String addrGbn = "";
			String ast;

			for(int i=0; i<arrayAddr.length; i++) {
				strAddr = arrayAddr[i] + " ";

				if(strAddr.indexOf("읍 ") > -1 || strAddr.indexOf("면 ") > -1 || strAddr.indexOf("동 ") > -1) {
					if(strAddr.indexOf("읍 ") > -1) {
						idx = strAddr.indexOf("읍 ");
						addrGbn = "읍 ";
					} else if(strAddr.indexOf("면 ") > -1) {
						idx = strAddr.indexOf("면 ");
						addrGbn = "면 ";
					} else if(strAddr.indexOf("동 ") > -1) {
						idx = strAddr.indexOf("동 ");
						addrGbn = "동 ";
					}

					hideLen = strAddr.length() - idx;
					ast = "*";

					while(ast.length() < hideLen) {
						ast = ast.concat("*");
					}

					strAddr = ast + addrGbn;
				}

				sb.append(strAddr);
			}
		}

		String addr = sb.toString();

		StringBuffer sbf = new StringBuffer();
		char c;

		for(int i=0; i<addr.length(); i++){
			c = addr.charAt(i);
			if(String.valueOf(c).matches("[\\d]+")){
				sbf.append("*");
			} else {
				sbf.append(c);
			}
		}

		return sbf.toString();
    }

    /**
     * <pre>
     *  Html 화면 구성에 말줄임 표시 기능.
     * </pre>
     *
     * @param inputString 변환할 String 값
     * @param max_Length 반환할 String Length
     * @return "aaaaaaaaa" 를 "aaaa..."으로 변환하여 Return
     */
    public static String getStrCut(String inputString, int max_Length) {
        String outputString = "";
        int string_size = 0;
        int new_size = 0;

        try {
            for (int i = 0; i < max_Length && i < inputString.length(); i++) {
                if (Character.getType(inputString.charAt(i)) == 5) {
                    string_size += 2;
                } else if (Character.getType(inputString.charAt(i)) == 1
                        || Character.getType(inputString.charAt(i)) == 2
                        || Character.getType(inputString.charAt(i)) == 15
                        || Character.getType(inputString.charAt(i)) == 24) {
                    string_size += 1;
                } else {
                    string_size += 1;
                }
            }

            if (inputString == null)
                return outputString;

            if (max_Length < 4 || string_size < max_Length)
                return inputString;

            for (int i = 0; new_size < max_Length - 3; i++) {
                if (Character.getType(inputString.charAt(i)) == 5) {
                    new_size += 2;
                } else if (Character.getType(inputString.charAt(i)) == 1
                        || Character.getType(inputString.charAt(i)) == 2
                        || Character.getType(inputString.charAt(i)) == 15
                        || Character.getType(inputString.charAt(i)) == 24) {
                    new_size += 1;
                } else {
                    new_size += 1;
                }

                if (new_size <= max_Length - 3) {
                    outputString += new Character(inputString.charAt(i)).toString();
                }
            }
            outputString += "...";
            return outputString;
        } catch (Exception E) {
            E.printStackTrace();
            return inputString;
        }
    }


    /**
     * <pre>
     *  Html 화면 구성에 이름 암호화.
     * </pre>
     *
     * @param inputString 변환할 String 값
     * @return "mangs84" 를 nLen 만큼 출력, 뒷문자는 "*" 으로 변환하여 Return
     */
    public static String getStrFrontStr(String inputString, int nLen) {
    	String outputString = "";

    	try {
        	if(inputString == null) {
        		return "";
        	}

        	if( inputString.length() > nLen ) {
        		outputString = inputString.substring(0, nLen);

        		for(int nLoop=0; nLoop<inputString.length() - nLen; nLen++ ) {
        			outputString += "*";
        		}

        	} else {
        		outputString = inputString;
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return inputString;
        }

    	return outputString;
    }

    /**
     * <pre>
     *  Html 화면 구성에 이름 암호화.
     * </pre>
     *
     * @param inputString 변환할 String 값
     * @return "홍길동" 을 "홍*동"으로 변환하여 Return
     */
    public static String getStrName(String inputString) {
    	String outputString = "";

    	try {
        	if(inputString == null) {
        		return "";
        	}

        	//한글자 이상
        	if( inputString.length() > 1 ) {
    			outputString = inputString.substring(0, 1);
    			outputString += "*";

    			//2자 이상
        		if( inputString.length() > 2 ) {
        			outputString += inputString.substring(inputString.length() - 1, inputString.length());
        		}
        	} else {
        		outputString = inputString;
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return inputString;
        }

    	return outputString;
    }

    /**
     * <pre>
     *  Html 화면 구성에 이름 암호화.
     * </pre>
     *
     * @param inputString 변환할 String 값
     * @return "011-111-1111" 을 "011-****-1111"으로 변환하여 Return
     */
    public static String getStrPhone(String inputString) {
    	String outputString = "";

    	try {
        	if(inputString == null) {
        		return "";
        	}

        	if( inputString.length() >= 8 ) {
        		if( inputString.indexOf("-") > -1 && inputString.lastIndexOf("-") > 4 ) {
        			outputString = inputString.substring(0, inputString.indexOf("-") + 1);

	        		for(int nLoop=inputString.indexOf("-") + 1; nLoop<inputString.lastIndexOf("-"); nLoop++) {
	        			outputString += "*";
	        		}

	        		outputString += inputString.substring( inputString.lastIndexOf("-"), inputString.length());
        		} else if(  inputString.indexOf("-") == -1 ) {
        			outputString = inputString.substring(0, 3);
        			outputString += "****";
        			outputString += inputString.substring( 7, inputString.length());
        		}
        	} else {
        		outputString = inputString;
        	}
        } catch (Exception e) {
            e.printStackTrace();
            return inputString;
        }

    	return outputString;
    }

    public static String getTitleLimit(String title, int maxNum, int re_level) {
        int blankLen = 0;
        if (re_level != 0) {
            blankLen = (re_level + 1) * 2;
        }
        int tLen = title.length();
        int count = 0;
        char c;
        int s = 0;

        for (s = 0; s < tLen; s++) {
            c = title.charAt(s);
            if ((int) (count) > (int) (maxNum - blankLen)) {
                break;
            }

            if (c > 127)
                count += 2;
            else
                count++;
        }
        return (tLen > s) ? title.substring(0, s) + "..." : title;
    }

    /**
     * 입력된 String을 Delimeter로 토크나이징 하여 토크나이징된 토큰들을 String 배열로 반환한다.
     * @param pm_sString 토크나이징되는 문자열
     * @param pm_sDelimeter 문자열를 분리하는 delimeter 문자
     * @return 토크나이징된 토큰들의 String 배열
     * @see java.util.StringTokenizer
     */
    public static String[] getTokens(String pm_sString, String pm_sDelimeter) {
        if (pm_sString == null)
            return null;

        StringTokenizer lm_oTokenizer = new StringTokenizer(pm_sString, pm_sDelimeter);
        String[] lm_sReturns = new String[lm_oTokenizer.countTokens()];
        for (int i = 0; lm_oTokenizer.hasMoreTokens(); i++) {
            lm_sReturns[i] = lm_oTokenizer.nextToken();
        }// for

        return lm_sReturns;
    }

    /**
     * Position Data를 얻기위해 입력된 String을 Delimeter로 토크나이징 하여 토크나이징된 토큰들을 int 배열로 반환한다.
     * @param pm_sString 토크나이징되는 문자열
     * @param pm_sDelimeter 문자열를 분리하는 delimeter 문자
     * @return 토크나이징된 Position Data토큰들의 int 배열
     * @see java.util.StringTokenizer
     */
    public static int[] getTokensPos(String pm_sString, String pm_sDelimeter) {
        if (pm_sString == null)
            return null;

        StringTokenizer lm_oTokenizer = new StringTokenizer(pm_sString, pm_sDelimeter);
        int[] lm_nReturns = new int[lm_oTokenizer.countTokens()];

        for (int i = 0; lm_oTokenizer.hasMoreTokens(); i++) {
            lm_nReturns[i] = Integer.parseInt(lm_oTokenizer.nextToken());
        }// for

        return lm_nReturns;
    }

    /**
     * pm_sDelimeter의 한 문자씩을 토크나징의 하는 것이 아니라 StringTokenizer를 사용하지 않고 pm_sDelimeter 전체를 이용하여 토크나이징한다.
     * @param pm_sString 토크나이징되는 문자열
     * @param pm_sDelimeter 문자열를 분리하는 delimeter 문자열(문자열 전체 이용)
     * @return 토크나이징된 토큰들의 String 배열
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String[] getTokensWithMultiDelim(String pm_sString, String pm_sDelimeter) {
        String lm_sString = pm_sString;

        int lm_iLength = pm_sDelimeter.length();
        int lm_iIndex = -1;
        Vector lm_vList = new Vector();

        while ((lm_iIndex = lm_sString.indexOf(pm_sDelimeter)) != -1) {
            String lm_sToken = lm_sString.substring(0, lm_iIndex);
            lm_sString = lm_sString.substring(lm_iIndex + lm_iLength);
            lm_vList.addElement(lm_sToken);
        } // while

        if (lm_sString != null) {
            lm_vList.addElement(lm_sString);
        }

        String[] lm_sTokens = new String[lm_vList.size()];
        lm_vList.copyInto(lm_sTokens);

        return lm_sTokens;
    }

    /**
     * Method Summary. <br>
     * 고유번호 UUID를 조회.
     * @param
     * @return String
     * @throws
     * @since 1.00
     * @see
     */
	public static String getUniqueId() {
		return UUID.randomUUID().toString();
	}

	/**
     * URL과 파라미터들을 조합하여 URL 을 반환한다. <br>
     * 내부적으로 URLEncoder의 encode() 처리를 수행하게 된다.
     * @param sUrl 기준이 되는 URL 정보
     * @param params parameter 정보를 key, value로 가지고 있는 Properties 객체
     * @return sUrl에 파라미터들이 결합된 URL 문자열
     * @since 1.00
     * @see
     */
    @SuppressWarnings("rawtypes")
	public static String getUrlString(String sUrl, Properties params) {
        Iterator iterator = params.keySet().iterator();

        StringBuffer sb = new StringBuffer();
        sb.append(sUrl);
        if (sUrl.lastIndexOf("?") < 0)
            sb.append("?");
        else
            sb.append("&");
        for (int i = 0; iterator.hasNext(); i++) {
            String sKey = (String) iterator.next();
            String sValue = params.getProperty(sKey);
            if (i != 0)
                sb.append("&");
            try {
                sb.append(sKey + "=" + URLEncoder.encode(sValue, "8859_1"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }// while
        return sb.toString();
    }

	/**
     * Method Summary. <br>
     * 원하는 년도 selectBox로 변환하는 method
     * @param nStart
     * @param nEnd
     * @param nComp
     * @return strSel
     */
    public static String getYearSelectBox(int nStart, int nEnd, String nComp){
    	String strSel ="";
    	strSel += " <option value=''>" + "선택(select)" + "</option>\n";

    	for(int i=nStart; i>=nEnd; i--){
    		String strSelected = Integer.toString(i).equals(nComp) ? " selected " : "";
    		strSel += " <option value=\'" + i + "\'" + strSelected + ">" + i + "</option>\n";
    	}

    	return strSel;
    }

	/**
     * <pre>
     *
     *   대소문자 구분없이 String 값에서 시작위치를 주고 찾고자하는 값이 몇번째 순서에 있는지 리턴하는 메소드.
     *
     * </pre>
     *
     * @param str String 값
     * @param indexstr 찾고자 하는 값
     * @param fromindex String 의 시작위치
     * @see StringHandler#indexOfaA(String str, String indexstr)
     * @return 찾고자 하는 값의 String 의 위치
     */
    public static int indexOfaA(String str, String indexstr, int fromindex) {
        int index = 0;

        indexstr	= indexstr.toLowerCase();
        str			= str.toLowerCase();
        index		= str.indexOf(indexstr, fromindex);

        return index;
    }

    /**
     * <pre>
     * 배열에 값이 들어있을경우 해당 인덱스 반환
     * </pre>
     *
     * @param array 배열
     * @param value 값
     * @return 배열 의 해당값 포함여부
     */
    public static int indexOfContainsValue(Object[] array, Object value) {
        for (int i = 0, size = (isArray(array) ? array.length : 0); i < size; i++) {
            if (nvl(array[i]).equals(nvl(value))) {
                return i;
            }
        }

        return -1;
    }

	/**
     * <pre>
     * 배열여부 검증
     * </pre>
     * @param o (target)
     * @return boolean
     */
    public static boolean isArray(Object obj) {
 	   return (obj != null && obj.getClass().isArray()) ? true : false;
    }

	/**
	 * <pre>
	 * Map, Collection, Array, String, File 객체형 비워있는지 여부 체크
	 * (단 위에서 지원하지 객체형 은 null 을 기준으로 판단)
	 *
	 * Map : Map == null || Map.isEmpty()
	 * Collection : Collection == null || Collection.isEmpty()
	 * Array : Array == null || Array.length == 0
	 * String : String == null || String.trim().length() == 0
     * File : File == null || !File.exists()
	 * Other : Other == null
	 * </pre>
	 * @param o 비워있는지 검사할 객체
	 * @return 비워있는지 여부
	 */
	public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            return (Array.getLength(obj) == 0);
        } else if (obj instanceof String) {
            return (((String) obj).trim().length() == 0);
        } else if (obj instanceof File) {
            return (!((File) obj).exists());
        }

        return false;
	}

	 /**
	 * <pre>
	 * 배열이 비거나 배열안의 모든 값이 비었는지를 검증
	 * </pre>
	 * @param os (targets)
	 * @return boolean
	 */
	public static boolean isEmptyAll(Object... objs) {
		 if (isNotEmpty(objs)) {
			 for (Object obj : objs) {
				 if (isNotEmpty(obj)) return false;
			 }
		 }

		 return true;
	 }

	/**
     * <pre>
     * 맵 의 값들이 비워있는지 여부 체크
     * </pre>
     *
     * @param map 값을 확인할 맵
     * @return 값들이 비워있는지 여부
     */
	public static boolean isEmptyMapValue(Map<String, Object> map) {
		boolean isEmpty = true;

		for (Object obj : map.values()) {
			if (CommonUtil.isNotEmpty(obj)) {
				isEmpty = false;
			}
		}

		return isEmpty;
	}

	/**
     * Method Summary. <br>
     * 주민등록번호 점검
     * @param 주민등록번호
     * @return 주민등록번호 여부
     * @throws Exception ex
     * @since 1.00
     * @see
     */
    public static boolean isJumin( String jumin ) {
    	boolean isKorean = true;
    	int check = 0;

    	if( jumin == null || jumin.length() != 13 ) return false;
    	if( Character.getNumericValue( jumin.charAt( 6 ) ) > 4 && Character.getNumericValue( jumin.charAt( 6 ) ) < 9 ) {
    	  isKorean = false;
    	}

    	for( int i = 0 ; i < 12 ; i++ ) {
    	  if( isKorean ) check += ( ( i % 8 + 2 ) * Character.getNumericValue( jumin.charAt( i ) ) );
    	  else check += ( ( 9 - i % 8 ) * Character.getNumericValue( jumin.charAt( i ) ) );
    	}

    	if( isKorean ) {
    	  check = 11 - ( check % 11 );
    	  check %= 10;
    	} else {
    	  int remainder = check % 11;

    	  if ( remainder == 0 ) check = 1;
    	  else if ( remainder==10 ) check = 0;
    	  else check = remainder;

    	  int check2 = check + 2;
    	  if ( check2 > 9 ) check = check2 - 10;
    	  else check = check2;
    	}

    	if( check == Character.getNumericValue( jumin.charAt( 12 ) ) ) return true;
    	else return false;
    }

    /**
	 * <pre>
	 * Map, Collection, Array, String, File 객체형 비워있는지 여부 체크
	 * (단 위에서 지원하지 객체형 은 null 을 기준으로 판단)
	 *
	 * Map : Map != null && !Map.isEmpty()
	 * Collection : Collection != null && !Collection.isEmpty()
	 * String : String != null && String.trim().length() != 0
	 * Array : Array != null && Array.length != 0
     * File : File != null && File.exists()
	 * Other : Other != null
	 * </pre>
	 * @param o 비워있는지 검사할 객체
	 * @return 비워있는지 여부
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	/**
	  * <pre>
	  * 배열이 비어있지 않고 배열안의 값이 empty가 아님을 검증
	  * </pre>
	  * @param os (targets)
	  * @return boolean
	  */
	 public static boolean isNotEmptyAll(Object... objs) {
		 boolean result = true;

		 if (isNotEmpty(objs)) {
			 for (Object obj : objs) {
				 if (isEmpty(obj)) {
					 result = false;
					 break;
				 }
			 }
		 } else {
			 result = false;
		 }

		 return result;
	}
    /**
     * 실수형 숫자를 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(float objNumber) {
        return number_format(objNumber, "#,##0");
    }

    /**
     * 정수형 숫자를 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(int objNumber) {
        return number_format(objNumber, "#,##0");
    }

    /**
     * 정수형 숫자를 넘겨준 포맷으로 맞춘 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(int objNumber, String strFormat) {
        String strRetVal = "0";

        try {
            if (objNumber <= 0) return "0";
            String strNumber = Integer.toString(objNumber);

            Double dblNumber = Double.valueOf(strNumber);

            strFormat = ("".equals(strFormat)) ? "#,##0" : strFormat;

            DecimalFormat formatter = new DecimalFormat(strFormat);
            strRetVal = formatter.format(dblNumber);
        } catch (Exception e) {
            //System.out.println("CommonUtil [number_format] " + e.toString());
        }

        return strRetVal;
    }

    /**
     * 정수형 숫자를 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(long objNumber) {
        return number_format(objNumber, "#,##0");
    }

    /**
     * Object형태로 넘어온 숫자를 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(Object objNumber) {
        return number_format(objNumber, "#,##0");
    }

    /**
     * Object형태로 넘어온 숫자를 넘겨준 포맷에 맞는 문자열로 반환. <br>
     * @param objNumber 숫자
     * @return 문자열
     * @throws name description
     * @since 1.00
     * @see
     */
    public static String number_format(Object objNumber, String strFormat) {
        String strRetVal = "0";

        try {
            if (objNumber == null || "".equals(objNumber.toString()))
                return "0";
            String strNumber = objNumber.toString();

            Double dblNumber = Double.valueOf(strNumber);

            strFormat = ("".equals(strFormat)) ? "#,##0" : strFormat;

            DecimalFormat formatter = new DecimalFormat(strFormat);
            strRetVal = formatter.format(dblNumber);
        } catch (Exception e) {
            //System.out.println("CommonUtil [number_format] " + e.toString());
        }

        return strRetVal;
    }

    /**
     * URL 중에서 해당 필드 값을 삭제함 <br>
     * Method Description.
     * @param strParam URL 파라메터
     * @param strNotWord 삭제할 필드
     * @return String
     * @since 1.00
     * @see
     */
    public static String removeParam(String strParam, String strNotWord) {
        String strRetVal = "";

        try {
            if (strParam == null || "".equals(strParam))
                return "";
            if (strNotWord == null || "".equals(strNotWord))
                return "";

        	int iStartPos = indexOfaA(strParam, strNotWord, 0);

            if (iStartPos < 0)
                return strParam;

            strRetVal = strParam;

            while (iStartPos >= 0)
            {
	            int iEndPos = indexOfaA(strRetVal, "&", iStartPos);

	            if (iEndPos <= 0)
	                iEndPos = strRetVal.length() - 1;

	            strRetVal = strRetVal.substring(0, iStartPos) + strRetVal.substring(iEndPos + 1);

            	iStartPos = indexOfaA(strRetVal, strNotWord, 0);
            }
        } catch (Exception e) {
            //System.out.println("CommonUtil[removeParam]=>" + e.toString());
        }
        return strRetVal;

    }

    /**
     * 입력 문자열 끝부분의 공백문자 " "를 제거한다. Method Description.
     * @param String str 공백을 제거할 문자열
     * @return String
     * @since 1.00
     * @see
     */
    public static String rightTrim(String str) {
        if (str == null || str.equals(""))
            return str;

        int i;
        int len = str.length();

        for (i = 0; i < len; i++) {
            if (str.charAt(len - i - 1) != ' ')
                break;
        }

        return str.substring(0, len - i);
    }

    public static String scriptMsg(String strMsg) {
    	return scriptMsg(strMsg, "");
    }

    public static String scriptMsg(String strMsg, String strUrl) {
    	StringBuffer sb = new StringBuffer();
    	sb.append("<script>");
    	sb.append("alert('" + strMsg + "')");

    	sb.append("</script>");

    	if ( !"".equals(strUrl))
    		sb.append("<meta http-equiv='refresh' content='0;url=" + strUrl + "'>");

    	return sb.toString();
    }

    /**
     * Map 초기값 처리. <br>
     * Map의 Key에 해당하는 값이 존재하지 않으면 초기값을 부여한다.
     * @param map
     * @param strKey
     * @param strVal
     * @return description
     * @since 1.00
     * @see
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map setNullVal(Map map, String strKey, String strVal) {
        String strMapVal = "";

        if (map.get(strKey) != null && map.get(strKey) != "") {
            strMapVal = map.get(strKey).toString().trim();
        }

        if ("".equals(strMapVal)) {
            map.put(strKey, strVal);
        }

        return map;
    }

    public static String setNullVal(Object objKey) {
        return setNullVal(objKey, "");
    }

	public static String setNullVal(Object objKey, String strVal) {
        if (objKey != null) {
            String strKey = objKey.toString().trim();
            if (!"".equals(strKey))
                return strKey;
        }

        return strVal;
    }

	public static String setNullVal(String strKey, String strVal) {
        if (strKey == null || "".equals(strKey)) {
            return strVal;
        }

        return strKey;
    }

    /**
     *
     * 임시비밀번호 생성
     * @return strVal
     */
    public static String shufflePasswd(int nLen) {
		char[] charSet = new char[]{
		    '0','1','2','3','4','5','6','7','8','9'
		    ,'a','b','c','d','e','f','g','h','i','j','k','l','m'
		    ,'n','o','p','q','r','s','t','u','v','w','x','y','z'};

		int idx = 0;
		StringBuffer sb = new StringBuffer();

		for(int i=0; i<nLen; i++) {
		   idx = (int)(charSet.length*Math.random());
		   sb.append(charSet[idx]);
		}

    	return sb.toString();
    }

    public static long toLong(float fNum){
        return toLong( (Float) fNum );
    }

	public static long toLong(Float fNum) {
        String strVal;

        strVal = String.valueOf(fNum);

        if ( strVal.indexOf('.') > 0 ) {
            strVal =  strVal.substring(0, strVal.indexOf('.'));
        }

        return Long.valueOf(strVal);
    }

    /**
     * <pre>
     * 2Level 까지 가지고 있는 유형구분 코드를 Level 별로 Map에 담아 가져온다
     * </pre>
     * @param listRow List 공통코드 쿼리 list
     * @return Map
     * @since 1.00
     * @see
     */
    public static Map getTwoLevelCodeMap( List listRow, String clsCd) {
        Map mapResult 	= new HashMap<String, List>();

        List<Map> listLev1 	= new ArrayList<Map>();		// lev1 리스트
        List<Map> listLev2 	= new ArrayList<Map>();		// lev2 리스트

        int intLev			= 0;

        String strLev1		= "";		// lev2 map 키
        String strLev2		= "";		// lev3 map 키

        try {
            Iterator<Map> iterator 	= listRow.iterator();
            Map whileMap;

            while (iterator.hasNext()) {
                whileMap = iterator.next();

                switch (nvlInt(whileMap.get("CD_LVL"), 0)) {
                case 1:
                    // LEVEL이 바꼇을때 list를 map에 담는다
                    if((intLev - nvlInt(whileMap.get("CD_LVL"), 0)) == 1 && !strLev2.equals(String.valueOf(whileMap.get("COMN_CD")))) {
                        mapResult.put(strLev1, listLev2);
                        listLev2 = new ArrayList<Map>();
                    }

                    strLev1 = String.valueOf(whileMap.get("COMN_CD"));
                    listLev1.add(whileMap);
                    break;
                case 2:
                    strLev2 = String.valueOf(whileMap.get("COMN_CD"));
                    listLev2.add(whileMap);
                    break;
                } // switch (nvlInt(whileMap.get("CD_LVL"), 0)) {

                intLev = nvlInt(whileMap.get("CD_LVL"), 0);
            } // while (iterator.hasNext()) {

            //루프를 벗어난 리스트를 모두 다시 담는다.
            mapResult.put(clsCd, listLev1);
            mapResult.put(strLev1, listLev2);
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return mapResult;
    }

    /**
     * <pre>
     * key : 코드 value : 코드명 또는 key : 코드명 value : 코드 를 가진 코드 정보맵 을 반환한다.
     * </pre>
     *
     * @param type 코드 정보 맵 의 key 와 value 값 유형
     * @param codeMap 공통코드
     * @param clsCd DB필드명
     * @return 코드 와 코드명 을 가진 코드 정보
     */
	public static Map<String, String> getSimpleCodeInfo(String type, Map codeMap, String clsCd) {
	   Map<String, String> simpleCodeInfo = new HashMap<String, String>();

	   List<Map<String, Object>> listRow = (List<Map<String, Object>>) codeMap.get(clsCd);

	   if (isNotEmpty(listRow)) {
	       String key      = ("KEY_CODE".equals(type)) ? "COMN_CD" : "COMN_CD_NM";
	       String value    = ("KEY_CODE".equals(type)) ? "COMN_CD_NM" : "COMN_CD";

	       for (Map<String, Object> row : listRow) {
	           if(isNotEmpty(row.get(key))) {
	               simpleCodeInfo.put(nvlMap(row, key), nvlMap(row, value));
	           }
	       }
	   }

	   return simpleCodeInfo;
	}

	/**
	 * Method Summary. <br>
	 * List에서 strField(테이블 필드명) 기준으로  Map에 담는다
	 * @param listRow List 공통코드 쿼리 list
	 * @param strField DB필드명
	 * @return Map
	 * @throws name description
	 * @since 1.00
	 * @see
	 */
	public static Map getComnCodeMap( List listRow, String strField ) {
		Map mapResult 	= new HashMap<String, List>();

		List<Map> listResult = new ArrayList<Map>();

		String strInclsCd		= "";
		boolean keyCheck 		= false;

		try {
			Iterator<Map> iterator = listRow.iterator();
			Map whileMap;
			while (iterator.hasNext()) {
				whileMap = iterator.next();

				// 키가 바꼇을때 list를 map에 담는다
				if(keyCheck && !strInclsCd.equals(String.valueOf(whileMap.get(strField)))) {
					mapResult.put(strInclsCd, listResult);
					listResult 	= new ArrayList<Map>();
				}

				//map를 그대로 list에 담는다
				listResult.add(whileMap);

				strInclsCd = String.valueOf(whileMap.get(strField));
				keyCheck = true;
			}
			mapResult.put(strInclsCd, listResult);
		} catch (Exception e) {

		}

		return mapResult;
	}
	
	/**
	 * Method Summary. <br>
	 * List에서 strField(테이블 필드명) 기준으로  코드명을 돌라준다.
	 * @param listRow List 공통코드 쿼리 list
	 * @param strCompare 비교값
	 * @return String
	 * @throws name description
	 * @since 1.00
	 * @see
	 */
	public static String getCodeNm( List listRow, String strCompare ) {
		String strResult 	= "";

		List<Map> listResult = new ArrayList<Map>();

		String strInclsCd		= "";

		try {
			Iterator<Map> iterator = listRow.iterator();
			Map whileMap;
			while (iterator.hasNext()) {
				whileMap = iterator.next();

				if(strCompare.equals(nvlMap(whileMap, "comm_cd"))) {
					strResult = nvlMap(whileMap, "cd_nm");
					break;
				}

			}
		} catch (Exception e) {

		}

		return strResult;
	}

	/**
	 * Method Summary. <br>
	 * List에서 strCode(코드) 기준으로  코드 value 을 돌라준다.
	 * @param listRow List 공통코드 쿼리 list
	 * @param strField DB필드명
	 * @return String
	 * @throws name description
	 * @since 1.00
	 * @see
	 */
	public static String getCodeVal( List listRow, String strCode ) {
		String strResult 	= "";

		List<Map> listResult = new ArrayList<Map>();

		String strInclsCd		= "";

		try {
			Iterator<Map> iterator = listRow.iterator();
			Map whileMap;
			while (iterator.hasNext()) {
				whileMap = iterator.next();

				if( nvlMap(whileMap, "comm_cd").equals(strCode) ) {
					strResult = nvlMap(whileMap, "cd_val");
				}

			}
		} catch (Exception e) {

		}

		return strResult;
	}

	/**
     * Method Summary. <br>
     * @param sData String : 데이터 값
     * @return String
     */
    public static String getNullTrans(String sData) {
        return getNullTrans(sData, "");
    }

    /**
     * Method Summary. <br>
     * @param sData String : 데이터 값
     * @param sTrans String : null, "", "null"일 경우 변경할값
     * @return String
     */
    public static String getNullTrans(String sData, String sTrans) {
        if (sTrans == null)
            sTrans = "";
        if (sData != null && !"".equals(sData) && !"null".equals(sData))
            return removeXSS(sData.trim());

        return removeXSS(sTrans);
    }

    /**
     * Method Summary. <br>
     * @param oData Object : 객체
     * @param sTrans String : null, "", "null"일 경우 변경할값
     * @return String
     */
    public static String getNullTrans(Object oData, String sTrans) {
        if (sTrans == null)
            sTrans = "";
        if (oData != null && !"".equals(oData) && !"null".equals(oData))
            return removeXSS(oData.toString().trim());

        return removeXSS(sTrans);
    }

    public static String getNullTrans(Object oData, int nTrans) {
        return getNullTrans(oData, Integer.toString(nTrans));
    }

	public static String getClientIP(HttpServletRequest request) {
		String ip = null;
	    if (ip == null) {
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("HTTP_CLIENT_IP");
	    }
	    if (ip == null) {
	        ip = request.getHeader("x-forwarded-for");
	    }
	    if (ip == null) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}

	/**
	 * Method Summary. <br>
	 * @param oData Map : Map
	 * @param sKey String : Map에 키값
	 * @return String
	 */
	public static String nvlMap(Map oData, String sKey) {
	    return nvlMap(oData, sKey, "");
	}

	/**
	 * Method Summary. <br>
	 * @param oData Map : Map
	 * @param sKey String : Map에 키값
	 * @param sKey sTrans : value null값일때 대체 값
	 * @return String
	 */
    public static String nvlMap(Map oData, String sKey, String sTrans) {
    	String strValue = "";
    	if(oData !=null && oData.containsKey(sKey)) {
    		strValue = String.valueOf(oData.get(sKey));
    	}
    	return getNullTrans(strValue, sTrans);
    }

    public static String removeXSS(String strData)
    {
    	String[] arrSrcCode = {"<", ">", "\"", "\'", "%00", "%"};
    	String[] arrTgtCode = {"&lt;", "&gt;", "&#34;", "&#39;", "null;", "&#37;"};

    	if ( strData == null || "".equals(strData) )
    		return strData;

    	for (int nLoop=0; nLoop < arrSrcCode.length; nLoop++)
    	{
    		strData = strData.replaceAll(arrSrcCode[nLoop], arrTgtCode[nLoop]);
    	}

    	return strData;

    }


    public static String DecodeXSS(String strData)
    {
    	String[] arrSrcCode = {"&lt;", "&gt;", "&#34;", "&#39;", "null;", "&#37;", "\\r", "\\n"};
    	String[] arrTgtCode = {"<", ">", "\"", "\'", "%00", "%", "<br />", "<br />"};

    	if ( strData == null || "".equals(strData) )
    		return strData;
    	for (int nLoop=0; nLoop < arrSrcCode.length; nLoop++)
    	{
    		strData = strData.replaceAll(arrSrcCode[nLoop], arrTgtCode[nLoop]);
    	}
    	// XXX : XSS / CSRF 공격 가능성 (Stored XSS) 처리.
    	/* 2018.04.02 스크립팅 방지를 위한 처리*/
    	Pattern replacePattern = Pattern.compile("<(no)?\\s*script[^>]*>.*?</(no)?script>", Pattern.DOTALL);
    	Matcher matcher = replacePattern.matcher(strData);
    	strData = matcher.replaceAll("");

    	/* 2018.04.02 스타일 변경방지를 위한 처리*/
    	replacePattern = Pattern.compile("<\\s*style[^>]*>.*</style>",Pattern.DOTALL);
    	matcher = replacePattern.matcher(strData);
    	strData = matcher.replaceAll("");

    	return strData;

    }


    // 이벤트 관리용 DecodeXSS(br태그가 찍히면 안에 있는 html에서 오류가 나기 때문에 안 찍히게 해줌)
    public static String EventDecodeXSS(String strData)
    {
    	String[] arrSrcCode = {"&lt;", "&gt;", "&#34;", "&#39;", "null;", "&#37;"};
    	String[] arrTgtCode = {"<", ">", "\"", "\'", "%00", "%"};

    	if ( strData == null || "".equals(strData) )
    		return strData;

    	for (int nLoop=0; nLoop < arrSrcCode.length; nLoop++)
    	{
    		strData = strData.replaceAll(arrSrcCode[nLoop], arrTgtCode[nLoop]);
    	}

    	return strData;

    }

    /**
     * 구분자로 날짜 형식으로 반환해준다
     * @param _value		201202030102
     * @param delimiter
     * @return
     */
    public static String ConvertDate(String _value, String delimiter) {
        if(_value.length() < 8) {
            return  _value ;
        }

        String strRetValue = "";
        char [] arrCDate = _value.toCharArray();

        StringBuffer sb = new StringBuffer(strRetValue);

        for(int count = 0; count < 8; count++) {
            sb.append(arrCDate[count]);

            if(count == 3 || count == 5) {
            	sb.append(delimiter);
            }
        }

        strRetValue = sb.toString();

        return strRetValue;
    }

    /**
    * 문자열을 합친다.
    *
    * @param strings
    * @param delimiter
    * @return
    */
    public static String join(String[] strings, String delimiter) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]).append(delimiter);
        }

        sb.append(strings[strings.length - 1]);

        return sb.toString();
    }

    /**
     * 1줄이면, 1줄만 가져오고,
     * 2줄이면, 합쳐서 가져온다.
     *
     * @param data
     * @return
     */
    public static String getOrJoinJamak(String[] data) {
        if( data != null ) {
            if (data[1] != null && data[1].length() >= 1) {
                return join(data, "\n");
            } else {
                return data[0];
            }
        }

        return "";
    }

    /**
     * 해당 문자열이, array 안에 있는지 검사한다.
     *
     * @param s
     * @param array
     * @return
     */
    public static boolean contains(String s, String[] array) {
        if (s == null) {
        	return false;
        }

        for (int i = 0; i < array.length; i++) {
            if (s.equals(array[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method Summary. <br>
     * @param sData String : 데이터 값
     * @return boolean (true:문자열이 없는 경우, false:문자열이 있는 경우)
     */
    public static boolean isBlank(String sData) {
        if (sData == null || sData.length() == 0) {
            return true;
        } else {
        	return false;
        }
    }

    /**
    * 해당 문자열를 byte배열에 담아 int 값으로 변환하여 반환.
    *
    * @param s
    * @return
    */
    public static int getByteToInt(String szVal) {
    	int nResult = -1;

    	int index = szVal.length();
    	byte[] b = new byte[index];
    	b = szVal.getBytes();

    	int nQuot	= b.length / 3;
    	int nMod	= b.length % 3;
    	int	x		= 0;

		for(int quotLoop = 0; quotLoop < nQuot; quotLoop++) {
			for(int loop = 0; loop < 3; loop++, x++) {
				if(loop == 0) {
					nResult += (Integer.valueOf(b[x]) * (nQuot - quotLoop +1));
				} else if(loop == 1) {
					nResult += ((Integer.valueOf(b[x]) * 10) * (nQuot - quotLoop +1));
				} else {
					nResult += ((Integer.valueOf(b[x]) * 100) * (nQuot - quotLoop +1));
				}
			}
		}

		if( nMod > 0 ) {
			for(int modLoop = 0; modLoop < nMod; modLoop ++) {
				if(modLoop == 0) {
					nResult += Integer.valueOf(b[x]);
				} else {
					nResult += (Integer.valueOf(b[x]) * 10);
				}
			}
		}

		return nResult;
    }

    /**
     * 해당하는 Byte길이에 구분자를 넣어 값을 넘겨준다
     * @param str			문자열
     * @param putByteLength	구분자를 넣어줄 Byte 길이
     * @param delimiter		구분자
     * @return
     */
	public static String addDelimiterByte(String str, int putByteLength, String delimiter){
    	int strLength = 0;

    	char tempChar[] = new char[str.length()];
    	StringBuffer stb = new StringBuffer();
    	try{
        	for (int i = 0; i < tempChar.length; i++) {
        		tempChar[i] = str.charAt(i) ;
        		if (tempChar[i] < 128) {
        			strLength++;
        		}else{
        			strLength += 2;
        		}
        		if( strLength > putByteLength && delimiter != null){
        			stb.append(delimiter);
        			delimiter = null;
        		}else if( strLength > (putByteLength * 2 ) && delimiter == null){
        			stb.append("...");
        			break;
        		}
        		stb.append(tempChar[i]);
        	}
    	} catch(Exception e){
    		e.printStackTrace();
    	}

    	return stb.toString();
	}

    /**
     * 문자열을 정의한 Byte로 잘라 끝을 ...으로 표현해준다.
     * @param str			문자열
     * @param putByteLength	구분자를 넣어줄 Byte 길이
     * @param delimiter		구분자
     * @return
     */
	public static String getStringByteCut(String str, int putByteLength, String delimiter){
    	int strLength = 0;

    	char tempChar[] = new char[str.length()];
    	StringBuffer stb = new StringBuffer();

    	try {
        	for (int i = 0; i < tempChar.length; i++) {
        		tempChar[i] = str.charAt(i);
        		if (tempChar[i] < 128) {
        			strLength++;
        		} else {
        			strLength += 2;
        		}

        		stb.append(tempChar[i]);

        		if( strLength > putByteLength ) {
        			stb.append(delimiter);
        			break;
        		}
        	}
    	} catch(Exception e){
    		e.printStackTrace();
    	}

    	return stb.toString();
	}

    /**
     * 자릿수에 0 채워주기
     * @param n
     * @param cipher	자릿수
     * @return
     */
    public static String addZero(int n, int cipher) {
    	String num = String.valueOf(n);
    	StringBuffer str = new StringBuffer();

    	for ( int i = 0 ; i < cipher - num.length(); i++ ) {
    		str.append("0");
    	}

    	return str.append(num).toString();
    }

    /**
     * 이메일 유효성 체크
     * @param email
     * @return
     */
	public static boolean isValidEmail(String email) {
		boolean err = false;
		Pattern p = Pattern.compile( "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+" );
		Matcher m = p.matcher(email);

		if( !m.matches() ) {
			err = true;
		}

		return err;
	}

	/**
	 * Method Summary. <br>
	 * @param oData Object : 객체
	 * @return String
	 */
	public static String nvl(Object oData) {
	    return nvl(oData, "");
	}

	/**
	 * Method Summary. <br>
	 * @param oData Object : 객체
	 * @return String
	 */
	public static String nvl(Object oData, int sTrans) {
	    return nvl(oData, Integer.toString(sTrans));
	}

	/**
	 * Method Summary. <br>
	 * @param oData Object : 객체
	 * @param sTrans String : null, "", "null"일 경우 변경할값
	 * @return String
	 */
	public static String nvl(Object oData, String sTrans) {
	    if (sTrans == null) {
	        sTrans = "";
	    }

	    if (oData != null && !"".equals(oData) && !"null".equals(oData)) {
	        return removeXSS(oData.toString().trim());
	    }

	    return removeXSS(sTrans);
	}

	/**
	 * <pre>
	 * 배열안의 값을 NVL&removeXSS 가공처리하여 얻는다.
	 * </pre>
	 * @author 양준성
	 * @param array 대상배열
	 * @param index 참조인덱스
	 * @param def	nvl default
	 * @return Object
	 */
	public static Object nvlArray(Object[] array, int index, Object def) {
		return (CommonUtil.isNotEmpty(array) && array.length > index) ? nvl(array[index]) : def;
	}

	/**
	 * Method Summary. <br>
	 * @param sData String : 데이터 값
	 * @param sTrans String : null, "", "null"일 경우 변경할값
	 * @return String
	 */
	public static int nvlInt(Object objData, int nTrans) {
		return Integer.parseInt(nvl(objData, nTrans));
	}


	/**
	 * Parameters : int convert, int size, String padString <br>
	 * Return Value : String <br>
	 * 내용 : 위 메소드 확장 , padding string을 지정한다. 왼쪽으로 패딩 <br>
	 */
	public static String padLeftwithString(int convert, int size, String padString) throws IOException {
	    Integer inTemp		= new Integer(convert);
	    String	stTemp		= new String();
	    String	stReturn	= new String();

	    stTemp = inTemp.toString();

	    if (stTemp.length() < size) {
	        for (int i = 0; i < size - stTemp.length(); i++) {
	            stReturn += padString;
	        }
	    }

	    return (stReturn + stTemp);
	}

	/**
	 * Parameters : int convert, int size <br>
	 * Return Value : String <br>
	 * 내용 : 숫자의 왼쪽 자리를 '0'으로 채운다. <br>
	 */
	public static String padLeftwithZero(int convert, int size) throws IOException {
	    StringBuffer sbRtn = new StringBuffer();

	    Integer inTemp  = new Integer(convert);
	    String  stTemp;

	    stTemp = inTemp.toString();

	    if (stTemp.length() < size) {
	        for (int i = 0; i < size - stTemp.length(); i++) {
	            sbRtn.append("0");
	        }
	    }

	    sbRtn.append(stTemp);

	    return sbRtn.toString();
	}

	/**
	 * Parameters : int convert, int size <br>
	 * Return Value : String <br>
	 * 내용 : 왼쪽에 0이 붙은 문자를 제거한다. <br>
	 */
	public static String removeLeftZero(String sbRtn) throws IOException {
		return sbRtn.replaceFirst("^0+(?!$)", "");
	}

	/**
	 * Return Value : String <br>
	 * 내용 : 호스트명 가져오기. <br>
	 */
	public static String getHostNameInfo(HttpServletRequest request) throws IOException {
		String port = (request.getServerPort() > 0)? ":" + request.getServerPort(): "";
		String currentUrl = request.getScheme() + "://" + request.getServerName() + port + request.getContextPath();

		return currentUrl;
	}

    /**
     * 쿠키를 설정한다. <br>
     * Method Description.
     * @param response
     * @param name 쿠기명
     * @param value 값
     * @param iMinute 설정시간 (분)
     * @throws java.io.UnsupportedEncodingException description
     * @since 1.00
     * @see
     */
    public static void setCookieObject(HttpServletResponse response, String name, String value, int iMinute)
            throws java.io.UnsupportedEncodingException {

        Cookie cookie = new Cookie(name, URLEncoder.encode(value, "utf-8"));

        cookie.setMaxAge(60 * iMinute);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static String getCookieObject(HttpServletRequest request, String cookieName)
            throws UnsupportedEncodingException {

        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            return null;
        String value = null;
        for (int i = 0; i < cookies.length; i++) {

            if (cookieName.equals(cookies[i].getName())) {
                value = cookies[i].getValue();
                if ("".equals(value))
                    value = null;
                break;
            }
        }

        return (value == null ? "" : URLDecoder.decode(value, "utf-8"));
    }
    
    /**
     * <pre>
     * Map안의 실수형 값을 얻는다.
     * </pre>
     * @param oData
     * @param sKey
     * @return int
     */
	/*
	 * public static double nvlMapDouble(Map oData, String sKey) { return
	 * nvlMapDouble(oData, sKey, NumberUtils.DOUBLE_ZERO); }
	 */

    /**
     * <pre>
     * Map안의 실수형 값을 얻는다.
     * </pre>
     * @param oData
     * @param sKey
     * @param def
     * @return int
     */
	/*
	 * public static double nvlMapDouble(Map oData, String sKey, double def) {
	 * return NumberUtils.toDouble(nvlMap(oData, sKey, Double.toString(def))); }
	 */

    /**
     * <pre>
     * Map안의 정수형 값을 얻는다.(int)
     * </pre>
     * @param oData
     * @param sKey
     * @return int
     */
	 
	  public static int nvlMapInt(Map oData, String sKey) 
	    { 
		    return nvlMapInt(oData,	  sKey, 0); 
	    }
	 

    /**
     * <pre>
     * Map안의 정수형 값을 얻는다.
     * </pre>
     * @param oData
     * @param sKey
     * @param def
     * @return int
     */
	 
	  public static int nvlMapInt(Map oData, String sKey, int def) 
	  { 
		  
		  return Integer.parseInt( nvlMap(oData, sKey, Integer.toString(def)) );
		   
	  }
	 

    /**
     * <pre>
     * Map안의 정수형 값을 얻는다.(long)
     * </pre>
     * @param oData
     * @param sKey
     * @return int
     */
    public static long nvlMapLong(Map oData, String sKey) {
        return nvlMapLong(oData, sKey);
    }

    /**
     * <pre>
     * Map안의 정수형 값을 얻는다.
     * </pre>
     * @param oData
     * @param sKey
     * @param def
     * @return int
     */
	/*
	 * public static long nvlMapLong(Map oData, String sKey, long def) { return
	 * NumberUtils.toLong(nvlMap(oData, sKey, Long.toString(def))); }
	 */

    
	public static StringBuffer addMsgBuffer(StringBuffer sb, String strMsg) {

		if (strMsg == null || strMsg.length() <= 0)
			return sb;

		if (sb.length() > 0) {
			sb.append(String.valueOf((char) 1));
		}

		sb.append(strMsg);

		return sb;

	}    
    


	/**
	 * <pre>
	 *  Map의 Key에 해당하는 값이 존재하지 않으면 초기값을 부여한다.
	 * </pre>
	 *
	 * @param mapRs  : Map 데이터
	 * @param strKey : Map Key
	 * @param strVal : Map의 초기값으로 세팅할 값
	 * @return Map
	 * @Method Name : setMap
	 */

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Map setMap(Map mapRs, String strKey, String strVal) {
		String strMapVal = "";

		if (mapRs == null || mapRs.isEmpty()) {
			Map mapVal = new HashMap();

			mapVal.put(strKey, strVal);
			return mapVal;
		}

		if (mapRs.get(strKey) != null && mapRs.get(strKey) != "") {
			strMapVal = mapRs.get(strKey).toString().trim();
		}

		if ("".equals(strMapVal)) {
			mapRs.put(strKey, strVal);
		}

		return mapRs;
	}	


	/**
	 * <pre>
	 *  도메인명을 반환한다.
	 * </pre>
	 *
	 * @param request : HttpServletRequest
	 * @return String
	 * @Method Name : getDomainName
	 */

	public static String getCurrentUrl(HttpServletRequest request) {
		String strUrl = "";

		try {
			   UrlPathHelper urlPathHelper  = new UrlPathHelper();     	
			   strUrl = urlPathHelper.getOriginatingRequestUri(request);	
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strUrl;
	}
		
	
	
}
