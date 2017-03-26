package ist.healthcheck.files;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import ist.healthcheck.beans.MailConfiguration;

/**
 * this class to read mail configutaion file
 * @author Mohammed Samir
 *
 */
public class MailConfigurationFile {
	public static final String path = "configuration/Mail_Configuration.json";

	public static MailConfiguration readConfiguration() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new File(path), MailConfiguration.class);
		} catch (IOException e) {
			// System.out.println("Mail_Configuration.json Not Found");
		}
		return null;

	}

}
