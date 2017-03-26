package ist.healthcheck.controller;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;

import ist.healthcheck.beans.Call;
import ist.healthcheck.beans.GeneralConfiguration;
import ist.healthcheck.beans.Interval;
import ist.healthcheck.beans.MailConfiguration;
import ist.healthcheck.beans.Pair;
import ist.healthcheck.beans.SMSConfiguration;
import ist.healthcheck.dao.DataSource;
import ist.healthcheck.dao.GIM;
import ist.healthcheck.dao.ICON;
import ist.healthcheck.dao.SpeechMiner;
import ist.healthcheck.files.GeneralConfiguratuionFile;
import ist.healthcheck.files.JsonFile;
import ist.healthcheck.files.MailConfigurationFile;
import ist.healthcheck.files.SMSConfigurationFile;
import ist.healthcheck.mail.SendMail;
import ist.healthcheck.mail.SendSMS;
import ist.healthcheck.timeintervals.ThirtyMinutesInterval;
import ist.healthcheck.timeintervals.DateInterval;


/**
 * this class used to construct calls information according to a specific interval.
 * also this class runs periodically after interval expiration period.
 * @author Mohammed Samir
 *
 */
public class HealthCheckInterval extends TimerTask {

	private Interval[] intervals;
	private Pair<String, Integer>[] actuals = new Pair[8];
	private ArrayList<String> callTypes = new ArrayList<>();
	private boolean sendSMSBelowAverage = false;
	private boolean sendMailBelowAverage = false;
	private SendMail sendMail;
	private SendSMS sendSMS;
	private String accountName;
	private DateInterval dateInterval;

	public HealthCheckInterval(DateInterval dateInterval) throws FileNotFoundException {
		callTypes.add("Calls");
		callTypes.add("Received Calls");
		callTypes.add("Trails");
		callTypes.add("Answered Calls");
		callTypes.add("Reached Outbound Calls");
		callTypes.add("Answered Campaign Calls");
		callTypes.add("Submitted Calls");
		callTypes.add("Played Calls");

		this.dateInterval = dateInterval;
		GeneralConfiguration generalConfiguration = GeneralConfiguratuionFile.readConfiguration();
		MailConfiguration mailConfiguration = MailConfigurationFile.readConfiguration();
		SMSConfiguration smsConfiguration = SMSConfigurationFile.readConfiguration();

		if (generalConfiguration == null)
			throw new FileNotFoundException("Database Configuration File Not Found");
		if (mailConfiguration == null && smsConfiguration == null)
			throw new FileNotFoundException("There is No Mail Configuration File Nor Sms Configuration File");

		DataSource.setDatabaseConfiguration(generalConfiguration);
		accountName = generalConfiguration.getAccountName();

		if (mailConfiguration != null)
			this.setSendMail(new SendMail(mailConfiguration));
		if (smsConfiguration != null)
			this.setSendSMS(new SendSMS(smsConfiguration));

	}

