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
@MultipartConfig(fileSizeThreshold = 20971520)
public class RestApiController {

	public static final Logger logger = LoggerFactory.getLogger(RestApiController.class);

	@Autowired
	UserService userService; //Service which will do all data retrieval/manipulation work

	@Autowired
	TemplateService templateService;

	// -------------------Retrieve All Users---------------------------------------------

	@RequestMapping(value = "/template/", method = RequestMethod.GET)
	public ResponseEntity<List<User>> sampleTemlate() {
		List<User> users = userService.findAllUsers();
		System.out.println("testing api");
		if (users.isEmpty()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}
	
	
	@CrossOrigin(origins = "http://67ca2d52.ngrok.io")
	@RequestMapping(value = "/templateMetadata/", method = RequestMethod.GET)
	public String getTemlateMetadata(@RequestParam("accountName") String accountName,@RequestParam("templateType") String templateType,@RequestParam("templateName") String templateName, HttpServletResponse response) throws Exception {
		List<User> users = userService.findAllUsers();
		System.out.println("testing api");
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
		System.out.println(keywords);
		return json;
	}
	
	
	@CrossOrigin(origins = "http://67ca2d52.ngrok.io")
	@RequestMapping(value = "/getSample/", method = RequestMethod.GET)
	public void getSample(@RequestParam Map<String, String> json, HttpServletResponse response) throws Exception {
		Map<String, String> users = null;//userService.findAllUsers();
		InputStream fis = null;
		String newExtension= ".java";
		System.out.println("test get sample"+json);
		String accountName = json.get("accountName");
		String templateType = json.get("templateType");
		String templateName = json.get("templateName");
		StringBuffer sb =  new StringBuffer();
		//sb.append(Constants.rootPath);
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
//		FileUtils.copy(tempDirectory,directoryToZip);
		List<File> fileList = new ArrayList<File>();
		ZipDirectory zip = new ZipDirectory(); 
		zip.getAllFiles(tempDirectory, fileList);
		
		Map<String, Map<String, String>> replacableKeys = parserRequestParam(json);
		for (File file : fileList) {
			if (!file.isDirectory()) { 
				Map<String, String> valueMap = replacableKeys.get(file.getName());
				String out = templateService.updateTemplate(file,valueMap);
				System.out.println(file.getAbsolutePath());
				System.out.println(StringUtils.stripFilenameExtension(file.getAbsolutePath()));
				System.out.println(file.getName());
				FileWriter writer = new FileWriter(StringUtils.stripFilenameExtension(file.getAbsolutePath())+""+newExtension);
		        writer.write(out);
		        writer.close();
		        org.apache.commons.io.FileUtils.forceDeleteOnExit(new File(file.getAbsolutePath()));
			}
		}
		
		zip.writeZipFile(directoryToZip, fileList);
		
		 fis = new FileInputStream(directoryToZip.getName() + ".zip");
		 response.addHeader("Content-disposition", "attachment;filename="+directoryToZip.getName()+".zip");
		 response.setContentType("application/zip");
			IOUtils.copy(fis, response.getOutputStream());
			response.flushBuffer();
		System.out.println(replacableKeys);
		users = new HashMap<String, String>();
		users.put("k1", "v1");
		users.put("k2", "v2");
		users.put("k3", "v3");
		
		System.out.println("testing api");
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
	
	// -------------------Retrieve Single User------------------------------------------
	 @CrossOrigin(origins = "http://67ca2d52.ngrok.io")
	@RequestMapping(value = "/downloadTemplate/", method = RequestMethod.GET, produces = "application/zip")
	public void downloadTemplate(@RequestParam("accountName") String accountName,@RequestParam("templateType") String templateType,@RequestParam("templateName") String templateName, HttpServletResponse response) throws IOException {
		logger.info("Fetching User with id {}");
		InputStream fis = null;
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
		String path = sb.toString();
		//FIXME replace keywords
		
		File directoryToZip = new File(path);
		List<File> fileList = new ArrayList<File>();
		ZipDirectory zip = new ZipDirectory(); 
		
		zip.getAllFiles(directoryToZip, fileList);
		System.out.println("---Creating zip file");
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
	 @CrossOrigin(origins = "http://67ca2d52.ngrok.io")
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
	
	 @CrossOrigin(origins = "http://67ca2d52.ngrok.io")
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