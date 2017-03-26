package ist.healthcheck.beans;


/**
 * 
 * this class contains general information about 
 *  Database server IP,
 *  Database server port Number,
 *  ICON database name,
 *  SpeechMiner database name,
 *  GIM database name,
 *  option to enable mail sender,
 *  option to enable SMS sender,
 *  database username,
 *  database password and
 *  account name.
 *  
 *  @author Mohammed Samir
 * 
 */
public class GeneralConfiguration {
	private String serverIP;
	private int portNumber;
	private String iconDatabaseName;
	private String speechMinorDatabaseName;
	private String gimDatabaseName;
	private boolean enableMail;
	private boolean enableSMS;
	private String userName;
	private String password;
	private String accountName;

	public GeneralConfiguration() {
	}
	/**
	 * 
	 * @param serverIP database server IP address 
	 * @param portNumber database server port number
	 * @param iconDatabaseName ICOM database name
	 * @param speechMinorDatabaseName speech miner database name
	 * @param gimDatabaseName GIM database name
	 * @param enableMail option to enable mail sender
	 * @param enableSMS option to enable SMS sender
	 * @param userName database username
	 * @param password database password
	 * @param accountName 
	 */

	public GeneralConfiguration(String serverIP, int portNumber, String iconDatabaseName,
			String speechMinorDatabaseName, String gimDatabaseName, boolean enableMail, boolean enableSMS,
			String userName, String password, String accountName) {
		super();
		this.serverIP = serverIP;
		this.portNumber = portNumber;
		this.iconDatabaseName = iconDatabaseName;
		this.speechMinorDatabaseName = speechMinorDatabaseName;
		this.gimDatabaseName = gimDatabaseName;
		this.enableMail = enableMail;
		this.enableSMS = enableSMS;
		this.userName = userName;
		this.password = password;
		this.accountName = accountName;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getIconDatabaseName() {
		return iconDatabaseName;
	}

	public void setIconDatabaseName(String iconDatabaseName) {
		this.iconDatabaseName = iconDatabaseName;
	}

	public String getSpeechMinorDatabaseName() {
		return speechMinorDatabaseName;
	}

	public void setSpeechMinorDatabaseName(String speechMinorDatabaseName) {
		this.speechMinorDatabaseName = speechMinorDatabaseName;
	}

	public boolean isEnableMail() {
		return enableMail;
	}

	public void setEnableMail(boolean enableMail) {
		this.enableMail = enableMail;
	}

	public boolean isEnableSMS() {
		return enableSMS;
	}

	public void setEnableSMS(boolean enableSMS) {
		this.enableSMS = enableSMS;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getGimDatabaseName() {
		return gimDatabaseName;
	}

	public void setGimDatabaseName(String gimDatabaseName) {
		this.gimDatabaseName = gimDatabaseName;
	}

}
