package ist.healthcheck.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.print.attribute.SetOfIntegerSyntax;

import ist.healthcheck.beans.Call;
import ist.healthcheck.beans.Interval;
import ist.healthcheck.beans.Pair;


/**
 * this calls contains ICON needed Queries.
 * @author Mohammed Samir
 *
 */
public class ICON {
	private static DataSource dataSource = DataSource.getInstance("icon");
	private static final String DATE_TIME_TABLE = "IF OBJECT_ID('tempdb..#DATE_TABLE') IS NOT NULL DROP TABLE tempdb..#DATE_TABLE ; IF OBJECT_ID('tempdb..#DATE_TABLE') IS NULL BEGIN DECLARE @INTERVAL_COUNT INT =  0 ; create table #DATE_TABLE  (TIME_INTERVAL INT) ; WHILE @INTERVAL_COUNT < @NOI BEGIN; INSERT INTO #DATE_TABLE VALUES (@INTERVAL_COUNT)  ; SET @INTERVAL_COUNT  = @INTERVAL_COUNT +1 ; END ; END  ";

	/**
	 * Return number of all calls entered the contact center.
	 * @param date
	 * @param minutes
	 * @return pair the number of in-bound and out-bound calls from date-minutes to date 
	 * @throws Exception
	 */
	
	public static Pair<Integer, Integer> getNumberOfCalls(String date, int minutes) throws Exception {
		int in = 0, out = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT CALLTYPE , COUNT(CALLID)AS NOC FROM G_CALL WHERE CREATED BETWEEN DATEADD(MINUTE , ? , ?) AND ? GROUP BY CALLTYPE");

		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			if (resultSet.getInt(1) == 2) {
				in = resultSet.getInt(2);
			} else if (resultSet.getInt(1) == 3) {
				out = resultSet.getInt(2);
			}
		}
		resultSet.close();
		connection.close();

