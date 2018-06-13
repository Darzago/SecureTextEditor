package tests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import logic.CryptoManager;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			CryptoManager.generateKey(EncryptionType.AES, KeyLength.x1024);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

}
