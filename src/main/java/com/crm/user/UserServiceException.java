package com.crm.user;

public class UserServiceException extends RuntimeException {

	
	private static final long serialVersionUID = 1L;
	private final int statusCode;

	    public UserServiceException(int statusCode, String message, Throwable cause) {
	        super(message, cause);
	        this.statusCode = statusCode;
	    }

	    public UserServiceException(int statusCode, String message) {
	    	   super(message);
			this.statusCode =statusCode;
		}

		public int getStatusCode() {
	        return statusCode;
	    }
}
