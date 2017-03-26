package ist.healthcheck.beans;

import java.util.Date;
/**
 * this class encapsulate intervals historical data according to a specific time interval.
 * @author Mohammed Samir
 *
 */

public class IntervalsWithDate {
	private Interval[] intervals;
	private Date expirationDate;
	
	public IntervalsWithDate(){}

	public IntervalsWithDate(Interval[] intervals, Date date) {
		super();
		this.intervals = intervals;
		this.expirationDate = date;
	}

	public Interval[] getIntervals() {
		return intervals;
	}

	public void setIntervals(Interval[] intervals) {
		this.intervals = intervals;
	}

	public Date getDate() {
		return expirationDate;
	}

	public void setDate(Date date) {
		this.expirationDate = date;
	}

}
