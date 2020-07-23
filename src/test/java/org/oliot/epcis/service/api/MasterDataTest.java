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
//public class MasterDataTest {
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
//    String testJsonMasterData = "{\n" +
//            "    \"epcismd\": {\n" +
//            "        \"EPCISBody\": {\n" +
//            "            \"VocabularyList\": [\n" +
//            "                {\n" +
//            "                    \"Vocabulary\": {\n" +
//            "                        \"id\": \"af\",\n" +
//            "                        \"type\": \"urn:epcglobal:epcis:vtype:CountryCode\",\n" +
//            "                        \"attributes\": {\n" +
//            "                            \"urn:epcglobal:cbv:mda#description\": \"Bangladesh\"\n" +
//            "                        }\n" +
//            "                    }\n" +
//            "                }, \n" +
//            "                {\n" +
//            "                    \"Vocabulary\": {\n" +
//            "                        \"id\": \"al\",\n" +
//            "                        \"type\": \"urn:epcglobal:epcis:vtype:CountryCode\",\n" +
//            "                        \"attributes\": {\n" +
//            "                            \"urn:epcglobal:cbv:mda#description\": \"Albania\"\n" +
//            "                        }\n" +
//            "                    }\n" +
//            "                }\n" +
//            "            ]\n" +
//            "        }\n" +
//            "    }\n" +
//            "}";
//
//    String textXmlMasterData = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
//            "<!DOCTYPE project>\n" +
//            "<epcismd:EPCISMasterDataDocument\n" +
//            "\txmlns:epcismd=\"urn:epcglobal:epcis-masterdata:xsd:1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
//            "\tschemaVersion=\"1.0\" creationDate=\"2005-07-11T11:30:47.0Z\">\n" +
//            "\t<EPCISBody>\n" +
//            "\t\t<VocabularyList>\n" +
//            "\t\t\t<Vocabulary type=\"urn:epcglobal:epcis:vtype:BusinessLocation\">\n" +
//            "\t\t\t\t<VocabularyElementList>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.0\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"http://epcis.example.com/mda/latitude\">+18.0000</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"http://epcis.example.com/mda/longitude\">-70.0000</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"http://epcis.example.com/mda/address\">\n" +
//            "\t\t\t\t\t\t\t<example:Address xmlns:example=\"http://epcis.example.com/ns\">\n" +
//            "\t\t\t\t\t\t\t\t<Street>100 Nowhere Street</Street>\n" +
//            "\t\t\t\t\t\t\t\t<City>Fancy</City>\n" +
//            "\t\t\t\t\t\t\t\t<State>DC</State>\n" +
//            "\t\t\t\t\t\t\t\t<Zip>99999</Zip>\n" +
//            "\t\t\t\t\t\t\t</example:Address>\n" +
//            "\t\t\t\t\t\t</attribute>\n" +
//            "\t\t\t\t\t\t<children>\n" +
//            "\t\t\t\t\t\t\t<id>urn:epc:id:sgln:0037000.00729.8201</id>\n" +
//            "\t\t\t\t\t\t\t<id>urn:epc:id:sgln:0037000.00729.8202</id>\n" +
//            "\t\t\t\t\t\t\t<id>urn:epc:id:sgln:0037000.00729.8203</id>\n" +
//            "\t\t\t\t\t\t</children>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8201\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">201</attribute>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8202\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">202</attribute>\n" +
//            "\t\t\t\t\t\t<children>\n" +
//            "\t\t\t\t\t\t\t<id>urn:epc:id:sgln:0037000.00729.8203</id>\n" +
//            "\t\t\t\t\t\t</children>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8203\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">202</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:ssa\">402</attribute>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t</VocabularyElementList>\n" +
//            "\t\t\t</Vocabulary>\n" +
//            "\t\t\t<Vocabulary type=\"urn:epcglobal:epcis:vtype:ReadPoint\">\n" +
//            "\t\t\t\t<VocabularyElementList>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8201\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:site\">0037000007296</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">201</attribute>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8202\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:site\">0037000007296</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">202</attribute>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t\t<VocabularyElement id=\"urn:epc:id:sgln:0037000.00729.8203\">\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:site\">0037000007296</attribute>\n" +
//            "\t\t\t\t\t\t<attribute id=\"urn:epcglobal:cbv:mda:sst\">203</attribute>\n" +
//            "\t\t\t\t\t</VocabularyElement>\n" +
//            "\t\t\t\t</VocabularyElementList>\n" +
//            "\t\t\t</Vocabulary>\n" +
//            "\t\t</VocabularyList>\n" +
//            "\t</EPCISBody>\n" +
//            "</epcismd:EPCISMasterDataDocument>";
//
//    @Test
//    public void xmlMasterCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/VocabularyCapture", "application/xml", textXmlMasterData);
//    }
//
//    @Test
//    public void testReplicateXmlMasterCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/IntelligentVocabularyCapture", "application/xml", textXmlMasterData);
//    }
//
//    @Test
//    public void jsonMasterCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/JSONMasterCapture", "application/json", testJsonMasterData);
//    }
//
//    @Test
//    public void testReplicateJsonMasterCapture() {
//        this.postRestAPITest(this.getBaseUrl() + "/IntelligentJSONMasterCapture", "application/json", testJsonMasterData);
//    }
//
//    @Test
//    public void getJsonMasterQuery() {
//        this.getRestAPITest(this.getBaseUrl() + "/Poll/SimpleMasterDataQuery?includeAttributes=true&includeChildren=true&masterDataFormat=JSON");
//    }
//
//    @Test
//    public void getXmlMasterQuery() {
//        this.getRestAPITest(this.getBaseUrl() + "/Poll/SimpleMasterDataQuery?includeAttributes=true&includeChildren=true&masterDataFormat=XML");
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
