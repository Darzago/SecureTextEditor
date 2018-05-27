package tests;

import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/*
 * Interface classes
 * 8 	Mass Storage
 * 14 	Video
 * 
 */

public class USBTest 
{
    public static int vid = 1423;
    public static int pid = 25479;


    public static void main(String[] args) 
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
        System.out.println(result);
        
        	DeviceDescriptor device_descriptor = new DeviceDescriptor();
        
            try
            {
                for(Device device : list)
                {
                    
                    result = LibUsb.getDeviceDescriptor(device, device_descriptor);
                    if(result != LibUsb.SUCCESS)throw new LibUsbException("Unable to get device descriptor : ",result);
                    
                    System.out.println("Product id is : "+device_descriptor.idProduct()+" "+"Vendor id is : "+device_descriptor.idVendor());
                    
                    ConfigDescriptor descriptor = new ConfigDescriptor();

	                    
                    LibUsb.getActiveConfigDescriptor(device, descriptor);
                    long pointer = descriptor.getPointer();
                    System.out.println("Pointer: " + pointer);
                    
                    
                    if(pointer != 0)
                    for (Interface iface: descriptor.iface())
                    {
                    	
                        for (InterfaceDescriptor ifaceDescriptor: iface.altsetting())
                        {
                        	System.out.println(ifaceDescriptor.dump());
                        }
                    }

                    
                    if(device_descriptor.idProduct()==pid && device_descriptor.idVendor()==vid)
                    {
                        System.out.println("Product id and vendor id was matched");
                    }
                    else
                    {

                        System.out.println("Product id and vendor id was not matched");
                    }
                    System.out.println("------------------------------------------------------------");
                }

            }
            finally
            {
                LibUsb.freeDeviceList(list, true);
            }


    }

}