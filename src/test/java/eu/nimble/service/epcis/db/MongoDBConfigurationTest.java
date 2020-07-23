//package eu.nimble.service.epcis.db;
//
//import java.util.List;
//import java.util.Set;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
//import org.springframework.boot.test.IntegrationTest;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.mchange.v2.beans.swing.TestBean;
//
////@ContextConfiguration(classes = MongoConfig.class)
//@PropertySource(value = "classpath:application.properties")
//@RunWith(SpringJUnit4ClassRunner.class)
////@RunWith(SpringRunner.class)
////@SpringBootTest
//public class MongoDBConfigurationTest {
//
//    @Value("${server.servlet-path}")
//    public String mongoHost;
//
//
////	 @Autowired
////	    private MongoConfig config;
//
//	   @Test
//	    public void test() {
//		   System.out.println("collectionName==="+mongoHost);
////	    	System.out.println("collectionName==="+config.mongoDB);
////		     List<String> collectionNames = config.mongoClient().getDatabaseNames();
////		      for (String name : collectionNames) {
////		            System.out.println("collectionName==="+name);
////		      }
//	    }
//
//}
