package ist.healthcheck.beans;

/**
 * this class contains information about mail configuration needed information
 * sender email, sender password , receivers, icons KPIs, speechminer KPIs, send
 * mail if only the KPIs below average
 * 
 * @author Mohammed Samir
 *
 */

public class MailConfiguration {
	private String senderEmail;
	private String password;
	private String receivers;
	private boolean[] iconKPIs;
	private boolean[] speechMinerKPIs;
	private boolean sendOnlyIfKPISBelowAverage;

	public MailConfiguration() {
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public MailConfiguration(String senderEmail, String password, String receivers, boolean[] iconKPIs,
			boolean[] speechMinerKPIs, boolean sendOnlyIfKPISBelowAverage) {
		super();
		this.senderEmail = senderEmail;
		this.password = password;
		this.receivers = receivers;
		this.setIconKPIs(iconKPIs);
		this.speechMinerKPIs = speechMinerKPIs;
		this.sendOnlyIfKPISBelowAverage = sendOnlyIfKPISBelowAverage;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	public boolean isSendOnlyIfKPISBelowAverage() {
		return sendOnlyIfKPISBelowAverage;
	}

	public void setSendOnlyIfKPISBelowAverage(boolean sendOnlyIfKPISBelowAverage) {
		this.sendOnlyIfKPISBelowAverage = sendOnlyIfKPISBelowAverage;
	}

	public boolean[] getIconKPIs() {
		return iconKPIs;
	}

	public void setIconKPIs(boolean[] iconKPIs) {
		this.iconKPIs = iconKPIs;
	}

	public boolean[] getSpeechMinerKPIs() {
		return speechMinerKPIs;
	}

	public void setSpeechMinerKPIs(boolean[] speechMinerKPIs) {
		this.speechMinerKPIs = speechMinerKPIs;
	}

}
