import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

public class FileManager {
	
	public String openFileFromPath(String filePath)
	{
    	try {
    		
    		
			FileReader reader = new FileReader(filePath);
			BufferedReader bufferdReader = new BufferedReader(reader);
			
			String lineToRead = "";
			String readText = "";
			
			lineToRead = bufferdReader.readLine();
			
			while(lineToRead != null)
			{
				readText = readText + lineToRead;
				readText = readText + "\n";
				lineToRead = bufferdReader.readLine();
			}
			
			bufferdReader.close();
			
			return readText;
		} catch (FileNotFoundException e) {
			System.err.println("FILE WAS NOT FOUND");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("LINE READING ERROR");
			e.printStackTrace();
		}
    	
    	return null;
	}
	
	
	/**
	 * Writes the content into a file
	 * @param path File to be written
	 */
	public void saveFileInPath(File path, String fileContent)
	{
		if(path != null){
			try {
				FileWriter fileWriter = new FileWriter(path);
				fileWriter.write(fileContent);
				
				fileWriter.close();
				
			} catch (IOException e) {
				System.err.println("FILE WRITING EXCEPTION");
				e.printStackTrace();
			}
		}
	}
	
	public void writeConfig()
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document config = docBuilder.newDocument();
			
			//Root element
			Element rootElement = config.createElement("STEConfig");
			config.appendChild(rootElement);
			
			//options
			Element optionsElement = config.createElement("options");
			rootElement.appendChild(optionsElement);
			
			//encryption
			Element encryptionElement = config.createElement("encryption");
			optionsElement.appendChild(encryptionElement);
			encryptionElement.appendChild(config.createTextNode("AES"));
			
			//filename
			Element fileNameElement = config.createElement("fileName");
			fileNameElement.appendChild(config.createTextNode("Peter Hans.txt"));
			rootElement.appendChild(fileNameElement);
			
			
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
	
	public void loadConfig(String path)
	{
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document loadedConfig = docBuilder.parse(new File(path));
			
			NodeList nList = loadedConfig.getElementsByTagName("STEConfig");
			
			
			Node nNode;
			for(int nodeIndex = 0; nodeIndex < nList.getLength(); nodeIndex ++)
			{
				 nNode = nList.item(nodeIndex);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					
					System.out.println("encryption : " + eElement.getAttribute("encryption"));
					System.out.println("encryption : " + eElement.getElementsByTagName("encryption").item(0).getTextContent());
					/*
					System.out.println("Staff id : " + eElement.getAttribute("id"));
					System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
					System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
					System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
					System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
					*/
				}
			}
			
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
	}
	
}
