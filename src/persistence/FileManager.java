package persistence;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.HashFunction;
import enums.KeyLength;
import enums.OperationMode;
import enums.PaddingType;
import logic.CryptoManager;

/**
 * Saves and loads files and configs
 * @author Joel
 *
 */
public class FileManager {
	
	//Path of the config.xml file	
	private static final String usbConfigPath = "usbConfig.xml";

	/**
	 * Opens and decrypts a file from the given filepath using the given information
	 * @param fileLocation file to be opened
	 * @param fileData file metadata used to decrypt
	 * @return decoded content of the file
	 * @throws Exception
	 */
	public static String openFileFromPath(String fileLocation, MetaData fileData) throws Exception
	{
		//Get a path object from the location String to read all bytes
    	Path filePath = Paths.get(fileLocation);
    	byte[] readByteArray= Files.readAllBytes(filePath);
    	
    	//Return the decrypted string
    	return CryptoManager.decryptString(Base64.getDecoder().decode(readByteArray), fileData);
	}
	

	/**
	 * Writes and encrypts the given content into a file 
	 * @param path File to be written
	 * @param fileContent content of the file
	 * @param fileData metadata of the file
	 * @throws Exception
	 */
	public static void saveFileInPath(File path, String fileContent, MetaData fileData) throws Exception
	{
		if(path != null){
			//create an object of FileOutputStream
			FileOutputStream fos = new FileOutputStream(path);			
				
			//create an object of BufferedOutputStream
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			//Encode the content & write it into a byte array
			byte[] contentToWrite = Base64.getEncoder().encode(CryptoManager.encryptString(fileContent, fileData));
			
			//write metadata to the file so that it can be decrypted
			writeMetaData(path, fileData);
			
			//Write the file
			bos.write(contentToWrite);
			
			//Close the output stream
			bos.close();
		}
	}
	
	/**
	 * Writes the metadata into a file
	 * @param file file whichs metadata will be written
	 * @param fileData data to write
	 * @throws Exception
	 */
	private static void writeMetaData(File file, MetaData fileData) throws Exception
	{
		//Get the operation Mode of the encryption type the file was encrypted with
		OperationMode mode = fileData.getEncryptionType().getOperationMode();
		
		//Load hash function
		Files.setAttribute(file.toPath(), "user:Type", (fileData.getEncryptionType().toString() + "").getBytes() );
		Files.setAttribute(file.toPath(), "user:HashF", (fileData.getHashFunction().toString()+ "").getBytes() );
		
		//Depending on the mode, write different parameters
		//If the mode is Symmetric
		if(mode == OperationMode.Symmetric)
		{
			//Write mode, padding, iv, and keylength
			Files.setAttribute(file.toPath(), "user:Mode", (fileData.getEncryptionMode().toString()+ "").getBytes() );
			Files.setAttribute(file.toPath(), "user:Padding", (fileData.getPaddingType().toString()+ "").getBytes() );
			Files.setAttribute(file.toPath(), "user:IV", (fileData.getiV() + "").getBytes());
			Files.setAttribute(file.toPath(), "user:keyLength", (fileData.getKeyLength().toString() + "").getBytes());
		}
		//if the mode is asymmetric
		else if(mode == OperationMode.Asymmetric)
		{
			//Write keylength
			Files.setAttribute(file.toPath(), "user:keyLength", (fileData.getKeyLength().toString() + "").getBytes());
		}
		//If the mode is passwordbased
		else if(mode == OperationMode.Passwordbased)
		{
			//Write the salt
			Files.setAttribute(file.toPath(), "user:salt", (fileData.getSalt()));
		}
	}
	
	
	/**
	 * Write usb config data
	 * @param dataList data to be written into a config
	 */
	public static void writeUSBConfig(List<USBMetaData> dataList) throws Exception
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document config = docBuilder.newDocument();
		
		//Root element
		Element rootElement = config.createElement("USBCONFIG");
		config.appendChild(rootElement);
		
		for(USBMetaData filedata : dataList){
			
			//options
			Element fileElement = config.createElement("usbdrive");
			rootElement.appendChild(fileElement);
			
			//drive letter
			Element encryptionElement = config.createElement("driveLetter");
			fileElement.appendChild(encryptionElement);
			encryptionElement.appendChild(config.createTextNode(filedata.getDriveLetter() + ""));
			
			//hash
			Element encryptionModeElement = config.createElement("hash");
			encryptionModeElement.appendChild(config.createTextNode(filedata.getHash() + ""));
			fileElement.appendChild(encryptionModeElement);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", 2);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(config);
		StreamResult result = new StreamResult(new File(usbConfigPath));
		transformer.transform(source, result);		
	}
	
