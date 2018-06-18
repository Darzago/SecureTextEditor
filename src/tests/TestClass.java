package tests;
import static org.junit.Assert.*;

import java.io.File;
import java.security.Security;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PBEType;
import enums.PaddingType;
import logic.CryptoManager;
import persistence.MetaData;
import persistence.USBMetaData;

public class TestClass {
	String testString = "TestDaten";
	
	/**
	 * Clears all generated keys
	 */
	@After
	public void clearKeys()
	{
		File folder = new File("C:/STE-KeyFiles");
		if(folder.exists() && folder.isDirectory())
		{
			File[] fileList =  folder.listFiles();
			for(File currentFile : fileList)
			{
				currentFile.delete();
			}
			folder.delete();
		}
	}
	
    @Test
    public void modeTest() throws Exception
    {
    	MetaData testData = new MetaData(OperationMode.Symmetric, PaddingType.PKCS7Padding, EncryptionType.AES, EncryptionMode.CBC, HashFunction.MD5, KeyLength.x128, "", PBEType.PBEWisthSHAAnd128BitAES_CBC_BC);
    	
    	EncryptionMode[] array = new EncryptionMode[]{EncryptionMode.ECB, EncryptionMode.CBC, EncryptionMode.CTS, EncryptionMode.CTR,  EncryptionMode.OFB, EncryptionMode.CFB, EncryptionMode.CFB8};
    	for(EncryptionMode mode : array)
    	{
    		testData.setEncryptionMode(mode);
    		testEncryptDecrypt(testData);
    	}
    }
    
    /**
     * En and decrypts a test String 
     * @param testData
     * @throws Exception
     */
    public void testEncryptDecrypt(MetaData testData) throws Exception
    {
    	testData.setUsbData(new USBMetaData("C", 1));
    	byte[] result = CryptoManager.encryptString(testString, testData);
    	String resultString = CryptoManager.decryptString(result, testData);
        assertEquals(testString, removeSpaces(resultString));
    }
    
    /**
     * Test method for removing spaces at the end of a string
     * @param string String with spaces
     * @return String without spaces
     */
    private String removeSpaces(String string)
    {
    	for(int i = string.length() -1; i > 0; i--)
    	{
    		if(string.charAt(i) != 0)
    		{
    			return string.substring(0, i + 1);
    		}
    	}
    	return string;
    }
}