package enums;

/**
 * Enum that represents encryption types
 * @author Joel
 *
 */
public enum EncryptionType {
	none, AES, DES, RSA, ARC4, PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4, PBEWithSHA256And128BitAES_CBC_BC;

	
	public int getPBESaltLength()
	{
		switch(this)
		{
		case PBEWithMD5AndDES:
			return 8;
		case PBEWithSHA256And128BitAES_CBC_BC:
			return 16;
		case PBEWithSHAAnd40BitRC4:
			return 40;
			
		case AES:
		case ARC4:
		case DES:
		case RSA:
		case none:
		default:
			return 0;
		}
	}
	
	/**
	 * Returns the Operation mode of this encryption type
	 * @return Opertaion mode of this type
	 */
	public OperationMode getOperationMode()
	{
		switch(this)
		{
		case AES:
		case ARC4:
		case DES:
			return OperationMode.Symmetric;
		
		case PBEWithMD5AndDES:
		case PBEWithSHAAnd40BitRC4:
		case PBEWithSHA256And128BitAES_CBC_BC:
			return OperationMode.Passwordbased;
		case RSA:
			return OperationMode.Asymmetric;
		case none:
		default:
			return null;
		}
	}
	
	/**
	 * Returns all encryption typesof the passed operation mode 
	 * @param operation operation
	 * @return Array of all encryption types of the corresponding operation
	 */
	public static EncryptionType[] getValuesByOperation(OperationMode operation)
	{
		switch(operation)
		{
		case Asymmetric:
			return new EncryptionType[]{RSA}; 
		case Symmetric:
			return new EncryptionType[]{none, AES, DES, ARC4};
		case Passwordbased:
			return new EncryptionType[]{PBEWithMD5AndDES, PBEWithSHAAnd40BitRC4, PBEWithSHA256And128BitAES_CBC_BC};
		default:
			return new EncryptionType[]{};		
		}
	}
	
	/**
	 * ValueOf method with the special case PBEWithSHA256And128BitAES-CBC-BC because '-' can not be used in Strings
	 * @param arg String to be converted to an enum
	 * @return encryption type
	 */
	public static EncryptionType filteredValueOf(String arg)
	{
		if(arg.equals("PBEWithSHA256And128BitAES-CBC-BC"))
		{
			return PBEWithSHA256And128BitAES_CBC_BC;
		}
		return valueOf(arg);
	}
	
	/**
	 * ToString method with the special case PBEWithSHA256And128BitAES-CBC-BC because '-' can not be used in Strings
	 * @return type as a string that can be used ti init a cipher
	 */
	public String toString()
	{
		if(this == PBEWithSHA256And128BitAES_CBC_BC)
		{
			return "PBEWithSHA256And128BitAES-CBC-BC";
		}
		else return this.name();
	}
	
}
