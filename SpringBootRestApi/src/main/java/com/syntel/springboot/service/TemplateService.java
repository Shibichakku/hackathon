/**
 * 
 */
package com.syntel.springboot.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author rajus
 *
 */
public interface TemplateService {
	
	List<String> parseTemplate(InputStream template);
	
	int saveTemplate(MultipartFile uploadedFileRef, String path) throws Exception;

	int updateMetadata(Map<String, Object> input) throws Exception;
	
	List<String> getTemplate(InputStream template);
	
	 Map<String, String> getMetadata(String path) throws Exception;
	 
	 String updateTemplate(File file,Map<String, String> valueMap ) throws Exception;
	 
//	 updateTemplate();
	
}
