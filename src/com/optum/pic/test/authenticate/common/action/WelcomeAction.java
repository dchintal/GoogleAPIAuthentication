package com.optum.pic.test.authenticate.common.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.optum.pic.test.authenticate.common.form.WelcomeForm;



public class WelcomeAction extends Action{
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		WelcomeForm helloWorldForm = (WelcomeForm) form;
		helloWorldForm.setMessage("Getting and integerated into Struts world");
		
		//System.out.println("welcome action: ");
		
		//System.out.println("Auth Code:- " + helloWorldForm.getAuthCode());
		
		return mapping.findForward("success");
	
	}
	
}