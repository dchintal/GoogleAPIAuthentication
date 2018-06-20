<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<html>
<head>
</head>
<body>
	<h1>User Logged In</h1>
	<h2>
		<bean:write name="welcomeForm" property="message" />
	</h2>
	<h3>
		Auth Code:
		<bean:write name="welcomeForm" property="authCode" />
	</h3>
	
	<!-- Click <a href="http://localhost:8080/GoogleAPIAuthentication/addOnDetails?authCode=
			<%=request.getParameter("authCode")%> "> here </a> 
		to get next page using existing Auth code. <br />
	
	Click <a href="http://localhost:8080/GoogleAPIAuthentication/addOnDetails.do"> here </a> to get next page with out using existing Auth code.
	 -->
</body>
</html>