package com.sonar.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;

public class FileUtils {
	
	private static final Logger LOGGER	=  Logger.getLogger(FileUtils.class);
	
	public static BufferedReader getBufferedReader(String fileName) {
		InputStream is = getInputStream(fileName);
		return new BufferedReader(new InputStreamReader(is));
	}
	
	public static String loadFromFile(String fileName) {
		InputStream input = getInputStream(fileName);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
	    byte[] buffer = new byte[4096 * 1024];
	    int n = 0;
	    try {
			while (-1 != (n = input.read(buffer))) {
			    output.write(buffer, 0, n);
			}
		} catch (IOException e) {
			return null;
		}
	    return new String(output.toByteArray());
	}

	public static InputStream getInputStream(String fileName) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Load file from = [" + fileName + "]");
		}
		if(!isAbsolutePath(fileName)){
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		}

		File f = null;
		if (isAbsolutePath(fileName)) {
			f = new File(fileName);
		} else {
			f = new File(getPath(fileName) + fileName);
		}
		try {
			if (!f.exists()) {
				f.createNewFile();
			}
			return new FileInputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean isAbsolutePath(String fileName) {
		if(fileName.startsWith("./") || Character.isLetter(fileName.charAt(0))){
			return false;
		}
		return true;
	}
	
	public static FileOutputStream getFileOutputStream(String fileName) {
		
		File f = null;
		if (!StringUtils.contains(fileName, "/")) {
			f = new File(getPath(fileName) + fileName);
		} else {
			f = new File(fileName);
		}
		try {
			if(!f.exists()){
				f.createNewFile();
			}
			return new FileOutputStream(f);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static PrintStream getPrintStream(String fileName) {
		return new PrintStream(getFileOutputStream(fileName));
	}

	public static String getPath(String fName) {
		ClassLoader loader = getClassLoader();
		return loader.getResource("//").getPath();
	}

	private static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static void close(PrintStream out) {
		out.flush();
		out.close();
	}

	public static void close(InputStream is) {
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void close(BufferedReader br) {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delete(String keysFile) {
		File f = new File(keysFile);
		if(f.exists()){
			f.delete();
		}
	}
	
	public static void createFolder(File file){
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	public static void createFile(File file){
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
