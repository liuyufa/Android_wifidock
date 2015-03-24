package com.hualu.wifistart.music;
import java.io.BufferedReader;  
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;  
import java.net.URL;  
import java.net.URLEncoder;
import android.util.Log; 
public class SearchLRC {
	private URL url;  
	private String TAG = "SearchLRC"; 
    public static final String DEFAULT_LOCAL = "GB2312";  //utf-8
    StringBuffer sb = new StringBuffer();  
    String musicName;
    String singerName;
    String lrcPath;
    /* 
     * 初期化，根据参数取得lrc的地址 
     */  
    public SearchLRC(String musicName, String singerName,String path) {  
        // 将空格替换成+号  
    	Log.i(TAG,"SearchLRC " + musicName + " " + singerName);
    	this.musicName = musicName;
    	this.singerName = singerName;
    	lrcPath = path;
        //musicName = musicName.replace(' ', '+');  
        //singerName = singerName.replace(' ', '+');  
    	//传进来的如果是汉字，那么就要进行编码转化  
        try {  
            musicName = URLEncoder.encode(musicName, "utf-8");  
            singerName = URLEncoder.encode(singerName, "utf-8");  
        } catch (UnsupportedEncodingException e2) {  
            // TODO Auto-generated catch block  
            e2.printStackTrace();  
        }        
        
        //String strUrl = "http://box.zhangmen.baidu.com/x?op=12&title="  
        //        + musicName + "$$" + singerName + "$$$$";  
        String strUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=" +  
        musicName + "$$"+ singerName +"$$$$";  
        Log.d(TAG, strUrl);  
        try {  
            url = new URL(strUrl);  
        } catch (Exception e1) {  
            e1.printStackTrace();  
        }  
        BufferedReader br = null;  
        String s;  
        try {  
            InputStreamReader inReader = new InputStreamReader(url.openStream(),"utf-8");  
            /*
        	HttpURLConnection   httpConn   =   (HttpURLConnection)url.openConnection();  
        	httpConn.connect();  
            InputStreamReader inReader = new InputStreamReader(httpConn.getInputStream()); 
            */
        	Log.d(TAG,"the encode is " + inReader.getEncoding());  
            br = new BufferedReader(inReader);  
        } catch (IOException e1) {  
        	//e1.printStackTrace();
            Log.d(TAG, "br is null " + e1.getMessage());  
            return;
        }  
        try {  
            while ((s = br.readLine()) != null) {  
                sb.append(s + "/r/n");  
                br.close();  
            }  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
  
    }  
    public boolean downloadLyric() {  
        int begin = 0, end = 0, number = 0;// number=0表示暂无歌词  
        String strid = "";  
        Log.d(TAG, "sb = " + sb);  
        begin = sb.indexOf("<lrcid>");  
        
        if (begin != -1) {  
            end = sb.indexOf("</lrcid>", begin);  
            strid = sb.substring(begin + 7, end);  
            number = Integer.parseInt(strid);  
        }  
  
        String geciURL = "http://box.zhangmen.baidu.com/bdlrc/" + number / 100  
                + "/" + number + ".lrc";  
        Log.d(TAG, "geciURL = " + geciURL);          
        
        try {  
            url = new URL(geciURL);  
        } catch (MalformedURLException e2) {  
            e2.printStackTrace();  
        }
      
		try {
			//HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			//InputStream inputStream = httpURLConnection.getInputStream();
			InputStreamReader inputStream = new InputStreamReader(url.openStream()); 
			File file = write2SDFromInput(musicName+"1.lrc", inputStream);
		    if ( file != null ){	
		    	Log.i("SearchLRC", "download success");
		    	return true;		    	
		    }
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  
		Log.i("SearchLRC", "download fail");
		return false;
    }  
    /*
    public File createSDDir(String dirName){
    	  File dir = new File(Singleton.LOCAL_ROOT + dirName);
    	  dir.mkdir();
    	  return dir;
    	 }
    public File createSDFile(String fileName) throws IOException{
    	  File file  = new File(SD_PATH + fileName);
    	  file.createNewFile();
    	  return file;
    	 }*/
    	 
    public File write2SDFromInput(String fileName,InputStreamReader input){
    	  File file = null;
    	  OutputStream output = null;
    	  try {
    	   file  = new File(lrcPath); 
    	   file.createNewFile();
    	   output = new FileOutputStream(file);
    	   OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output); 
    	   char buffer[] = new char[4 * 1024];
    	   int size = 0;
    	   while((size = input.read(buffer)) != -1){
    		   outputStreamWriter.write(buffer, 0, size);
    	   }
    	   //清掉缓存
    	   outputStreamWriter.flush();    	   
    	   output.flush(); 
    	   outputStreamWriter.close();
    	  } catch (Exception e) {
    	   Log.e(TAG,"write2SDFromInput "+e.getMessage());
    	  } finally {
    	   try {
    	    if (output != null){
    	     output.close();    	     
    	    }
    	    if(input!=null)
    	    	input.close();
    	   } catch (IOException ioe){
    	    Log.e(TAG,"write2SDFromInput"+ioe.getMessage());
    	   }
    	  }
    	  return file;
    	 }
    
    /* 
     * 根据lrc的地址，读取lrc文件流 
     * 生成歌词的ArryList 
     * 每句歌词是一个String 
     */  
    public  boolean fetchLyric() {  
        int begin = 0, end = 0, number = 0;// number=0表示暂无歌词  
        boolean ret = false;
        String strid = "";  
        begin = sb.indexOf("<lrcid>");  
        Log.d(TAG, "sb = " + sb);  
        if (begin != -1) {  
            end = sb.indexOf("</lrcid>", begin);  
            strid = sb.substring(begin + 7, end);  
            number = Integer.parseInt(strid);  
        }  
  
        String geciURL = "http://box.zhangmen.baidu.com/bdlrc/" + number / 100  
                + "/" + number + ".lrc";  
        Log.d(TAG, "geciURL = " + geciURL);  
        //ArrayList gcContent =new ArrayList();  
        String s = new String();  
        try {  
            url = new URL(geciURL);  
        } catch (MalformedURLException e2) {  
            e2.printStackTrace();  
        }  
  
        BufferedReader br = null;  
        try {  
            br = new BufferedReader(new InputStreamReader(url.openStream(),"GB2312"));//"utf-8"  
        } catch (IOException e1) {  
            e1.printStackTrace();  
        }  
        if (br == null) {  
           Log.i(TAG,"stream is null");  
        } else {  
            try {  
                
               //File file  = new File(Singleton.LOCAL_ROOT + musicName + ".lrc");
               File file  = new File(lrcPath);               
         	   file.createNewFile();
         	   //file = createSDFile(path + fileName);
         	  OutputStream output = new FileOutputStream(file);
         	  OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output,"utf-8"); 
         	   
         	   
            	while ((s = br.readLine()) != null) {  
//                  Sentence sentence = new Sentence(s);  
                    //gcContent.add(s);  
                    outputStreamWriter.write(s+"\n");
                    Log.i(TAG,"" + s);   
                } 
            	outputStreamWriter.flush();
            	outputStreamWriter.close();
                br.close();  
                ret = true;
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
  
        }  
        return ret;  
    }  
   
}
