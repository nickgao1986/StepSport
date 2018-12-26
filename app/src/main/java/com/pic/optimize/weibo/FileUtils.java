package com.pic.optimize.weibo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtils {

	//path /data/data/com.sina.weibo/cache/1316202912_favorite
	public static Object load(String path) {
		Object obj = null;
		File file = new File(path);
		try {
			/*if(file != null){
				file.mkdirs();
			}*/
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				try {
					obj = ois.readObject();
				}
				catch (ClassNotFoundException e) {
				}
				ois.close();
			}	
		}catch (IOException e) {
			}
		return obj;
	}
	
	public static void saveObject(String text, FileOutputStream fos) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(text);
			System.out.println("====saveobject text="+text);
			oos.flush();
			oos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static String loadObject(String str) {
	    Object obj = null; 
		File f = new File(str);
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (String)obj;
	}
	
	public static boolean deleteFile(String str) {
		File f = new File(str);
		return f.delete();
	}
	
	public static void makesureFileExist(String str) {
		int index = str.lastIndexOf("/");
		String path = str.substring(0,index); 
		File f = new File(path);
		f.mkdirs();
		
		File f1 = new File(str);
		try {
			f1.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getSDPath(){
	       File sdDir = null;
	       boolean sdCardExist = Environment.getExternalStorageState()  
	                           .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
	       if(sdCardExist)  
	       {               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录
	      }  
	       if(sdDir!=null){
	    	   ensureFold(sdDir.getAbsolutePath());
	    	   return sdDir.toString();
	       }else
	    	   return null;
	      
	}
	
	private static void ensureFold(String sdir) {
		String s1 = sdir + Constants.PORTRAIT_DIR_SUFFIX;
		String s2 = sdir + Constants.PREVIEW_BMP_DIR_SUFFIX;
		File file = new File(s1);
		System.out.println("====s1="+s1);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		File file2 = new File(s2);
		if(!file.exists()) {
			file.mkdirs();
		}
		
	}
	
	public static boolean doesExisted(File f) {
		if(f.exists()) {
			return true;
		}
		return false;
	}
	
	public static boolean doesExisted(String path) {
		File f = new File(path);
		if(f.exists()) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isSdCardAvailable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	public static File file_put_contents(InputStream is,File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			int len = -1;
			while((len = is.read(buffer)) != -1)
			{
			 os.write(buffer,0,len);
			}
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	// path1和path2都应该是File的path
	public static void copy(String inputFilePath, String path2) {
		String separator = File.separator;

		int index = path2.lastIndexOf(separator);
		String path = path2.substring(0, index);

		File temp = new File(path);
		if (!temp.exists()) {
			temp.mkdirs();
		}

		File tempFile = new File(path2);
		try {
			tempFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			inputStream = new FileInputStream(inputFilePath);
			outputStream = new FileOutputStream(path2);
			byte buffer[] = new byte[4 * 1024];
			int len = -1;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			outputStream.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public static void save(Object obj, String path) {
		try {
			File f = new File(path);
			/*if(f != null){
				f.mkdirs();
				f.createNewFile();
			}*/
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
		}
		catch (IOException e) {
		}
	}
	
	

	public static void save(String message, String name) {
		try {
		String path = Utils.getSDPath()+"/"+name;
		System.out.println("====path="+path);
		System.out.println("===message="+message);
		File f = new File(path);
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write(message);
		bw.close();
		}catch (IOException e) {
		}
	}
}
