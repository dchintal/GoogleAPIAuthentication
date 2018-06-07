<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- The top of file index.html -->
<html itemscope itemtype="http://schema.org/Article">
<head>
<!-- BEGIN Pre-requisites -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
	
</script>
<script src="https://apis.google.com/js/client:platform.js?onload=start"
	async defer>
	
</script>
<!-- Continuing the <head> section -->
<script>
	function start() {
		gapi.load( 'auth2', function() {
					auth2 = gapi.auth2.init({
					client_id : '554083208552-5jb4dno6hcd4hb5itphg7qai626a5t4u.apps.googleusercontent.com',
					// Scopes to request in addition to 'profile' and 'email'
					});
				});
	}
</script>
</head>
<body>
	<form name="welcomeHome" action="welcomeHome.do" method="post">
		<h1>Google API Auth test</h1>
		<br /> <input type="hidden" id="authCode" name="authCode" />
	</form>
	<!-- Add where you want your sign-in button to render -->
	<!-- Use an image that follows the branding guidelines in a real app -->
	<button id="signinButton">Sign in with Google</button>
	<script>
		$('#signinButton').click(function() {
			// signInCallback defined in step 6.
			auth2.grantOfflineAccess().then(signInCallback, function(res) {
				console.log(res);
			});
		});
	</script>
	<!-- Last part of BODY element in file index.html -->
	<script>
		function signInCallback(authResult) {
			if (authResult['code']) {
				document.getElementById('authCode').value = authResult['code'];
				document.welcomeHome.submit();
			}
		}
	</script>
</body>
</html>