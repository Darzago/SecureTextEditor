package tests;
import java.nio.charset.Charset;

import enums.EncryptionType;
import persistence.FileManager;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			String hallo = "Lulilil";
			
			System.out.println(new String(hallo.getBytes(), "UTF-8"));
			
			FileManager test = new FileManager();
			test.loadConfig("test.xml");
			
			EncryptionType test2 = EncryptionType.valueOf("AES");
			System.out.println(test2);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
