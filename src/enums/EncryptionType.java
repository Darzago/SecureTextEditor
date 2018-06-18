package enums;

public enum EncryptionType {
	none, AES, DES, RSA, ARC4;
	

	
	public static EncryptionType[] getValuesByOperation(OperationMode operation)
	{
		switch(operation)
		{
		case Asymmetric:
			return new EncryptionType[]{RSA}; 
		case Symmetric:
			return new EncryptionType[]{none, AES, DES, ARC4};
		default:
			return new EncryptionType[]{};
		
		}
	}
	
}
