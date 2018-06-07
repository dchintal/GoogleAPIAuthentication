package com.optum.pic.test.authenticate.common.form;

import org.apache.struts.action.ActionForm;

public class WelcomeForm extends ActionForm{
	
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}