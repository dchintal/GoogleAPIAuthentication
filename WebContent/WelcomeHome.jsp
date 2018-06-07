<%@taglib uri="/WEB-INF/tld/struts-bean.tld" prefix="bean"%>

<html>
<head>
</head>
<body>
<h1>User Logged In </h1>
<h2><bean:write name="welcomeForm" property="message" /></h2>
<h3>Auth Code: <bean:write name="welcomeForm" property="authCode" /></h3>

</body>
</html>