<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- The top of file index.html -->
<html itemscope itemtype="http://schema.org/Article">
<head>
  <!-- BEGIN Pre-requisites -->
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js">
  </script>
  <script src="https://apis.google.com/js/client:platform.js?onload=start" async defer>
  </script>
<!-- Continuing the <head> section -->
  <script>
    function start() {
      gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
          client_id: '554083208552-5jb4dno6hcd4hb5itphg7qai626a5t4u.apps.googleusercontent.com',
          // Scopes to request in addition to 'profile' and 'email'
          scope: 'email'
        });
      });
    }
  </script>
</head>
  <body>
  
   <h1> Google API Auth test </h1> <br/>
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
	
    // Hide the sign-in button now that the user is authorized, for example:
    $('#signinButton').attr('style', 'display: none');

    // Send the code to the server
    $.ajax({
      type: 'POST',
      url: 'http://localhost:8080/GoogleAPIAuthentication/LoginServlet?modeofAction=welcomePage',
      // Always include an `X-Requested-With` header in every AJAX request,
      // to protect against CSRF attacks.
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      },
      //contentType: 'application/octet-stream; charset=utf-8',
      success: function(result) {
        // Handle or verify the server response.
    	  alert(result);
    	  window.location.href = result;
      },
     // processData: false,
      data : {
    	  authCode : authResult['code']
      },
    });
  } else {
    // There was an error.
  }
}
</script>  
  </body>
</html>