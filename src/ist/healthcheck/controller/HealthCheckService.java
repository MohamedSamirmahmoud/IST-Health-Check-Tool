package ist.healthcheck.controller;

import java.util.List;

import java.util.Timer;

import ist.healthcheck.timeintervals.DayInterval;
import ist.healthcheck.timeintervals.ThirtyMinutesInterval;

/**
 * this class is the main entrance of the application and where the interval services are registered.
 * @author Mohammed Samir
 *
 */
public class HealthCheckService {

	private static Timer timer = new Timer();

	public static void start(String[] args) {
		HealthCheckSchedular thirtyMinute = new HealthCheckSchedular(timer, new ThirtyMinutesInterval());
//		HealthCheckSchedular hour = new HealthCheckSchedular(timer, new HourInterval());
		 HealthCheckSchedular day = new HealthCheckSchedular(timer, new DayInterval());
	}

	public static void stop(String[] args) {
		timer.cancel();
	}

	public static void main(String[] args) {
		start(args);
	}

}
