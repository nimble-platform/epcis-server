package org.oliot.epcis.service.capture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.model.jsonschema.JsonSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.bson.BsonDocument;

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

@Component 
public class JSONEventCaptureService {
    private static Logger log = LoggerFactory.getLogger(JSONEventCaptureService.class);

	/**
	 * Verify JSON events against JSON event Schema, in case of required.
	 * @param inputString JSON events
	 * @return Valid JSON event list; null, in case of existing invalid JSON event
	 */
	public List<JSONObject> prepareJSONEvents(String inputString) {
		
		List<JSONObject> INVALID_JSON_EVENTS = null;
		List<JSONObject> validJsonEventList =  new ArrayList<>();
		
		if (Configuration.isCaptureVerfificationOn == true) {

			// JSONParser parser = new JSONParser();
			JsonSchemaLoader schemaLoader = new JsonSchemaLoader();

			try {

				JSONObject jsonEvent = new JSONObject(inputString);
				JSONObject jsonEventSchema = schemaLoader.getEventSchema();

				if (!CaptureUtil.validate(jsonEvent, jsonEventSchema)) {
					log.info("Json Document is invalid" + " about general_validcheck");

					return INVALID_JSON_EVENTS;
				}

				/* Schema check for Capture */

				JSONArray jsonEventList = jsonEvent.getJSONObject("epcis").getJSONObject("EPCISBody")
						.getJSONArray("EventList");

				for (int i = 0; i < jsonEventList.length(); i++) {
					JSONObject jsonEventElement = jsonEventList.getJSONObject(i);

					if (jsonEventElement.has("ObjectEvent") == true) {

						/* startpoint of validation logic for ObjectEvent */
						JSONObject objectEventSchema = schemaLoader.getObjectEventSchema();
						JSONObject jsonObjectEvent = jsonEventElement.getJSONObject("ObjectEvent");

						if (!CaptureUtil.validate(jsonObjectEvent, objectEventSchema)) {
							log.info("Json Document is not valid" + " detail validation check for objectevent");

							return INVALID_JSON_EVENTS;
						}

						/* finish validation logic for ObjectEvent */
						if (!jsonObjectEvent.has("recordTime")) {
							jsonObjectEvent.put("recordTime", System.currentTimeMillis());
						}
						
						if (!jsonObjectEvent.has("eventType")) {
							jsonObjectEvent.put("eventType", "ObjectEvent");
						}

						if (jsonObjectEvent.has("any")) {
							/* start finding namespace in the any field. */
							JSONObject anyobject = jsonObjectEvent.getJSONObject("any");
							String namespace = "";
							boolean namespace_flag = false;

							Iterator<String> keyIter_ns = anyobject.keys();
							while (keyIter_ns.hasNext()) {
								String temp = keyIter_ns.next();
								if (temp.substring(0, 1).equals("@")) {
									namespace_flag = true;
									namespace = temp.substring(1, temp.length());
								}
							}

							if (!namespace_flag) {
								log.info("Json Document doesn't have namespace in any field");
								
								return INVALID_JSON_EVENTS;
							}
							/* finish finding namespace in the any field. */

							/*
							 * Start Validation whether each component use correct name space
							 */;

							Iterator<String> keyIter = anyobject.keys();
							while (keyIter.hasNext()) {
								String temp = keyIter.next();

								if (!temp.contains(namespace)) {
									log.info("Json Document use invalid namespace in anyfield");

									return INVALID_JSON_EVENTS;
								}
							}
							/*
							 * Finish validation whether each component use correct name space
							 */

						}

						validJsonEventList.add(jsonObjectEvent);	

					} else if (jsonEventElement.has("AggregationEvent") == true) {

						/*
						 * startpoint of validation logic for AggregationEvent
						 */
						JSONObject aggregationEventSchema = schemaLoader.getAggregationEventSchema();
						JSONObject jsonAggregationEvent = jsonEventElement.getJSONObject("AggregationEvent");

						if (!CaptureUtil.validate(jsonAggregationEvent, aggregationEventSchema)) {

							log.info(
									"Json Document is not valid" + " detail validation check for aggregationevent");

							return INVALID_JSON_EVENTS;

						}
						/* finish validation logic for AggregationEvent */

						if (!jsonAggregationEvent.has("recordTime")) {
							jsonAggregationEvent.put("recordTime", System.currentTimeMillis());
						}
						
						if (!jsonAggregationEvent.has("eventType")) {
							jsonAggregationEvent.put("eventType", "AggregationEvent");
						}

						if (jsonAggregationEvent.has("any")) {
							/* start finding namespace in the any field. */
							JSONObject anyobject = jsonAggregationEvent.getJSONObject("any");
							String namespace = "";
							boolean namespace_flag = false;

							Iterator<String> keyIter_ns = anyobject.keys();
							while (keyIter_ns.hasNext()) {
								String temp = keyIter_ns.next();
								if (temp.substring(0, 1).equals("@")) {
									namespace_flag = true;
									namespace = temp.substring(1, temp.length());
								}
							}

							if (!namespace_flag) {
								log.info("Json Document doesn't have namespace in any field");

								return INVALID_JSON_EVENTS;

							}
							/* finish finding namespace in the any field. */

							/*
							 * Start Validation whether each component use correct name space
							 */;

							Iterator<String> keyIter = anyobject.keys();
							while (keyIter.hasNext()) {
								String temp = keyIter.next();

								if (!temp.contains(namespace)) {
									log.info("Json Document use invalid namespace in anyfield");

									return INVALID_JSON_EVENTS;
								}
							}

						}
						
						validJsonEventList.add(jsonEventList.getJSONObject(i).getJSONObject("AggregationEvent"));

					} else if (jsonEventElement.has("TransformationEvent") == true) {

						/*
						 * startpoint of validation logic for TransFormationEvent
						 */
						JSONObject transformationEventSchema = schemaLoader.getTransformationEventSchema();
						JSONObject jsonTransformationEvent = jsonEventElement.getJSONObject("TransformationEvent");

						if (!CaptureUtil.validate(jsonTransformationEvent, transformationEventSchema)) {

							log.info(
									"Json Document is not valid" + " detail validation check for TransFormationEvent");

							return INVALID_JSON_EVENTS;
						}
						/* finish validation logic for TransFormationEvent */

						if (!jsonTransformationEvent.has("recordTime")) {
							jsonTransformationEvent.put("recordTime", System.currentTimeMillis());
						}
						
						if (!jsonTransformationEvent.has("eventType")) {
							jsonTransformationEvent.put("eventType", "TransformationEvent");
						}

						if (jsonTransformationEvent.has("any")) {
							/* start finding namespace in the any field. */
							JSONObject anyobject = jsonTransformationEvent.getJSONObject("any");
							String namespace = "";
							boolean namespace_flag = false;

							Iterator<String> keyIter_ns = anyobject.keys();
							while (keyIter_ns.hasNext()) {
								String temp = keyIter_ns.next();
								if (temp.substring(0, 1).equals("@")) {
									namespace_flag = true;
									namespace = temp.substring(1, temp.length());
								}
							}

							if (!namespace_flag) {
								log.info("Json Document doesn't have namespace in any field");
								return INVALID_JSON_EVENTS;

							}
							/* finish finding namespace in the any field. */

							/*
							 * Start Validation whether each component use correct name space
							 */;

							Iterator<String> keyIter = anyobject.keys();
							while (keyIter.hasNext()) {
								String temp = keyIter.next();

								if (!temp.contains(namespace)) {
									log.info("Json Document use invalid namespace in anyfield");
									return INVALID_JSON_EVENTS;
								}
							}
						}

						validJsonEventList.add(jsonEventList.getJSONObject(i).getJSONObject("TransformationEvent"));
					} else if (jsonEventElement.has("TransactionEvent") == true) {

						/*
						 * startpoint of validation logic for TransFormationEvent
						 */
						JSONObject transactionEventSchema = schemaLoader.getTransactionEventSchema();
						JSONObject jsonTransactionEvent = jsonEventElement.getJSONObject("TransactionEvent");

						if (!CaptureUtil.validate(jsonTransactionEvent, transactionEventSchema)) {

							log.info(
									"Json Document is not valid." + " detail validation check for TransactionEvent");
							return INVALID_JSON_EVENTS;

						}
						/* finish validation logic for TransFormationEvent */

						if (!jsonTransactionEvent.has("recordTime")) {
							jsonTransactionEvent.put("recordTime", System.currentTimeMillis());
						}
						
						if (!jsonTransactionEvent.has("eventType")) {
							jsonTransactionEvent.put("eventType", "TransactionEvent");
						}

						if (jsonTransactionEvent.has("any")) {
							/* start finding namespace in the any field. */
							JSONObject anyobject = jsonTransactionEvent.getJSONObject("any");
							String namespace = "";
							boolean namespace_flag = false;

							Iterator<String> keyIter_ns = anyobject.keys();
							while (keyIter_ns.hasNext()) {
								String temp = keyIter_ns.next();
								if (temp.substring(0, 1).equals("@")) {
									namespace_flag = true;
									namespace = temp.substring(1, temp.length());
								}
							}

							if (!namespace_flag) {
								log.info("Json Document doesn't have namespace in any field");
								return INVALID_JSON_EVENTS;

							}
							/* finish finding namespace in the any field. */

							/*
							 * Start Validation whether each component use correct name space
							 */;

							Iterator<String> keyIter = anyobject.keys();
							while (keyIter.hasNext()) {
								String temp = keyIter.next();

								if (!temp.contains(namespace)) {
									log.info("Json Document use invalid namespace in anyfield");
									return INVALID_JSON_EVENTS;
								}
							}

						}

						validJsonEventList.add(jsonEventList.getJSONObject(i).getJSONObject("TransactionEvent"));
					} else {
						log.info("Json Document is not valid. " + " It doesn't have standard event_type");
						return INVALID_JSON_EVENTS;
					}

				}
				if (jsonEventList.length() != 0)
					log.info(" EPCIS Document : Captured ");

			} catch (JSONException e) {
				log.info(" Json Document is not valid " + "second_validcheck");
				return INVALID_JSON_EVENTS;
			} catch (Exception e) {
				log.error(e.toString());
				return INVALID_JSON_EVENTS;
			}

		} else {
			JSONObject jsonEvent = new JSONObject(inputString);
			JSONArray jsonEventList = jsonEvent.getJSONObject("epcis").getJSONObject("EPCISBody")
					.getJSONArray("EventList");

			for (int i = 0; i < jsonEventList.length(); i++) {

				JSONObject jsonEventElement = jsonEventList.getJSONObject(i);

				if (jsonEventElement.has("ObjectEvent") == true) {
					validJsonEventList.add(jsonEventElement.getJSONObject("ObjectEvent"));
				} else if (jsonEventElement.has("AggregationEvent") == true) {
					validJsonEventList.add(jsonEventElement.getJSONObject("AggregationEvent"));
				} else if (jsonEventElement.has("TransformationEvent") == true) {
					validJsonEventList.add(jsonEventElement.getJSONObject("TransformationEvent"));
				} else if (jsonEventElement.has("TransactionEvent") == true) {
					validJsonEventList.add(jsonEventElement.getJSONObject("TransactionEvent"));
				}
			}
		}
		
		List<JSONObject> validJsonEventListWithUnifiedTimeFormat = unifyJSONTimeFormat(validJsonEventList);
		
		return validJsonEventListWithUnifiedTimeFormat;
	}
	
