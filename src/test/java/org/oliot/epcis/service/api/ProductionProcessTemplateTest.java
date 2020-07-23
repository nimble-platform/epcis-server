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
//public class ProductionProcessTemplateTest {
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
//    @Test
//    public void jsonProductionCapture() {
//        String testJsonProductionData = "{\n" +
//                "  \"productClass\": \"testProductionClass\",\n" +
//                "  \"productionProcessTemplate\": [\n" +
//                "    {\n" +
//                "      \"id\": \"1\",\n" +
//                "      \"hasPrev\": \"\",\n" +
//                "      \"readPoint\": \"urn:epc:id:sgln:readPoint.lindbacks.1\",\n" +
//                "      \"bizLocation\": \"urn:epc:id:sgln:bizLocation.lindbacks.2\",\n" +
//                "      \"bizStep\": \"urn:epcglobal:cbv:bizstep:other\",\n" +
//                "      \"hasNext\": \"2\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"id\": \"2\",\n" +
//                "      \"hasPrev\": \"1\",\n" +
//                "      \"readPoint\": \"urn:epc:id:sgln:readPoint.lindbacks.2\",\n" +
//                "      \"bizLocation\": \"urn:epc:id:sgln:bizLocation.lindbacks.3\",\n" +
//                "      \"bizStep\": \"urn:epcglobal:cbv:bizstep:installing\",\n" +
//                "      \"hasNext\": \"3\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"id\": \"3\",\n" +
//                "      \"hasPrev\": \"2\",\n" +
//                "      \"readPoint\": \"urn:epc:id:sgln:readPoint.lindbacks.3\",\n" +
//                "      \"bizLocation\": \"urn:epc:id:sgln:bizLocation.lindbacks.4\",\n" +
//                "      \"bizStep\": \"urn:epcglobal:cbv:bizstep:entering_exiting\",\n" +
//                "      \"hasNext\": \"4\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"id\": \"4\",\n" +
//                "      \"hasPrev\": \"3\",\n" +
//                "      \"readPoint\": \"urn:epc:id:sgln:readPoint.lindbacks.4\",\n" +
//                "      \"bizLocation\": \"urn:epc:id:sgln:bizLocation.lindbacks.5\",\n" +
//                "      \"bizStep\": \"urn:epcglobal:cbv:bizstep:shipping\",\n" +
//                "      \"hasNext\": \"\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//        this.postRestAPITest(this.getBaseUrl() + "/JSONProductionProcTemplateCapture", "application/json", testJsonProductionData);
//    }
//
//    @Test
//    public void getJsonProductionProcessTemplate() {
//        this.getRestAPITest(this.getBaseUrl() + "/GetProductionProcessTemplate/testProductionClass");
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
//}
