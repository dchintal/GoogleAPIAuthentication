package com.optum.pic.test.authenticate.filter;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
			System.out.println("authCode: " + authCode);
			
			System.out.println("Using AuthCode Fetching Access Token and User Info");
			
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
			
			System.out.println("tokenResponse: " + tokenResponse.getExpiresInSeconds());
		
			System.out.println("");
			
			if (tokenResponse.getIdToken() != null) {		
			  System.out.println( "Id Token Provided is Valid -- > "  + validateIdToken(tokenResponse.getIdToken()) );
	        }
			
			System.out.println("");
			if (accessToken != null) {
				try {
					System.out.println( "Access Token Provided is Valid -- > "  + validateAccessToken(accessToken) );
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }
			
			System.out.println("");
			System.out.println("+++++++++++++++++++++++ Profile Info +++++++++++++++++++++++");
			System.out.println("emailVerified: " + emailVerified);
			System.out.println("name: " + name);
			System.out.println("locale: " + locale);
			System.out.println("familyName: " + familyName);
			System.out.println("givenName: " + givenName);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
			}
	      // Pass request back down the filter chain
	      chain.doFilter(request, response);
	}
	
	 private static final HttpTransport TRANSPORT = new NetHttpTransport();
     
	  private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	  
	  private static final String CLIENT_ID = "554083208552-5jb4dno6hcd4hb5itphg7qai626a5t4u.apps.googleusercontent.com";
	  
	  // Check that the Id Token is valid.
	 private boolean validateIdToken(String idToken){ 
		 if (idToken != null) {		
			 ValidateIdToken checker = new ValidateIdToken(new String[]{CLIENT_ID}, CLIENT_ID);
	          GoogleIdToken.Payload jwt = checker.check(idToken); 
 			 //use Optum team provided verifyJwtSignatureAndUpdateResult sample code
 			 HashMap<String, String> result = new HashMap<String, String>();			 
 			 checker.verifyJwtSignatureAndUpdateResult(result, idToken, "Cybx4hJYTx_V_kpNK1AHkAdD");
 			 //System.out.println("signature verify " + result.get("sign_verification_status"));
 			 if( !result.get("sign_verification_status").equals("Error")) {
 				if (jwt != null) {	 
 		        	  return true;
 		          } 
 			}  	 
	     }
	   return false;	 
	 }	 
	 
	 // Check that the Access Token is valid.
     private boolean validateAccessToken(String accessToken) throws InterruptedException{	 
    	 if (accessToken != null) {
          try {
            GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
            Oauth2 oauth2 = new Oauth2.Builder(
                TRANSPORT, JSON_FACTORY, credential).build();
            Tokeninfo tokenInfo = oauth2.tokeninfo()
                .setAccessToken(accessToken).execute();
            Thread.sleep(4000);
            oauth2 = new Oauth2.Builder(
                TRANSPORT, JSON_FACTORY, credential).build();
            tokenInfo = oauth2.tokeninfo()
                .setAccessToken(accessToken).execute();
            System.out.println("Expires in ---> "+ tokenInfo.getExpiresIn() + " Access Token --> " + credential.getAccessToken());
            
           /* 
            Thread.sleep(100000);
            oauth2 = new Oauth2.Builder(
                    TRANSPORT, JSON_FACTORY, credential).build();
                tokenInfo = oauth2.tokeninfo()
                    .setAccessToken(accessToken).execute();
         
            System.out.println("Expires in ---> "+  tokenInfo.getExpiresIn() + " Access Token --> " + credential.getAccessToken());
            
            Thread.sleep(100000);
            oauth2 = new Oauth2.Builder(
                    TRANSPORT, JSON_FACTORY, credential).build();
                tokenInfo = oauth2.tokeninfo()
                    .setAccessToken(accessToken).execute();
  
            System.out.println("Expires in ---> "+ tokenInfo.getExpiresIn()+ " Access Token --> " + credential.getAccessToken());
            
            Thread.sleep(100000);
            oauth2 = new Oauth2.Builder(
                    TRANSPORT, JSON_FACTORY, credential).build();
                tokenInfo = oauth2.tokeninfo()
                    .setAccessToken(accessToken).execute();
           System.out.println("Expires in ---> "+ tokenInfo.getExpiresIn() + " Access Token --> " + credential.getAccessToken());
            
            Thread.sleep(100000);
            oauth2 = new Oauth2.Builder(
                    TRANSPORT, JSON_FACTORY, credential).build();
                tokenInfo = oauth2.tokeninfo()
                    .setAccessToken(accessToken).execute();
          
            System.out.println("Expires in ---> "+ tokenInfo.getExpiresIn() + " Access Token --> " + credential.getAccessToken());
            */
            if (!tokenInfo.getIssuedTo().equals(CLIENT_ID)) {
              // This is not meant for this app. It is VERY important to check
              // the client ID in order to prevent man-in-the-middle attacks.
            	 return false;
            } else {
            	 return true;
            }
          } catch (IOException e) {
        	  return false;
          }
        } else {
        	 return false;
        }
	 }

	@Override
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub		
	}

}
