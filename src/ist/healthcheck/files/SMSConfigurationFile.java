package ist.healthcheck.files;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import ist.healthcheck.beans.SMSConfiguration;


/**
 * this class to read SMS configuration file
 * @author Mohammed Samir
 *
 */
public class SMSConfigurationFile {

	private static final String path = "configuration/SMS_Configuration.json";

	public static SMSConfiguration readConfiguration() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new File(path), SMSConfiguration.class);
		} catch (IOException e) {
			// System.out.println("SMS_Configuration.json Not Found");
		}
		return null;
	}

}
