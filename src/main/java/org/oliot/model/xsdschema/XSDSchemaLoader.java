package org.oliot.model.xsdschema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
* Created by Quan Deng, 2019
*/

public class XSDSchemaLoader {
    private static Logger log = LoggerFactory.getLogger(XSDSchemaLoader.class);

    /**
     * 
     * @param schemaName schema name with file extension e.g. EPCglobal-epcis-1_2.xsd 
     * @return
     */
    public InputStream getXSDSchema(String schemaName)
    {
    	InputStream file = null;
    	
		try {
			file = getFileWithUtil("xsdSchema" + File.separator + schemaName);
		} catch (IOException e) {
			log.error("not able to load xsd schema: " + schemaName, e);
		}
    	
    	return file;
    }
    
    
	  private InputStream getFileWithUtil(String fileName) throws IOException {
			InputStream result = null;
				
			ClassLoader classLoader = getClass().getClassLoader();
			result = classLoader.getResourceAsStream(fileName);

			return result;
	}
}
