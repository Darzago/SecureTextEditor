import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CryptoManager {
	
    byte[] hardDESKey = new byte[] { 
            (byte)0x5e, (byte)0x8e, (byte)0x9e, (byte)0xf2,
            (byte)0xf8, (byte)0x5e, (byte)0x8e, (byte)0x6e
            };
    
	
	public String encryptString(String input, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType)
	{ 
		byte[] keyToUse;
		switch(encryptionType){
			case AES:
				
			break;
			case DES:
				keyToUse = hardDESKey;
			break;
			case none:
				return input;
				
		}
		
		 
		
		return "";
	}
	
	public String decryptString(String input, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType)
	{
		
		
		return "";
	}
}
