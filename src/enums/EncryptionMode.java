package enums;

public enum EncryptionMode {
ECB, CBC, CTS, CTR, OFB, CFB, CFB8;

	public boolean usesIV()
	{
		if(this == EncryptionMode.CTR || this == EncryptionMode.CBC 
		|| this == EncryptionMode.CTS || this == EncryptionMode.OFB 
		|| this == EncryptionMode.CFB || this == EncryptionMode.CFB8)
		{
			return true;
		}
		return false;
	}
	
	//TODO Make noPadding unavailable if the wrong mode is used
	public boolean usesPadding()
	{
		if(this == EncryptionMode.CFB8 || this == EncryptionMode.CTR 
		|| this == EncryptionMode.CBC  || this == EncryptionMode.CTS)
		return true;
		else
		return false;
	}
}