	public void execute() throws Exception {

		dateInterval.setDate(new Date());
		final String format = dateInterval.getDateUTC();
		Pair<Integer, Integer> inOut = ICON.getNumberOfCalls(format, dateInterval.minutes);
		int buffer = (inOut.first + inOut.second);
		for (int i = 0; i < actuals.length; i++)
			actuals[i] = new Pair<String, Integer>();
		actuals[0].first = String.format("(inbound :  %d, Outbound : %d ) = %d ", inOut.first, inOut.second, buffer);
		int intervalIndex = dateInterval.getIntervalIndex();
		Call[] calls = intervals[intervalIndex].getCalls();
		actuals[0].second = buffer < calls[0].getMin() ? 0 : (calls[0].getAverage() > buffer ? 1 : 2);
		Pair<Integer, Integer> normalAbandand = ICON.getNumberOfCallsVirtualQueue(format, dateInterval.minutes);
		buffer = normalAbandand.first + normalAbandand.second;
		int numberOfClearedCalls = GIM.getNumberOfClearedCalls(format, dateInterval.minutes);
		actuals[1].first = String.format("(Normal : %d, Abandand : %d, Cleared : %d ) = %d ",
				normalAbandand.first - numberOfClearedCalls, normalAbandand.second, numberOfClearedCalls, buffer);
		actuals[1].second = (calls[1].getMin() > buffer ? 0 : (calls[1].getAverage() > buffer ? 1 : 2));
		buffer = ICON.getNumberOFCallsRoute(format, dateInterval.minutes);
		actuals[2].first = String.valueOf(buffer);
		actuals[2].second = (calls[2].getMin() > buffer ? 0 : (calls[2].getAverage() > buffer ? 1 : 2));
		buffer = ICON.getNumberOfCallsRouteAgent(format, dateInterval.minutes);
		int abandonedInvite = GIM.getNumberOfcallsAbandonedInviteInbound(format, dateInterval.minutes);
		actuals[3].first = String.format("( Answered Successfully : %d ,  Abandoned Ringing : %d ) = %d ",
				buffer - abandonedInvite, abandonedInvite, buffer);
		actuals[3].second = (calls[3].getMin() > buffer ? 0 : (calls[3].getAverage() > buffer ? 1 : 2));
		buffer = ICON.getNumberOfReachedOutboundCalls(format, dateInterval.minutes);
		abandonedInvite = GIM.getNumberOfcallsAbandonedInviteOutbound(format, dateInterval.minutes);
		actuals[4].first = String.format("( Answered Successfully : %d ,  Abandoned Ringing : %d ) = %d ", buffer,
				abandonedInvite, buffer + abandonedInvite);
		actuals[4].second = (calls[4].getMin() > buffer ? 0 : (calls[4].getAverage() > buffer ? 1 : 2));

		buffer = ICON.getNumberOfAcceptedCallsCampaign(format, dateInterval.minutes);
		actuals[5].first = String.valueOf(buffer);
		actuals[5].second = (calls[5].getMin() > buffer ? 0 : (calls[5].getAverage() > buffer ? 1 : 2));

		buffer = SpeechMiner.getSubmittedCalls(format, dateInterval.minutes);
		actuals[6].first = String.valueOf(buffer);
		actuals[6].second = (calls[6].getMin() > buffer ? 0 : (calls[6].getAverage() > buffer ? 1 : 2));
		buffer = SpeechMiner.getPlayedCalls(format, dateInterval.minutes);
		actuals[7].first = String.valueOf(buffer);
		actuals[7].second = (calls[7].getMin() > buffer ? 0 : (calls[7].getAverage() > buffer ? 1 : 2));

		String intervalString = dateInterval.getIntervalString();

		if (sendMail != null) {
			for (int i = 0; i < sendMail.getConfiguration().getIconKPIs().length; i++) {
				if (sendMail.getConfiguration().getIconKPIs()[i])
					if (actuals[i].second == 0 || actuals[i].second == 1)
						sendMailBelowAverage = true;
			}

			for (int i = 0; i < sendMail.getConfiguration().getSpeechMinerKPIs().length; i++) {
				if (sendMail.getConfiguration().getSpeechMinerKPIs()[i])
					if (actuals[i + 6].second == 0 || actuals[i + 6].second == 1)
						sendMailBelowAverage = true;
			}

			sendMail.sendMails(calls, callTypes, actuals, intervalString, sendMailBelowAverage, accountName,
					dateInterval.intervalName);
		}

		if (sendSMS != null) {
			for (int i = 0; i < sendSMS.getConfiguration().getIconKPIs().length; i++) {
				if (sendSMS.getConfiguration().getIconKPIs()[i])
					if (actuals[i].second == 0 || actuals[i].second == 1)
						sendSMSBelowAverage = true;
			}

			for (int i = 0; i < sendSMS.getConfiguration().getSpeechMinerKPIs().length; i++) {
				if (sendSMS.getConfiguration().getSpeechMinerKPIs()[i])
					if (actuals[i + 6].second == 0 || actuals[i + 6].second == 1)
						sendSMSBelowAverage = true;
			}

			sendSMS.sendHealthCheckSMS(intervalString, calls, callTypes, actuals, sendSMSBelowAverage, accountName,
					dateInterval.intervalName);

		}
		sendMailBelowAverage = false;
		sendSMSBelowAverage = false;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Interval[] getIntervals() {
		return intervals;
	}

	public void setInervals(Interval[] intervals) {
		this.intervals = intervals;
	}

	public SendMail getSendMail() {
		return sendMail;
	}

	public void setSendMail(SendMail sendMail) {
		this.sendMail = sendMail;
	}

	public SendSMS getSendSMS() {
		return sendSMS;
	}

	public void setSendSMS(SendSMS sendSMS) {
		this.sendSMS = sendSMS;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public DateInterval getDateInterval() {
		return dateInterval;
	}

	public void setDateInterval(DateInterval dateInterval) {
		this.dateInterval = dateInterval;
	}

}
