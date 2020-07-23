package org.oliot.epcis.service.capture;

import java.io.InputStream;

import javax.xml.bind.JAXB;

import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
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

import eu.nimble.service.epcis.services.AuthorizationSrv;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Copyright (C) 2014-2017 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

/**
* Modifications copyright (C) 2019 Quan Deng
*/


@Api(tags = {"EPCIS XML Master Data Capture"})
@CrossOrigin()
@RestController
@RequestMapping("/VocabularyCapture")
public class VocabularyCapture {
    private static Logger log = LoggerFactory.getLogger(VocabularyCapture.class);


	@Autowired
	AuthorizationSrv authorizationSrv;
	@Autowired
	CaptureService captureService;
	

	@ApiOperation(value = "", notes = "Capture an EPCIS Master Data in XML. An example EPCIS Master Data is: <br><textarea disabled style=\"width:98%\" class=\"body-textarea\">" + 
			" <!DOCTYPE EPCISDocument>\r\n" + 
			"<epcismd:EPCISMasterDataDocument\r\n" + 
			"	creationDate=\"2015-04-09T12:00:00\" schemaVersion=\"0.0\" xmlns:epcglobal=\"epcglobal.oliot.org\"\r\n" + 
			"	xmlns:epcis=\"epcis.oliot.org\" xmlns:epcismd=\"masterdata.epcis.oliot.org\">\r\n" + 
			"	<EPCISBody>\r\n" + 
			"		<VocabularyList>\r\n" + 
			"			<Vocabulary type=\"urn:epcglobal:epcis:vtype:ReadPoint\">\r\n" + 
			"				<VocabularyElementList>\r\n" + 
			"					<!-- Section 10.3 - Location Master Data Names -->\r\n" + 
			"					<VocabularyElement id=\"urn:epc:id:sgln:readPoint.PodComp.0\">\r\n" + 
			"						<attribute id=\"urn:epcglobal:cbv:mda#name\">PodComp factory</attribute>\r\n" + 
			"						<attribute id=\"urn:epcglobal:cbv:mda:site\">PodComp factory</attribute>\r\n" + 
			"					</VocabularyElement>\r\n" + 
			"				</VocabularyElementList>\r\n" + 
			"			</Vocabulary>\r\n" + 
			"		</VocabularyList>\r\n" + 
			"	</EPCISBody>\r\n" + 
			"</epcismd:EPCISMasterDataDocument>"
			+ " </textarea>", response = String.class)
	@ApiImplicitParam(name = "inputString", value = "A XML value representing EPCIS Master Data.", dataType = "String", paramType = "body", required = true)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 400, message = "Failed. EPCIS Masterdata Document is not valid?"),
			@ApiResponse(code = 401, message = "Unauthorized. Are the headers correct?"), })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString, 
			@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken,
			@ApiParam(value = "Global Company Prefix(GCP) length") @RequestParam(required = false) Integer gcpLength) {

		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}
		
		//TODO: Permission check for each event on the list. Return error, in case no permission on some events.
		//TODO: Save userPartyID into Event i.e. MongoDB. So that it is possible to know from whom the Vocabulary is added, for the purpose of audit etc. 
		
		log.info(" EPCIS Masterdata Document Capture Started.... ");

		JSONObject retMsg = new JSONObject();
		if (Configuration.isCaptureVerfificationOn == true) {
			InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			// Parsing and Validating data
			boolean isValidated = CaptureUtil.validate(validateStream,
					 "EPCglobal-epcis-masterdata-1_2.xsd");
			if (isValidated == false) {
				return new ResponseEntity<>(new String("Error: EPCIS Masterdata Document is not validated"),
						HttpStatus.BAD_REQUEST);
			}
		}

		InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		EPCISMasterDataDocumentType epcisMasterDataDocument = JAXB.unmarshal(epcisStream,
				EPCISMasterDataDocumentType.class);
		retMsg = captureService.capture(epcisMasterDataDocument, userPartyID,  gcpLength);
		log.info(" EPCIS Masterdata Document : Captured ");

		if (retMsg.isNull("error") == true)
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
		else
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.BAD_REQUEST);
	}
}
