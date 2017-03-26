package ist.healthcheck.beans;

/**
 * this class contains historical data information about calls in a specific
 * interval
 * 
 * @author Mohammed Samir
 *
 */

public class Interval {

	private Call[] calls = new Call[8];

	public Call[] getCalls() {
		return calls;
	}

	public void setCalls(Call[] calls) {
		this.calls = calls;
	}

}
