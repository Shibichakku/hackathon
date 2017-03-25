package com.syntel.springboot.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipDirectory {

	/*public static void main(String[] args) throws IOException {
		StringBuffer sb =  new StringBuffer();
		//sb.append(Constants.rootPath);
		sb.append(Constants.seperator);
		sb.append("jh");
		sb.append(Constants.seperator);
		sb.append("api");
		sb.append(Constants.seperator);
		sb.append("ejb");
//		sb.append(Constants.seperator);
		String tempPath = "E:\\tes"+""+sb.toString();
		String path =  Constants.rootPath+""+sb.toString();
		File directoryToZip = new File("E:/FileRepo/a");
		cloneFolder(path, tempPath);
//		org.apache.commons.io.FileUtils.cop
		List<File> fileList = new ArrayList<File>();
		
	}
	
*/
	 public static void cloneFolder(String source, String target) {
	        File targetFile = new File(target);
	        if (!targetFile.exists()) {
	          System.out.println( targetFile.mkdir());
	        }
	        for (File f : new File(source).listFiles()) {
	        if (f.isDirectory()) {
	                String append = "\\" + f.getName();
	                System.out.println("Creating '" + target + append + "': "
	                        + new File(target + append).mkdir());
	                cloneFolder(source + append, target + append);
	            }
	        }
	    }   
	 

	public  void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
//					System.out.println("directory:" + file.getCanonicalPath());
				getAllFiles(file, fileList);
			} else {
//					System.out.println("     file:" + file.getCanonicalPath());
			}
		}
	}

	public  void writeZipFile(File directoryToZip, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream(directoryToZip.getName() + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
//		System.out.println("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

}