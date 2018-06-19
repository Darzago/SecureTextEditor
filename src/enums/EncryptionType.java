package enums;

public enum EncryptionType {
	none, AES, DES, RSA, ARC4, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4;
	
	public OperationMode getOperationMode()
	{
		switch(this)
		{
		case AES:
		case ARC4:
		case DES:
			return OperationMode.Symmetric;
		case PBEWithSHAAnd128BitAES_CBC_BC:
		case PBEWithMD5AndDES:
		case PBEWithSHAAnd40BitRC4:
			return OperationMode.Passwordbased;
		case RSA:
			return OperationMode.Asymmetric;
		case none:
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
		case Symmetric:
			return new EncryptionType[]{none, AES, DES, ARC4};
		case Passwordbased:
			return new EncryptionType[]{PBEWithMD5AndDES, PBEWithSHAAnd128BitAES_CBC_BC, PBEWithSHAAnd40BitRC4};
		default:
			return new EncryptionType[]{};		
		}
	}
	
}
