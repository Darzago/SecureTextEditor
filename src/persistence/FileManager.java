package persistence;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    	Path filePath = Paths.get(fileLocation);
    	byte[] readByteArray= Files.readAllBytes(filePath);
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
			
			
			//Set the metadata of the file to be written
			Files.setAttribute(path.toPath(), "user:Type", (fileData.getEncryptionType().toString() + "").getBytes() );
			Files.setAttribute(path.toPath(), "user:Mode", (fileData.getEncryptionMode().toString()+ "").getBytes() );
			Files.setAttribute(path.toPath(), "user:Padding", (fileData.getPaddingType().toString()+ "").getBytes() );
			Files.setAttribute(path.toPath(), "user:HashF", (fileData.getHashFunction().toString()+ "").getBytes() );
			
			
			
			
			//create an object of BufferedOutputStream
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			byte[] contentToWrite = Base64.getEncoder().encode(CryptoManager.encryptString(fileContent, fileData));
			
			//these attributes need to set after the encoding because their values are generated while encoding
			Files.setAttribute(path.toPath(), "user:IV", (fileData.getiV() + "").getBytes() );
			Files.setAttribute(path.toPath(), "user:Hash", (fileData.getHashValue()).getBytes());
			
			bos.write(contentToWrite);
			
			bos.close();
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
	
	
}
