package com.optum.pic.test.authenticate.common.form;

import org.apache.struts.action.ActionForm;

public class WelcomeForm extends ActionForm{
	
	String message;
	
	String authCode;

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}