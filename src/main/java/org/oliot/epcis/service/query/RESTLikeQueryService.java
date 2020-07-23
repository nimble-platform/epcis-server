package org.oliot.epcis.service.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.json.JSONArray;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.ImplementationException;
import org.oliot.model.epcis.NoSuchNameException;
import org.oliot.model.epcis.PollParameters;
import org.oliot.model.epcis.QueryParameterException;
import org.oliot.model.epcis.QueryTooComplexException;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SecurityException;
import org.oliot.model.epcis.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
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


@Api(tags = {"EPCIS Query Service"})
@RestController
@CrossOrigin()
public class RESTLikeQueryService {

	@Autowired
	AuthorizationSrv authorizationSrv;
	
	@Autowired
	MongoQueryService mongoQueryService;
	
	/**
	 * Returns a list of all query names available for use with the subscribe
	 * and poll methods. This includes all pre-defined queries provided by the
	 * implementation, including those specified in Section 8.2.7.
	 * 
	 * No Dependency with Backend
	 * 
	 * @return JSONArray of query names ( String )
	 */
	@ApiOperation(value = "", notes = "Returns a list of all query names available for use with the subscribe and poll methods. "
			+ "This includes all pre-defined queries provided by the implementation, including those specified in Section 8.2.7. ", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success")})
	@RequestMapping(value = "/GetQueryNames", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getQueryNamesREST()
			throws SecurityException, ValidationException, ImplementationException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		JSONArray jsonArray = new JSONArray();
		List<String> queryNames = getQueryNames();
		for (int i = 0; i < queryNames.size(); i++) {
			jsonArray.put(queryNames.get(i));
		}

		return new ResponseEntity<>(jsonArray.toString(1), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Returns a list of all query names available for use with the subscribe
	 * and poll methods. This includes all pre-defined queries provided by the
	 * implementation, including those specified in Section 8.2.7.
	 * 
	 * @return a list of all query names
	 */
	public List<String> getQueryNames() throws SecurityException, ValidationException, ImplementationException {
		List<String> queryNames = new ArrayList<String>();
		queryNames.add("SimpleEventQuery");
		queryNames.add("SimpleMasterDataQuery");
		return queryNames;
	}

	/**
	 * Returns a string that identifies what version of the specification this
	 * implementation complies with. The possible values for this string are
	 * defined by GS1. An implementation SHALL return a string corresponding to
	 * a version of this specification to which the implementation fully
	 * complies, and SHOULD return the string corresponding to the latest
	 * version to which it complies. To indicate compliance with this Version
	 * 1.2 of the EPCIS specification, the implementation SHALL return the
	 * string 1.2 .
	 * 
	 * @return 1.2
	 */
	@ApiOperation(value = "", notes = "Returns a string that identifies what version of the "
			+ "specification this implementation complies with. The possible values for this string are defined by GS1. "
			+ "An implementation SHALL return a string corresponding to a version of this specification to which the implementation fully "
			+ "complies, and SHOULD return the string corresponding to the latest version to which it complies. "
			+ "To indicate compliance with this Version 1.2 of the EPCIS specification, the implementation SHALL return the string 1.2.", response = String.class)
	@RequestMapping(value = "/GetStandardVersion", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getStandardVersion()
			throws SecurityException, ValidationException, ImplementationException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		return new ResponseEntity<>(new String("1.2"), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Returns a string that identifies what vendor extensions this
	 * implementation provides. The possible values of this string and their
	 * meanings are vendor-defined, except that the empty string SHALL indicate
	 * that the implementation implements only standard functionality with no
	 * vendor extensions. When an implementation chooses to return a non-empty
	 * string, the value returned SHALL be a URI where the vendor is the owning
	 * authority. For example, this may be an HTTP URL whose authority portion
	 * is a domain name owned by the vendor, a URN having a URN namespace
	 * identifier issued to the vendor by IANA, an OID URN whose initial path is
	 * a Private Enterprise Number assigned to the vendor, etc.
	 * 
	 * @return a string of vendor version
	 */
	@ApiOperation(value = "", notes = "Returns a string that identifies what vendor extensions this implementation provides. "
			+ "The possible values of this string and their meanings are vendor-defined, except that the empty string SHALL "
			+ "indicate that the implementation implements only standard functionality with no vendor extensions. "
			+ "When an implementation chooses to return a non-empty string, the value returned SHALL be a URI where"
			+ " the vendor is the owning authority. For example, this may be an HTTP URL whose authority portion is a domain name owned by the vendor, "
			+ "a URN having a URN namespace identifier issued to the vendor by IANA, an OID URN whose initial path is a Private Enterprise Number assigned to the vendor, etc. ",
			response = String.class)
	@RequestMapping(value = "/GetVendorVersion", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getVendorVersion() throws SecurityException, ValidationException, ImplementationException {
		// It is not a version of Vendor
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		return new ResponseEntity<>(new String("org.oliot.epcis-1.2.10"), responseHeaders, HttpStatus.OK);
	}	
	
	@ApiOperation(value = "Get production process template for the given product class.", notes = "Return production process template, which consists of a list of production process steps", 
			response = org.oliot.epcis.converter.mongodb.model.ProductionProcessStep.class, responseContainer="List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 401, message = "Unauthorized. Are the headers correct?"), })
	@RequestMapping(value = "/GetProductionProcessTemplate/{productClass}", method = RequestMethod.GET)
	@ResponseBody
    public ResponseEntity<?> getProductionProcessTemplate(@ApiParam(value = "Product Categoy ID in NIMBLE Platform", required = true)  @PathVariable String productClass, 
    		@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true)  String bearerToken) 
    		throws IOException, QueryTooLargeException, QueryParameterException {
		
		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}
		
		
        System.out.println("productClass:" + productClass);
        
		String result = mongoQueryService.pollProductionProcTemplateQuery(productClass);
                
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
    }   
	
	/**
	 * Invokes a previously defined query having the specified name, returning the results. The params argument provides the values to be used for any named parameters defined by the query
	 * @param queryName
	 * @param bearerToken
	 * @param eventType
	 * @param GE_eventTime
	 * @param LT_eventTime
	 * @param GE_masterTime
	 * @param LE_masterTime
	 * @param GE_recordTime
	 * @param LT_recordTime
	 * @param EQ_action
	 * @param EQ_bizStep
	 * @param EQ_disposition
	 * @param EQ_readPoint
	 * @param WD_readPoint
	 * @param EQ_bizLocation
	 * @param WD_bizLocation
	 * @param EQ_transformationID
	 * @param MATCH_epc
	 * @param MATCH_parentID
	 * @param MATCH_inputEPC
	 * @param MATCH_outputEPC
	 * @param MATCH_anyEPC
	 * @param MATCH_epcClass
	 * @param MATCH_inputEPCClass
	 * @param MATCH_outputEPCClass
	 * @param MATCH_anyEPCClass
	 * @param EQ_quantity
	 * @param GT_quantity
	 * @param GE_quantity
	 * @param LT_quantity
	 * @param LE_quantity
	 * @param EQ_eventID
	 * @param EXISTS_errorDeclaration
	 * @param GE_errorDeclarationTime
	 * @param LT_errorDeclarationTime
	 * @param EQ_errorReason
	 * @param EQ_correctiveEventID
	 * @param orderBy
	 * @param orderDirection
	 * @param eventCountLimit
	 * @param maxEventCount
	 * @param vocabularyName
	 * @param includeAttributes
	 * @param includeChildren
	 * @param attributeNames
	 * @param EQ_name
	 * @param WD_name
	 * @param HASATTR
	 * @param maxElementCount
	 * @param format Format of experted result. JSON or XML.
	 * @param params
	 * @return
	 * @throws QueryParameterException
	 * @throws QueryTooLargeException
	 * @throws QueryTooComplexException
	 * @throws NoSuchNameException
	 * @throws SecurityException
	 * @throws ValidationException
	 * @throws ImplementationException
	 */
	//TODO: Swagger has problem to document the API with too much params
	@ApiIgnore
	@RequestMapping(value = "/Poll/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> poll(@PathVariable String queryName, 
			@RequestHeader(value="Authorization", required=true) String bearerToken, 
			@RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime, @RequestParam(required = false) String LT_eventTime,
            @RequestParam(required = false) String GE_masterTime, @RequestParam(required = false) String LE_masterTime,
            @RequestParam(required = false) String GE_recordTime, @RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action, @RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition, @RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint, @RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc, @RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC, @RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) Integer EQ_quantity, @RequestParam(required = false) Integer GT_quantity,
			@RequestParam(required = false) Integer GE_quantity, @RequestParam(required = false) Integer LT_quantity,
			@RequestParam(required = false) Integer LE_quantity,

			@RequestParam(required = false) String EQ_eventID,
			@RequestParam(required = false) Boolean EXISTS_errorDeclaration,
			@RequestParam(required = false) String GE_errorDeclarationTime,
			@RequestParam(required = false) String LT_errorDeclarationTime,
			@RequestParam(required = false) String EQ_errorReason,
			@RequestParam(required = false) String EQ_correctiveEventID,

			@RequestParam(required = false) String orderBy, @RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) Integer eventCountLimit,
			@RequestParam(required = false) Integer maxEventCount,

			@RequestParam(required = false) String vocabularyName,
			@RequestParam(required = false) Boolean includeAttributes,
			@RequestParam(required = false) Boolean includeChildren,
			@RequestParam(required = false) String attributeNames, @RequestParam(required = false) String EQ_name,
			@RequestParam(required = false) String WD_name, @RequestParam(required = false) String HASATTR,
			@RequestParam(required = false) Integer maxElementCount,

			@RequestParam(required = false) String format,
			@RequestParam Map<String, String> params)
			throws QueryParameterException, QueryTooLargeException, QueryTooComplexException, NoSuchNameException,
			SecurityException, ValidationException, ImplementationException {

		HttpHeaders responseHeaders = new HttpHeaders();
		if (format != null && format.equals("JSON")) {
			responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		} else {
			responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
		}
		
		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}
		
		
		PollParameters pollParams = new PollParameters(queryName, eventType, GE_eventTime, LT_eventTime, GE_masterTime,
                LE_masterTime, GE_recordTime, LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
                WD_readPoint, EQ_bizLocation, WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID,
                MATCH_inputEPC, MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass,
                MATCH_anyEPCClass, EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, EQ_eventID,
                EXISTS_errorDeclaration, GE_errorDeclarationTime, LT_errorDeclarationTime, EQ_errorReason,
                EQ_correctiveEventID, orderBy, orderDirection, eventCountLimit, maxEventCount, vocabularyName,
                includeAttributes, includeChildren, attributeNames, EQ_name, WD_name, HASATTR, maxElementCount, format,
                 params);

		String result = mongoQueryService.poll(pollParams, userPartyID);
		return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "", notes = "Invokes a previously defined query having the specified name, returning the results. The\r\n" + 
			"params argument provides the values to be used for any named parameters defined by the\r\n" + 
			"query.", response = String.class)
	@RequestMapping(value = "/Poll/{queryName}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> poll(@ApiParam(value = "Available query names. i.e. SimpleEventQuery, SimpleMasterDataQuery.", allowableValues = "SimpleEventQuery,SimpleMasterDataQuery", 
	example = "SimpleEventQuery", required = true) @PathVariable String queryName, 
			@ApiParam(value = "The Bearer token provided by the identity service", required = true) @RequestHeader(value = "Authorization", required = true) String bearerToken, 
			@ApiParam(value = "Query Params according to the EPCIS Standard 1.2. "
					+ "A QueryParams instance is simply a set of name/value pairs, where the names correspond to parameter names defined by the query, "
					+ "and the values are the specific values to be used for that invocation of (poll) or subscription to (subscribe) the query. "
					+ "Example value for event query: {\"format\": \"JSON\", \"match_epc\":\"urn:epc:id:sgtin:0614141.107346.2017\"} ; Example value for master data query: {\"format\": \"JSON\", \"eq_name\":\"202\"}", required = true) 
	@Valid @RequestBody PollParameters parmas) 
					throws QueryParameterException, QueryTooLargeException, QueryTooComplexException, NoSuchNameException,
			SecurityException, ValidationException, ImplementationException {
		HttpHeaders responseHeaders = new HttpHeaders();
		if (parmas.getFormat() != null && parmas.getFormat().equals("JSON")) {
			responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		} else {
			responseHeaders.add("Content-Type", "application/xml; charset=utf-8");
		}
		
		parmas.setQueryName(queryName);
		
		// Check NIMBLE authorization
		String userPartyID = authorizationSrv.checkToken(bearerToken);
		if (userPartyID == null) {
			return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.UNAUTHORIZED);
		}
		
		String result = mongoQueryService.poll(parmas, userPartyID);
		return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
	}
	
	

}
