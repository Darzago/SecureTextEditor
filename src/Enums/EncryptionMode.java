package Enums;

public enum EncryptionMode {
ECB, CBC, CTS, CTR;

	public boolean usesIV()
	{
		if(this == EncryptionMode.CTR || this == EncryptionMode.CBC || this == EncryptionMode.CTS)
		{
			return true;
		}
		return false;
	}
}

