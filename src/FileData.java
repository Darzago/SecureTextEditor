import java.io.File;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;

public class FileData {
	
	//Maybe store a seperate .xml file for each text file, using the generated hash value as name? 
	
	PaddingType paddingType;
	EncryptionType encryptionType;
	EncryptionMode encryptionMode;
	File filePath;
	String hashValue;	
	
	public FileData(PaddingType _paddingType, EncryptionType _encryptionType, File _filePath, String _hashValue) {
		this.paddingType = _paddingType;
		this.encryptionType = _encryptionType;
		this.filePath = _filePath;
		this.hashValue = _hashValue;
	}

	//TODO Remove unwanted getter and setter
	
	public PaddingType getPaddingType() {
		return paddingType;
	}

	public EncryptionType getEncryptionType() {
		return encryptionType;
	}

	public String getFilePath() {
		return filePath.getPath();
	}

	public String getHashValue() {
		return hashValue;
	}

}
