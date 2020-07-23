package org.oliot.epcis.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoDatabase;

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
* 
*/

@Component
public class Configuration {
   
	public static MongoDatabase mongoDatabase;
    public static boolean isCaptureVerfificationOn = true;    

    @Autowired
    public void setMongoDatabase(MongoDatabase mongoDatabase) {
    	Configuration.mongoDatabase = mongoDatabase;
    }
    
    @Value("${epcis.capture.verification}")
    public void setDatabase(boolean isCaptureVerfificationOn) {
    	Configuration.isCaptureVerfificationOn = isCaptureVerfificationOn;
    }
    
}
