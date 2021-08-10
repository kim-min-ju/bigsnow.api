package kr.co.bigsnow.core.util;


import kr.co.bigsnow.model.ResBody;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseUtil {

	public static ResponseEntity<Object> createSuccessResponseEntity(
			Object data) {

		HttpStatus httpStatus = HttpStatus.OK;

		ResBody<Object> resBody = ResBody.builder()
				.message(httpStatus.getReasonPhrase())
				.status(httpStatus.value()).data(data).build();

		return new ResponseEntity<Object>(resBody, httpStatus);
	}

}
