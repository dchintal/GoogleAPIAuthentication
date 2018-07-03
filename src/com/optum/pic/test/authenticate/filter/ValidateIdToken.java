package com.optum.pic.test.authenticate.filter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.jose4j.base64url.Base64Url;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwx.CompactSerializer;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.StringUtil;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;


public class ValidateIdToken {
	private final List mClientIDs;
    private final String mAudience;
    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory mJFactory;
    private String mProblem = "Verification failed. (Time-out?)";

    public ValidateIdToken(String[] clientIDs, String audience) {
        mClientIDs = Arrays.asList(clientIDs);
        mAudience = audience;
        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
    }

    public GoogleIdToken.Payload check(String tokenString) {
        GoogleIdToken.Payload payload = null;
        try {
            GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
            if (mVerifier.verify(token)) {
                GoogleIdToken.Payload tempPayload = token.getPayload();
                if (!tempPayload.getAudience().equals(mAudience))
                    mProblem = "Audience mismatch";
                else if (!mClientIDs.contains(tempPayload.getAuthorizedParty()))
                    mProblem = "Client ID mismatch";
                else
                    payload = tempPayload;
            }
        } catch (GeneralSecurityException e) {
            mProblem = "Security issue: " + e.getLocalizedMessage();
        } catch (IOException e) {
            mProblem = "Network problem: " + e.getLocalizedMessage();
        }
        return payload;
    }
    
    // Introduce id signature verify sample code from OptumID team.
    public void verifyJwtSignatureAndUpdateResult(Map<String, String> result, String id_token, String secret) {
    	String decoded_header = null;
    	String decoded_payload = null;
    	String sign_verification_status = "error";

    	try {    		
    		//RSAPublicKey publicKey = null; --need to add other algorithms to validate google id tokens (different alg)
    		//System.out.println("See ID token and check its alg: "+id_token);
    		String[] parts = CompactSerializer.deserialize(id_token);
    		Validate.isTrue(parts.length == 3);
    		Base64Url base64url = new Base64Url();
    		decoded_header = base64url.base64UrlDecodeToString(parts[0], StringUtil.UTF_8);
    		decoded_payload = base64url.base64UrlDecodeToString(parts[1], StringUtil.UTF_8);
    		Map<String, Object> header = mapJson(decoded_header);
    		String alg = (String) header.get("alg");
    		Validate.isTrue(alg != null && alg.startsWith("HS"), "Unsupported algorithm");
    		JsonWebSignature jws = new JsonWebSignature();
    		jws.setCompactSerialization(id_token);
    		byte[] bytes = secret.getBytes("UTF-8");
    		HmacKey publicKey = new HmacKey(bytes);
    		jws.setKey(publicKey);
    		sign_verification_status = jws.verifySignature() ? "success": "error";
    		} catch (Exception ex) {
			//logger.error("Error in validating id_token signature", ex);
			System.out.println("Error in validating id_token signature: "+ex);
			sign_verification_status = "error";
		}
    	result.put("decoded_header", decoded_header);
    	result.put("decoded_payload", decoded_payload);
    	result.put("sign_verification_status", StringUtils.capitalize(sign_verification_status));
       }
    
	private Map<String, Object> mapJson(String json) {
	    try {
	    	TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
	    	return mapper.readValue(json, typeRef);
	    	} catch (Exception ex) {
	    		throw new RuntimeException(ex);    
	    	}
	}
	private ObjectMapper mapper = new ObjectMapper();
    public String problem() {
        return mProblem;
    }
}
