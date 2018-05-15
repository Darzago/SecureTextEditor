import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
	
    byte[] hardDESKey = new byte[] { 
            (byte)0x5e, (byte)0x8e, (byte)0x9e, (byte)0xf2,
            (byte)0xf8, (byte)0x5e, (byte)0x8e, (byte)0x6e
            };
    
    byte[]  hardAESKey = new byte[] { 
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
    
	
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
	
	private String generateKey()
	{
	return "";
	}
	
}
