package tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import enums.HashFunction;
import enums.OperationMode;
import logic.CryptoManager;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			String toHash = "Pödödel";
			
			File testFile = new File("TEST.txt");
			
			//create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(testFile);
			
			String penner = "penner";
			
			Files.setAttribute(testFile.toPath(), "user:penner", penner.getBytes());
			
			//create an object of BufferedOutputStream
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			bos.write(toHash.getBytes());
			
			Map<String, Object> attribs = Files.readAttributes(testFile.toPath(), "user:penner");
			
			OperationMode[] testModes = OperationMode.values();
			
			System.out.println(new String((byte[])attribs.get("penner"), "utf-8"));
			
			bos.close();
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}
