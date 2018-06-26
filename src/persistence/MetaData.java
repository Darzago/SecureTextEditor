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
	
	private PaddingType paddingType;
	private EncryptionType encryptionType;
	private EncryptionMode encryptionMode;
	private HashFunction hashFunction;
	private KeyLength keyLength;
	private String hashValue;
	private String iV;
	private byte[] salt = new byte[]{0x00};
	private USBMetaData usbData;
	private String password;

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the salt
	 */
	public byte[] getSalt() {
		return salt;
	}

	/**
	 * @param salt the salt to set
	 */
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	/**
	 * @return the usbData
	 */
	public USBMetaData getUsbData() {
		return usbData;
	}

	/**
	 * @param usbData the usbData to set
	 */
	public void setUsbData(USBMetaData usbData) {
		this.usbData = usbData;
	}

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


	public MetaData(){}

	/**
	 * Constructor
	 * @param paddingType Padding to be set
	 * @param encryptionType EncryptionType to be set
	 * @param encryptionMode Encryption mode to be set
	 * @param hashFunction Hash function to be set
	 * @param keyLength Key length to be set
	 * @param filePath Filepath to be set
	 */
	public MetaData(PaddingType paddingType, EncryptionType encryptionType,
			EncryptionMode encryptionMode, HashFunction hashFunction, KeyLength keyLength) {
		this.paddingType = paddingType;
		this.encryptionType = encryptionType;
		this.encryptionMode = encryptionMode;
		this.hashFunction = hashFunction;
		this.keyLength = keyLength;
	}

}
