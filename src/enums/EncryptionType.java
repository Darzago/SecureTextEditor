package enums;

public enum EncryptionType {
	none, AES, DES, RSA, DESede, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4;
	
	//TODO
	public EncryptionMode getModeByPBEType()
	{

		switch(this)
		{
		case PBEWithMD5AndDES:
			
		case PBEWithSHAAnd128BitAES_CBC_BC:
			return EncryptionMode.CBC;	
		case PBEWithSHAAnd40BitRC4:
			
		default:
			return null;	
		}
		
	}
	
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
