package tests;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;
import persistence.FileData;
import persistence.FileManager;

public class TestClass {

	public static void main(String[] args) {
		
		try {
			
			FileManager test = new FileManager();
			
			FileData testData1 = new FileData();
			testData1.setEncryptionMode(EncryptionMode.CBC);
			testData1.setEncryptionType(EncryptionType.AES);
			testData1.setFilePath("Peter.txt");
			testData1.setPaddingType(PaddingType.NoPadding);
			
			FileData testData2 = new FileData();
			testData2.setEncryptionMode(EncryptionMode.CBC);
			testData2.setEncryptionType(EncryptionType.AES);
			testData2.setFilePath("Peter.txt");
			testData2.setPaddingType(PaddingType.NoPadding);
			
			List<FileData> testList = new ArrayList<FileData>();
			
			testList.add(testData1);
			testList.add(testData2);
			
			test.writeConfig(testList);
			
			System.out.println(test.loadConfig("test.xml"));

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
