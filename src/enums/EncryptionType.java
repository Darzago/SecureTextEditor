package enums;

public enum EncryptionType {
	none, AES, DES, RSA;
	

	
	public static EncryptionType[] getValuesByOperation(OperationMode operation)
	{
		switch(operation)
		{
		case Asymmetric:
			return new EncryptionType[]{RSA}; 
		case Symmetric:
			return new EncryptionType[]{none, AES, DES};
		default:
			return new EncryptionType[]{};
		
		}
	}
	
}
