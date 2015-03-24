package com.hualu.wifistart.wifisetting.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SaveData {
	public static String oldinfo="";
	public static int isnosd=0;
	 public static String load(String info,String filename,Context context)
	    {	
		 if(!info.equals("getinfo")){
	        try{
	        	if(getSDPath(filename)!="NG"){
	        		String path=getSDPath(filename);
	        		FileWriter fw=new FileWriter(path+"/"+filename);
					fw.write(info);
					fw.flush();
					fw.close();
				} 
		        String path = context.getFilesDir().getPath()+"data/com.hualu.wifistart/files/"+filename;
		        Log.i("path", path);
		    	File nowFile = new File(path);
		    	if(!nowFile.exists()){
		    		writedata(info,filename,context);
		    		return "OK";
		    	}
		        FileInputStream inStream=context.openFileInput(filename);
		        ByteArrayOutputStream stream=new ByteArrayOutputStream();
		        byte[] buffer=new byte[1024];
		        int length=-1;
		    	while((length=inStream.read(buffer))!=-1)   {
		                stream.write(buffer,0,length);
		            }
		            stream.close();
		            inStream.close();
		            String oldinfo=stream.toString();
		            Log.i("dataold",oldinfo);
		            Log.i("datanew",info);
		            if(!info.equals(oldinfo)){
		            	writedata(info,filename,context);	
		            }
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e){
	            return "NG";
	        }
	        return "OK";
	    }
		 else{
			try{
				if(getSDPath(filename)!="NG"){
					String path=getSDPath(filename);
					File nowFile = new File(path+ "/" + filename);
					if(!nowFile.exists()){
						isnosd=1;
					}
					else{
	                    StringBuffer sb = new StringBuffer();
	                    BufferedReader br = new BufferedReader(new FileReader(nowFile));
	                    if(br.toString().length()==0){
	                    	isnosd=1;
	                    	br.close();
	                    }
	                    else{
		                    String line = "";
		                    while((line = br.readLine())!=null){
		                    	sb.append(line);
		                    }
		                    br.close();
		                    Log.i("infofile", sb.toString());
		                    oldinfo=sb.toString();
		                    return oldinfo;
	                    }
					}
				}
				if(1==isnosd) {
					isnosd=0;
					String path = context.getFilesDir().getPath()+"/"+filename;
					Log.i("path", path);
					File nowFile = new File(path);
					if(!nowFile.exists()){
						return "NG";
					}
					FileInputStream inStream=context.openFileInput(filename);
					ByteArrayOutputStream stream=new ByteArrayOutputStream();
					byte[] buffer=new byte[1024];
					int length=-1;
					while((length=inStream.read(buffer))!=-1)   {
		                stream.write(buffer,0,length);
		            }
		            stream.close();
		            inStream.close();
		            oldinfo=stream.toString();
				}
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		        }
		        catch (IOException e){
		        	return "NG";
		        }
		        return oldinfo;
			}
	    }  
	 	/*
	    public static String getdata(String[] data){
	    	int len=data.length;
	    	String info="";
	    	for(int i=1;i<len;i++){
	    		info=info+data[i];
	    	}
			data= Integer.toString(year)+Integer.toString(month)+Integer.toString(date)+Integer.toString(hour)+Integer.toString(minute)+Integer.toString(second);
			return info;
	    }
	    */
	    public static void writedata(String info,String filename,Context context){
			try{
				FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
				fos.write(info.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e){
	            return ;
	        }
			return ;	
	    }
	    public static String getSDPath(String filename){ 
	        File sdDir = null; 
	        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
	        if(sdCardExist)   
	        {                               
	          sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
	          return sdDir.toString()+"/wifidock";
	        }
	        else{
	        	return "NG";
	        }        
	 }
}
