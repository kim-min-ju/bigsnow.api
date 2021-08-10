 
package kr.co.bigsnow.core.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * @Class Name : DbDAO.java
 * @Description : DbDAO DAO Class
 * @Modification Information
 * @
 * @  수정일                   수정자                    수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2019.07.04  hch         최초생성
 *
 * @author 개발프레임웍크 표준
 * @since 2019. 07.04
 * @version 1.0
 * @see
 *
 */
@Slf4j
@Repository("dbMapper")
public class DbMapper {

	@Autowired
	@Qualifier("sqlSession")
	private SqlSessionTemplate sqlSession;

	public int dbInsert(String strXPath, Map<String, Object> mapReq ) throws Exception
	{

		int nSuccess = 1;

		try {
			mapReq = checkNullMap(mapReq);

			if ( sqlSession != null) {
				sqlSession.insert(strXPath , mapReq );
			}

		} catch ( NullPointerException e1) {
			nSuccess = -1;
			log.error(e1.toString());

			throw e1;
		} catch ( Exception e) {
			nSuccess = -1;
			log.error(e.toString());

			throw e;
		}

		return nSuccess;
	}

	public int dbUpdate(String strXPath, Map<String, Object> mapReq ) throws Exception
	{

		int nSuccess = 1;

		try {
			mapReq = checkNullMap(mapReq);

			if ( sqlSession != null ) {
				sqlSession.update(strXPath , mapReq );
			}
		} catch ( Exception e) {
			nSuccess = -1;

			log.error(e.toString());
			throw e;
		}

		return nSuccess;
	}


	public int dbDelete( String strXPath, Map<String, Object> mapReq ) throws Exception
	{

		int nSuccess = 1;

		try {
			mapReq = checkNullMap(mapReq);

			if ( sqlSession != null ) {
				sqlSession.delete(strXPath , mapReq );
			}
		} catch ( Exception e) {
			nSuccess = -1;

			log.error(e.toString());
			throw e;
		}
		return nSuccess;
	}


	public int dbDelete(String strXPath) throws Exception
	{

		int nSuccess = 1;

		try {
			if ( sqlSession != null ) {
				sqlSession.delete(strXPath );
			}
		} catch ( Exception e) {
			nSuccess = -1;

			log.error(e.toString());
			throw e;
		}

		return nSuccess;
	}


	@SuppressWarnings( "unchecked" )
	public List dbList(String strXPath ) throws Exception
	{

		List  list = null;
		try
		{
			list = sqlSession.selectList(strXPath , null );
		} catch ( Exception e) {
			log.error(e.toString());
			throw e;
		}

		return list;
	}


	@SuppressWarnings( "unchecked" )
	public List dbList( Map<String, Object> mapReq, String strXPath ) throws Exception
	{
 		List  list = null;
		try
		{
			mapReq = checkNullMap(mapReq);

			list = sqlSession.selectList(strXPath , mapReq );
		} catch ( Exception e) {
			log.error(e.toString());
			throw e;
		}
		return list;
	}

 	@SuppressWarnings( "unchecked" )
	public Map<String, Object> dbDetail( String strXPath, Map<String, Object> mapReq ) throws Exception
	{
		Map<String, Object>  mapRs = null;

		try
		{
			mapReq = checkNullMap(mapReq);

			mapRs =  sqlSession.selectOne(strXPath, mapReq);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}
		return mapRs;
	}

 	@SuppressWarnings( "unchecked" )
	public Map<String, Object> dbDetail( String strXPath) throws Exception
	{
		Map<String, Object>  mapRs = null;

		try
		{
			mapRs = dbDetail(strXPath, null);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}
		return mapRs;
	}



	public int getInt(String strXPath, Map<String, Object> mapReq)  throws Exception {
		int nCount = -1;

		try
		{
			mapReq = checkNullMap(mapReq);

			nCount = (Integer)sqlSession.selectOne(strXPath, mapReq);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}

		return nCount;
	}


	public int getInt(String strXPath) throws Exception {

		int nCount = -1;

		try
		{
			nCount = getInt(strXPath, null);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}
	    //iCount = Integer.parseInt(super.queryForObject(strXPath, map).toString());

		return nCount;
	}

	public String getString(String strXPath, Map<String, Object> mapReq)  throws Exception {
		String str = "";

		try
		{
			mapReq = checkNullMap(mapReq);

			str = (String)sqlSession.selectOne(strXPath, mapReq);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}

		return str;
	}


	public String getString(String strXPath) throws Exception {

		String str = "";

		try
		{
			str = getString(strXPath, null);
		} catch ( Exception e) {

			log.error(e.toString());
			throw e;
		}

		return str;
	}


	public int getCount(String strXPath, Map<String, Object> mapReq) throws Exception {
		return getInt(strXPath, mapReq) ;
	}

	public int getCount(String strXPath)  throws Exception{
		return getInt(strXPath) ;
	}


	public Map<String, Object> checkNullMap(Map<String, Object> reqMap) {

		if(reqMap == null){
			reqMap = new HashMap();
		}

		return reqMap;
	}
}