	/**
	 * Unify time format to standard BsonDateTime.
	 * @param jsonEventList
	 * @return jsonEventList with time format in standard BsonDateTime
	 */
	private List<JSONObject> unifyJSONTimeFormat(List<JSONObject> jsonEventList)
	{
		List<JSONObject> validJsonEventList =  new ArrayList<>();
		
		for(JSONObject event:jsonEventList ) {
			BsonDocument dbObject = BsonDocument.parse(event.toString());
			dbObject.put("recordTime", new BsonDateTime(dbObject.getInt64("recordTime").getValue()));
			dbObject.put("eventTime", new BsonDateTime(dbObject.getInt64("eventTime").getValue()));
			JSONObject jsonObject = new JSONObject(dbObject.toJson());
			validJsonEventList.add(jsonObject);
		}
	
		return validJsonEventList;
	}
	
	public void capturePreparedJSONEvents(List<JSONObject> validJsonEventList, String userPartyID)
	{
		if(null == validJsonEventList)
		{
			log.info("No Events Captured!");
			return;
		}
		
		MongoCaptureUtil m = new MongoCaptureUtil();
		for (JSONObject jsonObj :  validJsonEventList) {
			m.captureJSONEvent(jsonObj, userPartyID);
		}
		
		log.info("Events Captured:" + validJsonEventList.size());
	}
	
}
