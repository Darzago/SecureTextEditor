package enums;

public enum EncryptionType {
	none, AES, DES, RSA, ARC4;
	

	
	public static EncryptionType[] getValuesByOperation(OperationMode operation)
	{
		switch(operation)
		{
		case Asymmetric:
			return new EncryptionType[]{RSA}; 
		case Passwordbased:
			return new EncryptionType[]{AES, DES, ARC4};
		case Symmetric:
			return new EncryptionType[]{none, AES, DES};
		default:
			return new EncryptionType[]{};
		
		}
	}
	
}
