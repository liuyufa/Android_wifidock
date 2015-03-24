package com.hualu.wifistart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.util.EncodingUtils;
import com.hualu.wifistart.wifisetting.utils.LanguageCheck;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public final class GetConfigInfo {
	public static String dess;
	public static String readFileSdcardFile(String fileName) throws IOException{
		  try{   
		         FileInputStream fin = new FileInputStream(fileName);   	   
		         byte [] buffer = new byte[1];   
		         fin.read(buffer);        
		         dess = EncodingUtils.getString(buffer, "UTF-8");     
		         fin.close();       
		        }     
		        catch(Exception e){   
		         e.printStackTrace();   
		        }   
		        return dess;   
		}   

	public static void smbGet(String remoteUrl, String localDir) {   
		   InputStream in = null ;
		   OutputStream out = null ;
		   try  {   
		          SmbFile remoteFile = new SmbFile(remoteUrl);   
		          String fileName = remoteFile.getName();
		          File localFile = new File(localDir + File.separator + fileName);   
		          in = new BufferedInputStream( new SmbFileInputStream(remoteFile));
		          out = new  BufferedOutputStream( new FileOutputStream(localFile));
		          byte[] buffer = new byte[1024]; 
		          while(in.read(buffer) != -1) {   
		              out.write(buffer);
		              buffer = new byte[1024];
		          }
		    } catch  (Exception e) {   
		         e.printStackTrace();   
		    } finally{   
		        try  {   
		               out.close();   
		               in.close();   
		        } catch  (IOException e) {   
		               e.printStackTrace();   
		        }   
		    }   
	}   
	public static String getConfiginfo(String flag){
		int flags = Integer.parseInt(flag);
		if(LanguageCheck.isZh()){
			switch(flags){
			case 0:
				dess = "¿©’πŒÎ";
				break;
			case 1:
				dess = "TFø®";
				break;
			case 2:
				dess = "U≈Ã";
				break;
			}
		}
		else {
			switch(flags){
			case 0:
				dess = "WiFiDock";
				break;
			case 1:
				dess = "SD";
				break;
			case 2:
				dess = "USB";
				break;
			}
		}
		return dess;
		
	}
	public static void deleteConfig(String pathfile){
        File configfile = new File(pathfile);
        if (configfile.exists()) {
        	configfile.delete();
        }
	}
}
