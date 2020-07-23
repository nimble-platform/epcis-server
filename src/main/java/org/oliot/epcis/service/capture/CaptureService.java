package org.oliot.epcis.service.capture;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.bson.BsonDocument;
import org.json.JSONObject;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;

import org.oliot.model.epcis.EPCISDocumentType;

import org.oliot.model.epcis.EPCISMasterDataDocumentType;

import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.nimble.service.epcis.services.AuthorizationSrv;

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

@Component
public class CaptureService {

	@Autowired
	AuthorizationSrv authorizationSrv;
	
	// Return null -> Succeed, not null --> error message
	public JSONObject capture(EPCISDocumentType epcisDocument, String userID, 
			Integer gcpLength) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		// Capture EPCIS Events
		retMsg.putAll(captureEvents(epcisDocument, userID,  gcpLength));
		// Capture EPCIS Vocabularies
		retMsg.putAll(captureVocabularies(epcisDocument, userID, gcpLength));
		return new JSONObject(retMsg);
	}

	public JSONObject capture(EPCISMasterDataDocumentType epcisMasterDataDocument, String userPartyID, Integer gcpLength) {

		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		try {
			List<VocabularyType> vocabularyTypeList = epcisMasterDataDocument.getEPCISBody().getVocabularyList()
					.getVocabulary();
			retMsg.putAll(captureVocabularies(vocabularyTypeList, userPartyID, gcpLength));
		} catch (NullPointerException ex) {

		}
		return new JSONObject(retMsg);
	}

	@SuppressWarnings("rawtypes")
	private BsonDocument prepareEvent(Object jaxbEvent, String userID, Integer gcpLength) {
		JAXBElement eventElement = (JAXBElement) jaxbEvent;
		Object event = eventElement.getValue();
		CaptureUtil.isCorrectEvent(event);
		MongoCaptureUtil m = new MongoCaptureUtil();
		BsonDocument doc = m.convert(event, userID, gcpLength);
		return doc;
	}

	private HashMap<String, Object> captureEvents(EPCISDocumentType epcisDocument, String userID,
			Integer gcpLength) {
		
		HashMap<String, Object> retMessage = new HashMap<String, Object>();
		
		try {
			List<Object> eventList = epcisDocument.getEPCISBody().getEventList()
					.getObjectEventOrAggregationEventOrQuantityEvent();
			List<BsonDocument> bsonDocumentList = eventList.parallelStream().parallel()
					.map(jaxbEvent -> prepareEvent(jaxbEvent, userID, gcpLength))
					.filter(doc -> doc != null).collect(Collectors.toList());
			MongoCaptureUtil util = new MongoCaptureUtil();
			
			if (bsonDocumentList != null && bsonDocumentList.size() != 0)
			{
				if(!authorizationSrv.hasCapturePermission(userID, bsonDocumentList))
				{
					retMessage.put("error", "userPartyID has no permission for capturing the given events!" );
					return retMessage;
				}
				return util.capture(bsonDocumentList);
				
			}
		} catch (NullPointerException ex) {
			// No Event
		}
		return retMessage;
	}

	private HashMap<String, Object> captureVocabularies(EPCISDocumentType epcisDocument, String userID,
			 Integer gcpLength) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		try {
			// Master Data in the document
			List<VocabularyType> vocabularyTypeList = epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData()
					.getVocabularyList().getVocabulary();
			retMsg = captureVocabularies(vocabularyTypeList, userID, gcpLength);
		} catch (NullPointerException ex) {
			// No vocabulary in the document
		}
		return retMsg;
	}

	private HashMap<String, Object> captureVocabularies(List<VocabularyType> vocabularyTypeList, String userID, Integer gcpLength) {

		HashMap<String, Object> retMsg = new HashMap<String, Object>();

		AtomicInteger cntVoc = new AtomicInteger(0);

		vocabularyTypeList.parallelStream().forEach(vocabulary -> {
			try {
				List<VocabularyElementType> vetList = vocabulary.getVocabularyElementList().getVocabularyElement();
				List<VocabularyElementType> vetTempList = vetList.parallelStream().collect(Collectors.toList());

				for (int j = 0; j < vetTempList.size(); j++) {
					vocabulary.getVocabularyElementList().getVocabularyElement().clear();
					vocabulary.getVocabularyElementList().getVocabularyElement().add(vetTempList.get(j));
					String message = capture(vocabulary, userID,  gcpLength);
					if (message != null) {
						retMsg.put("error", message);
					} else {
						cntVoc.incrementAndGet();
					}
				}
			} catch (NullPointerException ex) {

			}
		});

		retMsg.put("vocabularyCaptured", cntVoc);
		return retMsg;
	}

	private String capture(VocabularyType vocabulary, String userPartyID, Integer gcpLength) {
		MongoCaptureUtil m = new MongoCaptureUtil();
		return m.capture(vocabulary, userPartyID, gcpLength);
	}
}
