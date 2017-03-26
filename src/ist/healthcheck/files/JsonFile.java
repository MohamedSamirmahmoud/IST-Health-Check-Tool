package ist.healthcheck.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import ist.healthcheck.beans.IntervalsWithDate;

/**
 * this class to read and write historical data.
 * @author Mohammed Samir
 *
 */


public class JsonFile {
	/**
	 *  read historical data KPIs
	 * @param fileName the file name of the historical data
	 * @return historical data intervals
	 */

	public static IntervalsWithDate readKPISJsonFile(String fileName) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			IntervalsWithDate intervalsWithDate = mapper.readValue(new File("data/" + fileName),
					IntervalsWithDate.class);
			return intervalsWithDate;
		} catch (Exception e) {
			System.out.println(fileName + " Not Found");
			return null;
		}
	}
	
	/**
	 * write historical data KPIs
	 * @param intervalsWithDate KPIs intervals 
	 * @param fileName the file name of the historical data 
	 */

	public static void writeKPIJsonFile(IntervalsWithDate intervalsWithDate, String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(intervalsWithDate);
			File file = new File("data/" + fileName);
			if (!file.exists())
				file.getParentFile().mkdirs();
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(jsonString);
			bufferedWriter.flush();
			bufferedWriter.close();
			System.out.println(fileName + " Created");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
