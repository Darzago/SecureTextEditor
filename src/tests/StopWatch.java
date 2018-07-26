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
		this.lastTime = System.currentTimeMillis();	
	}
	
	/**
	 * Returns the time difference from the last stopped time in s
	 * @return stopped time in s
	 */
	public float getTimeInSec()
	{
		return (System.currentTimeMillis() - lastTime)/1000f;
	}
	
	/**
	 * Constructor
	 * Sets the last stopped time on creation
	 */
	public StopWatch() {
		this.lastTime = System.currentTimeMillis();
	}
}
