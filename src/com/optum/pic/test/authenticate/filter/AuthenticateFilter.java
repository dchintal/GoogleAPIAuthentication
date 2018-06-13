package com.optum.pic.test.authenticate.filter;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;

public class AuthenticateFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	 
      HttpServletRequest httpRequest = (HttpServletRequest) request;
       
      String path = ((HttpServletRequest) request).getRequestURI();
    
		// Set path to the Web application client_secret_*.json file you downloaded from the
		// Google API Console: https://console.developers.google.com/apis/credentials
		// You can also find your Web application client ID and client secret from the
		// console and specify them directly when you create the GoogleAuthorizationCodeTokenRequest
		// object.
	
		String CLIENT_SECRET_FILE = httpRequest.getServletContext().getRealPath("/credentials.json");

		String authCode = httpRequest.getParameter("authCode"); 
		System.out.println("authCode: " + authCode);
		
		if(authCode != null) {
			
			//System.out.println(authCode);
			// Exchange auth code for access token
			GoogleClientSecrets clientSecrets =
			    GoogleClientSecrets.load(
			        JacksonFactory.getDefaultInstance(), new FileReader(CLIENT_SECRET_FILE));
			GoogleTokenResponse tokenResponse =
			          new GoogleAuthorizationCodeTokenRequest(
			              new NetHttpTransport(),
			              JacksonFactory.getDefaultInstance(),
			              "https://www.googleapis.com/oauth2/v4/token",
			              clientSecrets.getDetails().getClientId(),
			              clientSecrets.getDetails().getClientSecret(),
			              authCode,
			              "postmessage")  // Specify the same redirect URI that you use with your web
			                             // app. If you don't have a web version of your app, you can
			                             // specify an empty string.
			              .execute();
			
			//GET ACCESSTOKEN
			String accessToken = tokenResponse.getAccessToken();
			System.out.println("accessToken: " + accessToken);
			response.setContentType("text/html");
		 
		    // Get profile info from ID token
			GoogleIdToken idToken = tokenResponse.parseIdToken();
			GoogleIdToken.Payload payload = idToken.getPayload();
			
			//String userId = payload.getUserId();  // Use this value as a key to identify a user.
			String email = payload.getEmail();
			boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			String name = (String) payload.get("name");
			String pictureUrl = (String) payload.get("picture");
			String locale = (String) payload.get("locale");
			String familyName = (String) payload.get("family_name");
			String givenName = (String) payload.get("given_name");	
			System.out.println("payload: " + payload);
			
			if (tokenResponse.getIdToken() != null) {		
			  System.out.println( "Id Token Provided is Valid -- > "  + validateIdToken(tokenResponse.getIdToken()) );
	        }
			
			if (accessToken != null) {
				System.out.println( "Access Token Provided is Valid -- > "  + validateAccessToken(accessToken) );
		     }
			
			System.out.println("email: " + email);
			System.out.println("emailVerified: " + emailVerified);
			System.out.println("name: " + name);
			System.out.println("pictureUrl: " + pictureUrl);
			System.out.println("locale: " + locale);
			System.out.println("familyName: " + familyName);
			System.out.println("givenName: " + givenName);
		
	      // Pass request back down the filter chain
	      chain.doFilter(request, response);
		} else {
			 System.out.println("path: " + path);
		      if (path.contains("/SignIn.jsp")) {
		          chain.doFilter(request, response); // Just continue chain.
		      } else {
		    	  HttpServletResponse httpResponse = (HttpServletResponse) response;
		  		 httpResponse.sendRedirect("SignIn.jsp");;  
		      }
		}
	
	}
	
	 private static final HttpTransport TRANSPORT = new NetHttpTransport();
     
	  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	  
	  private static final String CLIENT_ID = "554083208552-5jb4dno6hcd4hb5itphg7qai626a5t4u.apps.googleusercontent.com";
	  
	  // Check that the Id Token is valid.
	 private boolean validateIdToken(String idToken){ 
		 if (idToken != null) {		
			 ValidateIdToken checker = new ValidateIdToken(new String[]{CLIENT_ID}, CLIENT_ID);
	          GoogleIdToken.Payload jwt = checker.check(idToken);
	          if (jwt == null) {
	        	  System.out.println("Invalid ID Token.");
	        	  return false;
	          } else {
	            System.out.println("ID Token is valid.");
	            return true;
	          }
	        } else {
	        	 System.out.println("ID Token not provided");
	        	 return false;
	        }		
	 }
	 
	 // Check that the Access Token is valid.
     private boolean validateAccessToken(String accessToken){	 
    	 if (accessToken != null) {
          try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            Oauth2 oauth2 = new Oauth2.Builder(
                TRANSPORT, JSON_FACTORY, credential).build();
            Tokeninfo tokenInfo = oauth2.tokeninfo()
                .setAccessToken(accessToken).execute();
            System.out.println(tokenInfo.getIssuedTo());
            if (!tokenInfo.getIssuedTo().equals(CLIENT_ID)) {
              // This is not meant for this app. It is VERY important to check
              // the client ID in order to prevent man-in-the-middle attacks.
            	System.out.println("Access Token not meant for this app.");
            	 return false;
            } else {
            	System.out.println("Access Token is valid.");
            	 return true;
            }
          } catch (IOException e) {
        	  System.out.println("Invalid Access Token.");
        	  return false;
          }
        } else {
        	System.out.println("Access Token not provided");
        	 return false;
        }
	 }

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub		
	}

}