		return new Pair<Integer, Integer>(in, out);
	}
	
	/**
	 * Return the number of the virtual queue calls 
	 * @param date
	 * @param minutes
	 * @return pair the number of normal and abandoned calls from date-minutes to date
	 * @throws Exception
	 */

	public static Pair<Integer, Integer> getNumberOfCallsVirtualQueue(String date, int minutes) throws Exception {
		int normal = 0, abandoned = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT CAUSE , COUNT(DISTINCT DISTCALLID) FROM G_VIRTUAL_QUEUE WHERE CREATED BETWEEN DATEADD(MINUTE , ? , ?) AND ?  GROUP BY CAUSE");

		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);

		ResultSet resultSet = preparedStatement.executeQuery();
		while (resultSet.next()) {
			if (resultSet.getInt(1) == 1) {
				normal = resultSet.getInt(2);
			} else if (resultSet.getInt(1) == 2) {
				abandoned = resultSet.getInt(2);
			}
		}
		resultSet.close();
		connection.close();

		return new Pair<Integer, Integer>(normal, abandoned);
	}
	
	/**
	 * Return the number of routed calls
	 * 
	 * @param date
	 * @param minutes
	 * @return integer the number of calls from date-minutes to date
	 * @throws Exception
	 */

	public static int getNumberOFCallsRoute(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT COUNT(DISTINCT CALLID)  FROM G_ROUTE_RESULT WHERE CREATED BETWEEN DATEADD(MINUTE , ? , ? ) AND ? AND RTARGETAGENTSELECTED <> '' ");

		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);

		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		connection.close();
		return numberOfCalls;
	}

  /**
   * Return the number of answered calls 
   * @param date
   * @param minutes
   * @return integer the number of calls from date-minutes to date
   * @throws Exception
   */
	public static int getNumberOfCallsRouteAgent(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"WITH ANSTOT AS (SELECT DISTINCT CALLID  FROM  G_PARTY WHERE  CREATED BETWEEN DATEADD(MINUTE , ? , ? ) AND ? AND AGENTID != 0 AND  CALLID IN (SELECT CALLID FROM G_CALL WHERE CALLTYPE = 2)) SELECT COUNT (CALLID) FROM ANSTOT WHERE CALLID NOT IN (SELECT CALL_2 FROM (SELECT CALL_1 , CALL_2 FROM (SELECT LINKID , MAX(CALLID) AS CALL_1 FROM G_IS_LINK GROUP BY LINKID) G1 INNER JOIN (SELECT LINKID , MIN(CALLID) AS CALL_2 FROM G_IS_LINK GROUP BY LINKID) G2 ON G1.LINKID = G2.LINKID)AS T WHERE T.CALL_1 IN (SELECT CALLID FROM ANSTOT))");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);

		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		connection.close();
		return numberOfCalls;
	}
	/***
	 * Return the number of reached out-bound calls
	 * @param date
	 * @param minutes
	 * @return integer the number of calls from date-minutes to date
	 * @throws Exception
	 */

	public static int getNumberOfReachedOutboundCalls(String date, int minutes) throws Exception {
		int numberOfCalls = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"select count(distinct CALLID) from G_PARTY where len(ENDPOINTDN) > 4 and CREATED between DATEADD(MINUTE , ? , ? )  and ? and len(ENDPOINTDN) = 15 and CALLID in (select CALLID from G_CALL where CALLTYPE = 3) and CALLID in (select CALLID from G_CALL_STAT where F_CONN = 1)");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next()) {
			numberOfCalls = resultSet.getInt(1);
		}
		resultSet.close();
		connection.close();
		return numberOfCalls;
	}
	/**
	 * Return the number of accepted campaigns calls 
	 * @param date
	 * @param minutes
	 * @return integer the number of calls from date-minutes to date
	 * @throws Exception
	 */

	public static int getNumberOfAcceptedCallsCampaign(String date, int minutes) throws Exception {
		int numberOfCall = 0;
		Connection connection = dataSource.getConnection();
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT COUNT (DISTINCT CHAINGUID) FROM GO_CHAIN WHERE CALLRESULT = 33  AND CREATED BETWEEN DATEADD(MINUTE , ? , ?  ) AND ? ");
		preparedStatement.setInt(1, minutes * -1);
		preparedStatement.setString(2, date);
		preparedStatement.setString(3, date);
		ResultSet resultSet = preparedStatement.executeQuery();
		if (resultSet.next())
			numberOfCall = resultSet.getInt(1);

		resultSet.close();
		connection.close();
		return numberOfCall;
	}

	private static void initCalls30MinutesInterval(ResultSet resultSet, Interval[] intervals, int type)
			throws Exception {
		int index = 0;
		while (resultSet.next()) {
			if (intervals[index] == null)
				intervals[index] = new Interval();
			intervals[index].getCalls()[type] = new Call(resultSet.getInt(2), resultSet.getInt(4), resultSet.getInt(3));
			++index;
		}

		if (index != intervals.length)
			throw new Exception("No Historical Data Found ");

	}

	public static void getCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes, int numberOfIntervals,
			int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ " DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1  , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE , MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN   FROM (SELECT COUNT(CALLID)AS CCOUNT  ,(DATEDIFF(MINUTE ,@LAST_WEEK ,CREATED)/@NOM) AS TIMEINTERVAL FROM G_CALL GROUP BY (DATEDIFF(MINUTE , @LAST_WEEK ,CREATED)/@NOM)) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND  DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI ) AS I  RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL ";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);

		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 0);
		resultSet.close();
		preparedStatement.close();
		statement.close();
		connection.close();

	}

	public static void getVirtualQueueCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes,
			int numberOfIntervals, int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1 , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE,  MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN FROM (SELECT COUNT(DISTINCT DISTCALLID)AS CCOUNT  ,(DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM) AS TIMEINTERVAL FROM G_VIRTUAL_QUEUE GROUP BY (DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM)) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI) AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 1);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();
	}

	public static void getRoutedResultXMinutesInterval(Interval[] intervals, int numberOfMinutes, int numberOfIntervals,
			int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1 , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE ,MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN  FROM (SELECT COUNT(DISTINCT CALLID)AS CCOUNT  ,(DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM) AS TIMEINTERVAL FROM G_ROUTE_RESULT WHERE RTARGETAGENTSELECTED <> '' GROUP BY (DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM)) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND  DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI) AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 2);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();

	}

	public static void getReachedCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes, int numberOfIntervals,
			int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1  , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; WITH ANSTOT AS (SELECT CALLID , CREATED  FROM  G_PARTY WHERE  AGENTID != 0 AND  CALLID IN (SELECT CALLID FROM G_CALL WHERE CALLTYPE = 2)) SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE ,MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN FROM (SELECT COUNT(DISTINCT CALLID)AS CCOUNT  ,(DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM) AS TIMEINTERVAL FROM ANSTOT WHERE CALLID NOT IN (SELECT CALL_2 FROM (SELECT CALL_1 , CALL_2 FROM (SELECT LINKID , MAX(CALLID) AS CALL_1 FROM G_IS_LINK GROUP BY LINKID) G1 INNER JOIN (SELECT LINKID , MIN(CALLID) AS CALL_2 FROM G_IS_LINK GROUP BY LINKID) G2 ON G1.LINKID = G2.LINKID)AS T WHERE T.CALL_1 IN (SELECT CALLID FROM ANSTOT)) GROUP BY (DATEDIFF(MINUTE , @LAST_WEEK,CREATED)/@NOM)) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI) AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 3);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();

	}

	public static void getOutboundReachedCallsXMinutesInterval(Interval[] intervals, int numberOfMinutes,
			int numberOfIntervals, int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ; SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS  * -1 , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ;  SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE , MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN  FROM (select count(distinct CALLID)AS CCOUNT , DATEDIFF(MINUTE , @LAST_WEEK , CREATED)/@NOM AS TIMEINTERVAL from G_PARTY where len(ENDPOINTDN) > 4 and len(ENDPOINTDN) = 15 and CALLID in (select CALLID from G_CALL where CALLTYPE = 3) and CALLID in (select CALLID from G_CALL_STAT where F_CONN = 1) GROUP BY DATEDIFF(MINUTE , @LAST_WEEK , CREATED)/@NOM) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI) AS I RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);
		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 4);
		resultSet.close();
		statement.close();
		preparedStatement.close();
		connection.close();
	}

	public static void getCallsXminutesIntervalCampaign(Interval[] intervals, int numberOfMinutes,
			int numberOfIntervals, int numberOfWeeks) throws Exception {
		Connection connection = dataSource.getConnection();
		final String query = "DECLARE @NOM INT = ? , @NOI INT = ? , @NUM_WEEKS INT = ? ; "
				+ "DECLARE @LAST_WEEK DATETIME  ;  SET @LAST_WEEK =  DATEADD(WEEK , @NUM_WEEKS * -1  , DATEADD(DAY, 0, DATEDIFF(DAY, 0, GETUTCDATE()))) ; SELECT DT.TIME_INTERVAL , COALESCE(I.MAX ,0) AS MAX , COALESCE(I.AVG,0) AS AVG , COALESCE(I.MIN,0) AS MIN FROM (SELECT C.TIMEINTERVAL%@NOI AS DATE , MAX(C.CCOUNT)AS MAX , AVG(C.CCOUNT)AS AVG  , MIN(C.CCOUNT) AS MIN  FROM (SELECT COUNT(CHAINGUID)AS CCOUNT  ,(DATEDIFF(MINUTE ,@LAST_WEEK ,CREATED)/@NOM) AS TIMEINTERVAL FROM GO_CHAIN WHERE CALLRESULT=33 GROUP BY (DATEDIFF(MINUTE , @LAST_WEEK ,CREATED)/@NOM)) AS C WHERE C.TIMEINTERVAL BETWEEN 0 AND  DATEDIFF(MINUTE , @LAST_WEEK , DATEADD(WEEK , @NUM_WEEKS , @LAST_WEEK))/@NOM GROUP BY C.TIMEINTERVAL%@NOI ) AS I  RIGHT JOIN #DATE_TABLE AS DT ON I.DATE = DT.TIME_INTERVAL ORDER BY DT.TIME_INTERVAL";
		Statement statement = connection.createStatement();
		statement.executeUpdate(String.format("DECLARE @NOI INT = %d ; " + DATE_TIME_TABLE, numberOfIntervals));
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, numberOfMinutes);
		preparedStatement.setInt(2, numberOfIntervals);
		preparedStatement.setInt(3, numberOfWeeks);

		ResultSet resultSet = preparedStatement.executeQuery();
		initCalls30MinutesInterval(resultSet, intervals, 5);
		resultSet.close();
		preparedStatement.close();
		statement.close();
		connection.close();

	}

}
