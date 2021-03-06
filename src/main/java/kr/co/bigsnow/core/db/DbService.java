package kr.co.bigsnow.core.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.bigsnow.core.util.CommonUtil;


@Service
public class DbService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DbService.class);

	/**
	 * Mapper
	 */
	@Resource(name="dbMapper")
	private DbMapper dbMapper;

	public final static int  PAGE_ROWCOUNT = 10;

//	public Connection getCurrentConnection()  throws Exception {
//
//	      SqlSession mSqlSession = dbMapper.getSqlSession();
//
//	      Connection conn = mSqlSession.getConnection();
//	      // conn.setAutoCommit(false);
//
//	      return conn;
//	}
//
	public int dbInsert( SqlSession sqlSession, String strXPath, Map<String, Object> mapReq ) throws Exception {
		int nRlt = -1;

		try {
		      nRlt = dbMapper.dbInsert(strXPath, mapReq );
		} catch ( Exception e )  {

			errorLogWrite(sqlSession, strXPath, mapReq, e);

			throw e;
		}

		return nRlt;
	}

	public int dbInsert( String strXPath, Map<String, Object> mapReq ) throws Exception {
		return dbInsert(null, strXPath, mapReq);
	}

	public int dbUpdate( SqlSession sqlSession, String strXPath, Map<String, Object> mapReq ) throws Exception {
		int nRlt = -1;

		try {
		      nRlt = dbMapper.dbUpdate(strXPath, mapReq );
		} catch ( Exception e )  {

			errorLogWrite(sqlSession, strXPath, mapReq, e);
			throw e;
		}

		return nRlt;
	}

	public int dbUpdate( String strXPath, Map<String, Object> mapReq ) throws Exception {
		return dbUpdate(null, strXPath, mapReq);
	}

	public int dbDelete( SqlSession sqlSession, String strXPath, Map<String, Object> mapReq ) throws Exception {

		int nRlt = -1;

		try {
		      nRlt = dbMapper.dbDelete(strXPath, mapReq );
		} catch ( Exception e )  {

			errorLogWrite(sqlSession, strXPath, mapReq, e);
			throw e;
		}

		return nRlt;
	}

	public int dbDelete( String strXPath, Map<String, Object> mapReq ) throws Exception {
		return dbDelete(null, strXPath, mapReq);
	}

	public int dbDelete(SqlSession sqlSession, String strXPath ) throws Exception {
		int nRlt = -1;

		try {
		      nRlt = dbMapper.dbDelete( strXPath );
		} catch ( Exception e )  {

			errorLogWrite(sqlSession, strXPath, null, e);

			throw e;
		}

		return nRlt;
	}

	public int dbDelete(String strXPath ) throws Exception {

		return dbDelete(null, strXPath);

	}

	public List dbList(String strXPath ) throws Exception {
		List rsList =  null;

		try {
			rsList = dbMapper.dbList(strXPath);
		} catch ( Exception e )  {

			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return rsList;

	}

	public List<Map<String, Object>> dbList( String queryId, Map<String, Object> mapReq ) throws Exception {

		List<Map<String, Object>> dbLst = null;
		Map<String, Object> dbMap = null;
		try{
			    dbLst = dbMapper.dbList(mapReq, queryId);

				if( dbLst !=null && dbLst.size() > 0) {

					for(int nLoop=0; nLoop<dbLst.size(); nLoop++) {

						dbMap = (Map) dbLst.get(nLoop);

						// ?????? ???????????? ?????????
						setFieldScty( dbMap , mapReq );

					} //end for(int nLoop=0; nLoop<dbLst.size(); nLoop++)
				}

		} catch ( Exception e )  {

			throw e;
		}

		return dbLst;
	}

	public Map<String, Object> dbDetail(String strXPath ) throws Exception
	{

		Map<String, Object> dbMap = null;

		try{
			dbMap = dbMapper.dbDetail(strXPath);
		} catch ( Exception e )  {

			errorLogWrite(null, strXPath, null, e);

			throw e;
		}

		return dbMap;

	}

	public Map<String, Object> dbDetail( String strXPath, Map<String, Object> mapReq ) throws Exception
	{

		Map<String, Object> dbMap = null;

		try{
		        dbMap = dbMapper.dbDetail(strXPath, mapReq);

		        // ?????? ???????????? ?????????
				setFieldScty( dbMap , mapReq );

		} catch ( Exception e )  {
			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return dbMap;
	}

	
	public int dbCount( String strXPath, Map<String, Object> mapReq) throws Exception
	{
		return dbInt(  strXPath, mapReq);
	}	
	
	public int dbInt( String strXPath, Map<String, Object> mapReq) throws Exception
	{
	    int nRlt = -1;
		try{
		    nRlt = dbMapper.getInt(strXPath, mapReq);
		} catch ( Exception e )  {
			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return nRlt;
	}

	public int dbInt( String strXPath) throws Exception
	{

	    int nRlt = -1;
		try{
		    nRlt = dbMapper.getInt(strXPath);
		} catch ( Exception e )  {
			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return nRlt;

	}

	public String dbString( String strXPath, Map<String, Object> mapReq) throws Exception
	{

	    String strRlt = "";

		try{
			strRlt = dbMapper.getString(strXPath, mapReq);
		} catch ( Exception e )  {
			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return strRlt;

	}

	public String dbString( String strXPath) throws Exception
	{
	    String strRlt = "";

		try{
			strRlt = dbMapper.getString(strXPath);
		} catch ( Exception e )  {
			errorLogWrite(null, strXPath, null, e);
			throw e;
		}

		return strRlt;
	}

    /**
     * Method Summary. <br>
     * Excel ?????? ???????????? ?????????????????? ????????? insert ?????????.
     * @param SqlSession sqlSession ???????????? ??????
     * @param String strXPath xml ID
     * @param List excelList
     * @return insert ????????? ????????? Map??? ?????? List??? ????????????.
     * @throws
     * @since 1.00
     * @see
     */
	public List<Map<String, Object>> dbExcelInsert( SqlSession sqlSession, String strXPath, List<Map<String, Object>> excelList ) throws Exception {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		Map<String, Object> mapData = null;

		if (CommonUtil.isEmpty(excelList)) {

			for (int nLoop = 0; nLoop < excelList.size(); nLoop++) {
				mapData = excelList.get(nLoop);

				try {
					dbMapper.dbInsert( strXPath, mapData );
				} catch (Exception e) {
					
					resultList.add(mapData);
					LOGGER.error("fail to process file", e);
				}

			}
		}

		return resultList;
	}


	/**
     * Method Summary. <br>
     * ??? ???????????? ????????? ?????? ?????? ????????????. method.
     * @param HashMap mapReq ???????????? ??????
     * @param String strMapKey HashMap??? ???
     * @return int ??? ???????????? ????????? ??????
     * @throws
     * @since 1.00
     * @see
     */
	public int getPageRowCount(Map<String, Object> mapReq, String strMapKey) {
		return getPageRowCount(mapReq, strMapKey, PAGE_ROWCOUNT);
	}

    /**
     * Method Summary. <br>
     * ??? ???????????? ????????? ?????? ?????? ????????????. method.
     * @param HashMap mapReq ???????????? ??????
     * @param String strMapKey HashMap??? ???
     * @return int ??? ???????????? ????????? ??????
     * @throws
     * @since 1.00
     * @see
     */
	public int getPageRowCount(Map<String, Object> mapReq, String strMapKey, int nRowCount) {

		if (mapReq == null || mapReq.isEmpty())
			return nRowCount;

		try
		{
			nRowCount = Integer.parseInt( CommonUtil.getNullTrans(mapReq.get( strMapKey), nRowCount));
		} catch (Exception e) {
			nRowCount = PAGE_ROWCOUNT;
		}

		return nRowCount;
	}




	/**
	  * Method Summary. <br>
	  * ??????????????? ????????? ?????? ????????????..
	  * 2013.12.24 	?????????
	  * @param Map mapReq	???????????? ??????
	  * @param Map dbMap	???????????? ?????? ??????
	  * @return void
	  * @throws
	  * @since 1.00
	  * @see
	  */
	private void setFieldScty(Map<String, Object> dbMap, Map<String, Object> mapReq)
	{

	}

	private void errorLogWrite(SqlSession sqlSession, String strSqlPath, Map<String, Object> mapReq, Exception e)
	{

//		String strXPath = "log.proclogInsert";
//		Map paramMap = new HashMap();
//
//		try {
//
//			paramMap.put("site_type", "ADMIN");
//			paramMap.put("log_type", "?????????");
//			paramMap.put("log_stat", "ERR");
//			paramMap.put("pgm_nm",    strSqlPath);
//			paramMap.put("log_cont",  e.toString());
//			paramMap.put("remk",      (mapReq != null ) ? mapReq.toString() : "");
//
//			if ( sqlSession != null) {
//				sqlSession.insert(strXPath , paramMap );
//				//sqlSession.getCurrentConnection().commit();
//			} else {
//			    dbMapper.insert( strXPath , paramMap );
//			}
//
//
//		} catch ( Exception err) {
//			LOGGER.error(e.toString());
//		}

	}


}
