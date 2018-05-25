package persistence;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;

/**
 * Used to store a files metadata
 * @author Joel
 *
 */
public class MetaData {
	
	PaddingType paddingType;
	EncryptionType encryptionType;
	EncryptionMode encryptionMode;
	String filePath;
	String iV;

	/**
	 * @return the iV
	 */
	public String getiV() {
		return iV;
	}

	/**
	 * @param iV the iV to set
	 */
	public void setiV(String iV) {
		if(this.iV == null)
			this.iV = iV;
	}

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
			this.filePath = filePath;
	}

	public MetaData(){}
	
	public MetaData(PaddingType paddingType, EncryptionType encryptionType, EncryptionMode encryptionMode, String filePath) 
	{
		this.paddingType = paddingType;
		this.encryptionType = encryptionType;
		this.encryptionMode = encryptionMode;
		this.filePath = filePath;
	}


}
