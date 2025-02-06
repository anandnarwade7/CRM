package com.crm.Exception;

public class GlobalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public int errorCode;
	public String developermsg;
	public String msg;
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getDevelopermsg() {
		return developermsg;
	}
	public void setDevelopermsg(String developermsg) {
		this.developermsg = developermsg;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public GlobalException(int errorCode, String developermsg, String msg) {
		super(msg);
		this.errorCode = errorCode;
		this.developermsg = developermsg;
		this.msg = msg;
	}
	public GlobalException(int errorCode, String developermsg, String msg, Throwable cause) {
		super(msg, cause);
		this.errorCode = errorCode;
		this.developermsg = developermsg;
		this.msg = msg;
	}
	
	public GlobalException() {
		super();
		
	}
	
	
	
	

}
