package ist.healthcheck.beans;

/**
 * this class contains information about SMS configuration needed information
 * service URL, token, method, sender password, receivers, icons KPIs, speechminer KPIs, send
 * mail if only the KPIs below average
 * 
 * @author Mohammed Samir
 *
 */
public class SMSConfiguration {
	private String serviceURL;
	private String token;
	private String method;
	private String receivers;
	private boolean sendOnlyIfKPISBelowAverage;
	private boolean[] iconKPIs;
	private boolean[] speechMinerKPIs;

	public SMSConfiguration() {
	}

	public SMSConfiguration(String serviceURL, String token, String method, String receivers,
			boolean sendOnlyIfKPISBelowAverage, boolean[] iconKPIs, boolean[] speechMinerKPIs) {
		super();
		this.serviceURL = serviceURL;
		this.token = token;
		this.method = method;
		this.receivers = receivers;
		this.sendOnlyIfKPISBelowAverage = sendOnlyIfKPISBelowAverage;
		this.setIconKPIs(iconKPIs);
		this.speechMinerKPIs = speechMinerKPIs;
	}

	public String getServiceURL() {
		return serviceURL;
	}

	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	

	public boolean[] getSpeechMinerKPIs() {
		return speechMinerKPIs;
	}

	public void setSpeechMinerKPIs(boolean[] speechMinerKPIs) {
		this.speechMinerKPIs = speechMinerKPIs;
	}

	public boolean[] getIconKPIs() {
		return iconKPIs;
	}

	public void setIconKPIs(boolean[] iconKPIs) {
		this.iconKPIs = iconKPIs;
	}

}
