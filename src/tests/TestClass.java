package tests;

import logic.USBDetectionThread;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			Thread test = new USBDetectionThread();
			test.start();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
