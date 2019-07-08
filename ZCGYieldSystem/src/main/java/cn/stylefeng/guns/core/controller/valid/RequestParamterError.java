package cn.stylefeng.guns.core.controller.valid;

import org.springframework.validation.Errors;

public class RequestParamterError extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 	参数错误原因
	 */
	private Errors error;

	public RequestParamterError() {
		super();
	}
	
	public RequestParamterError(String msg) {
		super(msg);
	}
	
	public RequestParamterError(String msg ,Throwable e) {
		super(msg, e);
	}
	
	public RequestParamterError(String msg ,Errors error) {
		super(msg);
		this.error = error;
	}

	public RequestParamterError(Errors error2) {
		super();
		this.error = error2;
	}

	public Errors getError() {
		return error;
	}

	public void setError(Errors error) {
		this.error = error;
	}
	
	
}
