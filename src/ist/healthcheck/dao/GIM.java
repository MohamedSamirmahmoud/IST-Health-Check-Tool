package ist.healthcheck.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * this class contains Genesys infomart needed Queries.
 * @author Mohammed Samir
 *
 */

public class GIM {
	private static DataSource dataSource = DataSource.getInstance("gim");

	/**
	 * Return Number Of abandoned invite in-bound calls
	 * @param date 
	 * @param minutes
	 * @return integer the number of Calls from date-minutes to date  
	 * @throws Exception
	 */
	public static int getNumberOfcallsAbandonedInviteInbound(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT COUNT(*) FROM MEDIATION_SEGMENT_FACT MSF INNER JOIN TECHNICAL_DESCRIPTOR TD  ON MSF.TECHNICAL_DESCRIPTOR_KEY = TD.TECHNICAL_DESCRIPTOR_KEY  INNER JOIN INTERACTION_TYPE IT ON IT.INTERACTION_TYPE_KEY  = MSF. INTERACTION_TYPE_KEY WHERE TD.RESULT_REASON_CODE =  'ABANDONEDWHILERINGING' AND INTERACTION_TYPE_CODE = 'INBOUND' AND START_TS BETWEEN DATEDIFF(SECOND , '1970-01-01 00:00:00' ,  DATEADD(MINUTE , ? , ? ) ) AND DATEDIFF(SECOND , '1970-01-01 00:00:00' ,  ? )");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		preparedStatement.close();
		connection.close();

		return numberOfCalls;
	}

	/**
	 * Return the number of abandoned invite out-bound calls 
	 * @param date
	 * @param minutes
	 * @return integer the number of abandoned invite out-bound calls from date-minutes to date 
	 * @throws Exception
	 */
	
	public static int getNumberOfcallsAbandonedInviteOutbound(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT COUNT(*) FROM MEDIATION_SEGMENT_FACT MSF INNER JOIN TECHNICAL_DESCRIPTOR TD  ON MSF.TECHNICAL_DESCRIPTOR_KEY = TD.TECHNICAL_DESCRIPTOR_KEY  INNER JOIN INTERACTION_TYPE IT ON IT.INTERACTION_TYPE_KEY  = MSF. INTERACTION_TYPE_KEY WHERE TD.RESULT_REASON_CODE =  'ABANDONEDWHILERINGING' AND INTERACTION_TYPE_CODE = 'OUTBOUND' AND START_TS BETWEEN DATEDIFF(SECOND , '1970-01-01 00:00:00' ,  DATEADD(MINUTE , ? , ? ) ) AND DATEDIFF(SECOND , '1970-01-01 00:00:00' ,  ? )");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		preparedStatement.close();
		connection.close();

		return numberOfCalls;
	}
	/**
	 * Return the number of cleared calls
	 * @param date
	 * @param minutes
	 * @return integer number of calls from date-minutes to date 
	 * @throws Exception
	 */

	public static int getNumberOfClearedCalls(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT COUNT(DISTINCT MEDIA_SERVER_IXN_GUID)  FROM MEDIATION_SEGMENT_FACT MSF JOIN TECHNICAL_DESCRIPTOR TD ON TD.TECHNICAL_DESCRIPTOR_KEY = MSF.TECHNICAL_DESCRIPTOR_KEY JOIN INTERACTION_TYPE IT ON IT.INTERACTION_TYPE_KEY = MSF.INTERACTION_TYPE_KEY AND  TD.RESOURCE_ROLE_CODE <> 'RECEIVEDCONSULT' AND IT.INTERACTION_SUBTYPE_CODE not in ('INTERNALCOLLABORATIONINVITE' , 'INTERNALCOLLABORATIONREPLY' , 'INBOUNDCOLLABORATIONREPLY' , 'OUTBOUNDCOLLABORATIONINVITE') and TD.TECHNICAL_RESULT_CODE in ('CLEARED' , 'ABNORMALSTOP') AND START_TS BETWEEN DATEDIFF(SECOND  , '1970-01-01 00:00:00' , DATEADD(MINUTE , ? , ?) )  AND DATEDIFF(SECOND  , '1970-01-01 00:00:00' ,  ?)");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		preparedStatement.close();
		connection.close();
		return numberOfCalls;
	}

}
