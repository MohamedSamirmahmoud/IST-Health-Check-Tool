package ist.healthcheck.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.dbcp2.BasicDataSource;

import ist.healthcheck.beans.GeneralConfiguration;

/**
 * this class provides connections to database.
 * @author Mohammed Samir
 *
 */

public class DataSource {
	private static Map<String, DataSource> dataSources = new TreeMap<String, DataSource>();
	private BasicDataSource basicDataSource;
/**
 * 
 * @param serverIP 
 * @param portNumber  
 * @param userName
 * @param password
 * @param databaseName
 */
	private DataSource(String serverIP, int portNumber, String userName, String password, String databaseName) {
		basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		basicDataSource.setUsername(userName);
		basicDataSource.setPassword(password);
		basicDataSource.setUrl("jdbc:sqlserver://" + serverIP + ":" + portNumber + ";databaseName=" + databaseName+ ";");
	}
	
	/**
	 * 
	 * @param configuration this variable contains General Configuration to configure data source.
	 */

	public static void setDatabaseConfiguration(GeneralConfiguration configuration) {
		dataSources.put("icon", new DataSource(configuration.getServerIP(), configuration.getPortNumber(),
				configuration.getUserName(), configuration.getPassword(), configuration.getIconDatabaseName()));
		
		dataSources.put("gim", new DataSource(configuration.getServerIP(), configuration.getPortNumber(),
				configuration.getUserName(), configuration.getPassword(), configuration.getGimDatabaseName()));
		
		if(!configuration.getSpeechMinorDatabaseName().equals(""))
		dataSources.put("speechminer", new DataSource(configuration.getServerIP(), configuration.getPortNumber(),
				configuration.getUserName(), configuration.getPassword(), configuration.getSpeechMinorDatabaseName()));

	}
	
	public static DataSource getInstance(String databaseName) {
		return dataSources.get(databaseName);
	}

	public Connection getConnection() throws SQLException {
		return this.basicDataSource.getConnection();
	}

}
