package me.astri.discordgarou.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
	public static String Read(String filePath) {
		BufferedReader br = null;
		String str = "";
		
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(filePath));
			
			while ((sCurrentLine = br.readLine()) != null) {
				str += sCurrentLine + "\n";
			}
			
		} catch (IOException e) {e.printStackTrace();}
		finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {e.printStackTrace();}			
		}
		return str;
	}
	
	public static void write(String path, String content) {
		FileWriter fw;
		try {
			fw = new FileWriter(path);
			fw.write(content);		
			fw.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}
