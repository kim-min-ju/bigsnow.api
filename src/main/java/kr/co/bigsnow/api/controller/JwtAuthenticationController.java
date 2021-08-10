package kr.co.bigsnow.api.controller;


import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import kr.co.bigsnow.api.jwt.JwtRequest;
import kr.co.bigsnow.api.jwt.JwtResponse;
import kr.co.bigsnow.api.jwt.JwtTokenUtil;
import kr.co.bigsnow.api.jwt.JwtUserDetailsService;
import kr.co.bigsnow.core.controller.StandardController;
import kr.co.bigsnow.core.util.CommonUtil;

@RestController
@CrossOrigin
public class JwtAuthenticationController  extends StandardController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@RequestMapping(value = "/authenticate"  , method = RequestMethod.POST )
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
 
	/* 
		String strUserName = authenticationRequest.getUsername();
		String strUserPwd = authenticationRequest.getPassword();
		
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

		final UserDetails userDetails = userDetailsService.loadUserByUsername("user_id");

	//	final String token = jwtTokenUtil.generateToken(userDetails);

		// Map mapToken = userDetailsService.verifyJWT( token );
		
		//-----------------------------------------------------------------------
*/		
		final String token = userDetailsService.createToken("1234566", "정소선", "sosun", "aaaa", "");
        System.out.println(token);
     /*   
        Map<String, Object> claimMap = userDetailsService.verifyJWT(jwt);
        System.out.println(claimMap); // 토큰이 만료되었거나 문제가있으면 null	
        
        String strReqtoken = authenticationRequest.getToken();
        Map<String, Object>  claimMap2 = userDetailsService.verifyJWT(strReqtoken);
        System.out.println(claimMap2); // 토큰이 만료되었거나 문제가있으면 null	
     */   	
		
		//-----------------------------------------------------------------------
		
		return ResponseEntity.ok(new JwtResponse(token));
	}
	
	
	@RequestMapping(value = "/tokenTest"  , method = RequestMethod.POST )
	public ResponseEntity<?> tokenTest(HttpServletRequest  request, HttpServletResponse response, RequestEntity<Map<String, Object>> requestEntity) throws Exception {
	
 
		final String requestTokenHeader = request.getHeader("Authorization");

		Map<String, Object> mapReq	= super.setRequestMap(request, response, requestEntity);
		
		System.out.println("mapReq:" + mapReq.toString());
		
        Map<String, Object>  claimMap2 = userDetailsService.verifyJWT(requestTokenHeader);
        System.out.println(claimMap2); // 토큰이 만료되었거나 문제가있으면 null	
		
		//-----------------------------------------------------------------------
		
		return ResponseEntity.ok(new JwtResponse(requestTokenHeader));
	}	
	
	private void authenticate(String username, String password) throws Exception {
		try {
			
			Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
			
			authenticationManager.authenticate(authentication);
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
	
	
}

