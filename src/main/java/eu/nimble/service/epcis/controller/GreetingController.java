package eu.nimble.service.epcis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
//    private final AtomicLong counter = new AtomicLong();
    
//    @Autowired
//    private MongoOperations mongo;   
//    
//    @Autowired
//    private MongoClient mongoClient;   

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }
    
//    @RequestMapping("/testConnection")
//    public List<String> testConnection() {
//    
//	     List<String> collectionNames = mongoClient.getDatabaseNames();// mongo.getCollectionNames();  
//	      for (String name : collectionNames) {  
//	            System.out.println("collectionName==="+name);  
//	      }  
//	      
//	   //  System.out.println("client db name 1 ==="+mongoClient.getDatabase("epcis").getName());  
//	     
//	     System.out.println("client db name==="+mongoClient.getConnectPoint());  
//     
//	    // System.out.println("configuration wsdle==="+ Configuration.wsdlPath);  
//
//        return collectionNames;
//    }
    
}
