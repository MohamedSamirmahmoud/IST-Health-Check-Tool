package ist.healthcheck.beans;

/**
 * this class contains call historical data information relative to specific interval.
 *  * @author Mohammed Samir
 */

public class Call {
	private int max;
	private int min;
	private int average;

	public Call(int max, int min, int average) {
		super();
		this.max = max;
		this.min = min;
		this.average = average;
	}
	public Call(){}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getAverage() {
		return average;
	}

	public void setAverage(int average) {
		this.average = average;
	}

	@Override
	public String toString() {
		return "Call [max=" + max + ", min=" + min + ", average=" + average + "]";
	}

}
