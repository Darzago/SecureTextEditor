import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Used to de and encrypt data
 * @author Joel
 *
 */
public class CryptoManager {
	
	//hardcoded DES Key
    byte[] hardDESKey = new byte[] { 
            (byte)0x5e, (byte)0x8e, (byte)0x9e, (byte)0xf2,
            (byte)0xf8, (byte)0x5e, (byte)0x8e, (byte)0x6e
            };
    
    //Hardcoded AES Key
    byte[]  hardAESKey = new byte[] { 
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
    
	/**
	 * Encrypts a given string with the desired encryption, encryption mode and padding type 
	 * @param input String to be encrypted
	 * @param encryptionType desired encryption type
	 * @param encryptionMode desired encryption mode
	 * @param paddingType desired padding type
	 * @return encrypted string in a byte array
	 * @throws Exception
	 * 
	 * TODO Currently hard coded keys
	 */
	public byte[] encryptString(String input, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType) throws Exception
	{ 
		
		byte[] inputByteArray = input.getBytes();
		
		byte[] keyToUse = hardDESKey;
		
		switch(encryptionType){
			case AES:
				keyToUse = hardAESKey;
			break;
			case DES:
				keyToUse = hardDESKey;
			break;
			case none:
				return input.getBytes();
			default:
			break;
				
		}
		
		SecretKeySpec key = new SecretKeySpec(keyToUse, encryptionType.toString());
		
		Cipher cipher = Cipher.getInstance(encryptionType.toString() + "/" + encryptionMode.toString() + "/" + paddingType.toString(), "BC");
		
		cipher.init(Cipher.ENCRYPT_MODE, key);
		
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
	 * TODO Hard coded keys
	 */
	public String decryptString(byte[] input, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType) throws Exception
	{
		byte[] inputByteArray = input;
		byte[] keyToUse = hardDESKey;
		
		switch(encryptionType){
			case AES:
				keyToUse = hardAESKey;
			break;
			case DES:
				keyToUse = hardDESKey;
			break;
			case none:
				return new String(input, "UTF-8");
			default:
			break;
				
		}
		
		SecretKeySpec key = new SecretKeySpec(keyToUse, encryptionType.toString());
		
		Cipher cipher = Cipher.getInstance(encryptionType.toString() + "/" + encryptionMode.toString() + "/" + paddingType.toString(), "BC");
		
		cipher.init(Cipher.DECRYPT_MODE, key);
		
		byte[] cipherText = new byte[cipher.getOutputSize(inputByteArray.length)];
		
		int ctLength = cipher.update(inputByteArray, 0, inputByteArray.length, cipherText, 0);
		
		ctLength += cipher.doFinal(cipherText, ctLength);
		
		return new String(cipherText, "UTF-8");
	}
	
	/**
	 * TODO
	 * Generate Key
	 * @return
	 */
	private byte[] generateKey()
	{
	return new byte[0];
	}
	
}
