package logic;

import java.io.File;

import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class USBDetectionThread extends Thread{
	
	private String[] driveLetters ;
	private File[] externalDrives ;
	private boolean[] foundDrive;
	private TextEditor callBackEditor;
	private int minDeviceCount = Integer.MAX_VALUE;
	private int currentDeviceCount;
	private int[] minDeviceList;
	
    public void run() {
    	
    	String driveLetter;
    	
    	while(true)
    	{
    		currentDeviceCount = getCurrentDeviceCount();
    		if(currentDeviceCount < minDeviceCount)
    		{
    			minDeviceCount = currentDeviceCount;
    			minDeviceList = getUSBList();
    		}
    		
    		
    		driveLetter = detectUSBDriveLetter();
    		if(driveLetter != null)
    		{
    			int foundDeviceId = getNewDeviceID();
    			System.out.println(foundDeviceId + " Letter: " + driveLetter);
    			//editor.checkDevices(hashList, deviceLetter)
    		}
    		
    		try 
    		{
				sleep(500);
			} 
    		catch (InterruptedException e) 
    		{
				e.printStackTrace();
			}
    	}
    }
    
    
	public String detectUSBDriveLetter()
	{
		for ( int i = 0; i < driveLetters.length; ++i )
		{
			boolean pluggedIn = externalDrives[i].canRead();
			 
			// if the state has changed return the letter
			if ( pluggedIn != foundDrive[i] )
			{
				if ( pluggedIn )
					return driveLetters[i];
				 
				foundDrive[i] = pluggedIn;
			}
		}
		return null;
	}
	
    public static int vid = 1423;
    public static int pid = 25479;

    public int[] getUSBList() 
    {
    	
        Context context = new Context();
        
        int result = LibUsb.init(context);
        if(result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize the usb device",result);
        }
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if(result < 0 )throw new LibUsbException("Unable to get device list",result);
        
        int[] idList = new int[result];
        int arrayIndex = 0;
            try
            {
                for(Device device : list)
                {
                    DeviceDescriptor device_descriptor = new DeviceDescriptor();
                    result = LibUsb.getDeviceDescriptor(device, device_descriptor);
                    if(result != LibUsb.SUCCESS)throw new LibUsbException("Unable to get device descriptor : ",result);
                    
                    System.out.println("Product id is : "+device_descriptor.idProduct()+" "+"Vendor id is : "+device_descriptor.idVendor());
                    
                    /*
                     *	if( id < 0 )
                     *{
                     *	id = id * -2
                     *} 
                     */
                     
                    //is the variable cached if the '?' expression is used? 
                    //TODO Hash that shit and btw that math is fockin BULLSHIT
                    /*
                    idList[arrayIndex] = Integer.parseInt(
                    									((device_descriptor.idProduct() < 0)? device_descriptor.idProduct() * (-2) : device_descriptor.idProduct()) 
                    									+ "" 
                    									+ ((device_descriptor.idVendor() < 0)? device_descriptor.idVendor() * (-2) : device_descriptor.idVendor()));
                    									*/
                    
                    arrayIndex ++;

                }
            }
            finally
            {
                LibUsb.freeDeviceList(list, true);
                
            }
            return idList;
    }
    
	private int getCurrentDeviceCount()
	{
        Context context = new Context();
        
        int result = LibUsb.init(context);
        if(result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize the usb device",result);
        }
        
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(null, list);
        if(result < 0 )throw new LibUsbException("Unable to get device list",result);
        return result;
	}
    
    private int getNewDeviceID()
    {
    	boolean found = false;
    	int[] newDeviceList = getUSBList();
    	
    	for(int newListE : newDeviceList)
    	{
    		found = false;
    		for(int oldListE : minDeviceList)
    		{
    			System.out.println(oldListE + "        " + newListE);
    			if(newListE == oldListE)
    			{
    				found = true;
    			}
    		}
    		if(found)
    		{
    			return newListE;
    		}
    	}
    	return 900;
    }
	
	
	public USBDetectionThread() {
		driveLetters = new String[]{ "E", "F", "G", "H", "I" ,"J","K", "L","M", "N"};
		externalDrives = new File[driveLetters.length];
		foundDrive = new boolean[driveLetters.length];
		
		//init the inital drive state
		for ( int i = 0; i < driveLetters.length; ++i )
		{
			externalDrives[i] = new File(driveLetters[i]+":/");
		 
			foundDrive[i] = externalDrives[i].canRead();
		}
	}
}