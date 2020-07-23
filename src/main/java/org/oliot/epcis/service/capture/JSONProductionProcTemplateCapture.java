package org.oliot.epcis.service.capture;

import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.model.ProductProcessTemplate;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.model.jsonschema.JsonSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import eu.nimble.service.epcis.services.AuthorizationSrv;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
* Created by Quan Deng, 2019
*/

@Api(tags = {"Production Process JSON Template Capture"})
@CrossOrigin()
@RestController
@RequestMapping("/JSONProductionProcTemplateCapture")
public class JSONProductionProcTemplateCapture {
    private static Logger log = LoggerFactory.getLogger(JSONProductionProcTemplateCapture.class);


	@Autowired
	AuthorizationSrv authorizationSrv;


	@ApiOperation(value = "", notes = "Capture an production process template, which consists of a list of production process steps.", response = String.class )
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 400, message = "Json Document is not valid?"),
			@ApiResponse(code = 401, message = "Unauthorized. Are the headers correct?"), })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@ApiParam(value = "ProductProcessTemplate object", required = true)@Valid @RequestBody ProductProcessTemplate productProcessTemplate, 
			@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken) {
		
		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}
		
		//TODO: Permission check. Return error, in case no permission.

		log.info(" Production Process Template Json Document Capture Started.... ");

		Gson gson = new Gson();
	    String productProcessTemplateJson = gson.toJson(productProcessTemplate);
		JSONObject jsonProductionProc = new JSONObject(productProcessTemplateJson);
		
		MongoCaptureUtil m = new MongoCaptureUtil();
		m.captureJSONProductionProcTemplate(jsonProductionProc, userPartyID);
		
		return new ResponseEntity<>("Production Process Template Document : Captured ", HttpStatus.OK);

	}

}
