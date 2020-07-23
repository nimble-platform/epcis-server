package org.oliot.epcis.service.capture;

import java.util.Iterator;
import java.util.List;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.nimble.service.epcis.services.AuthorizationSrv;
import io.swagger.annotations.*;
import springfox.documentation.annotations.ApiIgnore;

import org.oliot.model.epcis.EPCISDocumentType;

/**
 * Copyright (C) 2017 Jaewook Jack Byun, Sungpil Woo
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.2 specification in
 * EPCglobal.
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 * @author Sungpil Woo, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         woosungpil@kaist.ac.kr, woosungpil7@gmail.com
 */

/**
 * Modifications copyright (C) 2019 Quan Deng
 */

@Api(tags = { "EPCIS JSON Event Capture" })
@CrossOrigin()
@RestController
@RequestMapping("/JSONEventCapture")
public class JSONEventCapture {
	private static Logger log = LoggerFactory.getLogger(JSONEventCapture.class);

	@Autowired
	AuthorizationSrv authorizationSrv;

	@Autowired
	JSONEventCaptureService jsonEventCaptureSrv;

	@ApiOperation(value = "", notes = "Capture an EPCIS Event in JSON. An example EPCIS Event is: <br> <textarea disabled style=\"width:98%\" class=\"body-textarea\">" 
			+ " {\r\n" + 
			"  \"epcis\": {\r\n" + 
			"    \"EPCISBody\": {\r\n" + 
			"      \"EventList\": [\r\n" + 
			"        {\r\n" + 
			"          \"ObjectEvent\": {\r\n" + 
			"            \"eventTime\": 1522809211116,\r\n" + 
			"            \"eventTimeZoneOffset\": \"-06:00\",\r\n" + 
			"            \"epcList\": [\r\n" + 
			"              {\r\n" + 
			"                \"epc\": \"TEST848777\"\r\n" + 
			"              }\r\n" + 
			"            ],\r\n" + 
			"            \"action\": \"OBSERVE\",\r\n" + 
			"            \"bizStep\": \"urn:epcglobal:cbv:bizstep:other\",\r\n" + 
			"            \"readPoint\": {\r\n" + 
			"              \"id\": \"urn:epc:id:sgln:readPoint.PodComp.1\"\r\n" + 
			"            },\r\n" + 
			"            \"bizLocation\": {\r\n" + 
			"              \"id\": \"urn:epc:id:sgln:bizLocation.PodComp.2\"\r\n" + 
			"            }\r\n" + 
			"          }\r\n" + 
			"        }\r\n" + 
			"      ]\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}\r\n" + 
			""
			+ "</textarea> ", response = String.class)
	@ApiImplicitParam(name = "inputString", value = "A JSON value representing EPCIS Events.", dataType = "String", paramType = "body", required = true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 400, message = "Json Document is not valid?"),
			@ApiResponse(code = 401, message = "Unauthorized. Are the headers correct?"), })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString,
			@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken) {

		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}

		// TODO: Advanced permission control. Permission check for each event on the
		// list. Return error, in case no permission on some events.

		log.info(" EPCIS Json Document Capture Started.... ");

		List<JSONObject> validJsonEventList = jsonEventCaptureSrv.prepareJSONEvents(inputString);
		if (null == validJsonEventList) {
			log.info("No Events Captured!");
			return new ResponseEntity<>("Error: Json Document is not valid. Details can be found in the log files.",
					HttpStatus.BAD_REQUEST);
		}

		jsonEventCaptureSrv.capturePreparedJSONEvents(validJsonEventList, userPartyID);

		return new ResponseEntity<>("EPCIS Document : Captured ", HttpStatus.OK);
	}

}