	/**
	 * Loads a config.xml 
	 * @param path Path of the .xml
	 * @return List of the read data
	 */	
	public static List<USBMetaData> loadUSBConfig() throws Exception
	{	
		List<USBMetaData> dataList = new ArrayList<USBMetaData>();	
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		
		DefaultHandler handler = new DefaultHandler() 
		{
			
			USBMetaData fileData;
			
			boolean bDriveLetter = false;
			boolean bHash = false;
			
			public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException 
			{
				
				if (qName.equalsIgnoreCase("usbdrive")) 
				{
					fileData = new USBMetaData();
				}
				else if (qName.equalsIgnoreCase("driveletter")) {
					bDriveLetter = true;
				}
				else if (qName.equalsIgnoreCase("hash")) {
					bHash = true;
				}
			}
			
			public void endElement(String uri, String localName, String qName) throws SAXException 
			{
				if (qName.equalsIgnoreCase("usbdrive")) 
				{
					dataList.add(fileData);
				}
			}

			public void characters(char ch[], int start, int length) throws SAXException 
			{
				if (bDriveLetter) {
					fileData.setDriveLetter(new String(ch, start, length));
					bDriveLetter = false;
				}
				if (bHash) {
					fileData.setHash(Integer.parseInt(new String(ch, start, length)));
					bHash = false;
				}
			}	
		};
		
		saxParser.parse(usbConfigPath, handler);
		return dataList;
	}
	
	/**
	 * Loads meta data from a file
	 * @param file File to be read
	 * @return read metadata
	 * @throws Exception
	 */
	public static MetaData loadMetaData(File file) throws Exception
	{
		MetaData openedData = new MetaData();
		
		openedData.setEncryptionType(EncryptionType.filteredValueOf(getAttributeAsString(file, "user:Type")));
		
		//Get the operation mode of the read encryption type to determine which parameters should be loaded
		OperationMode mode = openedData.getEncryptionType().getOperationMode();
		
		//Load hash function
		openedData.setHashFunction(HashFunction.valueOf(getAttributeAsString(file, "user:HashF")));
		
		//If the mode is symmetric
		if(mode == OperationMode.Symmetric)
		{
			//Load mode, padding, iv and keylength
			openedData.setEncryptionMode(EncryptionMode.valueOf(getAttributeAsString(file, "user:Mode")));
			openedData.setPaddingType(PaddingType.valueOf(getAttributeAsString(file, "user:Padding")));
			openedData.setiV(getAttributeAsString(file, "user:IV"));
			openedData.setKeyLength(KeyLength.valueOf(getAttributeAsString(file, "user:keyLength")));
		}
		//if the mode is asymmetric
		else if(mode == OperationMode.Asymmetric)
		{
			//Load keylength
			openedData.setKeyLength(KeyLength.valueOf(getAttributeAsString(file, "user:keyLength")));
		}
		//if the mode is passsword based
		else if(mode == OperationMode.Passwordbased)
		{
			//load the salt
			openedData.setSalt((byte[])Files.getAttribute(file.toPath(), "user:salt"));
		}
		
		//Return the read metadata as a metadata object
		return openedData;
	}
	
	/**
	 * Returns an attribute of a file as a string
	 * @param file file to be read
	 * @param attributeName name of the attribute to be read
	 * @return attribute as a string
	 * @throws IOException
	 */
	private static String getAttributeAsString(File file, String attributeName) throws IOException
	{
		return (new String((byte[])Files.getAttribute(file.toPath(), attributeName)));
	}
	
	/**
	 * Saves a key to a .STEKEY file (hash value as name)
	 * @param key Key to be written
	 * @param hashValue Hash value to be used as name
	 * @param driveLetter drive letter of the usb drive the file will be written on
	 * @throws Exception
	 */
	public static void saveKey(byte[] key, String hashValue, String driveLetter) throws Exception
	{
		//Get a file object from the dir the file will be saved in
		File testForFolder = new File(driveLetter + ":/STE-KeyFiles");
		
		//if the dir does not exist or it is not a directory
		if( !testForFolder.exists() || !testForFolder.isDirectory())
		{
			//Create the dir
			boolean dirCreated = testForFolder.mkdir();
			
			//If the directory could not be created
			if(!dirCreated)
			{
				//Throw an error
				throw new Exception("Key File Directory could not be created!");
			}
		}
		
		//Remove all chars that can not be used in a file name from the files hash to use it as file name
		hashValue = removeSpecialChars(hashValue);
		
		//Setup up an output stream
		FileOutputStream fos = new FileOutputStream(driveLetter + ":/STE-KeyFiles/" + hashValue + ".STEkey");
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		//Write the key (with a Base64 encodding)
		bos.write(Base64.getEncoder().encode(key));
		
		//Close the output stream
		bos.close();
	}
	
	/**
	 * Removes all chars from a string that can not be used in a file name
	 * @param input String to be processed
	 * @return processed String
	 */
	private static String removeSpecialChars(String input)
	{
		input = input.replaceAll("/", "");
		input = input.replaceAll("\\+", "");
		input = input.replaceAll("=", "");
		input = input.replaceAll("\\\\", "");
		return input;
	}
	
	/**
	 * Loads a key from a file
	 * @param hashValue hash value of the file
	 * @param driveLetter drive letter of the drive the key is stored on
	 * @return key in a byte array
	 * @throws Exception
	 */
	public static byte[] getKeyFromFile(String hashValue, String driveLetter) throws Exception
	{
		//Remove all chars that can not be used in a file name from the files hash to use it as file name
		hashValue = removeSpecialChars(hashValue);
		
		//Create a file object from the path
		File fileToOpen = new File(driveLetter + ":/STE-KeyFiles/" + hashValue + ".STEkey");
		
		//If the key file exists
		if(fileToOpen.exists())
		{
			//decode the file content and return it as a byte array
			return Base64.getDecoder().decode(Files.readAllBytes(fileToOpen.toPath()));
		}
		//if the key file does not exist
		else
		{
			//throw an error
			throw new Exception("Key File not found");
		}
	}
	
}
