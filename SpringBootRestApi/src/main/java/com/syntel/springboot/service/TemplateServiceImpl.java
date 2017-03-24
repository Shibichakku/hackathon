package com.syntel.springboot.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.syntel.springboot.service.TemplateService;
import com.syntel.springboot.util.Constants;
import com.syntel.springboot.util.UploadException;

/**
 * @author Sreenivasa Raju K
 *
 */
@Service("templateService")
public class TemplateServiceImpl implements TemplateService {
	
	public static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);


	@Override
	public List<String> parseTemplate(InputStream template) {
		List<String> keywords = new ArrayList<String>();
		
		Scanner scanner = new Scanner(template);
			// read file line by line
			scanner.useDelimiter(System.getProperty("line.separator"));
			while (scanner.hasNext()) {

				String newLine = scanner.next();
				Matcher m = Constants.pattern.matcher(newLine);
				while (m.find()) {
					keywords.add(m.group(3));
				}
			}
			System.out.println(keywords);
		
		return keywords;
	}

	@Override
	public int saveTemplate(MultipartFile template, String path) throws Exception {

		File folder = new File(path);
		 if (!folder.exists()) {
			 folder.mkdirs();
		 }
		 
		 path = path+template.getOriginalFilename();
		 File outputFile = new File(path);
		 InputStream stream = null;
		int totalBytes = 0;
		FileOutputStream writer = null;
		byte[] buffer = new byte[1000];
		try {
			writer = new FileOutputStream(outputFile);
			int bytesRead = 0;
			stream = template.getInputStream();
			while ((bytesRead = stream.read(buffer)) != -1) {
				writer.write(buffer);
				totalBytes += bytesRead;
			}

		} catch (FileNotFoundException e) {
			throw new UploadException("File not found", e);
		} catch (IOException e) {
			throw new UploadException("Unable to load exception", e);
		} finally {
			if (stream != null) {
				stream.close();
			}

			if (writer != null) {
				writer.close();
			}
		}
		return totalBytes;
	}

	@Override
	public List<String> getTemplate(InputStream template) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateMetadata(Map<String, Object> input) throws Exception {
		
//		File metadata = new File((String)input.get("path")+"metadata.properties");
		Properties prop = new Properties();
			FileOutputStream output = new FileOutputStream((String)input.get("path")+"metadata.properties");
			String keywords = StringUtils.collectionToCommaDelimitedString((List)input.get("keywords"));
			// set the properties value
			prop.setProperty((String)input.get("templateName"),keywords );
			
			// save properties to project root folder
			prop.store(output, null);
		return 0;
	}

	@Override
	public Map<String, String> getMetadata(String path) throws Exception {

		File metadata = new File(path + "metadata.properties");
		InputStream input = new FileInputStream(metadata);
		 Map<String, String> keyword = new  HashMap<String, String>();
		if (input == null) {
			return null;
		}
		Properties prop = new Properties();
		prop.load(input);

		Iterator iterator = prop.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			System.out.println(key);
			String value = prop.getProperty(key);
			System.out.println(value);
			keyword.put(key, value);

		}
		
		input.close();
		return keyword;
	}

	@Override
	public  String updateTemplate(File file, Map<String, String> keywordValue ) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		 Scanner scanner = new Scanner(fis);
	        //read file line by line
	        scanner.useDelimiter(System.getProperty("line.separator"));
	        StringBuffer sb = new StringBuffer();
	      
	      
	        while(scanner.hasNext()){
	        	
	        	String newLine = scanner.next();
	        	Matcher m = Constants.pattern.matcher(newLine);
	        	while(m.find()){
	        		
	        		String key =  m.group(3);
	        		String replacableKey = "<<"+ key+">>";
	        		newLine = 	newLine.replace(replacableKey, (String) keywordValue.get(key));
//	        		keywords.add(m.group(3));
	        	}
	        	sb.append(newLine);
	        	sb.append('\n');
	        }
//	        System.out.println(keywords);
	        return  sb.toString();
		// TODO Auto-generated method stub
	}


}
