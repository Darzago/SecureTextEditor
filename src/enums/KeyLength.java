package enums;

public enum KeyLength {
	x0, x64, x128, x192, x256, x1024, x4096;
	
	public int returnAsInt()
	{
		return Integer.parseInt(this.toString().substring(1));
	}
	
	public static KeyLength[] getFittingKeyLength(EncryptionType encryptionType)
	{
		
		switch(encryptionType)
		{
		case AES:
			return new KeyLength[]{x128, x192, x256};
		case DES:
			return new KeyLength[]{x64};
		case RSA:
			return new KeyLength[]{x1024, x4096};
		case none:
			return new KeyLength[]{x0};
		default:
			return new KeyLength[]{x0};
		
		}
	}
}
