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

	//Stopwatch to display the time that was needed for each encryption
	StopWatch stopwatch = new StopWatch();
	
	//Testdata to be encrypted
	String testString = "TestDaten";
	
	//Test metadata
	MetaData testData = new MetaData(PaddingType.PKCS7Padding, EncryptionType.AES, EncryptionMode.CBC, HashFunction.MD5, KeyLength.x128);
	
	
	/**
	 * Clears all generated keys after the test
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
	
	/**
	 * Tests all Hash functions and prints the result on the console
	 * @throws Exception
	 */
	@Test
	public void testAllHasheFunctions() throws Exception
	{
		System.out.println("Hash Tests \t ---------------------------------------------------------------------------");
		for(HashFunction function : HashFunction.values())
		{
			testData.setHashFunction(function);
			
			stopwatch.stopAndStart();
			String generatedHash = CryptoManager.generateHash(testData, testString.getBytes());
			CryptoManager.validateHash(testData, testString.getBytes(), generatedHash);
			float time = stopwatch.getTimeInSec();
			
			String out = function.toString() + ": ";
			if(out.length() < 8)
			{
				out += "\t";
			}
			System.out.println("\t" + out + "\tValid \t" + time + "s");
		}
	}
	
	/**
	 * Tests all possible combinations of EncTypes, Modes, Paddings and Keylenght
	 * @throws Exception
	 */
    @Test
    public void testAllEncTypesAndModes() throws Exception
    {
    	System.out.println("EncType Tests \t ---------------------------------------------------------------------------\n");
    	
    	System.out.println("\tType\tMode\t\tPadding\t\tKeyLength\tTime in s");
    	System.out.println("\t---------------------------------------------------------------------------");
    	
    	testData.setPassword("Password".toCharArray());
		testData.setEncryptionMode(EncryptionMode.ECB);
		for(EncryptionType type : EncryptionType.values())
		{
			testData.setEncryptionType(type);
			testData.setKeyLength(KeyLength.getFittingKeyLength(type)[0]);
			
			testData.setiV(null);
			
			if(type.getOperationMode() == OperationMode.Symmetric && type != EncryptionType.none && type != EncryptionType.ARC4)
			{
		    	EncryptionMode[] array = new EncryptionMode[]{EncryptionMode.ECB, EncryptionMode.CBC, EncryptionMode.CTS, EncryptionMode.CTR,  EncryptionMode.OFB, EncryptionMode.CFB, EncryptionMode.CFB8, EncryptionMode.GCM, EncryptionMode.OpenPGPCFB};
		    	for(EncryptionMode mode : array)
		    	{	
		    		testData.setEncryptionMode(mode);
		    		
		    		if(!(testData.getEncryptionMode() == EncryptionMode.GCM && testData.getEncryptionType() == EncryptionType.DES))
		    		{
		    			testAllPaddings(testData);
		    		}
		    		
		    	}
			}
			else
			{
				testAllKeyLengths(testData);
			}
		}
    }
    
    /**
     * Tests all paddings and calls the testAllKeyLength method
     * @param testData TestData to be used in the test
     * @throws Exception
     */
    private void testAllPaddings(MetaData testData) throws Exception
    {
    	for(PaddingType padding : PaddingType.values())
    	{
    		testData.setPaddingType(padding);
    		
    		if(!(testData.getEncryptionMode().needsPadding() && testData.getPaddingType() == PaddingType.NoPadding) && testData.getEncryptionMode() != EncryptionMode.GCM)
    		{
    			testAllKeyLengths(testData);
    		}
    		else if(testData.getEncryptionMode() == EncryptionMode.GCM)
    		{
    			testData.setPaddingType(PaddingType.NoPadding);
    		}
    	}
    }
    
    /**
     * Loops through all keylengths and calls the testEncryptDecrypt method
     * @param testData TestData to be used in the test
     * @throws Exception
     */
    private void testAllKeyLengths(MetaData testData) throws Exception
    {
    	for(KeyLength currentLength : KeyLength.getFittingKeyLength(testData.getEncryptionType()))
    	{
    		testData.setKeyLength(currentLength);
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
    	System.out.print("\t" + testData.getEncryptionType().toString() + "\t" );
    	if(testData.getEncryptionType() != EncryptionType.none && testData.getEncryptionType() != EncryptionType.ARC4 && testData.getEncryptionType().getOperationMode() == OperationMode.Symmetric)
    	{
    		//used to line up the outup
    		String outputMode = testData.getEncryptionMode().toString();
    		if(outputMode.length() < 8)
    		{
    			outputMode += "\t";
    		}
    		System.out.print(outputMode + "\t" + testData.getPaddingType() + "\t");
    	}
    	else if(testData.getEncryptionType().getOperationMode() == OperationMode.Passwordbased)
    	{
    		System.out.print("\t");
    	}
    	else 
    	{
    		System.out.print("\t\t\t");
    	}
    	
    	if(testData.getEncryptionType() == EncryptionType.ARC4 || testData.getEncryptionType() == EncryptionType.RSA)
    	{
    		System.out.print("\t");
    	}
    	if(testData.getEncryptionType().getOperationMode() != OperationMode.Passwordbased && testData.getEncryptionType() != EncryptionType.none)
    	{
    		System.out.print(testData.getKeyLength() + "\t");
    	}
    	else
    	{
    		System.out.print("\t\t");
    	}
    	
    	testData.setUsbData(new USBMetaData("C", 1));
    	
    	stopwatch.stopAndStart();
    	byte[] result = CryptoManager.encryptString(testString, testData);
    	String resultString = CryptoManager.decryptString(result, testData);
    	
    	System.out.println("\t" + stopwatch.getTimeInSec() + "s");

        assertEquals(testString, resultString.trim());
    }
    
}