package org.oliot.model.jsonschema;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
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
 * @author Sungpil Woo, Master student
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

public class JsonSchemaLoader {
	
    private static Logger log = LoggerFactory.getLogger(JsonSchemaLoader.class);


	JSONObject eventSchema;
	JSONObject masterDataSchema;
	JSONObject objectMasterSchema;
	JSONObject objectEventSchema;
	JSONObject aggregationEventSchema;
	JSONObject transformationEventSchema;
	JSONObject transactionEventSchema;
	JSONObject productionProcTemplateSchema;

	public JsonSchemaLoader() {
		try {
			log.info(" JsonSchemaLoader Started.... ");

			eventSchema = new JSONObject(getFileWithUtil("jsonSchema/GeneralEventSchema.json"));
			masterDataSchema = new JSONObject(getFileWithUtil("jsonSchema/GeneralMasterSchema.json"));
			objectMasterSchema = new JSONObject(getFileWithUtil("jsonSchema/ObjectMasterSchema.json"));
			objectEventSchema = new JSONObject(getFileWithUtil("jsonSchema/ObjectEventSchema.json"));	
			aggregationEventSchema = new JSONObject(getFileWithUtil("jsonSchema/AggregationEventSchema.json"));	
			transformationEventSchema = new JSONObject(getFileWithUtil("jsonSchema/TransformationEventSchema.json"));	
			transactionEventSchema = new JSONObject(getFileWithUtil("jsonSchema/TransactionEventSchema.json"));	
			productionProcTemplateSchema = new JSONObject(getFileWithUtil("jsonSchema/ProductionProcessSchema.json"));	
			log.info(" JsonSchemaLoader End.... ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	  private String getFileWithUtil(String fileName) throws IOException {
			String result = "";
				
			ClassLoader classLoader = getClass().getClassLoader();
			result = IOUtils.toString(classLoader.getResourceAsStream(fileName));

			return result;
	}


	public JSONObject getProductionProcTemplateSchema() {
		return productionProcTemplateSchema;
	}

	public void setProductionProcTemplateSchema(JSONObject productionProcTemplateSchema) {
		this.productionProcTemplateSchema = productionProcTemplateSchema;
	}
	
	public JSONObject getEventSchema() {
		return eventSchema;
	}

	public void setEventSchema(JSONObject eventSchema) {
		this.eventSchema = eventSchema;
	}

	public JSONObject getMasterDataSchema() {
		return masterDataSchema;
	}

	public void setMasterDataSchema(JSONObject masterDataSchema) {
		this.masterDataSchema = masterDataSchema;
	}

	public JSONObject getObjectMasterSchema() {
		return objectMasterSchema;
	}

	public void setObjectMasterSchema(JSONObject objectMasterSchema) {
		this.objectMasterSchema = objectMasterSchema;
	}

	public JSONObject getObjectEventSchema() {
		return objectEventSchema;
	}

	public void setObjectEventSchema(JSONObject objectEventSchema) {
		this.objectEventSchema = objectEventSchema;
	}

	public JSONObject getAggregationEventSchema() {
		return aggregationEventSchema;
	}

	public void setAggregationEventSchema(JSONObject aggregationEventSchema) {
		this.aggregationEventSchema = aggregationEventSchema;
	}

	public JSONObject getTransformationEventSchema() {
		return transformationEventSchema;
	}

	public void setTransformationEventSchema(JSONObject transformationEventSchema) {
		this.transformationEventSchema = transformationEventSchema;
	}

	public JSONObject getTransactionEventSchema() {
		return transactionEventSchema;
	}

	public void setTransactionEventSchema(JSONObject transactionEventSchema) {
		this.transactionEventSchema = transactionEventSchema;
	}

}
