package eu.nimble.service.epcis.services;

import java.util.List;

import org.bson.BsonDocument;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import eu.nimble.service.epcis.controller.NIMBLETokenController;

/**
* Created by Quan Deng, 2019
*/

@Service
public class AuthorizationSrv {
    private static Logger log = LoggerFactory.getLogger(NIMBLETokenController.class);

	@Value("${spring.identity-service.url}")
	private String identityServiceURL;
	
    @Autowired
    private RestTemplate restTemplate;
	
	/**
	 * Extracts the identity from an OpenID Connect token and fetches the associated company from the Identity Service.
	 * 
	 * @param accessToken  OpenID Connect token storing identity.
	 * @return  Identifier of associated company; null, in case of exception or no valid user.
	 */
     @Cacheable(value = "token",sync = true)
	 public String checkToken(String accessToken)
	 {
		 if (accessToken == null) {
			 log.error("No token is given");
			 return null;
			}
			 
	        String url = identityServiceURL + "/user-info";
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
	        headers.set("Authorization", accessToken);

	        String userPartyID = "";
	        try {
	        	HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<Object>(headers) , String.class);
	        	log.info("re-fetching ublPartyID from NIMBLE identity service!");
		        JSONObject jsonUser = new JSONObject(response.getBody());
		        userPartyID = jsonUser.getString("ublPartyID");
		        
		        return userPartyID;
	        } catch (HttpStatusCodeException e) {
	        	log.error("Error or the token is invalid in NIMBLE platform: " + e.getResponseBodyAsString());
	        }
	       
	        return null;
	 }
	 
	 //TODO: need to integrate with NIMBLE authorization service
	 /**
	  * Check if the user has permission to capture an EPCIS event
	  * @param userPartyID  user party ID
	  * @param bsonDocumentList a list of MongoDB document
	  * @return true, with permission for all MongoDB document in the list; false, otherwise
	  */
	 public boolean hasCapturePermission(String userPartyID, List<BsonDocument> bsonDocumentList)
	 {
		 return true;
	 }
	 
	 //TODO: need to integrate with NIMBLE authorization service
	 /**
	  * Check if the user has permission to capture an EPCIS event
	  * @param userPartyID  user party ID
	  * @param doc  MongoDB document of an EPCIS event
	  * @return true, with permission; false, otherwise because for example EPC code belongs not to the user party
	  */
	 public boolean hasCapturePermission(String userPartyID, BsonDocument doc)
	 {
		 return true;
	 }
	 

	 //TODO: need to integrate with NIMBLE authorization service
	 /**
	  * Check if the user has permission to query an EPCIS event
	  * @param userPartyID  user party ID
	  * @param doc	MongoDB document for a single EPCIS event
	  * @return true, with permission; false, without permission because for example T&T for the given EPC is only allowed for partners
	  */
	 public boolean hasQueryPermission(String userPartyID, BsonDocument doc)
	 {
		 return true;
	 }
}
