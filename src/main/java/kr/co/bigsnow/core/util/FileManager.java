package kr.co.bigsnow.core.util;

import static java.nio.file.Files.newBufferedWriter;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import kr.co.bigsnow.core.db.DbService;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 
 
/**
 * Class Summary. <br>
 * FileManager class.
 * @since 1.00
 * @version 1.00 - 2020. 01. 20
 * @author  sosunj
 * @see
 */
 
public class FileManager {

     @Resource(name = "dbSvc")
	 protected DbService dbSvc; 
    
    
	 private String    mFolderNm  = "/upload/file/";  // 폴더명
	 private String    mStagPath  = "";  
	 private String    mRealPath  = "";
	                   // 파일확장자가 DB에 들어가 있지 않는 경우에만 사용한다. 반드시 소문자로 기록 요망
	 //private List<String> mArrExtList = Arrays.asList( new String[]{"mp3","wma","wav","m4a","3gp","hwp","doc","docx","ppt","pptx","zip","xls","xlsx","pdf","txt","jpg","gif","png"});
	 private List<String> mArrExtList = new ArrayList<String>();
	 
	 private static final Logger LOG = Logger.getLogger(FileManager.class.getName());
    
	 public  StringBuffer mSb  = new StringBuffer();   
	 
	/**
     * Method Summary. <br>
     * 파일 저장소 및 폴더 정보 조회 method
     * @param 
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		 
	 public FileManager() {
		 
	 }
	 
	/**
     * Method Summary. <br>
     * 파일 저장소 및 폴더 정보 조회 method
     * @param HttpServletRequest
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		
	public  FileManager(HttpServletRequest request  )  {
			try {
				setRealPath(request);
				getFileExtAble();
				getFolderInfo();
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	}	 
	 
	/**
     * Method Summary. <br>
     * 파일 저장소 및 폴더 정보 조회 method
     * @param HttpServletRequest
     * @param DbService
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		
	public  FileManager(HttpServletRequest request, DbService db ) throws Exception  {
			dbSvc    = db;
			
			setRealPath(request);
			getFileExtAble();
			getFolderInfo();
	}	 
	
	/**
     * Method Summary. <br>
     * 파일 저장소 및 폴더 정보 조회 method
     * @param HttpServletRequest
     * @param DbService
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		
	public  FileManager(HttpServletRequest request, DbService db, String strFolder ) throws Exception  {
			dbSvc    = db;
			
			setRealPath(request);
			getFileExtAble();
			this.mFolderNm = strFolder;
			
			makeFolder();
	}	 
	 	
	
	
	/**
     * Method Summary. <br>
     * 파일 저장소 및 폴더 정보 조회 method
     * @param MultipartHttpServletRequest
     * @param DbService
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		
	 public  FileManager(MultipartHttpServletRequest  request, DbService db ) throws Exception  {
		dbSvc    = db;
		
		setRealPath(request);
		getFileExtAble();
		getFolderInfo();
	 }		
		 
		 
	public String getSaveFolder()  throws Exception  {
		getFolderInfo();
		
		return mStagPath + "/" +  mFolderNm;
	}
	 
    /**
     * Method Summary. <br>
     * RealPath를 조회 method
     * @param request
     * @return void
     * @throws 
     * @since 1.00
     * @see
     */		
	public String setRealPath(HttpServletRequest request) throws Exception {
	    
    	//String strOsName = System.getProperty("os.name").toUpperCase();
    	
    	//if ( strOsName.indexOf("WINDOW") > -1 )
    	mRealPath = request.getSession().getServletContext().getRealPath("/");
    	
    	if ( mRealPath == null) {
    		mRealPath = System.getProperty("user.dir");
    	}
    	
    	
        System.out.print("Real Path =====>" + mRealPath);;    	
    	
    	return mRealPath;

	}
	
	

	public Map<String, Object> getRequestMap(HttpServletRequest multipartRequest) {
	      
        return getRequestMap((MultipartHttpServletRequest)multipartRequest, false);
    }
	
	
	/**
     * Method Summary. <br>
     * RequestMap 조회 method
     * @param MultipartHttpServletRequest
     * @return HashMap 
     * @throws  
     * @since 1.00
     * @see
     */		
	 		
	public Map<String, Object> getRequestMap(MultipartHttpServletRequest multipartRequest) {
	      
	        return getRequestMap(multipartRequest, false);
	}
	
	/**
     * Method Summary. <br>
     * RequestMap 조회 method
     * @param MultipartHttpServletRequest
     * @return HashMap 
     * @throws  
     * @since 1.00
     * @see
     */		
	 		
	public Map<String, Object> getRequestMap(MultipartHttpServletRequest multipartRequest, boolean encUpload) {
	      HashMap map = new HashMap();
	        try {
	            Map parameter = multipartRequest.getParameterMap();

	            if (parameter == null)
	                return null;

	            Iterator it = parameter.keySet().iterator();
	            Object paramKey = null;
	            String[] paramValue = null;
	            String strKey;
	            String[] arrVal;
	            
	            while (it.hasNext()) {
	                paramKey = it.next();
	                paramValue = (String[]) parameter.get(paramKey);

	                strKey = paramKey.toString().toLowerCase();
	                if (paramValue.length > 1) {
	                	arrVal = multipartRequest.getParameterValues(paramKey.toString());
	                	
	                	for (int nLoop = 0; nLoop < arrVal.length; nLoop ++)
	                	{
	                	    arrVal[nLoop] = CommonUtil.removeXSS(arrVal[nLoop]);
	                	}
	                	
	                    map.put(strKey, arrVal);
	                } else {
	                    map.put(strKey, (paramValue[0] == null) ? "" : CommonUtil.removeXSS(paramValue[0].trim()));
	                }
	            }
	          
	           
	            
	        } catch (Exception e) {
	            LOG.error(e.toString());
	        }		
	        
	        // 파일정보를 Map에 기록
	        map.put(CoreConst.MAP_UPFILE_KEY, saveFileInfo(multipartRequest, encUpload));
	         
	        
	        return map;
	}
		
	
    /**
     * Method Summary. <br>
     * 업로드 가능한 파일 확장자 조회 method
     * @param request
     * @return void
     * @throws 
     * @since 1.00
     * @see
     */			
	
	private boolean getFileExtAble() throws Exception  {
		String[]  arrExtString={"mp3","wma","wav","m4a","3gp","hwp","doc","docx","ppt","pptx","zip","xls","xlsx","pdf","txt","jpg","gif","png", "jpeg"};
 
		 try {
			 
			   if ( mArrExtList.size() <= 0 ) 
			   {
				   mArrExtList.addAll(Arrays.asList(arrExtString));			 
			   }
/*			   
		       List rsList =  dbSvc.dbList("file.getFileExtAble");
		       
		       if ( rsList == null || rsList.isEmpty() ) 
		       {  // 정보가 없는 경우		    	   
		    	   LOG.info("파일 저장소 정보가 없습니다.");
		    	   return false;
		       }
		        
		       //mArrExtAble = new String[rsList.size()];
		       Map mapRs;
		       
		       for (int nLoop=0; nLoop < rsList.size(); nLoop++) {	
		    	   mapRs = (Map)rsList.get(nLoop);
		    	   mArrExtList.add( CommonUtil.nvlMap(mapRs, "FILE_EXT").toLowerCase());
		       }
	*/	 
		 } catch ( Exception e) {
			 LOG.error(e.toString());
			 return false;
		 }		
		 
		 return true;
	}	
	
