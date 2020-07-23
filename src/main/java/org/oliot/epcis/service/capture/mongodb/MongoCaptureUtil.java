package org.oliot.epcis.service.capture.mongodb;



import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.converter.mongodb.AggregationEventWriteConverter;
import org.oliot.epcis.converter.mongodb.MasterDataWriteConverter;
import org.oliot.epcis.converter.mongodb.ObjectEventWriteConverter;
import org.oliot.epcis.converter.mongodb.QuantityEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransactionEventWriteConverter;
import org.oliot.epcis.converter.mongodb.TransformationEventWriteConverter;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.VocabularyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;

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

public class MongoCaptureUtil {
    private static Logger log = LoggerFactory.getLogger(MongoCaptureUtil.class);


	public HashMap<String, Object> capture(List<BsonDocument> bsonDocumentList) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
				BsonDocument.class);
		try {
			InsertManyOptions option = new InsertManyOptions();
			option.ordered(false);			
			collection.insertMany(bsonDocumentList, option);
		} catch (MongoBulkWriteException e) {
			retMsg.put("error", e.getMessage());
			return retMsg;
		}
		retMsg.put("eventCaptured", bsonDocumentList.size());
		return retMsg;
	}

	public BsonDocument convert(Object event, String userID, Integer gcpLength) {
		
		BsonDocument object2Save = null;
		String type = null;
		if (event instanceof AggregationEventType) {
			type = "AggregationEvent";
			AggregationEventWriteConverter wc = new AggregationEventWriteConverter();
			object2Save = wc.convert((AggregationEventType) event, gcpLength);
		} else if (event instanceof ObjectEventType) {
			type = "ObjectEvent";
			ObjectEventWriteConverter wc = new ObjectEventWriteConverter();
			object2Save = wc.convert((ObjectEventType) event, gcpLength);
		} else if (event instanceof QuantityEventType) {
			type = "QuantityEvent";
			QuantityEventWriteConverter wc = new QuantityEventWriteConverter();
			object2Save = wc.convert((QuantityEventType) event, gcpLength);
		} else if (event instanceof TransactionEventType) {
			type = "TransactionEvent";
			TransactionEventWriteConverter wc = new TransactionEventWriteConverter();
			object2Save = wc.convert((TransactionEventType) event, gcpLength);
		} else if (event instanceof EPCISEventListExtensionType) {
			type = "TransformationEvent";
			TransformationEventWriteConverter wc = new TransformationEventWriteConverter();
			object2Save = wc.convert(((EPCISEventListExtensionType) event).getTransformationEvent(), gcpLength);
		}

		if (object2Save == null)
			return null;

		if (userID != null) {
			object2Save.put("userPartyID", new BsonString(userID));
		}
		return object2Save;
	}

	
	/* added for JSONcapture */
	public void captureJSONProductionProcTemplate(JSONObject productionProcTemp, String userID) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("ProductionProcessTemplate",BsonDocument.class);
		BsonDocument dbObject = BsonDocument.parse(productionProcTemp.toString());
		
		if (userID != null) {
			dbObject.put("userPartyID", new BsonString(userID));
		}
		
		
		String productClass = productionProcTemp.getString("productClass");
		if(collection.find(Filters.eq("productClass", productClass)).first() != null)
		{
			collection.replaceOne(Filters.eq("productClass", productClass), dbObject);
		}
		else
		{
			collection.insertOne(dbObject);
		}
		
		log.info(" Production Process Template Saved ");
	}
	
	/* added for JSONcapture */
	public void captureJSONEvent(JSONObject event, String userID) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",BsonDocument.class);
		BsonDocument dbObject = BsonDocument.parse(event.toString());
		
		if (userID != null) {
			dbObject.put("userPartyID", new BsonString(userID));
		}
		
		collection.insertOne(dbObject);
		log.info(" Event Saved ");
	}
	/* added for JSONcapture */

	/* added for Master JSONcapture */
	public void captureJSONMaster(JSONObject event, String userID) {
		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("MasterData",BsonDocument.class);
		BsonDocument dbObject = BsonDocument.parse(event.toString());

		if (userID != null) {
			dbObject.put("userPartyID", new BsonString(userID));
		}

		Date date = new Date();
//	      This method returns the time in millis
		dbObject.append("lastUpdated",new BsonDateTime(date.getTime()));
		collection.insertOne(dbObject);
		log.info(" Master Saved ");
	}
	/* added for Master JSONcapture */

	public String capture(VocabularyType vocabulary, String userPartyID, Integer gcpLength) {
		MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
		if (mdConverter.capture(vocabulary, userPartyID, gcpLength) != 0) {
			return "[ERROR] Vocabulary Capture Failed";
		} else {
			return null;
		}
	}
}
