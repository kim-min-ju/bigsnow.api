 
package kr.co.bigsnow.core.exception;

 
@SuppressWarnings("serial")
public class BusinessException extends RuntimeException {

    private final String code;
    private final Object data;

    public BusinessException(String code) {
    	super(code);
        this.code = code;
        this.data = null;
    }

    public BusinessException(String code, Object data) {
    	super(code);
        this.code = code;
        this.data = data;
    }

    @Override
    public String getMessage() {
    	return this.code;
    }

    public String getCode() {
        return this.code;
    }

    public Object getData() {
        return this.data;
    }

}
