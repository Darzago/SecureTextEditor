package tests;

import java.util.Base64;

public class basictests {
	
	public static void main(String[] args)
	{
		String outputString = "Das ist eine Test kKxkDw4C1J3Q3qcvrEZR4A==";
		String hash = "";
		
		for(int count = outputString.length() - 1; count > 0; count --)
		{
			//Char 3 is a char used to symbolize the end of a transmission (ASCII)
			if(outputString.charAt(count) == (char)3)
			{
				hash = outputString.substring(count +1, outputString.length());
				outputString = outputString.substring(0,count);
			}
		}
		System.out.println(outputString);
		System.out.println(hash);
	}
	
	private static void fill(byte[] array)
	{
		for(byte byt : array)
		{
			byt = 0x01;
		}
	}
	
	private static void print(byte[] array)
	{
		for(byte byt : array)
		{
			System.out.println(byt);
		}
	}
}
