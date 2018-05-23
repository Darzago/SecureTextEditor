package persistence;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;

public class FileData {
	
	PaddingType paddingType;
	EncryptionType encryptionType;
	EncryptionMode encryptionMode;
	String filePath;	

	/**
	 * @return the paddingType
	 */
	public PaddingType getPaddingType() {
		return paddingType;
	}

	/**
	 * @param paddingType the paddingType to set
	 */
	public void setPaddingType(PaddingType paddingType) {
		if(this.paddingType == null)
			this.paddingType = paddingType;
	}

	/**
	 * @return the encryptionType
	 */
	public EncryptionType getEncryptionType() {
		return encryptionType;
	}

	/**
	 * @param encryptionType the encryptionType to set
	 */
	public void setEncryptionType(EncryptionType encryptionType) {
		if(this.encryptionType == null)
			this.encryptionType = encryptionType;
	}

	/**
	 * @return the encryptionMode
	 */
	public EncryptionMode getEncryptionMode() {
		return encryptionMode;
	}

	/**
	 * @param encryptionMode the encryptionMode to set
	 */
	public void setEncryptionMode(EncryptionMode encryptionMode) {
		if(this.encryptionMode == null)
			this.encryptionMode = encryptionMode;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		if(this.filePath == null)
			this.filePath = filePath;
	}

	public FileData(){}
	
	public FileData(PaddingType paddingType, EncryptionType encryptionType, EncryptionMode encryptionMode, String filePath) 
	{
		this.paddingType = paddingType;
		this.encryptionType = encryptionType;
		this.encryptionMode = encryptionMode;
		this.filePath = filePath;
	}


}
