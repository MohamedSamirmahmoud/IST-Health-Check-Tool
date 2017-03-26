package ist.healthcheck.timeintervals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * this class contains hour interval information
 * @author Mohammed Samir
 *
 */
public class HourInterval extends DateInterval {

	private int localMinutes;
	private int localHours;

	public HourInterval() {
		super(60, 24, 4, "HoursIntervalsKPIs.json", "Hour Interval");
	}

	@Override
	public String getIntervalString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:00");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, -1);
		String interval = String.format("Interval : ( From %s to %s )", simpleDateFormat.format(calendar.getTime()),
				simpleDateFormat.format(date));
		return interval;
	}

	@Override
	public int getIntervalIndex() {
		return (24 + (localHours - 1)) % 24;
	}

	@Override
	public void compile() {
		localMinutes = date.getMinutes();
		localHours = date.getHours();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		format = sdf.format(date);
	}

	@Override
	public int delay() {
		return 65 - localMinutes;
	}

}
