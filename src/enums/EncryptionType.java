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
			return new EncryptionType[]{PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4, PBEWithSHAAnd128BitAES_CBC_BC};
		case Symmetric:
			return new EncryptionType[]{ none, AES, DES};
		default:
			return new EncryptionType[]{};
		
		}
	}
	
}
