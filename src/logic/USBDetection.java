package logic;

import java.io.File;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javafx.application.Platform;

/**
 * Thread Class that is used to detect newly connected USB devices 
 * @author Joel
 *
 */
public class USBDetection extends Thread{
	
	//Array of all drive letters
	private String[] driveLetters;
	
	//File Objects list, per drive letter
	private File[] externalDrives;
	
	//state array, if a device was previously readable
	private boolean[] foundDrive;
	
	//Texteditor object to call back to
	private TextEditor callBackEditor;
	
	//Minimal detected device count
	private int minDeviceCount = Integer.MAX_VALUE;
	
	//count of currently connected usb devices
	private int currentDeviceCount;
	
	//List of minimal connected devices
	private int[] minDeviceList;
	
	//Running flag, if set to false, the thread will die
	//Used to stop the thread from the outside
	private boolean runningFlag = true;
	
	public void run() {
    	
    	String driveLetter;
    	
    	//While the running flag is set
    	while(runningFlag)
    	{
    		//Get the current number of connected devices
    		currentDeviceCount = getCurrentDeviceCount();
    		
    		//If the device count is smaller than the minimal device count since the start of the thread
    		if(currentDeviceCount < minDeviceCount)
    		{
    			//Set the new minimum
    			minDeviceCount = currentDeviceCount;
    			//set the minimum list
    			minDeviceList = getUSBList();
    		}
    		
    		//get the drive letter of a newly connected device
    		driveLetter = detectUSBDriveLetter();
    		
    		//if there is a driveletter aka a new device connected
    		if(driveLetter != null)
    		{
    			//Register the new device by saving its hash/Id and the corresponding drive letter
    			registerUSBDevice(getNewDeviceID() , driveLetter);
    			
    			//Stop the thread from running by switching the 'running' flag
    			this.runningFlag = false;
    		}
    		
    		try 
    		{
    			//Wait for a bit because it would be unnecessary to check each 'frame'.
				sleep(500);
			} 
    		catch (InterruptedException e) 
    		{
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Returns the drive letter of a newly connected device
     * @return drive letter of the newly connected drive
     */
	public String detectUSBDriveLetter()
	{
		//Loops through all available drive letters
		for ( int i = 0; i < driveLetters.length; ++i )
		{
			//Return if the device which belongs to the letter can be read
			boolean pluggedIn = externalDrives[i].canRead();
			 
			// if the state has changed
			if ( pluggedIn != foundDrive[i] )
			{
				//If the changed state is 'pluggedIn = true', return the corresponding letter
				if ( pluggedIn )
					return driveLetters[i];
				 
				//save the changed state in the state array
				foundDrive[i] = pluggedIn;
			}
		}
		return null;
	}
	

	/**
	 * Gets a list of all connected usb devices
	 * @return
	 */
    public static int[] getUSBList() 
    {
    	
        Context context = new Context();
        
        //int a libUsb
        int result = LibUsb.init(context);
        if(result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize the usb device",result);
        }
        
        //Create and get the device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        
        //if the creation of the device list was not possible, throw an error
        if(result < 0 )throw new LibUsbException("Unable to get device list",result);
        
        //Array in which the hashcodes of all connected devices will be saved
        int[] idList = new int[result];
        int arrayIndex = 0;
        
            try
            {
            	//Loop through all devices in the list
                for(Device device : list)
                {
                	//Get a Descriptor object from the device which makes it possible to access its informations
                    DeviceDescriptor device_descriptor = new DeviceDescriptor();
                    result = LibUsb.getDeviceDescriptor(device, device_descriptor);
                    
                    //If the creation of a descriptor object was unsuccessful, throw an error
                    if(result != LibUsb.SUCCESS)throw new LibUsbException("Unable to get device descriptor : ",result);
                    
                    
                    //Write the hashcode of the current device into the idList secure?
                    idList[arrayIndex] = device_descriptor.hashCode();
                    arrayIndex ++;

                }
            }
            finally
            {
                LibUsb.freeDeviceList(list, true);
            }
            
            //return the id list
            return idList;
    }
    
    /**
     * Gets the number of currently connected USB devices
     * @return number of connected usb devices
     */
	private int getCurrentDeviceCount()
	{
        Context context = new Context();
        
        //inti a libUsb
        int result = LibUsb.init(context);
        if(result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize the usb device",result);
        }
        
        //get a device list and return its length
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if(result < 0 )throw new LibUsbException("Unable to get device list",result);
        return result;
	}
    
	/**
	 * Returns the hash of a newly connected usb
	 * @return
	 */
    private int getNewDeviceID()
    {
    	boolean found = false;
    	int[] newDeviceList = getUSBList();
    	
    	//Compares the list of the currently connected devices with the "minimal" list to make ou the difference
    	for(int newListE : newDeviceList)
    	{
    		//stores if the current device in the new list can also be found in the old/minimal list
    		found = false;
    		for(int oldListE : minDeviceList)
    		{
    			
    			if(newListE == oldListE)
    			{
    				found = true;
    			}
    		}
    		
    		//If the current device in the new list was not found in the old list, return it
    		if(!found)
    		{
    			return newListE;
    		}
    	}
    	return 0;
    }
	
	/**
	 * Constructor
	 * @param _callbackEditor Editor from which the thread is started
	 */
	public USBDetection(TextEditor _callbackEditor) {
		
		//Init all necessary instance variable's
		driveLetters = new String[]{ "E", "F", "G", "H", "I" ,"J","K", "L","M", "N"};
		externalDrives = new File[driveLetters.length];
		foundDrive = new boolean[driveLetters.length];
		this.callBackEditor = _callbackEditor;
		
		//init the inital drive state array
		for ( int i = 0; i < driveLetters.length; ++i )
		{
			externalDrives[i] = new File(driveLetters[i]+":/");
		 
			foundDrive[i] = externalDrives[i].canRead();
		}
	}
	
	/**
	 * Used to register a newly found usb drive
	 * @param foundDeviceId hash of the drive
	 * @param driveLetter drive letter of the drive
	 */
	private void registerUSBDevice(int foundDeviceId, String driveLetter)
	{
		//passes the task to the javafx main thread
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	//Register the divice with the hash and driveletter
		        callBackEditor.registerUSBDrive(foundDeviceId, driveLetter);
		    }
		});
	}
	
    /**
	 * @return the runningFlag
	 */
	public boolean isRunning() {
		return runningFlag;
	}


	/**
	 * @param runningFlag the runningFlag to set
	 */
	public void setRunning(boolean runningFlag) {
		this.runningFlag = runningFlag;
	}
}
