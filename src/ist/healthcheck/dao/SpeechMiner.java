package ist.healthcheck.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import ist.healthcheck.beans.Call;
import ist.healthcheck.beans.Interval;

public class SpeechMiner {
	private static DataSource dataSource = DataSource.getInstance("speechminer");
	private static final String DATE_TIME_TABLE = "IF OBJECT_ID('tempdb..#DATE_TABLE') IS NOT NULL DROP TABLE tempdb..#DATE_TABLE ; IF OBJECT_ID('tempdb..#DATE_TABLE') IS NULL BEGIN DECLARE @INTERVAL_COUNT INT =  0 ; create table #DATE_TABLE  (TIME_INTERVAL INT) ; WHILE @INTERVAL_COUNT < @NOI BEGIN; INSERT INTO #DATE_TABLE VALUES (@INTERVAL_COUNT)  ; SET @INTERVAL_COUNT  = @INTERVAL_COUNT +1 ; END ; END";

	/**
	 * Return the number of Submitted Calls
	 * 
	 * @param date
	 * @param minutes
	 * @return integer the number of calls from date-minutes to date
	 */

	public static int getSubmittedCalls(String date, int minutes) {
		if (dataSource == null)
			return 0;
		int numberOfCalls = 0;
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
					"SELECT COUNT(callId) AS Number_Of_Calls FROM callMetaTbl WHERE callTime BETWEEN DATEDIFF(SECOND , '1970-01-01 00:00:00' , DATEADD(MINUTE , ? , ?) ) AND DATEDIFF(SECOND , '1970-01-01 00:00:00' , ?  )");
			preparedStatement.setInt(1, minutes * -1);
			preparedStatement.setString(2, date);
			preparedStatement.setString(3, date);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				numberOfCalls = resultSet.getInt(1);
			}
			resultSet.close();
			connection.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberOfCalls;
	}

	/**
	 * Return number of played calls.
	 * 
	 * @param date
	 * @param minutes
	 * @return integer number of calls from date-minutes to date
	 */
	public static int getPlayedCalls(String date, int minutes) {
		if (dataSource == null)
			return 0;
		int numberOfCalls = 0;
		try {
			Connection connection = dataSource.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
					"SELECT COUNT(*) AS NUMBER_OF_PLAY FROM DBO.View_UserActions WHERE eventTime BETWEEN DATEADD(MINUTE , ? , ?) AND ?  AND comments LIKE 'Play call%'");
			preparedStatement.setInt(1, minutes * -1);
			preparedStatement.setString(2, date);
			preparedStatement.setString(3, date);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				numberOfCalls = resultSet.getInt(1);
			}
			resultSet.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberOfCalls;
	}

	private static void initCallsXMinutesInterval(ResultSet resultSet, Interval[] intervals, int type)
			throws Exception {
		int index = 0;
		while (resultSet.next()) {
			intervals[index].getCalls()[type] = new Call(resultSet.getInt(2), resultSet.getInt(4), resultSet.getInt(3));
			++index;
		}
		// System.out.println(index + " " + intervals.length);
		if (index < intervals.length)
			throw new Exception("No Historical Data Found");

	}

	public static void getSubmittedCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes,
			int numberOfIntervals, int numberOfWeeks) throws Exception {
		if (dataSource == null)
			return;
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1, DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT A.CINTERVAL%@NOI AS DATE , MAX(A.CC) AS MAX , AVG(A.CC) AS AVG , MIN(A.CC) AS MIN  FROM(SELECT COUNT(*) AS CC , (callTime - DATEDIFF(SECOND ,'1970-01-01 00:00:00' ,@LAST_WEEK))/(60*@NOM) AS CINTERVAL  FROM callMetaTbl GROUP BY (callTime - DATEDIFF(SECOND ,'1970-01-01 00:00:00' ,@LAST_WEEK))/(60*@NOM) ) AS A WHERE A.CINTERVAL BETWEEN 0 AND DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY A.CINTERVAL%@NOI) AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCallsXMinutesInterval(resultSet, intervals, 6);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();

	}

	public static void getPlayedCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes, int numberOfIntervals,
			int numberOfWeeks) throws Exception {
		if (dataSource == null)
			return;
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1 , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ;  SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT A.CINTERVAL%@NOI AS DATE , MAX(A.CCOUNT) AS MAX , AVG(A.CCOUNT)AS AVG , MIN(A.CCOUNT) AS MIN   FROM (SELECT COUNT(*) AS CCOUNT , DATEDIFF(MINUTE , @LAST_WEEK , EVENTTIME)/@NOM AS CINTERVAL FROM View_UserActions WHERE comments LIKE 'Play call%' GROUP BY  DATEDIFF(MINUTE , @LAST_WEEK , EVENTTIME)/@NOM) AS A WHERE A.CINTERVAL BETWEEN 0 AND DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY A.CINTERVAL%@NOI)AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCallsXMinutesInterval(resultSet, intervals, 7);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();

	}

}
