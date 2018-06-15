package enums;

public enum EncryptionMode {
ECB, CBC, CTS, CTR, OFB, CFB, CFB8, GCM, None;

	/**
	 * Returns if the Mode uses an IV
	 * @return Mode uses an IV
	 */
	public boolean usesIV()
	{
		if(this == EncryptionMode.CTR || this == EncryptionMode.CBC 
		|| this == EncryptionMode.CTS || this == EncryptionMode.OFB 
		|| this == EncryptionMode.CFB || this == EncryptionMode.CFB8
		|| this == EncryptionMode.GCM)
		{
			return true;
		}
		return false;
	}
	
}

