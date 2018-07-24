package tests;

/**
 * Stopwatch class for testing
 * @author Joel
 * 
 */
public class StopWatch {
	
	//Last stopped time
	private long lastTime;
	
	/**
	 * sets the last stopped time to the current time
	 */
	public void stopAndStart()
	{
		this.lastTime = System.nanoTime();	
	}
	
	/**
	 * Returns the time difference from the last stopped time in s
	 * @return stopped time in ss
	 */
	public float getTimeInSec()
	{
		return (System.nanoTime() - lastTime)/1000000000f;
	}
	
	/**
	 * Constructor
	 * Sets the last stopped time on creation
	 */
	public StopWatch() {
		this.lastTime = System.nanoTime();
	}
}
