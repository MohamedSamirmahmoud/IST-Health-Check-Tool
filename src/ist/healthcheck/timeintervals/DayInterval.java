package ist.healthcheck.timeintervals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * this class contains Day interval information.
 * 
 * @author Mohammed Samir
 *
 */
public class DayInterval extends DateInterval {
	
	private int day;
	private final int startDay;
	private int localHours;
	private int localMinutes;

	public DayInterval() {
		super(1440, 7, 4, "DayIntervalKPIs.json", "Day Interval");
		startDay = date.getDay();
	}

	@Override
	public String getIntervalString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		String interval = String.format("Interval : ( From %s to %s )", sdf.format(calendar.getTime()),
				sdf.format(date));
		return interval;
	}

	@Override
	public int getIntervalIndex() {
		return (7 + ((day - 1) - startDay)) % 7;
	}

	@Override
	public void compile() {
		localHours = date.getHours();
		localMinutes = date.getMinutes();
		day = date.getDay();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		format = sdf.format(date);
	}

	@Override
	public int delay() {
		return ((25 - localHours) * 60) - localMinutes;
	}

}
