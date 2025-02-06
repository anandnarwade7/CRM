package com.crm.Exception;

public class Error {
	 private int errorCode;
	    private String developermsg;
	    private String msg;
	    private long date;

	    // Constructor with all parameters
	    public Error(int errorCode, String developermsg, String msg, long date) {
	        this.errorCode = errorCode;
	        this.developermsg = developermsg;
	        this.msg = msg;
	        this.date = date;
	    }

	    // Getters and Setters
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

	    public long getDate() {
	        return date;
	    }

	    public void setDate(long date) {
	        this.date = date;
	    }

	    // Constructor with three parameters
	    public Error(int errorCode, String developermsg, String msg) {
	        this.errorCode = errorCode;
	        this.developermsg = developermsg;
	        this.msg = msg;
	    }

	    // Default constructor
	    public Error() {
	    }

	    @Override
	    public String toString() {
	        return "Error [errorCode=" + errorCode + ", developermsg=" + developermsg + ", msg=" + msg + ", date=" + date + "]";
	    }

}
