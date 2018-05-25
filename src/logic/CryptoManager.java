package logic;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import enums.EncryptionType;
import persistence.MetaData;

/**
 * Used to de and encrypt data
 * @author Joel
 */
public class CryptoManager {
	
	//hardcoded DES Key
    static byte[] hardDESKey = new byte[] { 
            (byte)0x5e, (byte)0x8e, (byte)0x9e, (byte)0xf2,
            (byte)0xf8, (byte)0x5e, (byte)0x8e, (byte)0x6e
            };
    
    //Hardcoded AES Key
    static byte[]  hardAESKey = new byte[] { 
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
    
	/**
	 * Encrypts a given string with the desired encryption, encryption mode and padding type 
	 * @param input String to be encrypted
	 * @param fileData file metadata
	 * @return encrypted string in a byte array
	 * @throws Exception
	 * 
	 * TODO Currently hard coded keys
	 */
	public static byte[] encryptString(String input, MetaData fileData) throws Exception
	{ 		
		byte[] inputByteArray = input.getBytes();
				
		IvParameterSpec ivSpec;
		
		byte[] keyToUse = getMatchingKey(fileData.getEncryptionType());
		
		if(keyToUse == null)
			return input.getBytes();
		
		SecretKeySpec key = new SecretKeySpec(keyToUse, fileData.getEncryptionType().toString());
		
		Cipher cipher = Cipher.getInstance(fileData.getEncryptionType().toString() + "/" + fileData.getEncryptionMode().toString() + "/" + fileData.getPaddingType().toString(), "BC");
		
		
		if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
		{
			byte[] ivArray = getMatchingIV(fileData.getEncryptionType()); 
			fileData.setiV(Base64.getEncoder().encodeToString(ivArray));
			ivSpec = new IvParameterSpec(ivArray);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		}
		else if(fileData.getEncryptionMode().usesIV())
		{
			ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		}
		else
		{
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}
		
		byte[] cipherText = new byte[cipher.getOutputSize(inputByteArray.length)];
		
		int ctLength = cipher.update(inputByteArray, 0, inputByteArray.length, cipherText, 0);
		
		ctLength += cipher.doFinal(cipherText, ctLength);
		
		return cipherText;
	}
	
	/**
	 * Decrypts a given byte array using the given parameters
	 * @param input byte array to be decrypted
	 * @param encryptionType encryption type used to decrypt
	 * @param encryptionMode encryption mode used to decrypt
	 * @param paddingType padding type used to decrypt
	 * @return Decrypted String
	 * @throws Exception
	 * 
	 * TODO Hard coded keys & Ivs
	 * 		Cipher-Based I/O ?
	 */
	public static String decryptString(byte[] input, MetaData fileData) throws Exception
	{
		byte[] inputByteArray = input;
		IvParameterSpec ivSpec;
		byte[] keyToUse = getMatchingKey(fileData.getEncryptionType());
		
		if(keyToUse == null)
			return new String(input, "UTF-8");
		
		SecretKeySpec key = new SecretKeySpec(keyToUse, fileData.getEncryptionType().toString());
		
		Cipher cipher = Cipher.getInstance(fileData.getEncryptionType().toString() + "/" + fileData.getEncryptionMode().toString() + "/" + fileData.getPaddingType().toString(), "BC");
		
		if(fileData.getEncryptionMode().usesIV() && !fileData.getiV().equals("null"))
		{
			ivSpec = new IvParameterSpec(Base64.getDecoder().decode(fileData.getiV()));
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		}
		else if(fileData.getEncryptionMode().usesIV() && (fileData.getiV() == null || fileData.getiV().equals("null")))
		{
			throw new Exception("IV WAS NOT SET");
		}
		else
		{
			cipher.init(Cipher.DECRYPT_MODE, key);
		}
		
		byte[] cipherText = new byte[cipher.getOutputSize(inputByteArray.length)];
		
		int ctLength = cipher.update(inputByteArray, 0, inputByteArray.length, cipherText, 0);
		
		ctLength += cipher.doFinal(cipherText, ctLength);
		
		return new String(cipherText, "UTF-8");
	}
	
	/**
	 * Gets a key matching the fitting length depending on the encryption type
	 * @param encryption encryption to be used
	 * @return key 
	 */
	private static byte[] getMatchingKey(EncryptionType encryption)
	{
		switch(encryption)
		{
		case AES:
			return hardAESKey;
		case DES:
			return hardDESKey;
		case none:
		default:
		return null;
		}
	}
	
	/**
	 * Gets an iv matching the fitting length depending on the encryption type
	 * @param encryption encryption to be used
	 * @return iv 
	 */
	private static byte[] getMatchingIV(EncryptionType encryption)
	{
		switch(encryption)
		{
		case DES:
			return generateIV(8);
		case AES:
			return generateIV(16);
		case none:
		default:
			return null;
		}
	}
	
	/**
	 * Randomly generates an IV with the given length
	 * @param length length of the IV
	 * @return iv
	 */
	private static byte[] generateIV(int length)
	{
		if(length > 0)
		{
			SecureRandom random = new SecureRandom();
			byte[] ivBytes = new byte[length];
			random.nextBytes(ivBytes);
			return ivBytes;
		}
		return null;
	}
	
}
