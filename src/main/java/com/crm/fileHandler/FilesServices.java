
package com.crm.fileHandler;


import java.io.File;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface FilesServices {

	String uploadFile(MultipartFile file);

	Resource load(String filePath);

	String delete(String key);

	File getOriginalFilePath(String key);

	List<String> uploadFiles(MultipartFile[] files);

	List<Resource> loadFiles(List<String> filePaths);

	List<String> deleteFiles(List<String> keys);

	String uploadFile(MultipartFile file, String type);

}