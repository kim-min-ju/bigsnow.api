
package kr.co.bigsnow.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@Configuration
public class EzpmpUrlProperty {
//
//	/** 서비스 관리 공통 */
//	@Value("${interface.url.svcm}")
//	private String svcm;
//
//	/** 시스템 관리 */
//	@Value("${interface.url.sysm}")
//	private String sysm;

	public static HttpHeaders createHeaderWithBoardApiToken() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}

	public static <T> HttpEntity<T> createHttpEntityWithEmptyHeaderAndBody(
			T body) {
		HttpHeaders httpHeaders = createHeaderWithBoardApiToken();
		return new HttpEntity<T>(body, httpHeaders);
	}

	public static <T> HttpEntity<T> createHttpEntityWithBody(T body) {
		HttpHeaders httpHeaders = createHeaderWithBoardApiToken();
		return new HttpEntity<T>(body, httpHeaders);
	}

	public static <T> HttpEntity<T> createHttpEntityWithEmptyBody() {
		HttpHeaders httpHeaders = createHeaderWithBoardApiToken();
		return new HttpEntity<T>(httpHeaders);
	}

	public static <T> HttpEntity<T> createHttpEntityWithEmptyBody(
			T serviceId) {
		HttpHeaders httpHeaders = createHeaderWithBoardApiToken();
		return new HttpEntity<T>(serviceId, httpHeaders);
	}

}
