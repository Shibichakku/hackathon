package com.syntel.springboot.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.syntel.springboot.model.User;
import com.syntel.springboot.service.TemplateService;
import com.syntel.springboot.service.UserService;
import com.syntel.springboot.util.Constants;
import com.syntel.springboot.util.ZipDirectory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
@MultipartConfig(fileSizeThreshold = 20971520)
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	UserService userService; //Service which will do all data retrieval/manipulation work

	@Autowired
	TemplateService templateService;

	
	
	/**
	 * @param accountName
	 * @param templateType
	 * @param templateName
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/templateMetadata/", method = RequestMethod.GET)
	public String getTemlateMetadata(@RequestParam("accountName") String accountName,@RequestParam("templateType") String templateType,@RequestParam("templateName") String templateName, HttpServletResponse response) throws Exception {
		List<User> users = userService.findAllUsers();
		StringBuffer sb =  new StringBuffer();
		sb.append(Constants.rootPath);
		sb.append(Constants.seperator);
		sb.append(accountName);
		sb.append(Constants.seperator);
		sb.append(templateType);
		sb.append(Constants.seperator);
		sb.append(templateName);
		sb.append(Constants.seperator);
		String path = sb.toString();
		Map<String, String> keywords = templateService.getMetadata(path);
		
		String json = new ObjectMapper().writeValueAsString(keywords);
		return json;
	}
	
	
	/**
	 * @param json
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/getSample/", method = RequestMethod.GET)
	public void getSample(@RequestParam Map<String, String> json, HttpServletResponse response) throws Exception {
		InputStream fis = null;
		String newExtension= ".java";
		String accountName = json.get("accountName");
		String templateType = json.get("templateType");
		String templateName = json.get("templateName");
		StringBuffer sb =  new StringBuffer();
		sb.append(Constants.seperator);
		sb.append(accountName);
		sb.append(Constants.seperator);
		sb.append(templateType);
		sb.append(Constants.seperator);
		sb.append(templateName);
		sb.append(Constants.seperator);
		
		String tempPath = Constants.tempRootPath+""+sb.toString();
		String path =  Constants.rootPath+""+sb.toString();
		File directoryToZip = new File(path);
		File tempDirectory = new File(tempPath);
		tempDirectory.mkdirs();
		org.apache.commons.io.FileUtils.copyDirectory(directoryToZip, tempDirectory);
		List<File> fileList = new ArrayList<File>();
		ZipDirectory zip = new ZipDirectory(); 
		zip.getAllFiles(directoryToZip, fileList);
		
		List<String> cleanedfolders = new ArrayList<String>();
		Map<String, Map<String, String>> replacableKeys = parserRequestParam(json);
		for (File file : fileList) {
			
			if (!file.isDirectory()) { 
				
				String filepath = file.getAbsolutePath();
				filepath = filepath.replace(file.getName(), "");
				filepath = filepath.replace(Constants.rootPath,Constants.tempRootPath );
				if(!cleanedfolders.contains(filepath)){
					cleanedfolders.add(filepath);
//					org.apache.commons.io.FileUtils.cleanDirectory(new File(filepath));
				}
				
				Map<String, String> valueMap = replacableKeys.get(file.getName());
				String out = templateService.updateTemplate(file,valueMap);
				String s = StringUtils.stripFilenameExtension(file.getAbsolutePath());
				s= s.replace(path, tempPath);
				
				if("metadata.properties".equalsIgnoreCase(file.getName())){
					org.apache.commons.io.FileUtils.forceDelete(new File(filepath+file.getName()));
					continue;
				}
				
				FileWriter writer = new FileWriter(filepath+StringUtils.stripFilenameExtension(file.getName())+""+newExtension);
		        writer.write(out);
		        writer.close();
				org.apache.commons.io.FileUtils.forceDelete(new File(filepath+file.getName()));

			}
		}
		
		zip.writeZipFile(tempDirectory, fileList);
		
		 fis = new FileInputStream(tempDirectory.getName() + ".zip");
		 response.addHeader("Content-disposition", "attachment;filename="+directoryToZip.getName()+".zip");
		 response.setContentType("application/zip");
		 IOUtils.copy(fis, response.getOutputStream());
		 response.flushBuffer();
	}

	

	/**
	 * @param json
	 * @return
	 */
	protected Map<String, Map<String, String>> parserRequestParam(Map<String, String> json) {
		String rkey[]= {""};
		Map<String, Map<String, String>> replacableKeys = new HashMap<String, Map<String,String>>();
		Map<String, String> fileKeys= null;
		String fileName="";
		for(String key : json.keySet()){
			rkey = StringUtils.split(key, "-");
			String value= json.get(key);
			if(rkey == null || rkey.length <=0){
				continue;
			}
			if(!fileName.equals(rkey[1])){
				fileKeys= new HashMap<String, String>();
				fileName = rkey[1];
				replacableKeys.put(fileName, fileKeys);
			}
			fileKeys.put(rkey[0], value);
		}
		return replacableKeys;
	}
	
	/**
	 * @param accountName
	 * @param templateType
	 * @param templateName
	 * @param downloadType
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/downloadTemplate/", method = RequestMethod.GET, produces = "application/zip")
	public void downloadTemplate(@RequestParam("accountName") String accountName,@RequestParam("templateType") String templateType,@RequestParam("templateName") String templateName, @RequestParam("downloadType") String downloadType, HttpServletResponse response) throws IOException {
		logger.info("Fetching User with id {}");
		InputStream fis = null;
		try {
		// Temp path.
		StringBuffer sb =  new StringBuffer();
		if("temp".equals(downloadType)){
			sb.append(Constants.tempRootPath);
		}else{
			sb.append(Constants.rootPath);
		}
		sb.append(Constants.seperator);
		sb.append(accountName);
		sb.append(Constants.seperator);
		sb.append(templateType);
		sb.append(Constants.seperator);
		sb.append(templateName);
		sb.append(Constants.seperator);
		String path = sb.toString();
		//FIXME replace keywords
		
		File directoryToZip = new File(path);
		List<File> fileList = new ArrayList<File>();
		ZipDirectory zip = new ZipDirectory(); 
		
		zip.getAllFiles(directoryToZip, fileList);
		zip.writeZipFile(directoryToZip, fileList);
		
					 fis = new FileInputStream(directoryToZip.getName() + ".zip");
					 response.addHeader("Content-disposition", "attachment;filename="+directoryToZip.getName()+".zip");
					 response.setContentType("application/zip");
						IOUtils.copy(fis, response.getOutputStream());
						response.flushBuffer();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
		
	}

	
	
		// -------------------Create a User-------------------------------------------
	/**
	 * @param uploadedFileRef
	 * @return
	 */
	@RequestMapping(value = "/parseTemplate/", method = RequestMethod.POST)
	public ResponseEntity<?> parseTemplate(@RequestParam("uploadedFile") MultipartFile uploadedFileRef) {

		logger.info("Upload template ---> Entry ");

		List<String> keywords = new ArrayList<String>();
		InputStream template = null;

		try {
			// Create the input stream to uploaded file to read data from it.
			template = (FileInputStream) uploadedFileRef.getInputStream();
			keywords = templateService.parseTemplate(template);
			// FIXME Need to add empty logic
			if (keywords.isEmpty()) {

			}
			

		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<List<String>>(keywords, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<List<String>>(keywords, HttpStatus.PRECONDITION_FAILED);
		}
		logger.info("Upload template  ---> Exit ");
		return new ResponseEntity<List<String>>(keywords, HttpStatus.OK);
	}
	
	/**
	 * @param uploadedFileRef
	 * @param accountName
	 * @param packageString
	 * @param templateType
	 * @param templateName
	 * @param ucBuilder
	 * @return
	 */
	@RequestMapping(value = "/saveTemplate/", method = RequestMethod.POST)
	public ResponseEntity<?> saveTemplate(@RequestParam("uploadedFile") MultipartFile uploadedFileRef,@RequestParam("accountName") String accountName,@RequestParam("package") String packageString,@RequestParam("templateType") String templateType,@RequestParam("templateName") String templateName, UriComponentsBuilder ucBuilder) {
		 System.out.println("saveTemplate template hit");
		logger.info("Upload template ---> Entry ");
		
		InputStream template = null;
		try {
			
					// Temp path.
					StringBuffer sb =  new StringBuffer();
					sb.append(Constants.rootPath);
					sb.append(Constants.seperator);
					sb.append(accountName);
					sb.append(Constants.seperator);
					sb.append(templateType);
					sb.append(Constants.seperator);
					sb.append(templateName);
					sb.append(Constants.seperator);
					
					StringBuffer templatePtah =  new StringBuffer();
					templatePtah.append(sb.toString());
					templatePtah.append(packageString.replace(".", "/"));
					templatePtah.append(Constants.seperator);
					String path = templatePtah.toString();
					
			List<String> keywords = new ArrayList<String>();
			template = (FileInputStream) uploadedFileRef.getInputStream();
			keywords = templateService.parseTemplate(template);
			
			templateService.saveTemplate(uploadedFileRef, path);
			//fileName: keywords
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("templateName", uploadedFileRef.getOriginalFilename());
			input.put("keywords", keywords);
			input.put("path", sb.toString());
			templateService.updateMetadata(input);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<List<String>>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
		}
		logger.info("Upload template  ---> Exit ");
		return new ResponseEntity<List<String>>(HttpStatus.OK);
	}


}