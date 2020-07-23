package org.oliot.epcis.converter.mongodb.model;

/**
* Created by Quan Deng, 2019
*/


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "All details about the Process Template. ")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"productClass",
"productionProcessTemplate"
})
public class ProductProcessTemplate {

/**
* The Productclass Schema
* <p>
*
* (Required)
*
*/
@ApiModelProperty(notes = "Product Category ID",  example = "PodComp_test", required = true)
@NotBlank(message = "productClass is mandatory")
@JsonProperty("productClass")
private String productClass = "";
/**
*
* (Required)
*
*/
@ApiModelProperty(notes = "Production Process Template", required = true)
@NotNull(message = "productionProcessTemplate is mandatory")
@JsonProperty("productionProcessTemplate")
private List<ProductionProcessStep> productionProcessTemplate = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* The Productclass Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("productClass")
public String getProductClass() {
return productClass;
}

/**
* The Productclass Schema
* <p>
*
* (Required)
*
*/
@JsonProperty("productClass")
public void setProductClass(String productClass) {
this.productClass = productClass;
}

/**
*
* (Required)
*
*/
@JsonProperty("productionProcessTemplate")
public List<ProductionProcessStep> getProductionProcessTemplate() {
return productionProcessTemplate;
}

/**
*
* (Required)
*
*/
@JsonProperty("productionProcessTemplate")
public void setProductionProcessTemplate(List<ProductionProcessStep> productionProcessTemplate) {
this.productionProcessTemplate = productionProcessTemplate;
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
