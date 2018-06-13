package persistence;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
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
	HashFunction hashFunction;
	KeyLength keyLength;
	String hashValue;
	String filePath;
	String iV;
	
	/**
	 * @return the keyLength
	 */
	public KeyLength getKeyLength() {
		return keyLength;
	}

	/**
	 * @param keyLength the keyLength to set
	 */
	public void setKeyLength(KeyLength keyLength) {
		this.keyLength = keyLength;
	}
	
	/**
	 * @return the hashValue
	 */
	public String getHashValue() {
		return hashValue;
	}

	/**
	 * @param hashValue the hashValue to set
	 */
	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
	
	/**
	 * @return the hashFunction
	 */
	public HashFunction getHashFunction() {
		return hashFunction;
	}

	/**
	 * @param hashFunction the hashFunction to set
	 */
	public void setHashFunction(HashFunction hashFunction) {
		this.hashFunction = hashFunction;
	}

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
	
	public MetaData(PaddingType paddingType, EncryptionType encryptionType, EncryptionMode encryptionMode, HashFunction hashFunction, KeyLength keyLength, String hashValue, String filePath) 
	{
		this.paddingType = paddingType;
		this.encryptionType = encryptionType;
		this.encryptionMode = encryptionMode;
		this.filePath = filePath;
		this.hashFunction = hashFunction;
		this.hashValue = hashValue;
		this.keyLength = keyLength;
	}


}
