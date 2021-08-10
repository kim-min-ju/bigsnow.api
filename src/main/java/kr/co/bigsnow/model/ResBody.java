 
package kr.co.bigsnow.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

 
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResBody<T> {

	@JsonInclude(Include.NON_NULL)
    private Integer status;

    @JsonInclude(Include.NON_NULL)
    private String code;

    @JsonInclude(Include.NON_NULL)
    private String message;

    @JsonInclude(Include.NON_NULL)
    private String messageDev;

    @JsonInclude(Include.NON_NULL)
    private T data;
}