    /**
     * Method Summary. <br>
     * 폴더정보를 조회 또는 물리적인 폴더를 생성 method
     * @param 
     * @return boolean
     * @throws 
     * @since 1.00
     * @see
     */	
	public boolean getFolderInfo() throws Exception  {
		 try {
		       makeFolder();  //폴더를 생성함
		       		    	      
		 } catch ( Exception e) {
			 LOG.error(e.toString());
			 throw e;
		 }		
		 
		 return true;
	}
	

	/**
     * Method Summary. <br>
     * 물리적인 폴더를 생성  method
     * @param 
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */	
	public boolean makeFolder()   {
		try {
			makeFullFolder( mRealPath + "/" + mFolderNm );
			
		} catch ( Exception  e) {
			 LOG.error(e.toString());
			 return false;
		}
		
		return true;
		
	}	
	
	/**
     * Method Summary. <br>
     * 물리적인 폴더를 생성  method
     * @param 
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */	
	public boolean makeFullFolder(String strMakeDir)   {
		try {
       	 
			strMakeDir = strMakeDir.replace("//", "/");
			File dir = new File(strMakeDir.replace("//", "/"));
			
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			
		} catch ( Exception  e) {
			 LOG.error(e.toString());
			 return false;
		}
		
		return true;
		
	}	
	
	/**
     * Method Summary. <br>
     * 물리적인 폴더를 생성  method
     * @param 
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */	
	public boolean makeFolder(String strMakeDir)   {
		try {
       	 
			strMakeDir = strMakeDir.replace("///", "/");
			strMakeDir = strMakeDir.replace("//", "/");
			strMakeDir = strMakeDir.replace("/\\", "/");
			
			File dir = new File(mRealPath + "/" + strMakeDir.replace("//", "/"));
			
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			
		} catch ( Exception  e) {
			 LOG.error(e.toString());
			 return false;
		}
		
		return true;
		
	}
	
