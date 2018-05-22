package persistence;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import enums.EncryptionMode;
import enums.EncryptionType;
import enums.PaddingType;
import logic.CryptoManager;

/**
 * Saves and loads files and configs
 * @author Joel
 *
 */
public class FileManager {
	
	CryptoManager cryptoManager = new CryptoManager();
	
	/**
	 * Opens and decrypts a file from the given filepath using the given settings
	 * @param fileLocation file to be opened
	 * @param encryptionType encryption type to be used to decrypt
	 * @param encryptionMode encryption mode to be used to decrypt
	 * @param paddingType padding type to be used to decrypt
	 * @return decoded content of the file
	 */
	public String openFileFromPath(String fileLocation, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType)
	{
    	try {
    		Path filePath = Paths.get(fileLocation);
    		byte[] readByteArray= Files.readAllBytes(filePath);
    		
			return cryptoManager.decryptString(readByteArray, encryptionType, encryptionMode, paddingType);
			
		} catch (FileNotFoundException e) {
			System.err.println("FILE WAS NOT FOUND");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("LINE READING ERROR");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	
	/**
	 * Writes the content into a file
	 * @param path File to be written
	 */
	
	/**
	 * Writes (and encodes) the current content of the editor into a file 
	 * @param path File to be written
	 * @param encryptionType encryption type to be used to encrypt
	 * @param encryptionMode encryption mode to be used to encrypt
	 * @param paddingType padding type to be used to encrypt
	 */
	public void saveFileInPath(File path, String fileContent, EncryptionType encryptionType,  EncryptionMode encryptionMode, PaddingType paddingType) throws Exception
	{
		if(path != null){
			try {
				//create an object of FileOutputStream
				FileOutputStream fos = new FileOutputStream(path);

				//create an object of BufferedOutputStream
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				
				bos.write(cryptoManager.encryptString(fileContent, encryptionType, encryptionMode, paddingType));
				
				bos.close();
				
			} catch (IOException e) {
				System.err.println("FILE WRITING EXCEPTION");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Write config data
	 * @param dataList data to be written into a config
	 */
	public void writeConfig(List<FileData> dataList)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document config = docBuilder.newDocument();
			
			//Root element
			Element rootElement = config.createElement("STEConfig");
			config.appendChild(rootElement);
			
			for(FileData filedata : dataList){
				//options
				Element fileElement = config.createElement("file");
				rootElement.appendChild(fileElement);
				
				//encryption
				Element encryptionElement = config.createElement("encryption");
				fileElement.appendChild(encryptionElement);
				encryptionElement.appendChild(config.createTextNode(filedata.getEncryptionType() + ""));
				
				//filename
				Element pathElement = config.createElement("filePath");
				pathElement.appendChild(config.createTextNode(filedata.getFilePath()));
				fileElement.appendChild(pathElement);
				
				//hash value
				Element hashElement = config.createElement("hashValue");
				hashElement.appendChild(config.createTextNode(filedata.getHashValue()));
				fileElement.appendChild(hashElement);
				
				//hash value
				Element paddingElement = config.createElement("padding");
				paddingElement.appendChild(config.createTextNode(filedata.getPaddingType() + ""));
				fileElement.appendChild(paddingElement);
				
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(config);
			StreamResult result = new StreamResult(new File("test.xml"));
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * TODO
	 * Loads a config.xml 
	 * @param path Path of the .xml
	 * @return List of the read data
	 */
	public List<FileData> loadConfig(String path)
	{	
		List<FileData> dataList = new ArrayList<FileData>();
		
		
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler() 
			{
				boolean bEncryption = false;
				
				public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException 
				{
					System.out.println("Start Element :" + qName);
	
					if (qName.equalsIgnoreCase("ENCRYPTION")) {
						bEncryption = true;
					}
				}
				
				public void endElement(String uri, String localName, String qName) throws SAXException 
				{
					System.out.println("End Element :" + qName);
				}

				public void characters(char ch[], int start, int length) throws SAXException 
				{
					if (bEncryption) {
						System.out.println("Encryption Type : " + new String(ch, start, length));
						bEncryption = false;
					}
				}	
			};
			
		saxParser.parse(path, handler);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return dataList;
	}
	
}
