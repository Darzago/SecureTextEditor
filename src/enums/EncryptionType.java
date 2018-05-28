package enums;

public enum EncryptionType {
	none, AES, DES, RSA, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4;
	
	public static EncryptionType[] getValuesByOperation(OperationMode operation)
	{
		switch(operation)
		{
		case Asymmetric:
			return new EncryptionType[]{RSA}; 
		case Passwordbased:
			return new EncryptionType[]{PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4};
		case Symmetric:
			return new EncryptionType[]{AES, DES, none};
		default:
			return new EncryptionType[]{};
		
		}
	}
	
}
