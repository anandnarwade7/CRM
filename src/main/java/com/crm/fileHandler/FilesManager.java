package com.crm.fileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.crm.eventDetails.EventDetailsController;
import com.crm.leads.LeadController;

@Service
public class FilesManager implements FilesServices {

	private String serverDocsUrl = "/root/mediadata/Docs/";
//	private String serverDocsUrl = "D:\\Files\\MediaData\\";

	File baseDir;

	File baseDir1;
	File baseDir2;
	File baseDir3;

	public FilesManager() {

//		baseDir1 = new File(serverProfilesUrl);
		baseDir2 = new File(serverDocsUrl);
//		baseDir3 = new File(serverImagesUrl);

//		if (!baseDir1.exists()) {
//			baseDir1.mkdirs();
//		}
		if (!baseDir2.exists()) {
			baseDir2.mkdirs();
		}
//		if (!baseDir3.exists()) {
//			baseDir3.mkdirs();
//		}
		System.out.print("\n Local File repo is initialized ");
	}

	public String uploadFile(MultipartFile file) {
		System.out.print("\n Upload the file to the Local directory ");
		try {
			String createNewFile = createNewFile(file.getInputStream(), file.getOriginalFilename());
			String rowUrl = MvcUriComponentsBuilder.fromMethodName(LeadController.class, "getFile", createNewFile)
					.build().toString();
			System.out.print("\nNew File is created :: " + createNewFile);
			String url = null;
			url = rowUrl.replace(serverDocsUrl, "");
			return url;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String uploadFile(MultipartFile file, String type) {
		System.out.print("\n Check Point 2 Upload the file to the Local directory ");
		try {
			String createNewFile = createNewFile(file.getInputStream(), file.getOriginalFilename(), type);
			System.out.print("\n Check point 12 New File is created :: " + createNewFile);

			String rowUrl = MvcUriComponentsBuilder
					.fromMethodName(EventDetailsController.class, "getFile", createNewFile, type).build().toString();
			System.out.println("Check point 13 url generated " + rowUrl);
			String url = null;

			if ("statusReport".equalsIgnoreCase(type)) {
				System.out.println("Check Point 14 in docs at upload ");
				url = rowUrl.replace(serverDocsUrl, "");
				System.out.println("Check Point 14.1 in docs at upload url generated :: " + url);
			} else if ("architectsLetter".equalsIgnoreCase(type)) {
				System.out.println("Check Point 14 in docs at upload ");
				url = rowUrl.replace(serverDocsUrl, "");
				System.out.println("Check Point 14.1 in docs at upload url generated :: " + url);
			} else if ("invoice".equalsIgnoreCase(type)) {
				System.out.println("Check Point 14 in docs at upload ");
				url = rowUrl.replace(serverDocsUrl, "");
				System.out.println("Check Point 14.1 in docs at upload url generated :: " + url);
			} else if ("receipt".equalsIgnoreCase(type)) {
				System.out.println("Check Point 14 in docs at upload ");
				url = rowUrl.replace(serverDocsUrl, "");
				System.out.println("Check Point 14.1 in docs at upload url generated :: " + url);
			}

			System.out.println("Check Point 16 final URL :: " + url);
			return url;
		} catch (Exception e) {
			System.out.println("Check Point 17 in catch block ");
			e.printStackTrace();
		}
		return null;
	}

	public List<String> uploadFiles(MultipartFile[] files) {
		System.out.print("\n Upload the files to the Local directory ");
		List<String> uploadedFilePaths = new ArrayList<>();
		try {
			for (MultipartFile file : files) {
				String createNewFile = createNewFile(file.getInputStream(), file.getOriginalFilename());
				System.out.print("\nNew File is created :: " + createNewFile);
				uploadedFilePaths.add(createNewFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadedFilePaths;
	}

	public String createNewFile(InputStream initialStream, String orgFile) throws Exception {

//		String uuid = UUID.randomUUID().toString();
		long timeMillis = System.currentTimeMillis();
		File targetFile = null;
		File baseDir;
		baseDir = new File(serverDocsUrl);
		targetFile = new File(baseDir, timeMillis + orgFile);
		System.out.print("\n New file is created :: " + targetFile.getAbsolutePath());

		OutputStream outStream = new FileOutputStream(targetFile);

		byte[] buffer = new byte[8 * 1024];
		int bytesRead;
		while ((bytesRead = initialStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		initialStream.close();
		outStream.close();
		return targetFile.getAbsolutePath();
	}

	public String createNewFile(InputStream initialStream, String orgFile, String type) throws Exception {
		System.out.println("Check Point 3");
//		String uuid = UUID.randomUUID().toString();
		long timeMillis = System.currentTimeMillis();
		File targetFile = null;
		File baseDir;
		if ("statusReport".equalsIgnoreCase(type)) {
			System.out.println("Check Point 5 if docs");
			baseDir = new File(serverDocsUrl);
			targetFile = new File(baseDir, timeMillis + orgFile);
			System.out.println("Check Point 5.1 if docs" + targetFile);
		} else if ("architectsLetter".equalsIgnoreCase(type)) {
			System.out.println("Check Point 5 if docs");
			baseDir = new File(serverDocsUrl);
			targetFile = new File(baseDir, timeMillis + orgFile);
			System.out.println("Check Point 5.1 if docs" + targetFile);
		} else if ("invoice".equalsIgnoreCase(type)) {
			System.out.println("Check Point 5 if docs");
			baseDir = new File(serverDocsUrl);
			targetFile = new File(baseDir, timeMillis + orgFile);
			System.out.println("Check Point 5.1 if docs" + targetFile);
		} else if ("receipt".equalsIgnoreCase(type)) {
			System.out.println("Check Point 5 if docs");
			baseDir = new File(serverDocsUrl);
			targetFile = new File(baseDir, timeMillis + orgFile);
			System.out.println("Check Point 5.1 if docs" + targetFile);
		}
		System.out.print("\n check point 7 New file is created :: " + targetFile.getAbsolutePath());

		OutputStream outStream = new FileOutputStream(targetFile);
		System.out.println("Check Point 8");

		byte[] buffer = new byte[8 * 1024];
		int bytesRead;
		while ((bytesRead = initialStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		System.out.println("Check Point 10");
		initialStream.close();
		outStream.close();
		System.out.println("Check Point 11:: " + targetFile.getAbsolutePath());
		return targetFile.getAbsolutePath();
	}

	public Resource load(String filePath) {
		try {
			System.out.println("\n Getting absolute path :: " + filePath);
			Path file = Paths.get(filePath);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}

	}

	public List<Resource> loadFiles(List<String> filePaths) {
		List<Resource> resources = new ArrayList<>();
		for (String filePath : filePaths) {
			try {
				System.out.println("\n Getting absolute path :: " + filePath);
				Path file = Paths.get(filePath);
				Resource resource = new UrlResource(file.toUri());
				if (resource.exists() || resource.isReadable()) {
					resources.add(resource);
				} else {
					throw new RuntimeException("Could not read the file: " + filePath);
				}
			} catch (MalformedURLException e) {
				throw new RuntimeException("Error: " + e.getMessage());
			}
		}
		return resources;
	}

	public String delete(String key) {
		System.out.print("\n Delete file with name key " + key);

		File ff = this.findFile(key);

		if (ff != null) {
			System.out.print("\nFile getting Deleted ::" + ff.getAbsolutePath());

			ff.delete();
			return key + " file found and deleted ";
		}
		return " No file found";

	}

	public List<String> deleteFiles(List<String> keys) {
		List<String> results = new ArrayList<>();
		for (String key : keys) {
			results.add(delete(key));
		}
		return results;
	}

	public File getOriginalFilePath(String key) {
		{
			return this.findFile(key);
		}
	}

	public File findFile(String key) {

		System.out.println("\n" + baseDir1);

		File[] matchingFiles = baseDir1.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String s) {

				String onlyFile = s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;

				return onlyFile.equalsIgnoreCase(key);

			}
		});

		if (matchingFiles.length == 0)
			return null;

		return matchingFiles[0];
	}

	public boolean deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				return file.delete();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
