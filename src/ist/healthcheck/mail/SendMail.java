package ist.healthcheck.mail;

import java.util.ArrayList;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ist.healthcheck.beans.Call;
import ist.healthcheck.beans.MailConfiguration;
import ist.healthcheck.beans.Pair;



/**
 * this class used to send health check mail
 * @author Mohammed Samir
 *
 */
public class SendMail {
	private MailConfiguration configuration;

	public SendMail(MailConfiguration configuration) {
		super();
		this.configuration = configuration;
	}

	private void sendHealthCheckMail(Call[] calls, ArrayList<String> callTypes, Pair<String, Integer>[] actuals,
			String to, String name, String interval, String accountName, String intervalName) {

		String table = "<table><tr><th>KPI</th> <th>Max</th> <th>Average</th> <th>Min</th> <th>Actual</th> </tr>";
		for (int i = 0; i < configuration.getIconKPIs().length; i++) {
			if (configuration.getIconKPIs()[i]) {
				table += "<tr>" + "<td>" + callTypes.get(i) + "</td>" + "<td>" + calls[i].getMax() + "</td>" + "<td>"
						+ calls[i].getAverage() + "</td>" + "<td>" + calls[i].getMin() + "</td>"
						+ String.format("<td bgcolor='%s'>",
								actuals[i].second == 0 ? "#FC2E02" : (actuals[i].second == 1 ? "#FDF900" : "FFFFFF"))
						+ actuals[i].first + "</td>" + "</tr>";
			}
		}

		for (int i = 0; i < configuration.getSpeechMinerKPIs().length; i++) {
			if (configuration.getSpeechMinerKPIs()[i]) {
				int buffer = i;
				i = i + 6;
				table += "<tr>" + "<td>" + callTypes.get(i) + "</td>" + "<td>" + calls[i].getMax() + "</td>" + "<td>"
						+ calls[i].getAverage() + "</td>" + "<td>" + calls[i].getMin() + "</td>"
						+ String.format("<td bgcolor='%s'>",
								actuals[i].second == 0 ? "#FC2E02" : (actuals[i].second == 1 ? "#FDF900" : "FFFFFF"))
						+ actuals[i].first + "</td>" + "</tr>";
				i = buffer;
			}
		}

		table += "</table>";
		final String username = configuration.getSenderEmail() + "@gmail.com";
		final String password = configuration.getPassword();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.port", "465");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(configuration.getSenderEmail()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(accountName + " - (Ist Health Check Tool) - " + intervalName);
			message.setContent(
					"<html> <head><style>table { font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}td, th {border: 1px solid #dddddd;text-align: left; padding: 8px;}tr:nth-child(even) { background-color: #dddddd;}</style></head><body>"
							+ "<h1>Hello " + name + "</h1>" + "<h2>" + interval + "</h2>" + table + "</body> </html>",
					"text/html");

			Transport.send(message);
			System.out.println("Sent Mail To : " + to);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
   /**
    * send health check mail
    * @param calls calls historical data
    * @param callTypes the name of each KPI
    * @param actuals calls data for a specific interval
    * @param interval interval date
    * @param blowAverage is any of the KPIs below average
    * @param accountName
    * @param intervalName
    */
	public void sendMails(Call[] calls, ArrayList<String> callTypes, Pair<String, Integer>[] actuals, String interval,
			boolean blowAverage, String accountName, String intervalName) {
		String[] receivers = configuration.getReceivers().split(";");
		if (!configuration.isSendOnlyIfKPISBelowAverage() || blowAverage) {
			for (int i = 0; i < receivers.length; i++) {
				String to = receivers[i];
				sendHealthCheckMail(calls, callTypes, actuals, to, to.split("@")[0], interval, accountName,
						intervalName);
			}
		}

	}

	public MailConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MailConfiguration configuration) {
		this.configuration = configuration;
	}

}
