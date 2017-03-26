package ist.healthcheck.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ist.healthcheck.beans.Interval;
import ist.healthcheck.beans.IntervalsWithDate;
import ist.healthcheck.dao.ICON;
import ist.healthcheck.dao.SpeechMiner;
import ist.healthcheck.files.JsonFile;
import ist.healthcheck.timeintervals.DateInterval;
/**
 * this class used to construct calls historical data information and store it into KPIs files.
 * this class runs periodically according to historical data expiration date which specified in interval configuration object.
 * @author Mohammed Samir
 *
 */
public class HealthCheckSchedular extends TimerTask {

	private Interval[] intervals;
	private HealthCheckInterval healthCheckSchedular;

	public HealthCheckSchedular(Timer timer, DateInterval dateInterval) {

		try {
			healthCheckSchedular = new HealthCheckInterval(dateInterval);
			this.intervals = new Interval[dateInterval.intervals];
			timer.scheduleAtFixedRate(this, 0,
					TimeUnit.MILLISECONDS.convert(dateInterval.noWeeksHisData * 7, TimeUnit.DAYS));
			timer.scheduleAtFixedRate(healthCheckSchedular,
					TimeUnit.MILLISECONDS.convert(dateInterval.delay(), TimeUnit.MINUTES),
					TimeUnit.MILLISECONDS.convert(dateInterval.minutes, TimeUnit.MINUTES));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void execute() {
		try {
			IntervalsWithDate intervalsWithDate = JsonFile
					.readKPISJsonFile(healthCheckSchedular.getDateInterval().fileName);
			if (intervalsWithDate != null
					&& intervalsWithDate.getDate().after(healthCheckSchedular.getDateInterval().getDate())) {
				healthCheckSchedular.setInervals(intervalsWithDate.getIntervals());
				return;
			}
			int minutes = healthCheckSchedular.getDateInterval().minutes,
					interval = healthCheckSchedular.getDateInterval().intervals,
					numberOfWeeks = healthCheckSchedular.getDateInterval().noWeeksHisData;

			ICON.getCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			ICON.getVirtualQueueCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			ICON.getRoutedResultXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			ICON.getReachedCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			ICON.getOutboundReachedCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			ICON.getCallsXminutesIntervalCampaign(intervals, minutes, interval, numberOfWeeks);
			SpeechMiner.getSubmittedCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			SpeechMiner.getPlayedCallsXMinutesInterval(intervals, minutes, interval, numberOfWeeks);
			healthCheckSchedular.setInervals(intervals);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(healthCheckSchedular.getDateInterval().getDate());
			calendar.add(Calendar.DATE, healthCheckSchedular.getDateInterval().noWeeksHisData);
			JsonFile.writeKPIJsonFile(new IntervalsWithDate(intervals, calendar.getTime()),
					healthCheckSchedular.getDateInterval().fileName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void run() {
		execute();

	}

	public Interval[] getIntervals() {
		return intervals;
	}

}
