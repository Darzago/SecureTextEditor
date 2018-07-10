package tests;

public class StopWatch {
	
	private long lastTime;
	
	public void stopAndStart()
	{
		this.lastTime = System.nanoTime();	
	}
	
	public float getTimeInSec()
	{
		return (System.nanoTime() - lastTime)/1000000000f;
	}
	
	public StopWatch() {
		this.lastTime = System.nanoTime();
	}
}
