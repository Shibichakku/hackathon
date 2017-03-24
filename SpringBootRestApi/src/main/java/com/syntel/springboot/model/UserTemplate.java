package com.syntel.springboot.model;

import org.springframework.web.multipart.MultipartFile;

public class UserTemplate {

	public UserTemplate( String accountName, String templateType, String templateName) {
		super();
//		this.file = file;
		this.accountName = accountName;
		this.templateType = templateType;
		this.templateName = templateName;
	}

	public MultipartFile file;
	
	public String accountName;
	
	public String templateType;

	public String templateName;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	
}
