//package org.oliot.epcis.service.api;
//
//import eu.nimble.service.epcis.EPCISRepositoryApplication;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.hamcrest.core.IsEqual;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = EPCISRepositoryApplication.class)
//public class EventDataTest {
//
//    @Value("${test.base-url}")
//    private String baseUrl;
//
//    private String getBaseUrl() {
//        return baseUrl + "/Service";
//    }
//
//    @Value("${test.accessToken}")
//    private String accessToken;
//
//    String testJsonEventData = "{\n" +
//            "    \"epcis\": {\n" +
//            "        \"EPCISBody\": {\n" +
//            "            \"EventList\": [\n" +
//            "                {\n" +
//            "                    \"ObjectEvent\": {\n" +
//            "                        \"eventTime\": 1370703536591,\n" +
//            "                        \"eventTimeZoneOffset\": \"+02:00\",\n" +
//            "                        \"recordTime\": 1441967382985,\n" +
//            "                        \"epcList\": [],\n" +
//            "                        \"action\": \"OBSERVE\",\n" +
//            "                        \"bizStep\": \"urn:epcglobal:cbv:bizstep:receiving\",\n" +
//            "                        \"disposition\": \"urn:epcglobal:cbv:disp:in_progress\",\n" +
//            "                        \"readPoint\": {\n" +
//            "                            \"id\": \"urn:epc:id:sgln:0614141.00777.0\"\n" +
//            "                        },\n" +
//            "                        \"bizLocation\": {\n" +
//            "                            \"id\": \"urn:epc:id:sgln:0614141.00888.0\"\n" +
//            "                        },\n" +
//            "                        \"extension\": {\n" +
//            "                            \"quantityList\": [\n" +
//            "                                {\n" +
//            "                                    \"epcClass\": \"urn:epc:class:lgtin:4012345.012345.998877\",\n" +
//            "                                    \"quantity\": 200.0,\n" +
//            "                                    \"uom\": \"KGM\"\n" +
//            "                                }\n" +
//            "                            ],\n" +
//            "                            \"sourceList\": [\n" +
//            "                                {\n" +
//            "                                    \"urn:epcglobal:cbv:sdt:possessing_party\": \"urn:epc:id:sgln:4012345.00001.0\"\n" +
//            "                                },\n" +
//            "                                {\n" +
//            "                                    \"urn:epcglobal:cbv:sdt:location\": \"urn:epc:id:sgln:4012345.00225.0\"\n" +
//            "                                }\n" +
//            "                            ],\n" +
//            "                            \"destinationList\": [\n" +
//            "                                {\n" +
//            "                                    \"urn:epcglobal:cbv:sdt:owning_party\": \"urn:epc:id:sgln:0614141.00001.0\"\n" +
//            "                                },\n" +
//            "                                {\n" +
//            "                                    \"urn:epcglobal:cbv:sdt:location\": \"urn:epc:id:sgln:0614141.00777.0\"\n" +
//            "                                }\n" +
//            "                            ]\n" +
//            "                        }\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ]\n" +
//            "        }\n" +
//            "    }\n" +
//            "}";
//
//    String testXmlEventData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//            "<!DOCTYPE project>\n" +
//            "<epcis:EPCISDocument xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n" +
//            "\txmlns:example=\"http://ns.example.com/epcis\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
//            "\tcreationDate=\"2005-07-11T11:30:47.0Z\" schemaVersion=\"1.2\">\n" +
//            "\t<EPCISBody>\n" +
//            "\t\t<EventList>\n" +
//            "\t\t\t<AggregationEvent>\n" +
//            "\t\t\t\t<eventTime>2013-06-08T14:58:56.591Z</eventTime>\n" +
//            "\t\t\t\t<eventTimeZoneOffset>+02:00</eventTimeZoneOffset>\n" +
//            "\t\t\t\t<parentID>urn:epc:id:sscc:0614141.1234567890</parentID>\n" +
//            "\t\t\t\t<childEPCs>\n" +
//            "\t\t\t\t\t<epc>urn:epc:id:sgtin:0614141.107346.localtest</epc>\n" +
//            "\t\t\t\t\t<epc>urn:epc:id:sgtin:0614141.107346.2018</epc>\n" +
//            "\t\t\t\t</childEPCs>\n" +
//            "\t\t\t\t<action>OBSERVE</action>\n" +
//            "\t\t\t\t<bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep>\n" +
//            "\t\t\t\t<disposition>urn:epcglobal:cbv:disp:in_progress</disposition>\n" +
//            "\t\t\t\t<readPoint>\n" +
//            "\t\t\t\t\t<id>urn:epc:id:sgln:0614141.00777.0</id>\n" +
//            "\t\t\t\t</readPoint>\n" +
//            "\t\t\t\t<bizLocation>\n" +
//            "\t\t\t\t\t<id>urn:epc:id:sgln:0614141.00888.0</id>\n" +
//            "\t\t\t\t</bizLocation>\n" +
//            "\t\t\t\t<extension>\n" +
//            "\t\t\t\t\t<childQuantityList>\n" +
//            "\t\t\t\t\t\t<quantityElement>\n" +
//            "\t\t\t\t\t\t\t<epcClass>urn:epc:idpat:sgtin:4012345.098765.*</epcClass>\n" +
//            "\t\t\t\t\t\t\t<quantity>10</quantity>\n" +
//            "\t\t\t\t\t\t\t<!-- Meaning: 10 units of GTIN '04012345987652' -->\n" +
//            "\t\t\t\t\t\t</quantityElement>\n" +
//            "\t\t\t\t\t\t<quantityElement>\n" +
//            "\t\t\t\t\t\t\t<epcClass>urn:epc:class:lgtin:4012345.012345.998877</epcClass>\n" +
//            "\t\t\t\t\t\t\t<quantity>200.5</quantity>\n" +
//            "\t\t\t\t\t\t\t<uom>KGM</uom>\n" +
//            "\t\t\t\t\t\t\t<!-- Meaning: 200.5 kg of GTIN '04012345123456' belonging to lot '998877' -->\n" +
//            "\t\t\t\t\t\t</quantityElement>\n" +
//            "\t\t\t\t\t</childQuantityList>\n" +
//            "\t\t\t\t</extension>\n" +
//            "\t\t\t\t<example:myField>Example of a vendor/user extension</example:myField>\n" +
//            "\t\t\t</AggregationEvent>\n" +
//            "\t\t</EventList>\n" +
//            "\t</EPCISBody>\n" +
//            "</epcis:EPCISDocument>";
//
//    @Test
//    public void xmlEventCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/EventCapture", "application/xml", testXmlEventData);
//    }
//
//    @Test
//    public void testReplicateXmlEventCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/IntelligentXMLEventCapture", "application/xml", testXmlEventData);
//    }
//
//    @Test
//    public void jsonEventCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/JSONEventCapture", "application/json", testJsonEventData);
//    }
//
//    @Test
//    public void testReplicateJsonEventCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/IntelligentJSONEventCapture", "application/json", testJsonEventData);
//    }
//
//
//    @Test
//    public void getJsonEventQuery() {
//        this.getRestAPITest(this.getBaseUrl() + "/Poll/SimpleEventQuery?format=JSON");
//    }
//
//    @Test
//    public void getXmlEventQuery() {
//        this.getRestAPITest(this.getBaseUrl() + "/Poll/SimpleEventQuery?format=XML");
//    }
//
//    private void postRestAPITest(String url, String contentType, String entity) {
//        try {
//            HttpPost httpRequest = new HttpPost(url);
//            httpRequest.addHeader("Authorization", accessToken);
//            httpRequest.setHeader("Content-Type", contentType);
//            StringEntity xmlEntity = new StringEntity(entity);
//            httpRequest.setEntity(xmlEntity );
//            HttpResponse httpresponse = HttpClientBuilder.create().build().execute(httpRequest);
//            Assert.assertThat(
//                    httpresponse.getStatusLine().getStatusCode(),
//                    IsEqual.equalTo(HttpStatus.SC_OK));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getRestAPITest(String url) {
//        try {
//            HttpUriRequest request = new HttpGet(url);
//            request.addHeader("Authorization", accessToken);
//            HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
//            Assert.assertThat(
//                    httpResponse.getStatusLine().getStatusCode(),
//                    IsEqual.equalTo(HttpStatus.SC_OK));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//
