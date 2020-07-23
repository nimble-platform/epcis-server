package eu.nimble.service.epcis.db;

import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
//import javax.security.cert.X509Certificate;

import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

/**
* Created by Quan Deng, 2019
*/

@Configuration
public class MongoConfig {
    private static Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.database}")
    public String mongoDB;
    
    @Value("${spring.data.mongodb.uri}")
    public String mongoURI;
    
    @Value("${spring.data.mongodb.ssl}")
    public boolean sslEnabled;
    
    /**
     * MongoTemplate Bean
     * @param mongoDbFactory
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws UnknownHostException 
     * @throws KeyManagementException 
     */
    @Bean
    public MongoOperations mongoTemplate() throws KeyManagementException, UnknownHostException, NoSuchAlgorithmException{
        return new MongoTemplate(mongoDbFactory());
    }
    
    /**
     * MongoDbFactory bean
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws UnknownHostException 
     * @throws KeyManagementException 
     */
    @Bean
    public MongoDbFactory mongoDbFactory() throws KeyManagementException, UnknownHostException, NoSuchAlgorithmException{
        return new SimpleMongoDbFactory(mongoClient(), mongoDB);
    }
    

    @Bean
    public MongoClient mongoClient() throws UnknownHostException, NoSuchAlgorithmException, KeyManagementException{
        
        MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
        

        MongoClientOptions options = MongoClientOptions.builder().build();
        if(sslEnabled)
        {
        	options = getSSLEnabledMongoClientOptions();
        }
        
        
        List<ServerAddress> serverAddressList = mongoClientURI.getHosts().stream().map(host -> new ServerAddress(host)).collect(Collectors.toList());
        
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        MongoCredential credential = mongoClientURI.getCredentials();
        if(null != credential)
        {
        	credentialList.add(credential);
        }
        

        MongoClient mongoClient = new MongoClient(serverAddressList, credentialList, options); 		
                
        return mongoClient;
    }

    private MongoClientOptions getSSLEnabledMongoClientOptions()throws UnknownHostException, NoSuchAlgorithmException, KeyManagementException{
    	
        TrustManager[] trustManagers = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String t) { }
                    public void checkServerTrusted(X509Certificate[] certs, String t) { }
                }
            };

            SSLContext sslContext=SSLContext.getInstance("TLS");
            sslContext.init(null,trustManagers,new SecureRandom());
            
            MongoClientOptions options = MongoClientOptions.builder().
                            sslEnabled(true).
                            sslInvalidHostNameAllowed(true).
                            socketFactory(sslContext.getSocketFactory()).
                            build();
            
            return options;
    }
    
    
    /**
     * MongoDatabase bean
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws UnknownHostException 
     * @throws KeyManagementException 
     */
    @Bean
    public MongoDatabase mongoDatabase() throws KeyManagementException, UnknownHostException, NoSuchAlgorithmException
    {
    	return mongoClient().getDatabase(mongoDB);
    }

}