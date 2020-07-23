package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/*added for json capture*/

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.oliot.gcp.core.SimplePureIdentityFilter;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.DocumentIdentification;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.StandardBusinessDocumentHeader;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.xsdschema.XSDSchemaLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* added for json capture */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

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

public class CaptureUtil {
    private static Logger log = LoggerFactory.getLogger(CaptureUtil.class);

    
	public static boolean validate(InputStream is, String xsdSchemaName) {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			// associate the schema factory with the resource resolver, which is responsible for resolving the imported XSD's
			schemaFactory.setResourceResolver(new ResourceResolver("xsdSchema/"));
			XSDSchemaLoader xsdSchemaLoader = new XSDSchemaLoader();
			InputStream xsdFile = xsdSchemaLoader.getXSDSchema(xsdSchemaName);
			Schema schema = schemaFactory.newSchema(new StreamSource(xsdFile));	
			Validator validator = schema.newValidator();
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			    builderFactory.setNamespaceAware(true);
			DocumentBuilder parser= builderFactory
	        .newDocumentBuilder();
			Document document =parser.parse(is);
		    validator.validate(new DOMSource(document));
		    
			return true;
		} catch (SAXException e) {
			log.error(e.toString());
			return false;
		} catch (IOException e) {
			log.error(e.toString());

			return false;
		}catch (ParserConfigurationException e) {
			log.error(e.toString());
			
			return false;
		}
	}

	public static String getValidationException(InputStream is, String xsdSchemaName) {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			// associate the schema factory with the resource resolver, which is responsible for resolving the imported XSD's
			schemaFactory.setResourceResolver(new ResourceResolver("xsdSchema/"));
			XSDSchemaLoader xsdSchemaLoader = new XSDSchemaLoader();
			InputStream xsdFile = xsdSchemaLoader.getXSDSchema(xsdSchemaName);
			Schema schema = schemaFactory.newSchema(new StreamSource(xsdFile));	
			Validator validator = schema.newValidator();
			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			    builderFactory.setNamespaceAware(true);
			DocumentBuilder parser= builderFactory
	        .newDocumentBuilder();
			Document document =parser.parse(is);
		    validator.validate(new DOMSource(document));
		
		    return null;
		} catch (SAXException e) {
			log.error(e.toString());
			return e.toString();
		} catch (IOException e) {
			log.error(e.toString());
			return e.toString();
		}catch (ParserConfigurationException e) {
			log.error(e.toString());
			return e.toString();
		}
	}
	
	public static boolean validate(JSONObject Json, JSONObject schema_obj) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode input_node = mapper.readTree(Json.toString());
			JsonNode schema_node = mapper.readTree(schema_obj.toString());

			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonSchema schema = factory.getJsonSchema(schema_node);
			ProcessingReport report;
			report = schema.validate(input_node);
			log.info("validation process report : " + report);
			return report.isSuccess();

		} catch (IOException e) {
			log.error(e.toString());
			return false;
		} catch (ProcessingException e) {
			log.error(e.toString());
			return false;
		}
	}

	public static InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
	}

	public static String getXMLDocumentString(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String xmlString = writer.toString();
			return xmlString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String makeTimeZoneString(int timeZone) {
		String retString = "";
		timeZone = timeZone / 60;

		if (timeZone >= 0) {
			retString = String.format("+%02d:00", timeZone);
		} else {
			timeZone = Math.abs(timeZone);
			retString = String.format("-%02d:00", timeZone);
		}
		return retString;
	}

	public static boolean isCorrectTimeZone(String timeZone) {

		boolean isMatch = timeZone.matches("^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$");

		return isMatch;
	}

	public static ResponseEntity<?> checkDocumentHeader(EPCISDocumentType epcisDocument) {
		// M50, M63
		if (epcisDocument.getEPCISHeader() != null) {
			if (epcisDocument.getEPCISHeader().getStandardBusinessDocumentHeader() != null) {
				StandardBusinessDocumentHeader header = epcisDocument.getEPCISHeader()
						.getStandardBusinessDocumentHeader();
				if (header.getHeaderVersion() == null || !header.getHeaderVersion().equals("1.2")) {
					log.error(" HeaderVersion should 1.2 if use SBDH ");

					return new ResponseEntity<>(new String("Error: HeaderVersion should 1.2 if use SBDH"),
							HttpStatus.BAD_REQUEST);
				}
				if (header.getDocumentIdentification() == null) {
					log.error(" DocumentIdentification should exist if use SBDH ");

					return new ResponseEntity<>(new String("Error: DocumentIdentification should exist if use SBDH"),
							HttpStatus.BAD_REQUEST);
				} else {
					DocumentIdentification docID = header.getDocumentIdentification();
					if (docID.getStandard() == null | !docID.getStandard().equals("EPCglobal")) {
						log.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
						return new ResponseEntity<>(
								new String("Error: DocumentIdentification/Standard should EPCglobal if use SBDH"),
								HttpStatus.BAD_REQUEST);
					}
					if (docID.getType() == null
							|| (!docID.getType().equals("Events") && !docID.getType().equals("MasterData"))) {
	
						log.error(" DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH ");

						return new ResponseEntity<>(
								new String(
										"Error: DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH"),
								HttpStatus.BAD_REQUEST);
					}
					if (docID.getTypeVersion() == null | !docID.getTypeVersion().equals("1.2")) {
						log.error(" DocumentIdentification/TypeVersion should 1.2 if use SBDH ");

						return new ResponseEntity<>(
								new String("Error: DocumentIdentification/TypeVersion should 1.2 if use SBDH"),
								HttpStatus.BAD_REQUEST);
					}

				}
			}
		}
		return null;
	}


	public static boolean isCorrectEvent(Object event) {
		if (event instanceof ObjectEventType) {
			return isCorrectObjectEvent((ObjectEventType) event);
		} else if (event instanceof AggregationEventType) {
			return isCorrectAggregationEvent((AggregationEventType) event);
		} else if (event instanceof TransactionEventType) {
			return isCorrectTransactionEvent((TransactionEventType) event);
		} else if (event instanceof QuantityEventType) {
			return isCorrectQuantityEvent((QuantityEventType) event);
		} else if (event instanceof EPCISEventListExtensionType) {
			return isCorrectTransformationEvent(((EPCISEventListExtensionType) event).getTransformationEvent());
		}
		return true;
	}

	public static boolean isCorrectAggregationEvent(AggregationEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			log.error("Req. M7 Error");
			return false;
		}

		// Mandatory Field: Action
		if (event.getAction() == null) {
			log.error("Aggregation Event should have 'Action' field ");
			return false;
		}
		// M13
		if (event.getAction() == ActionType.ADD || event.getAction() == ActionType.DELETE) {
			if (event.getParentID() == null) {
				log.error("Req. M13 Error");
				return false;
			}
		}
		// M10
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				log.error("Req. M10 Error");
				return false;
			}
		}
		return true;
	}

	public static boolean isCorrectObjectEvent(ObjectEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			log.error("Req. M7 Error");
			return false;
		}
		return true;
	}

	public static boolean isCorrectTransactionEvent(TransactionEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			log.error("Req. M7 Error");
			return false;
		}

		// M14
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				log.error("Req. M14 Error");
				return false;
			}
		}
		return true;
	}

	public static boolean isCorrectQuantityEvent(QuantityEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			log.error("Req. M7 Error");
			return false;
		}
		return true;
	}

	public static boolean isCorrectTransformationEvent(TransformationEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			log.error("Req. M7 Error");
			return false;
		}
		return true;
	}
}