	/**
     * Method Summary. <br>
     * 물리적인 파일을 생성  method
     * @param 
     * @return  
     * @throws  
     * @since 1.00
     * @see
     */		
	public boolean makeFile(String strFile, String strCtnt, String strType) {
        boolean bFlag = true;
        BufferedWriter output = null;
        
		try {
			File targetFile = new File(strFile);
			targetFile.createNewFile();

			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile.getPath()), strType));

			output.write(strCtnt);
			
		} catch(UnsupportedEncodingException uee) {
			uee.printStackTrace();
			bFlag = false;
		} catch(IOException ioe) {
			ioe.printStackTrace();
			bFlag = false;
		}finally {
			try {
				output.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		return bFlag;
	}
	
 
	
	
	/**
     * Method Summary. <br>
     * 파일정보를 List 변수에 할당 및 물리적인 파일 저장  method
     * @param MultipartHttpServletRequest
     * @param boolean 파일을 저장할 지 결정한다. 
     * @return List<Map> 
     * @throws  
     * @since 1.00
     * @see
     */		


	public List<Map> upfileWrite(MultipartHttpServletRequest multipartRequest, String strUploadFolder, boolean bFileEncryption) {
 
		List<Map>  lstUpFile = new ArrayList();
 		
		try {
			   
			  Iterator<String> iterator = multipartRequest.getFileNames();
			  Map fileMap = (Map)multipartRequest.getFileMap();
			  
			  String fileParameterName;
			  List<MultipartFile> fileList;
			  
			  Map<String, String> mapInfo;
			  
			  String strFileExt;
			  String strUniq;
			  String strPhyFile;
			  
			  mRealPath = setRealPath ( (HttpServletRequest)multipartRequest );
			  
			  String strFolderNm = (!"".equals(strUploadFolder)) ? strUploadFolder : mFolderNm;
			   
			  strFolderNm +=   "/" + DateUtil.getCurrentDate("", "YYYYMM");
			  
			  makeFullFolder( mRealPath + "/" + strFolderNm );
			  
System.out.println("File Save Folder=>" +  mRealPath + "/" + strFolderNm);			  
			  
			  while(iterator.hasNext()) {
				  
				   fileParameterName = iterator.next();
			       fileList = multipartRequest.getFiles(fileParameterName);			       			       

			       for(MultipartFile fileItem : fileList) {
			    	   mapInfo = new HashMap();

			    	   if(!fileItem.isEmpty()) 
			    	   {
			    		   // 파일 확장자 점검 , 사이즈 점검
			    		   
			    		   strFileExt = getFileExt(fileItem.getOriginalFilename()).toLowerCase();
			    		   strUniq    = CommonUtil.getUniqueId().replaceAll("-", "");
			    		   
			    		   if ( fileAbleExt(strFileExt)) { // 파일확장자가 업로드 가능한 경우
				    		   strPhyFile = "/" + strFolderNm + "/" + strUniq + "." + strFileExt;
				    		   strPhyFile = strPhyFile.replaceAll("//", "/");
				    		   
				    		   mapInfo.put("file_gbn",      fileParameterName); // 사용자가 upload한 input name 명
				    	
				    		   mapInfo.put("lgc_file_nm",  fileItem.getOriginalFilename());    // 사용자가 upload한 파일명
				    		   mapInfo.put("phy_file_nm",  strPhyFile);                        // 저장된 파일명				    		   
				    		   mapInfo.put("file_size",    Long.toString(fileItem.getSize())); // 파일 사이즈
				    		   mapInfo.put("file_ext",     strFileExt);                        // 파일 확장자
				    		   
				    		   
System.out.println(mapInfo.toString());				    		   
				    		   				    		   
				    		   
System.out.println("strPhyFile Save Before=>" +  mRealPath +  strPhyFile );	
				    		   
				    		   fileItem.transferTo(new File(mRealPath +  strPhyFile));         // 파일저장
System.out.println("strPhyFile Save File=>" +  mRealPath +  strPhyFile );				    		   
/*				    		   
				    		   if( bFileEncryption )  //파일 내용을 암호화 해서 저장한다.
				    		   {
				    			   mapInfo.put("phy_file_nm", ScureUtil.fileEncryptor( mRealPath , strPhyFile ));
				    			   
				    			   //암호화 전 파일 삭제
				    			   File delFile = new File( mRealPath + strPhyFile );
					    		   
				    			   if(delFile.exists()) {
				    				   delFile.delete();
				    			   }
				    		   }
*/				    		 		   
				    		   
			    		   } else {
			    			   
			    			   System.out.println( "Banned file extensions:" + fileItem.getOriginalFilename());
			    			   LOG.info("Banned file extensions:" + fileItem.getOriginalFilename());
			    		   }
			    	   }  
			    	   
			    	   lstUpFile.add(mapInfo);
			    	   
			       }
			  }

		} catch (Exception e) {
			
			System.out.println( e );
			
			LOG.error(e.toString());
		}
		
		return lstUpFile;
	}
	 
	
	public String getConvertFileName(String strOrgFileName)
	{
		   String strPhyFile = ""; 
		   String strFileExt  = getFileExt(strOrgFileName).toLowerCase();
		   String strUniq    =  CommonUtil.getUniqueId().replaceAll("-", "");
		   
		   if ( fileAbleExt(strFileExt)) { // 파일확장자가 업로드 가능한 경우
 		       //strPhyFile = "/" + mStagPath + "/" + mFolderNm + "/" + strUniq + "." + strFileExt;
			   strPhyFile = strUniq + "." + strFileExt;
 		       strPhyFile = strPhyFile.replaceAll("//", "/");		
		   }
		   
		   return strPhyFile;
	}
	


	/**
     * Method Summary. <br>
     * 업로드 가능한 파일인지 점검  method
     * @param String 파일확장자
     * @return boolean 가능여부
     * @throws  
     * @since 1.00
     * @see
     */	
	private boolean fileAbleExt(String strFileExt)   {
		try {
            
			if ( strFileExt == null || "".equals(strFileExt))
				return false;
			strFileExt = strFileExt.toLowerCase();
			
			for(int nLoop=0; nLoop < mArrExtList.size(); nLoop++ ) {
				if ( mArrExtList.get(nLoop).equals(strFileExt) ) { // 업로드가 가능한 파일 확장자 점검
					return true;
				}
			}
		 
		} catch ( Exception  e) {
			 LOG.error(e.toString());
			 return false;
		}
		
		return false;
		
	}
	
    /**
     * Method Summary. <br>
     * 업로드한 파일정보를 공통 DB에 기록  method
     * @param Map 파일 기록 정보
     * @return void
     * @throws Exception
     * @since 1.00
     * @see
     */			 
	
	public boolean fileDbSave(Map mapReq) throws Exception  {
		 
		 Map paramMap   = new HashMap();
		 
		 try {
			 
			   if (mapReq == null || mapReq.isEmpty())
				   return true;
			   
			   String strRelKey    = CommonUtil.nvlMap(mapReq, "ref_pk");  // 테이블 키
			   String strRelTblNm  = CommonUtil.nvlMap(mapReq, "ref_nm");   // 테이블 명
			   List fileList       = (List)mapReq.get(CoreConst.MAP_UPFILE_KEY);
			   
//			   if ("".equals(strRelKey) || "".equals(strRelTblNm) || fileList == null || fileList.isEmpty())  
//				   return false;
			   
			   if (fileList == null || fileList.isEmpty())  
			   return false;

			   
			   String[] arrFileEtc   = CommonUtil.getMapValArray(mapReq, "file_etc");  // 비고 값
 
			   
			   Map mapInfo;
   
			   
			   for(int nLoop=0; nLoop < fileList.size(); nLoop++) {
				   mapInfo = (Map)fileList.get(nLoop);
				   
				   if (mapInfo == null || mapInfo.isEmpty())
					   continue;
				   
				   mapInfo.put("reg_user_no" ,  CommonUtil.nvl(mapReq.get("reg_user_no")));  
				   mapInfo.put("ref_pk",  strRelKey);           // 테이블 키
				   mapInfo.put("ref_nm",  strRelTblNm);         // 테이블 명
                 
				   if (arrFileEtc != null && arrFileEtc.length == fileList.size() ) {
					   mapInfo.put("file_etc",  arrFileEtc[nLoop]); // 파일구분을 기록한다.
				   }				   
				   
				   dbProc(mapInfo);  // DB 처리
				   
				   mapReq.put("file_no", CommonUtil.nvlMap(mapInfo, "file_no"));
				   
			   }
 
			   
		 } catch ( Exception e) {
			 LOG.error(e.toString());
		 }		
		 
		 return true;
	}		
	
    /**
     * Method Summary. <br>
     * 파일정보를 DB에 기록한다(수정 및 신규등록) method
     * @param Map 파일정보
     * @return boolean
     * @throws Exception
     * @since 1.00
     * @see
     */		
	 
	public boolean dbProc(Map mapFile) throws Exception
	{
		boolean bProc = true;
		
		try {
			  String strFileName = CommonUtil.nvlMap(mapFile, "phy_file_nm");
			  String strFileNo   = CommonUtil.nvlMap(mapFile, "file_no");
			  
			  
			  
			  
			  /*			   
   		   mapInfo.put("file_gbn",      fileParameterName); // 사용자가 upload한 input name 명
		    	
   		   mapInfo.put("lgc_file_nm",  fileItem.getOriginalFilename());    // 사용자가 upload한 파일명
   		   mapInfo.put("phy_file_nm",  strPhyFile);                        // 저장된 파일명				    		   
   		   mapInfo.put("file_size",    Long.toString(fileItem.getSize())); // 파일 사이즈
   		   mapInfo.put("file_ext",     strFileExt);                        // 파일 확장자
*/    					  
			  
			  
			  
			  
			  
			  if ( "".equals(strFileName)) // 파일명이 없는 경우
				  return true;
			  
			  if ( "".equals(strFileNo)) {  // 신규 데이터
				 
			     dbSvc.dbInsert("file.insertFile", mapFile);  // DB에 기록함			     
			     
			  } else {  // 수정 데이터
				
			    removeFile(mapFile);  // 기존 파일을 삭제함

			    dbSvc.dbUpdate("file.updateFile", mapFile);  // DB에 기록함 			     
			  }

			  
		} catch ( Exception e) {
			//System.out.println( e.toString());
			
			LOG.error(e.toString());
			bProc = false;
			throw e;
		}
		
		return bProc;
	}
	

	
    /**
     * Method Summary. <br>
     * 화면에서 Check Box 일부 파일 삭제. method
     * @param Map 파일정보
     * @return 
     * @throws 
     * @since 1.00
     * @see
     */		
    public void checkRemoveFile(Map mapReq) {
    	Map mapParam = new HashMap();
    	
    	if (mapReq == null || mapReq.isEmpty())
    		return;
    	
    	String[] arrFileNo = CommonUtil.getMapValArray(mapReq, "rm_file_no");  // 삭제대상 파일 선정
    	for (int nLoop = 0; nLoop < arrFileNo.length ; nLoop++) {
    		mapParam.clear();
    		try {    		
    		   mapParam.put("file_no", arrFileNo[nLoop]);
    		
    		   removeFile(mapParam);    		
    		   dbSvc.dbDelete("file.deleteFile", mapParam);  // DB에 기록함 	
    		} catch (Exception e) {
    			LOG.error(e.toString());
    		}
    	}
    }
	
    /**
     * Method Summary. <br>
     * 물리적인 파일을 삭제한다. method
     * @param Map 파일정보
     * @return 
     * @throws 
     * @since 1.00
     * @see
     */		
    public void removeFile(Map mapReq) {
    	
    	if(mapReq == null)  
    		 return ;
    	
    	try {
	                
	            Map mapFile = dbSvc.dbDetail("file.getFileDetail", mapReq);
	            if ( mapFile == null || mapFile.isEmpty())
	            	return;
	            
	            File delFile = new File(mRealPath + this.changeFileName(CommonUtil.nvlMap(mapFile, "PHY_FILE_NM")));

	            if(delFile.exists()) 
		        delFile.delete();	
		        
	      
        } catch (Exception e) {
        	LOG.error(e);
        }	        
    }
    
    
    /**
     * Method Summary. <br>
     * comnfile테이블을 사용안하는 물리적인 파일을 삭제한다. method
     * @param Map 파일정보
     * @return 
     * @throws 
     * @since 1.00
     * @see
     */		
    public void removeNotComnFile(Map mapReq) {
    	
    	if(mapReq == null)  
    		 return ;
    	
    	try {
	            
	            File delFile = new File(mRealPath + this.changeFileName(CommonUtil.nvlMap(mapReq, "phy_file_nm")));
    			//File delFile = new File(mRealPath + CommonUtil.nvl(mapReq.get("phy_file_nm")));
    			if(delFile.exists()) 
		        	delFile.delete();	
		        
	      
        } catch (Exception e) {
        	LOG.error(e);
        }	        
    }
    
    /**
     * Method Summary. <br>
     * comnfile테이블을 사용안하는 물리적인 파일을 삭제한다. method
     * @param Map 파일정보
     * @return 
     * @throws 
     * @since 1.00
     * @see
     */		
    public void removeFile(String strFile, boolean bFullPath) {
    	
    	if(strFile == null)  
    		 return ;
    	
    	try {
    		
    		   if ( !bFullPath ) {
    			   strFile = mRealPath + strFile ; 
    		   }
    		    
	            File delFile = new File( strFile );
    			 
    			if(delFile.exists()) 
		        	delFile.delete();	
		        
	      
        } catch (Exception e) {
        	LOG.error(e);
        }	        
    }    
     
    /**
     * Method Summary. <br>
     * 게시물에 포함된 첨부파일 모두를 삭제한다. method
     * @param Map 파일정보
     * @return 
     * @throws 
     * @since 1.00
     * @see
     */		
    public void removeAllFile(Map mapReq) {
    	
    	if(mapReq == null || mapReq.isEmpty())  
    		 return ;
    	
    	String strRelKey    = CommonUtil.nvl(mapReq.get("rel_key"));
    	String strRelTblKey = CommonUtil.nvl(mapReq.get("rel_tbl"));
    	
    	if ( "".equals(strRelKey) || "".equals(strRelTblKey)) {
    		LOG.error("파일삭제시 필요한 인수 누락 : rel_key, rel_tbl");
    		return;
    	}
    	
    	try {
	           Map mapParam = new HashMap();
	           mapParam.put("rel_key",    strRelKey);
	           mapParam.put("rel_tbl", strRelTblKey);
	           
	           List lstFile = dbSvc.dbList("file.getFileList", mapReq);
	           if ( lstFile == null || lstFile.isEmpty())
	            	return;
	           
	           Map mapFile;
	           File delFile;
	           
	           for(int nLoop=0; nLoop < lstFile.size(); nLoop++) {
	              mapFile = (Map)lstFile.get(nLoop);
	            
	              delFile = new File(mRealPath + this.changeFileName(CommonUtil.nvlMap(mapFile, "PHY_FILE_NM")));
	            //delFile = new File(mRealPath + CommonUtil.nvl(mapReq.get("PHY_FILE_NM")));
			      if(delFile.exists()) 
			       	delFile.delete();
			      
			      dbSvc.dbDelete("file.deleteFile", mapFile);  // DB에 기록함 
			   
	            }
	           
        } catch (Exception e) {
        	LOG.error(e);
        }	        
    }
 
 
    /**
     * Method Summary. <br>
     * RealPath를 반환
     * @return String
     * @throws  
     * @since 1.00
     * @see
     */
    public String getRealPath() {
        return mRealPath;
    }
    
    /*
     * 경로 조작 및 자원 삽입에 대비한 PHY_FILE_NM에서 (. & )제거
     * 
     */
    public String changeFileName(String file_nm) {
    	file_nm = file_nm.replaceAll("\\.\\.","");
    	file_nm = file_nm.replaceAll("&","");
    	return file_nm;
    }
 
    public List<Map> excelFileToList( String strFilePath,  Map mapExcelInfo ) {
    
    	List<Map> lstRs = new ArrayList();
  
    	mSb.setLength(0); // StringBuffer 초기화
    	
    	if ( mapExcelInfo == null )
    		return null;
    				
    	int nSkipLine = CommonUtil.nvlMapInt(mapExcelInfo, CoreConst.EXCEL_SKIP_HEADER, 0);
    	
    	
		 if ( strFilePath.endsWith(".xls")) {
			lstRs = this.xlsToList (  mRealPath + strFilePath, 0, nSkipLine,   mapExcelInfo );	  
		 } else if (strFilePath.endsWith(".xlsx")) {
			lstRs = this.xlsxToList(  mRealPath + strFilePath, 0, nSkipLine,   mapExcelInfo );
		 }		
         
		 return lstRs;
   }
    
    public  List<Map> xlsToList( String strExcelFile, Map mapExcelInfo)   
    {
 	   return xlsToList( strExcelFile, 0, 0, mapExcelInfo); 
    }
   
    
    public  List<Map> xlsToList( String strExcelFile, int nSheetNo, int nSkipLine, Map mapExcelInfo )   
    {
  	  
  	  List<Map> lstRs = new ArrayList();
  	  
  	  if ( "".equals( strExcelFile)) 
  		 return lstRs;
  	  
    	Workbook tempWorkbook;
  	  
		DataFormatter formatter = new DataFormatter();
		// 데이트 포맷
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");		 
		 
		try  {
	 		
            String[][] arrExcelInfo   = (String[][])mapExcelInfo.get(CoreConst.EXCEL_FIELD_INFO);  // 엑셀 파일 정보
            Map        mapExcelAlpah  = this.getExcelColumnAlpha();
            Map		   mapExcelAlpah1 = this.getExcelColumnAlpha1();
            String     strExcelEOF    = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_EOF);  // 엑셀의 EOF
            		
            //int[] arrPos = this.getExcelColumnPos(arrExcelInfo);
            
			FileInputStream xlsFile = new FileInputStream(new File(strExcelFile));
			// 파일 스트림을 XSSFWorkbook 객체로 생성
			HSSFWorkbook  workbook = new HSSFWorkbook (xlsFile);
			 
			// XSSFWorkbook 의 첫번째 시트를 가져옴
			HSSFSheet sheet = workbook.getSheetAt(nSheetNo);
			DataFormatter dataFormatter = new DataFormatter();
			
			int nRow = 0;
			for (Row row: sheet) {
				
				nRow = row.getRowNum();
				
				if ( nRow < nSkipLine) 
					continue;
				
				
 		  		Map<String, String> mapRs = new HashMap();
 		  		
 		  		StringBuffer sbErrMsg = new StringBuffer();
 		  		
				int nIdx = 0;
				boolean bEOf = false;
				
				for(Cell cell: row) 
	            {
					
					
	            	String strCellVal = "";
	             	
	            	String strColAlpah = CommonUtil.nvlMap(mapExcelAlpah, String.valueOf(nIdx));
	            	
	            	if ( strColAlpah.equals(strExcelEOF) ) {  
	            		bEOf = true;
	            	}
	            	
	            	if (HSSFDateUtil.isInternalDateFormat( cell.getCellStyle().getDataFormat() )) {
                
	            		try {
                        	strCellVal = sdf.format(cell.getDateCellValue());
                        } catch  ( Exception e ) {
                        	strCellVal = dataFormatter.formatCellValue(cell);
                        }
	            		
	            	} else {
	            		strCellVal = dataFormatter.formatCellValue(cell);
	            	}
	            	
	            	if ( strColAlpah.equals(strExcelEOF) && (strCellVal == null || "".equals(strCellVal))) {  // 해당 컬럼에 데이터가 없는 경우 종료하고 For 문을 빠져 나간다ㅏ.

	            		return lstRs;
	            	}
	            	
	            	this.excelFieldToMap( mapRs, nRow, nIdx, strCellVal , arrExcelInfo, mapExcelAlpah, sbErrMsg );
	            	nIdx++;
	            }
	           
				CommonUtil.addMsgBuffer(mSb, sbErrMsg.toString());
	            this.excelInitVal(mapExcelInfo, mapRs );
	            
	            if(bEOf && !mapRs.isEmpty()) {
	            	lstRs.add(mapRs);
	            }
	            
	        }			
		 
		} catch ( Exception e) {
			//System.out.println(e.toString());
			LOG.error(e);
		}
 
  	  
  	  return lstRs;
  	  
    }              
    
    private void excelInitVal(Map mapExcelInfo, Map mapRs )
    {
    	String[][] arrExcelInfo   = (String[][])mapExcelInfo.get(CoreConst.EXCEL_INIT_VALUE);  // 엑셀 파일 정보

    	if ( arrExcelInfo == null || arrExcelInfo.length <= 0)
    		return;
    	
    	for(int nIdx=0; nIdx < arrExcelInfo.length; nIdx++)
    	{
    		String[] arrData = arrExcelInfo[nIdx];
    		
    		CommonUtil.setMap(mapRs, arrData[0], arrData[1]);
    		
    	}
    }
    
    
    public  List<Map> xlsxToList( String strExcelFile, Map mapExcelInfo)   
    {
 	   return xlsxToList( strExcelFile, 0, 0, mapExcelInfo); 
    }
   
    
    public  List<Map> xlsxToList( String strExcelFile, int nSheetNo, int nSkipLine, Map mapExcelInfo )   
    {
  	  
  	  List<Map> lstRs = new ArrayList();
  	  
  	  if ( "".equals( strExcelFile)) 
  		 return lstRs;
  	  
    	Workbook tempWorkbook;
  	  
		DataFormatter formatter = new DataFormatter();
		// 데이트 포맷
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
		 
		try  {
                 
                String[][] arrExcelInfo   = (String[][])mapExcelInfo.get(CoreConst.EXCEL_FIELD_INFO);  // 엑셀 파일 정보
                String     strExcelEOF    = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_EOF);  // 엑셀의 EOF
                
                Map        mapExcelAlpah  = this.getExcelColumnAlpha();
			
				FileInputStream xlsFile = new FileInputStream(new File(strExcelFile));
//---------------------------

	            XSSFWorkbook workbook = new XSSFWorkbook(xlsFile);
	            
	         
	            //시트 수 (첫번째에만 존재하므로 0을 준다)
	            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
	            XSSFSheet sheet=workbook.getSheetAt(0);
	            //행의 수
	            int rows=sheet.getPhysicalNumberOfRows();
	            for(int nRow=0; nRow < rows; nRow++){
	            	
					if ( nRow < nSkipLine) 
						continue;
	            	
	            	
					StringBuffer sbErrMsg     = new StringBuffer(); 
	 		  		Map<String, String> mapRs = new HashMap();
					
	                //행을읽는다
	                XSSFRow row=sheet.getRow(nRow);
	                if(row !=null){
	                    //셀의 수
	                    int cells=row.getPhysicalNumberOfCells();
	                    for(int nIdx=0; nIdx <= cells; nIdx++)
	                    {
	                    	
	                    	
	                        try {
										//셀값을 읽는다
										XSSFCell cell=row.getCell(nIdx);
										
										String strCellVal="";
										
										String strColAlpah = CommonUtil.nvlMap(mapExcelAlpah, String.valueOf(nIdx));
										
										//셀이 빈값일경우를 위한 널체크
										if(cell == null){
										    continue;
										}else{
											
											strCellVal=cell.getStringCellValue() + "";	                        	
										/*	
											
										    //타입별로 내용 읽기
										    switch (cell.getCellType()){
										    case XSSFCell.CELL_TYPE_FORMULA:
										        value=cell.getCellFormula();
										        break;
										    case XSSFCell.CELL_TYPE_NUMERIC:
										        value=cell.getNumericCellValue()+"";
										        break;
										    case XSSFCell.CELL_TYPE_STRING:
										        value=cell.getStringCellValue()+"";
										        break;
										    case XSSFCell.CELL_TYPE_BLANK:
										        value=cell.getBooleanCellValue()+"";
										        break;
										    case XSSFCell.CELL_TYPE_ERROR:
										        value=cell.getErrorCellValue()+"";
										        break;
										    }
										 */   
										}
										
										System.out.println(strColAlpah + "[" + nIdx + "]===>[" + strCellVal + "]");				   
										
										if ( strColAlpah.equals(strExcelEOF) && (strCellVal == null || "".equals(strCellVal))) {  // 해당 컬럼에 데이터가 없는 경우 종료하고 For 문을 빠져 나간다ㅏ.
											return lstRs;
										}	                        
										
										this.excelFieldToMap( mapRs, nRow, nIdx, strCellVal , arrExcelInfo, mapExcelAlpah, sbErrMsg );
										
							} catch (Exception e) {
								 
								LOG.error(e);
							}
			            	 
	                    }
	 
	                    this.excelInitVal(mapExcelInfo, mapRs );		            
			            
			            CommonUtil.addMsgBuffer(mSb, sbErrMsg.toString());		   
			            lstRs.add(mapRs);	                    
	                    
	                }
	            }				
				
				
//---------------------
				 
			 
		} catch ( Exception e) {
			//System.out.println(e.toString());
			LOG.error(e);
		}
 
  	  
  	  return lstRs;
  	  
    }        
    
    
    public  List<Map> xlsxToList_OLD( String strExcelFile, int nSheetNo, int nSkipLine, Map mapExcelInfo )   
    {
  	  
  	  List<Map> lstRs = new ArrayList();
  	  
  	  if ( "".equals( strExcelFile)) 
  		 return lstRs;
  	  
    	Workbook tempWorkbook;
  	  
		DataFormatter formatter = new DataFormatter();
		// 데이트 포맷
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");		 
		 
		try  {
                 
                String[][] arrExcelInfo   = (String[][])mapExcelInfo.get(CoreConst.EXCEL_FIELD_INFO);  // 엑셀 파일 정보
                String     strExcelEOF    = CommonUtil.nvlMap(mapExcelInfo, CoreConst.EXCEL_EOF);  // 엑셀의 EOF
                
                Map        mapExcelAlpah  = this.getExcelColumnAlpha();
			
				Workbook workbook;
				FileInputStream xlsFile = new FileInputStream(new File(strExcelFile));
			 
				workbook = new XSSFWorkbook(xlsFile);
				
				Sheet sheet = workbook.getSheetAt(nSheetNo);
				DataFormatter dataFormatter = new DataFormatter();
			
				int nRow = 0;
				for (Row row: sheet) {
					
					nRow++;
					
					if ( nRow <= nSkipLine) 
						continue;
					
					StringBuffer sbErrMsg     = new StringBuffer(); 
	 		  		Map<String, String> mapRs = new HashMap();
	 		  		
	 		  		
					int nIdx =0;
					
		            for(Cell cell: row) 
		            {
		            	
		            	String strCellVal = "";		            	
		            	
		            	try {
		            					            	
				            	String strColAlpah = CommonUtil.nvlMap(mapExcelAlpah, String.valueOf(nIdx));
				            	
				            	
				            	if (HSSFDateUtil.isInternalDateFormat(cell.getCellStyle().getDataFormat())) {
			                        try {
			                        	strCellVal = sdf.format(cell.getDateCellValue());
			                        } catch  ( Exception e ) {
			                        	LOG.error(e);
			                        	strCellVal = dataFormatter.formatCellValue(cell);
			                        }
				            		
				            	} else {
				            		strCellVal = dataFormatter.formatCellValue(cell);
				            	}
		 
				            	
				            	System.out.println(strColAlpah + "[" + nIdx + "]===>[" + strCellVal + "]");				            	
				            	
				            	if ( strColAlpah.equals(strExcelEOF) && (strCellVal == null || "".equals(strCellVal))) {  // 해당 컬럼에 데이터가 없는 경우 종료하고 For 문을 빠져 나간다ㅏ.
				            		return lstRs;
				            	}
		
				            	this.excelFieldToMap( mapRs, nRow, nIdx, strCellVal , arrExcelInfo, mapExcelAlpah, sbErrMsg );
		            	} catch ( Exception e) {
		            		LOG.error(e);
		            	} finally {
		            		nIdx++;	
		            	}
		            	
		            	
		            	
		            	
		            }
		            
		            this.excelInitVal(mapExcelInfo, mapRs );		            
		            
		            CommonUtil.addMsgBuffer(mSb, sbErrMsg.toString());		   
		            lstRs.add(mapRs);
		            
		           //System.out.println();
		        }			
				 
		} catch ( Exception e) {
			//System.out.println(e.toString());
			LOG.error(e);
		}
 
  	  
  	  return lstRs;
  	  
    }            
    
    private Map excelFieldToMap( Map mapRs, int  nRow, int nIdx, String strCellVal , String[][] arrExcelInfo, Map mapExcelAlpah, StringBuffer sbErrMsg )
    {
    	try {
    		
    		 
    		
    		String strSplitChar = String.valueOf( (char)2 );
    		
    		// 필드명 매핑 작업
    		String strFieldName = "col_" + mapRs.size();
    		String strFieldType = CoreConst.EXCEL_FMT_STRING;
    		String strFieldNece = "N";
    		
    		String strColAlpah = CommonUtil.nvlMap(mapExcelAlpah, String.valueOf(nIdx));
    		
    		
    		String[] arrFieldInfo = getExcelColumnInfo(arrExcelInfo, strColAlpah );
    		
    		if ( arrFieldInfo == null ) 
    			return mapRs;
    		
    		strFieldName = arrFieldInfo[1];
    		strFieldType = arrFieldInfo[2];
    		strFieldNece = arrFieldInfo[3];
    		
    		// 필요없는 문자열 삭제 작업
    		if ( CoreConst.EXCEL_FMT_DATE.equals(strFieldType) ) {
    			strCellVal = CommonUtil.removeDateFormat(strCellVal);
    			
    		    if ( !DateUtil.isDate( strCellVal ))	{
    		    	CommonUtil.addMsgBuffer(sbErrMsg, nRow  + strSplitChar + strColAlpah + strSplitChar + strCellVal + " 날짜 형식이 잘 못 되었습니다.");
    		    }
    			
    		} else if ( CoreConst.EXCEL_FMT_NUMERIC.equals(strFieldType) ) {
    			strCellVal = CommonUtil.removeComma(strCellVal);
    		} 
    		
    		if ( "Y".equals(strFieldNece) && "".equals(strCellVal)) {
    			CommonUtil.addMsgBuffer(sbErrMsg, nRow  + strSplitChar + strColAlpah + strSplitChar + "값이 입력되지 않았습니다.");
    		}
    		
    		mapRs.put(strFieldName, strCellVal);
    		
    		mapRs.put("excel_row", nRow);
    		mapRs.put("excel_col", strColAlpah );
    		
    	} catch ( Exception e) {
    		LOG.error(e);
    	}
    	
    	return mapRs;
    	
    }
    
    
 	 public  Map excelMapTo(Map mapData,  String[] arrCol) {
 	
 		 
 		 if ( arrCol == null && arrCol.length == 0)
 			 return mapData;
 		 
 		 for(int nLoop=0; nLoop < arrCol.length; nLoop++)
 		 {
             mapData.put(arrCol[nLoop], CommonUtil.nvlMap(mapData, "col_" + nLoop)); 
 		 }
 		
 		 return mapData;
 	 }		  
    
     public  String getFileExt(String strFileName) {
         if (strFileName != null && !"".equals(strFileName)) {
             return strFileName.substring(strFileName.lastIndexOf(".") + 1, strFileName.length()); // 파일확장빼기
         } else {
             return "";
         }

     }   
     
     public String fileRead( String strFilePath )
     {
    	    BufferedReader in = null; 
    	    StringBuffer  sbFile = new StringBuffer();
    	    
    		try {


    			strFilePath = strFilePath.replace("//", "/");
    			strFilePath = strFilePath.replace("/\\", "/");
    			strFilePath = strFilePath.replace("\\", "/");
    			
System.out.println("strFilePath :" + strFilePath);    			
    			
	    			File fileDir = new File(mRealPath + strFilePath);
	
	    			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
	
	    			String strFile;
	
	    			while ((strFile = in.readLine()) != null) {
	    			    sbFile.append( strFile + "\n");
	    			}
    		    }
    		    catch (UnsupportedEncodingException e)
    		    {
    		    	LOG.error(e);
    		    }
    		    catch (IOException e)
    		    {
    		    	LOG.error(e);
    		    }
    		    catch (Exception e)
    		    {
    		    	LOG.error(e);
    		    } finally {
    		    	if ( in != null) {
	    		    	try {
							in.close();
						} catch (IOException e) {
							
							e.printStackTrace();
						}
    		    	}
				}
    		return sbFile.toString(); 		
    		
    	} 
 
     public boolean fileWrite( String strFilePath, String strContent )  
     {  
    	 Writer out = null; 
         boolean bFlag = true;
         
         try {
        	    if ( strFilePath == null || "".equals(strFilePath) )
        	    	return false;
        	 
        	    /*int    nLastPos = strFilePath.lastIndexOf("\\");
        	    String strDir   = strFilePath.substring(0, nLastPos  );
        	    makeFolder(mRealPath + strDir);  // 폴더 생성
*/        	 
			/*
			 * makeFolder(mRealPath + strFilePath); // 폴더 생성
			 */        	    
        	    System.out.println(mRealPath + strFilePath);
        	    
	     		File fileDir = new File(mRealPath + strFilePath);
	     			
	     		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileDir), "UTF8"));
	 
	     		out.append(strContent);
	     		
	     		out.flush();
     	    } 
     	   catch (UnsupportedEncodingException e) 
     	   {
     		  bFlag = false;
     		  LOG.error(e);
     		  System.out.println(e.toString());
     	   } 
     	   catch (IOException e) 
     	   {
     		  bFlag = false;
     		  LOG.error(e);
     		 System.out.println(e.toString());
     	    }
     	   catch (Exception e)
     	   {
     		  bFlag = false;
     		  LOG.error(e);
     		 System.out.println(e.toString());
     	   }  finally {
		    	if ( out != null) {
    		    	try {
    		    		out.close();
					} catch (IOException e) {
						
						e.printStackTrace();
						
					}
		    	}    		  
    	  }
         
         return bFlag;
     }
     
     
 	public boolean fileDownload(HttpServletRequest request,HttpServletResponse response, String strRealFilNm,String strViewFileNm) throws IOException {
		 
 		FileInputStream fis = null;
 		boolean bRlt     = true;
        boolean bEncFile = false;
        String  strFullPath = "";
        
        
 		try {
			String strFilePath = setRealPath(request);
			
			
			if ("".equals(strFilePath)) {
				return false;
			}
			
			if ( strRealFilNm.endsWith(".enc")) {
			//	strFullPath = ScureUtil.fileDecrypt(strFilePath , strRealFilNm);
				bEncFile     = true;
				
			} else {
				strFullPath =  strFilePath + strRealFilNm;
			}
			
			File file        = new File( strFullPath);
			
			
			
			System.out.println("root = " + strFullPath );
			
			if (file.exists() && file.isFile()) {
				response.setContentType("application/octet-stream; charset=utf-8");
				response.setContentLength((int) file.length());
			
				String browser = getBrowser(request);
				String disposition = getDisposition(strViewFileNm, browser);
				
				response.setHeader("Content-Disposition", disposition);
				response.setHeader("Content-Transfer-Encoding", "binary");
				
				OutputStream out = response.getOutputStream();
				
				fis = new FileInputStream(file);
				FileCopyUtils.copy(fis, out);

				out.flush();
				out.close();
				
			} else {
				bRlt = false;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			bRlt = false;
		} finally {

			if ( bEncFile ) {
				removeFile(strFullPath, true);
			}			
			
			if (fis != null)
				fis.close();
			
		}   
 		
 		return bRlt;
	}

	private String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		if (header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1)
			return "MSIE";
		else if (header.indexOf("Chrome") > -1)
			return "Chrome";
		else if (header.indexOf("Opera") > -1)
			return "Opera";
		return "Firefox";
	}

	private String getDisposition(String filename, String browser)
			throws UnsupportedEncodingException {
		String dispositionPrefix = "attachment;filename=";
		String encodedFilename = null;
		if (browser.equals("MSIE")) {
			encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
		} else if (browser.equals("Firefox")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Opera")) {
			encodedFilename = "\"" + new String(filename.getBytes("UTF-8"), "8859_1") + "\"";
		} else if (browser.equals("Chrome")) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < filename.length(); i++) {
				char c = filename.charAt(i);
				if (c > '~') {
					sb.append(URLEncoder.encode("" + c, "UTF-8"));
				} else {
					sb.append(c);
				}
			}
			encodedFilename = sb.toString();
		}
		return dispositionPrefix + encodedFilename;
	}     

 	
	public StringBuffer getMsgBuffer() {
		return mSb;
	}
	
	public Map getExcelColumnAlpha() {
		
		Map mapColAlpha = new HashMap();
		
		String[] arrAlpha = {"A" ,"B" ,"C" ,"D" ,"E" ,"F" ,"G" ,"H" ,"I" ,"J" ,"K" ,"L" ,"M" ,"N" ,"O" ,"P" ,"Q" ,"R" ,"S" ,"T" ,"U" ,"V" ,"W" ,"X" ,"Y" ,"Z" ,"AA" ,"AB" ,"AC" ,"AD" ,"AE" ,"AF" ,"AG" ,"AH" ,"AI" ,"AJ" ,"AK"}; 
		
		for ( int nLoop=0; nLoop < arrAlpha.length; nLoop++)
		{
			mapColAlpha.put(String.valueOf(nLoop), arrAlpha[nLoop]);
		}
		
		return mapColAlpha;
		
	}
	
	public Map getExcelColumnAlpha1() {
		
		Map mapColAlpha = new HashMap();
		
		String[] arrAlpha = {"D" ,"E" ,"F" ,"G" ,"H" ,"I" ,"J" ,"K" ,"L" ,"M" ,"N" ,"O" ,"P" ,"Q" ,"R" ,"S" ,"T" ,"U" ,"V" ,"W" ,"X" ,"Y" ,"Z" ,"AA" ,"AB" ,"AC" ,"AD" ,"AE" ,"AF" ,"AG" ,"AH" ,"AI" ,"AJ" ,"AK"}; 
		
		for ( int nLoop=0; nLoop < arrAlpha.length; nLoop++)
		{
			mapColAlpha.put(String.valueOf(nLoop), arrAlpha[nLoop]);
		}
		
		return mapColAlpha;
		
	}
	
	public String[] getExcelColumnInfo(String[][] arrData, String strAlpah )
	{
		
		
		
		try {
			for (int nIdx=0; nIdx < arrData.length; nIdx++)
			{
				if ( strAlpah.equals(arrData[nIdx][0])) { 
				   return arrData[nIdx];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		 
	}

	
	public static boolean makeSignFile(String base64, String fileName){
			
			String data = base64.split(",")[1];
			
			byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);
			
			try {
			
				
				    String[] arrFile = fileName.split("\\.");
				
				    String strFileExt  = arrFile[ arrFile.length - 1 ]; 
					
					BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imageBytes));
					
					ImageIO.write(bufImg, strFileExt, new File(fileName));
				
					
					setFilePermition ( fileName );
					
			} catch (IOException e) {
				
				e.printStackTrace();
				
				return false;
			}
			
			return true;
			
		}	
		
	
	 public static void setFilePermition( String strFile )
	 {
		 try {
				 File targetFile = new File(strFile);
				 
		         targetFile.setReadable(true, false);
		         targetFile.setWritable(true, false);
		         targetFile.setExecutable(true, false);		 
		 } catch ( Exception e ) {
			 System.out.println(e.toString());
		 }
	 }	
	 
	 public boolean zipFileDownload(HttpServletRequest request, HttpServletResponse response, Map mapReq ,String[] arrFileLgcNm, String[] arrFilePhyNm ) { 
		 
		ZipOutputStream zout = null;
		
		boolean			bFlag = false;
		
		Map<String, Object> mapResult = new HashMap<String, Object>();

		File delFile;
		
		String 			zipName  = arrFileLgcNm[0].toString().substring(0, arrFileLgcNm[0].indexOf("."))  + ".zip"; // ZIP 압축 파일명
		String 			strPath = "";

		if (arrFilePhyNm.length > 0) {

			try {

				String strRealPath = request.getSession().getServletContext().getRealPath("/");

				strPath = "upload/doc/"; // ZIP 압축 파일 저장경로 //ZIP파일 압축 START
				zout = new ZipOutputStream(new FileOutputStream(strRealPath + strPath + zipName));
				byte[] buffer = new byte[1024];
				FileInputStream in = null;

				for (int nLoop = 0; nLoop < arrFilePhyNm.length; nLoop++) {

					in = new FileInputStream(strRealPath + arrFilePhyNm[nLoop]); // 압축 대상 파일
					zout.putNextEntry(new ZipEntry(arrFileLgcNm[nLoop])); // 압축파일에 저장될 파일명

					int len;

					while ((len = in.read(buffer)) > 0) {
						zout.write(buffer, 0, len);

						// 읽은 파일을 ZipOutputStream에 Write
					}
					zout.closeEntry();
					in.close();
				}

				zout.flush();
				zout.close();
				// ZIP파일 압축 END

				// 파일다운로드 START
				bFlag = this.fileDownload(request, response, strPath + zipName, zipName);
				
				// 파일다운로드 END
				
				// 다운로드 후 해당 파일 삭제
				delFile = new File(strRealPath + this.changeFileName( strPath + zipName ));
				
				if (delFile.exists())
					delFile.delete();
				
				return bFlag;
				
			} catch (IOException e) {
				System.out.println(e.getMessage());
				return false;
				// Exception
			} finally {
				if (zout != null) {
					zout = null;
				}
			}
		}
		
		return bFlag;
	}
	 
	/**
	 * File 반환
	 * @param request
	 * @param strRealFilNm
	 * @return
	 */
	public File getFileObject(HttpServletRequest request, String strRealFilNm) {
		String strFullPath = "";

		try {
			String strFilePath = setRealPath(request);

			if ("".equals(strFilePath)) {
				return null;
			}

			if (strRealFilNm.endsWith(".enc")) {
			//	strFullPath = ScureUtil.fileDecrypt(strFilePath, strRealFilNm);

			} else {
				strFullPath = strFilePath + strRealFilNm;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}

		return new File(strFullPath);
	}
	

	
	/**
     * Method Summary. <br>
     * 파일정보를 List 변수에 할당 및 물리적인 파일 저장  method
     * @param MultipartHttpServletRequest
     * @param boolean 파일을 저장할 지 결정한다. 
     * @return List<Map> 
     * @throws  
     * @since 1.00
     * @see
     */		
	public List<Map> saveFileInfo(MultipartHttpServletRequest multipartRequest, boolean encUpload) {
 
		List<Map>  uploadFileList = new ArrayList();
 		
		try {
			   
			  Iterator<String> iterator = multipartRequest.getFileNames();
			  Map fileMap = (Map)multipartRequest.getFileMap();
			      
			  String fileParameterName;
			  List<MultipartFile> fileList;
			  
			  Map<String, String> infoMap;
			  
			  String strFileExt;
			  String strUniq;
			  String strPhyFile;
			  
			  while(iterator.hasNext()) {
				  
				   fileParameterName = iterator.next();
			       fileList = multipartRequest.getFiles(fileParameterName);			       			       
			       
			       for(MultipartFile fileItem : fileList) {
			    	   infoMap = new HashMap();
			    	   
			    	   if(!fileItem.isEmpty()) {
			    		   // 파일 확장자 점검 , 사이즈 점검
			    		   
			    		   strFileExt = CommonUtil.getFileExt(fileItem.getOriginalFilename()).toLowerCase();
			    		   strUniq    =  CommonUtil.getUniqueId().replaceAll("-", "");
			    		   
			    		   if ( fileAbleExt(strFileExt)) { // 파일확장자가 업로드 가능한 경우
				    		   strPhyFile = "/" + mFolderNm + "/" + strUniq + "." + strFileExt;
				    		   strPhyFile =strPhyFile.replaceAll("//", "/");
				    		   
				    		   LOG.info("strPhyFile:" + strPhyFile);	   
				    		   
				    		   infoMap.put("input_name",   fileParameterName); // 사용자가 upload한 input name 명
				    	
				    		   infoMap.put("lgc_file_nm",  fileItem.getOriginalFilename());    // 사용자가 upload한 파일명
				    		   infoMap.put("file_size",    Long.toString(fileItem.getSize())); // 파일 사이즈
				    		   infoMap.put("phy_file_nm",  strPhyFile);                        // 저장된 파일명
				    		   infoMap.put("file_ext",     strFileExt);                        // 파일 확장자
				    		   
				    		   fileItem.transferTo(new File(mRealPath +  strPhyFile));         // 파일저장
				    		   
				    		   setFilePermition ( mRealPath +  strPhyFile );
/*				    		   
				    		   //파일 암호화 저장
				    		   if( encUpload ) {
				    			   
				    			   infoMap.put("phy_file_nm", fileEncryptor( strPhyFile ));
				    			   
				    			   //암호화 전 파일 삭제
				    			   File delFile = new File( mRealPath + strPhyFile );
					    		   
				    			   if(delFile.exists()) {
				    				   delFile.delete();
				    			   }
				    		   }
*/				    		   
				    		        		   
				    		   
			    		   } else {
			    			   LOG.info("Banned file extensions:" + fileItem.getOriginalFilename());
			    		   }
			    	   }  
			    	   
			    	   uploadFileList.add(infoMap);
			    	   
			       }
			  }

		} catch (Exception e) {
			LOG.error(e.toString());
		}
		
		return uploadFileList;
	}	
}