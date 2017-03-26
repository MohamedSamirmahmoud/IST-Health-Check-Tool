package ist.healthcheck.mail;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ist.healthcheck.beans.Call;
import ist.healthcheck.beans.Pair;
import ist.healthcheck.beans.SMSConfiguration;



/**
 * this class used to send health check SMS messages.
 * @author Mohammed Samir
 *
 */
public class SendSMS {
	private SMSConfiguration configuration;

	public SendSMS(SMSConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	/**
	 * send health check SMS message
	 * @param interval interval date
	 * @param calls calls historical data
	 * @param callTypes the name of each KPI
	 * @param actuals calls data for a specific interval
	 * @param send
	 * @param accountName
	 * @param intervalName 
	 */
	public void sendHealthCheckSMS(String interval, Call[] calls, ArrayList<String> callTypes,
			Pair<String, Integer>[] actuals, boolean send, String accountName, String intervalName) {
		if (!send && configuration.isSendOnlyIfKPISBelowAverage())
			return;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(accountName + " - (Ist Health Check Tool) - " + intervalName + "\n");
			buffer.append(interval + "\n");
			for (int i = 0; i < configuration.getIconKPIs().length; i++) {
				if (configuration.getIconKPIs()[i])
					buffer.append(callTypes.get(i) + " : " + actuals[i].first + "\n");
			}
			for (int i = 0; i < configuration.getSpeechMinerKPIs().length; i++) {
				if (configuration.getSpeechMinerKPIs()[i])
					buffer.append(callTypes.get(i + 5) + " : " + actuals[i + 5].first + "\n");
			}

			URL url = new URL(configuration.getServiceURL());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod(configuration.getMethod());
			connection.setRequestProperty("X-Version", "1");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", configuration.getToken());
			connection.setRequestProperty("Accept", "application/json");

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("text", buffer.toString());
			jsonObject.put("to", new JSONArray(configuration.getReceivers().split(";")));
			String jsonString = jsonObject.toString();
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(jsonString.getBytes());
			outputStream.flush();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {
				System.out.println("Http Response Erro Code : " + connection.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SMSConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SMSConfiguration configuration) {
		this.configuration = configuration;
	}

}
