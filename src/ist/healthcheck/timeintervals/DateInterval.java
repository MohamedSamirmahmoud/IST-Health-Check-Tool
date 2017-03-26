package ist.healthcheck.timeintervals;

import java.util.Date;

/**
 * this class holds information according to interval.
 * @author Mohammed Samir
 *
 */

public abstract class DateInterval {
	protected Date date;
	public final int minutes;
	public final int intervals;
	public final int noWeeksHisData;
	protected String format;
	public final String fileName;
	public final String intervalName;

	public DateInterval(int minutes, int intervals, int noWeeksHisData, String fileName, String intervalName) {
		this.minutes = minutes;
		this.intervals = intervals;
		this.noWeeksHisData = noWeeksHisData;
		this.fileName = fileName;
		this.intervalName = intervalName;
		setDate(new Date());
	}

	public final String getDateUTC() {
		return format;
	}

	public abstract String getIntervalString();

	public abstract int getIntervalIndex();

	public abstract void compile();

	public abstract int delay();

	public final void setDate(Date date) {
		this.date = date;
		compile();
	}

	public Date getDate() {
		return date;
	}

}
