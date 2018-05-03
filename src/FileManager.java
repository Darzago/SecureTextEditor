import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    	
    	return "You shouldnt be seeing this o.O";
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
}
