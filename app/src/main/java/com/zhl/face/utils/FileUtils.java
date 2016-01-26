package com.zhl.face.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.zhl.face.app.AppConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * 文件工具类
 * @author zhouhl
 */
public class FileUtils {

	public static final String TAG = "FileUtils";
	
	public static final String APP_DIR 
	= Environment.getExternalStorageDirectory().getPath() + "/快发表情/";

	public static final String SERIES_DIR   = APP_DIR + "表情系列/";
	public static final String TEMP_DIR     = APP_DIR + "temp/";
	public static final String FACE_DIR     = APP_DIR + "face/";
	public static final String DOWNLOAD_DIR = APP_DIR + "download/";

	public static final String HEAD_GIF = "474946";
	public static final String HEAD_JPG = "ffd8ff";
	public static final String HEAD_PNG = "89504e";
	
	public static final int THUMB_SIZE = 100;


	private static FileUtils mInstance;

	private FileUtils(){};

	public static FileUtils getInstance(){
		if (mInstance == null) {
			mInstance = new FileUtils();
		}
		return mInstance;
	}

	public void createFloder(String path){
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void createAppDir(){
		createFloder(APP_DIR);
		createFloder(FACE_DIR);
		createFloder(SERIES_DIR);
		createFloder(DOWNLOAD_DIR);
		createFloder(TEMP_DIR);
	}

	/**
	 * 删除文件或文件夹
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			Log.i(this.getClass().getName() , "文件不存在");
		}
	}

	/** 
	 * 复制单个文件 
	 * @param oldPath String 原文件路径 如：c:/fqf.txt 
	 * @param newPath String 复制后路径 如：f:/fqf.txt 
	 * @return boolean 
	 */
	public boolean copyFile(String oldPath, String newPath) { 
		boolean isok = true;
		try { 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1024]; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					fs.write(buffer, 0, byteread); 
				} 
				fs.flush(); 
				fs.close(); 
				inStream.close(); 
			}
			else
			{
				isok = false;
			}
		} 
		catch (Exception e) { 
			isok = false;
		} 
		return isok;
	} 

	public boolean isGif(String path){
		return getFileType(path).contains("gif");
	}

	public void saveFile(String path,byte[] data){
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(path);
			fileOutputStream.write(data);
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 清空temp下的所有文件
	 */
	public void clearTemp(){
		File file = new File(TEMP_DIR);
		File files[] = file.listFiles(); 
		if (files != null) {
			if (files.length >= 100) {
				for (int i = 0; i < files.length; i++) { 
					this.deleteFile(files[i]); 
				}
			}
		}
	}

	public String getFileType(String path){
		String type = ".gif";
		if (path != null && !"".equals(path)) {
			FileInputStream is;
			try {
				is = new FileInputStream(path);
				byte[] b = new byte[3];
				is.read(b, 0, b.length);
				String head = Utils.bytesToHexString(b);
				if (HEAD_GIF.equals(head)) {
					type = ".gif";
				}else if (HEAD_JPG.equals(head)) {
					type = ".jpg";
				}else if (HEAD_PNG.equals(head)) {
					type = ".png";
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		return type;
	}
	
	public void assetsToSd(Context context,String fileName,String newPath){
		InputStream myInput;  
        OutputStream myOutput;
        if (!new File(newPath + fileName).exists()) {
        	try {
        		myOutput = new FileOutputStream(newPath);
        		myInput = context.getAssets().open(fileName);  
        		byte[] buffer = new byte[1024];  
        		int length = myInput.read(buffer);
        		while(length > 0)
        		{
        			myOutput.write(buffer, 0, length); 
        			length = myInput.read(buffer);
        		}
        		myOutput.flush();  
        		myInput.close();  
        		myOutput.close();   
        	} catch (FileNotFoundException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}  
		}
	}
	
	public static byte[] readFromFile(String fileName, int offset, int len) {
		if (fileName == null) {
			return null;
		}
		File file = new File(fileName);
		if (!file.exists()) {
            Logger.i("readFromFile: file not found", 1);
			return null;
		}
		if (len == -1) {
			len = (int) file.length();
		}
        Logger.d("readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));
		if(offset <0){
            Logger.e("readFromFile invalid offset:" + offset,1);
			return null;
		}
		if(len <=0 ){
            Logger.e("readFromFile invalid len:" + len,1);
			return null;
		}
		if(offset + len > (int) file.length()){
            Logger.e("readFromFile invalid file len:" + file.length(),1);
			return null;
		}
		byte[] b = null;
		try {
			RandomAccessFile in = new RandomAccessFile(fileName, "r");
			b = new byte[len]; // 创建合适文件大小的数组
			in.seek(offset);
			in.readFully(b);
			in.close();
		} catch (Exception e) {
            Logger.e("readFromFile : errMsg = " + e.getMessage(),1);
			e.printStackTrace();
		}
		return b;
	}
	
	public void createThumb(String path,String thumbPath){
		if (path != null&&thumbPath != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			if (bitmap != null) {
                int oldH = bitmap.getHeight();
                int oldW = bitmap.getWidth();
                float scale = 1.0f;
                if (oldH > THUMB_SIZE || oldW > THUMB_SIZE) {
                    if (oldH >= oldW) {
                        scale = THUMB_SIZE / (oldH * 1.0f);
                    }else {
                        scale = THUMB_SIZE / (oldW * 1.0f);
                    }
                }
                int newH = (int) (oldH * scale);
                int newW = (int) (oldW * scale);
				Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, newW,newH);
				if (bitmap != null) {
					try {
						FileOutputStream fileOutputStream = new FileOutputStream(thumbPath);
						thumb.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
						fileOutputStream.flush();
						fileOutputStream.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (thumb != null) {
					thumb.recycle();
				}
				bitmap.recycle();
			}
		}
	}

	public boolean isExist(String path){
		if (TextUtils.isEmpty(path)){
			return false;
		}
		File file = new File(path);
		return file.exists();
	}
}
