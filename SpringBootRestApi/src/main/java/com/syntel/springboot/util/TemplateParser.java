package com.syntel.springboot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.coyote.http11.filters.SavedRequestInputFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TemplateParser {
	
	public static String Regx1 = "(.*?)(<<)\\s*(.*?)\\s*(>>)";

	/*public static void main(String[] args) throws Exception {
		new TemplateParser().updateTemplate();
	}*/

	/**
	 * 
	 */
	protected void parseTemplate() {
		List<String> keywords = new ArrayList<String>();
		Pattern p = Pattern.compile(Regx1, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL);
		InputStream fileName = TemplateParser.class.getResourceAsStream("Template.txt");
        Scanner scanner = new Scanner(fileName);
        //read file line by line
        scanner.useDelimiter(System.getProperty("line.separator"));
        while(scanner.hasNext()){
        	
        	String newLine = scanner.next();
        	Matcher m = p.matcher(newLine);
        	while(m.find()){
        		keywords.add(m.group(3));
        	}
        }
        System.out.println(keywords);
        scanner.close();
	}
	
	

	protected void updateTemplate() throws Exception {
		List<String> keywords = new ArrayList<String>();
		Pattern p = Pattern.compile(Regx1, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE|Pattern.DOTALL);
		InputStream fileName = TemplateParser.class.getResourceAsStream("Template.txt");
        Scanner scanner = new Scanner(fileName);
        //read file line by line
        scanner.useDelimiter(System.getProperty("line.separator"));
        StringBuffer sb = new StringBuffer();
      Map<String, String> keywordValue = new  HashMap<String, String>();
      keywordValue.put("className", "sample");
      keywordValue.put("superClass", "SuperSample");
      
      
        while(scanner.hasNext()){
        	
        	String newLine = scanner.next();
        	Matcher m = p.matcher(newLine);
        	while(m.find()){
        		
        		String key =  m.group(3);
        		String replacableKey = "<<"+ m.group(3)+">>";
        		
        		newLine = 	newLine.replace(replacableKey, (String) keywordValue.get(key));
        		keywords.add(m.group(3));
        	}
        	sb.append(newLine);
        	sb.append('\n');
        }
        System.out.println(keywords);
        scanner.close();
       byte[] output =  sb.toString().getBytes();
       saveFragment("E://newcommand1.java",output);
        FileWriter writer = new FileWriter("E://newcommand.java");
        writer.write(sb.toString());
        writer.close();
        System.out.println("Completed");
	}
	
	public void saveFragment(String fullFileName, byte[] fragment) {
		System.out.println("start");
		FileOutputStream file = null; // Used to hold file for input
		try {
			try {
				file = new FileOutputStream(new File(fullFileName));
				file.write(fragment);
				file.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			try {
				if (file != null) {
					file.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	
}
