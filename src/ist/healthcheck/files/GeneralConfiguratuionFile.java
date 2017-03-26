package ist.healthcheck.files;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import ist.healthcheck.beans.GeneralConfiguration;

/**
 * this class to read general configuration file
 * @author Mohammed Samir
 *
 */
public class GeneralConfiguratuionFile {
	private static final String path = "configuration/General_Configuration.json" ; 
	
	public static GeneralConfiguration readConfiguration(){
		ObjectMapper mapper = new ObjectMapper() ; 
		try {
			return mapper.readValue(new File(path), GeneralConfiguration.class);
		} catch (IOException e) {
//			System.out.println("General_Configuration.json Not Found");
		}
		
		return null ;
		
	}
	
	
	

}
