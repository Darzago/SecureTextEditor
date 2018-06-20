package tests;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Test;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PaddingType;
import logic.CryptoManager;
import persistence.MetaData;
import persistence.USBMetaData;

/**
 * Test Class 
 * 		implements several JUnit tests. 
 * @author Joel
 *
 */
public class TestClass {
	String testString = "TestDaten";
	MetaData testData = new MetaData(PaddingType.PKCS7Padding, EncryptionType.AES, EncryptionMode.CBC, HashFunction.MD5, KeyLength.x128, "");
	
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
	public void testHashes() throws Exception
	{
		System.out.println("Hash Tests \t ---------------------------------------------------------------------------");
		for(HashFunction function : HashFunction.values())
		{
			testData.setHashFunction(function);
			String generatedHash = CryptoManager.generateHash(testData, testString.getBytes());
			System.out.println("\t" + function.toString() + ":\t" + generatedHash);
			CryptoManager.validateHash(testData, testString.getBytes(), generatedHash);
		}
	}
	
    @Test
    public void modeTest() throws Exception
    {
    	System.out.println("EncMode Tests \t ---------------------------------------------------------------------------");
    	EncryptionMode[] array = new EncryptionMode[]{EncryptionMode.ECB, EncryptionMode.CBC, EncryptionMode.CTS, EncryptionMode.CTR,  EncryptionMode.OFB, EncryptionMode.CFB, EncryptionMode.CFB8};
    	for(EncryptionMode mode : array)
    	{
    		testData.setEncryptionMode(mode);
    		testEncryptDecrypt(testData);
    	}
    }
    
    @Test
    public void encTypeTest() throws Exception
    {
    	System.out.println("EncType Tests \t ---------------------------------------------------------------------------");
    	testData.setPassword("Password");
		testData.setEncryptionMode(EncryptionMode.ECB);
		for(EncryptionType type : EncryptionType.values())
		{
			testData.setEncryptionType(type);
			testData.setKeyLength(KeyLength.getFittingKeyLength(type)[0]);
			
			testData.setiV(null);
			
			if(type.getOperationMode() == OperationMode.Symmetric && type != EncryptionType.none && type != EncryptionType.ARC4)
			{
		    	EncryptionMode[] array = new EncryptionMode[]{EncryptionMode.ECB, EncryptionMode.CBC, EncryptionMode.CTS, EncryptionMode.CTR,  EncryptionMode.OFB, EncryptionMode.CFB, EncryptionMode.CFB8};
		    	for(EncryptionMode mode : array)
		    	{	
		    		testData.setEncryptionMode(mode);
		    		testEncryptDecrypt(testData);
		    	}
			}
			else
			{
				testEncryptDecrypt(testData);
			}
		}
    }
    
    /**
     * En and decrypts a test String 
     * @param testData
     * @throws Exception
     */
    public void testEncryptDecrypt(MetaData testData) throws Exception
    {
    	System.out.print("\t EncType:\t" + testData.getEncryptionType().toString() + "\t" );
    	if(testData.getEncryptionType() != EncryptionType.none && testData.getEncryptionType() != EncryptionType.ARC4 && testData.getEncryptionType().getOperationMode() == OperationMode.Symmetric)
    	{
    		System.out.print("Mode: " + testData.getEncryptionMode() + "\tPadding: " + testData.getPaddingType());
    	}
    	System.out.println("");
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