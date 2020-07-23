package org.oliot.epcis.converter.mongodb.model;


/**
* Created by Quan Deng, 2019
*/

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All step details about the Production Process Template. ")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"id",
"hasPrev",
"readPoint",
"bizLocation",
"bizStep",
"hasNext"
})
public class ProductionProcessStep {

/**
* The Id Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "Step ID",  example = "1", required = true)	
@JsonProperty("id")
@NotBlank(message = "Step ID is mandatory")
private String id = "";
/**
* The Hasprev Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "Previous Step ID",  example = "0")	
@JsonProperty("hasPrev")
private String hasPrev = "";
/**
* The Readpoint Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "Readpoint",  example = "urn:epc:id:sgln:readPoint.PodComp.1", required = true)		
@JsonProperty("readPoint")
@NotBlank(message = "readPoint is mandatory")
private String readPoint = "";
/**
* The Bizlocation Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "BizLocation",  example = "urn:epc:id:sgln:bizLocation.PodComp.2", required = true)		
@JsonProperty("bizLocation")
@NotBlank(message = "bizLocation is mandatory")
private String bizLocation = "";
/**
* The Bizstep Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "BizStep",  example = "urn:epcglobal:cbv:bizstep:installing", required = true)		
@JsonProperty("bizStep")
@NotBlank(message = "bizStep is mandatory")
private String bizStep = "";
/**
* The Hasnext Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "Next Step ID",  example = "2")	
@JsonProperty("hasNext")
private String hasNext = "";
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Id Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("id")
public String getId() {
return id;
}

/**
* The Id Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("id")
public void setId(String id) {
this.id = id;
}

/**
* The Hasprev Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("hasPrev")
public String getHasPrev() {
return hasPrev;
}

/**
* The Hasprev Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("hasPrev")
public void setHasPrev(String hasPrev) {
this.hasPrev = hasPrev;
}

/**
* The Readpoint Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("readPoint")
public String getReadPoint() {
return readPoint;
}

/**
* The Readpoint Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("readPoint")
public void setReadPoint(String readPoint) {
this.readPoint = readPoint;
}

/**
* The Bizlocation Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("bizLocation")
public String getBizLocation() {
return bizLocation;
}

/**
* The Bizlocation Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("bizLocation")
public void setBizLocation(String bizLocation) {
this.bizLocation = bizLocation;
}

/**
* The Bizstep Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("bizStep")
public String getBizStep() {
return bizStep;
}

/**
* The Bizstep Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("bizStep")
public void setBizStep(String bizStep) {
this.bizStep = bizStep;
}

/**
* The Hasnext Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("hasNext")
public String getHasNext() {
return hasNext;
}

/**
* The Hasnext Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("hasNext")
public void setHasNext(String hasNext) {
this.hasNext = hasNext;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}