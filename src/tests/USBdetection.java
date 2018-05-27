package tests;

import java.io.File;

public class USBdetection 
{
	public static void main(String[] args)
	{
		String[] letters = new String[]{ "E", "F", "G", "H", "I" ,"J","K", "L","M", "N"};
		File[] Externaldrives = new File[letters.length];
		boolean[] FoundDrive = new boolean[letters.length];
		 
		// init the file objects and the initial drive state
		 
		for ( int i = 0; i < letters.length; ++i )
		{
			Externaldrives[i] = new File(letters[i]+":/");
		 
			FoundDrive[i] = Externaldrives[i].canRead();
		}
		 
		System.out.println("Waiting for device, Please Wait");
		 
		// Search each drive
		while(true)
		{
			for ( int i = 0; i < letters.length; ++i )
			{
				boolean pluggedIn = Externaldrives[i].canRead();
				 
				// if the state has changed output a message
				if ( pluggedIn != FoundDrive[i] )
				{
					if ( pluggedIn )
					System.out.println("Drive "+letters[i]+" has been plugged in");
					 
					FoundDrive[i] = pluggedIn;
				}
			}
		}
	}
}
