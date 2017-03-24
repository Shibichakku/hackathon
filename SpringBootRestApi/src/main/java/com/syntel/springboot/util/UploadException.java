package com.syntel.springboot.util;

public class UploadException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadException(String message){
		super(message);
	}
	
	public UploadException(String message, Throwable t){
		super(message, t);
	}

}
