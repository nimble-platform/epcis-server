package org.oliot.epcis.service.capture;

import eu.nimble.service.epcis.services.AuthorizationSrv;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.model.jsonschema.JsonSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
* Created by hos, BIBA, 2019
*/

@Api(tags = {"EPCIS JSON Master Data Capture"})
@CrossOrigin()
@RestController
@RequestMapping("/JSONMasterCapture")
public class JSONMasterCapture {
    private static Logger log = LoggerFactory.getLogger(JSONMasterCapture.class);

    @Autowired
    AuthorizationSrv authorizationSrv;

	@ApiOperation(value = "", notes = "Capture an EPCIS Master Data in JSON. An example EPCIS Master Data is: <br><textarea disabled style=\"width:98%\" class=\"body-textarea\">" + 
			" {\r\n" + 
			"    \"epcismd\": {\r\n" + 
			"        \"EPCISBody\": {\r\n" + 
			"            \"VocabularyList\": [\r\n" + 
			"                {\r\n" + 
			"                    \"Vocabulary\": {\r\n" + 
			"                        \"id\": \"202\",\r\n" + 
			"                        \"type\": \"urn:epcglobal:epcis:vtype:SubSiteAttribute\",\r\n" + 
			"                        \"attributes\": {\r\n" + 
			"                            \"urn:epcglobal:cbv:mda#description\": \"Storage Area\"\r\n" + 
			"                        }\r\n" + 
			"                    }\r\n" + 
			"                }\r\n" + 
			"            ]\r\n" + 
			"        }\r\n" + 
			"    }\r\n" + 
			"}"
			+ " </textarea>", response = String.class)
	@ApiImplicitParam(name = "inputString", value = "A JSON value representing EPCIS Master Data.", dataType = "String", paramType = "body", required = true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 400, message = "Json Document is not valid?"),
			@ApiResponse(code = 401, message = "Unauthorized. Are the headers correct?"), })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> post(@RequestBody String inputString,
    		@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken
                                ) {

        // Check NIMBLE authorization
        String userPartyID = authorizationSrv.checkToken(bearerToken);
        if (userPartyID == null) {
            return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
        }

        // JSONObject retMsg = new JSONObject();
        log.info("EPCIS Master Json Document Capture Started.... ");
        if (Configuration.isCaptureVerfificationOn == true) {

            // JSONParser parser = new JSONParser();
            JsonSchemaLoader schemaLoader = new JsonSchemaLoader();

            try {

                JSONObject jsonMaster = new JSONObject(inputString);
                JSONObject jsonMasterSchema = schemaLoader.getMasterDataSchema();

                if (!CaptureUtil.validate(jsonMaster, jsonMasterSchema)) {
                    log.info("Json Document is invalid" + " about general_validcheck");

                    return new ResponseEntity<>("Error: Json Document is not valid" + "general_validcheck",
                            HttpStatus.BAD_REQUEST);

                }

                /* Schema check for Capture */

                JSONArray jsonEventList = jsonMaster.getJSONObject("epcismd").getJSONObject("EPCISBody")
                        .getJSONArray("VocabularyList");

                for (int i = 0; i < jsonEventList.length(); i++) {
                    JSONObject jsonEventElement = jsonEventList.getJSONObject(i);

                    if (jsonEventElement.has("Vocabulary") == true) {

                        /* startpoint of validation logic for ObjectMaster */
                        JSONObject objectMasterSchema = schemaLoader.getObjectMasterSchema();
                        JSONObject jsonObjectMaster = jsonEventElement.getJSONObject("Vocabulary");

                        if (!CaptureUtil.validate(jsonObjectMaster, objectMasterSchema)) {
                            log.info("Json Master Document is not valid" + " detail validation check for object Master");
                            return new ResponseEntity<>("Error: Json Master Document is not valid"
                                    + " for detail validation check for object Master", HttpStatus.BAD_REQUEST);
                        }

                        MongoCaptureUtil m = new MongoCaptureUtil();
                        m.captureJSONMaster(jsonObjectMaster, userPartyID);

                    }   else {
                        log.info("Json Master Document is not valid. " + " It doesn't have standard event_type");
                        return new ResponseEntity<>(
                                "Error: Json Master Document is not valid" + " It doesn't have standard event_type",
                                HttpStatus.BAD_REQUEST);
                    }

                }
                if (jsonEventList.length() != 0)
                    log.info(" EPCIS Master Document : Captured ");

            } catch (JSONException e) {
                log.info(" Json Document is not valid " + "second_validcheck");
            } catch (Exception e) {
                log.error(e.toString());
            }

            return new ResponseEntity<>("EPCIS Master Document : Captured ", HttpStatus.OK);

        } else {
            JSONObject jsonEvent = new JSONObject(inputString);
            JSONArray jsonEventList = jsonEvent.getJSONObject("epcismd").getJSONObject("EPCISBody")
                    .getJSONArray("VocabularyList");

            for (int i = 0; i < jsonEventList.length(); i++) {

                JSONObject jsonEventElement = jsonEventList.getJSONObject(i);

                if (jsonEventElement.has("Vocabulary") == true) {
                    MongoCaptureUtil m = new MongoCaptureUtil();
                    m.captureJSONMaster(jsonEventElement.getJSONObject("Vocabulary"), userPartyID);
                }
            }
        }
        return new ResponseEntity<>("EPCIS Master Document : Captured ", HttpStatus.OK);

    }

}
