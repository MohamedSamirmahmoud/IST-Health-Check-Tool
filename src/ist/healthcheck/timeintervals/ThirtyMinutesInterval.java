package ist.healthcheck.timeintervals;

import java.text.SimpleDateFormat;

import java.util.TimeZone;


/**
 * this class contains thirty minutes interval information.
 * @author Mohammed Samir
 *
 */
public class ThirtyMinutesInterval extends DateInterval {

	private int localHours;
	private int localMinutes;
	private int hoursUTC;
	private int minutesUTC;

	public ThirtyMinutesInterval() {
		super(30, 48, 1, "ThirtyMinutesIntervalKPIs.json", "30 Minute Interval");
	}

	@Override
	public String getIntervalString() {
		String interval = "Interval : (from "
				+ ((date.getMinutes() == 30) ? (date.getHours() == 0 ? "00" : date.getHours()) + ":00"
						: (((date.getHours() == 0 ? 24 : date.getHours()) - 1) == 0 ? "00"
								: (date.getHours() == 0 ? 24 : date.getHours()) - 1) + ":30")
				+ " to "
				+ ((date.getHours() == 0 ? "00" : date.getHours()) + ":" + (date.getMinutes() == 0 ? "00" : "30"))
				+ ")";
		return interval;
	}

	@Override
	public int getIntervalIndex() {
		int intervalIndex = (Integer.valueOf(hoursUTC) * 2 + (Integer.valueOf(minutesUTC) / 30));
		intervalIndex = (48 + (intervalIndex - 1)) % 48;
		return intervalIndex;
	}

	@Override
	public void compile() {
		localHours = date.getHours();
		localMinutes = date.getMinutes();
		date.setSeconds(0);
		if (date.getMinutes() >= 30)
			date.setMinutes(30);
		else
			date.setMinutes(0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		format = sdf.format(date);
		String[] hms = format.split(" ")[1].split(":");
		hoursUTC = Integer.valueOf(hms[0]);
		minutesUTC = Integer.valueOf(hms[1]);
	}

	@Override
	public int delay() {
		return Math.abs(localMinutes > 35 ? 65 - localMinutes : 35 - localMinutes);
	}

}
