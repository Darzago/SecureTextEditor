package persistence;
import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PBEType;
import enums.PaddingType;

/**
 * Used to store a files metadata
 * @author Joel
 *
 */
public class MetaData {
	
	private OperationMode operationMode;
	private PaddingType paddingType;
	private EncryptionType encryptionType;
	private EncryptionMode encryptionMode;
	private HashFunction hashFunction;
	private KeyLength keyLength;
	private String hashValue;
	private String filePath;
	private String iV;
	private byte[] salt = new byte[]{0x00};
	private USBMetaData usbData;
	private String password;
	private PBEType pbeType;
	
	/**
	 * @return the pbeType
	 */
	public PBEType getPbeType() {
		return pbeType;
	}

	/**
	 * @param pbeType the pbeType to set
	 */
	public void setPbeType(PBEType pbeType) {
		this.pbeType = pbeType;
	}

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
	 * @return the operationMode
	 */
	public OperationMode getOperationMode() {
		return operationMode;
	}

	/**
	 * @param operationMode the operationMode to set
	 */
	public void setOperationMode(OperationMode operationMode) {
		this.operationMode = operationMode;
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

	public MetaData(OperationMode operationMode, PaddingType paddingType, EncryptionType encryptionType,
			EncryptionMode encryptionMode, HashFunction hashFunction, KeyLength keyLength, String filePath, PBEType pbeType) {
		this.operationMode = operationMode;
		this.paddingType = paddingType;
		this.encryptionType = encryptionType;
		this.encryptionMode = encryptionMode;
		this.hashFunction = hashFunction;
		this.keyLength = keyLength;
		this.filePath = filePath;
		this.pbeType = pbeType;
	}

}
