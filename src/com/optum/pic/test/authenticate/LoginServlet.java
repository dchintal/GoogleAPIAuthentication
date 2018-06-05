package com.optum.pic.test.authenticate;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getHeader("X-Requested-With") == null) {
			  // Without the `X-Requested-With` header, this request could be forged. Aborts.
			}

			// Set path to the Web application client_secret_*.json file you downloaded from the
			// Google API Console: https://console.developers.google.com/apis/credentials
			// You can also find your Web application client ID and client secret from the
			// console and specify them directly when you create the GoogleAuthorizationCodeTokenRequest
			// object.
		
		    String modeofAction = request.getParameter("modeofAction");
		   
		    System.out.println("modeofAction: " + modeofAction);
			String CLIENT_SECRET_FILE = this.getServletContext().getRealPath("/credentials.json");
	
			String authCode = request.getParameter("authCode"); 
			System.out.println("authCode: " + authCode);

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
		    PrintWriter out = response.getWriter();
		    
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
			
			
			System.out.println("email: " + email);
			System.out.println("emailVerified: " + emailVerified);
			System.out.println("name: " + name);
			System.out.println("pictureUrl: " + pictureUrl);
			System.out.println("locale: " + locale);
			System.out.println("familyName: " + familyName);
			System.out.println("givenName: " + givenName);
			if(modeofAction.equals("checkPrice")) {
				 out.println("http://localhost:8080/GoogleAPIAuthentication/Welcome2.jsp");
			} else {
				 out.println("http://localhost:8080/GoogleAPIAuthentication/Welcome.jsp");
			}
		   
		    out.close();


	}

}
