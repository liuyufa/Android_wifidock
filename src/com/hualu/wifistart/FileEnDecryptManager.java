package com.hualu.wifistart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import com.hualu.wifistart.filecenter.filebrowser.TxtFileBrowser;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbRandomAccessFile;

/**
 * 加密解密管理类
 * 
 * @author x
 * 
 */

public class FileEnDecryptManager {
	public final int LOCK_START=400;
	public final int LOCK_INCREACE=500;
	public final int LOCK_END=600;

	private FileEnDecryptManager() {
	}

	private static FileEnDecryptManager instance = null;

	public static FileEnDecryptManager getInstance() {
		synchronized (FileEnDecryptManager.class) {
			if (instance == null)
				instance = new FileEnDecryptManager();
		}
		return instance;
	}

	/**
	 * 记录上次解密过的文件名
	 */
	private final String LastDecryptFile = null;
	// = Framework
	// .getModule(DownloadModule.class).getDownloadDir().getAbsolutePath()
	// + "/LastDecryptFilename.ttt";

	/**
	 * LastDecryptFilename.ttt 文件是否被清空
	 */
	private boolean isClear = false;

	/**
	 * 加密入口
	 * 
	 * @param fileUrl
	 *            文件绝对路径
	 * @return
	 *            加密是否成功
	 */
	public boolean InitEncrypt(String fileUrl, String password, String type,Handler handler) {
		if (fileUrl.startsWith("smb")) {
			// smbencrypt(fileUrl);
			return false;
		} else {
			return encrypt(fileUrl, password, type,handler);
		}
	}

	// private final int REVERSE_LENGTH = 56;
	// private String passString = "asf_wd";

	/**
	 * 加解密
	 * 
	 * @param strFile
	 *            源文件绝对路径
	 * @return
	 */
	private boolean encrypt(String strFile, String password, String type,Handler handler) {
		// int len = REVERSE_LENGTH;
		// int len = passString.length();
		if (type.startsWith("file")) {
            Log.i("文件夹加密","进入=====================");
            return true;
			}
		try {
			File f = new File(strFile);
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			// long totalLen = raf.length();

			// if (totalLen < passString.length())
			// len = (int) totalLen;

			FileChannel channel = raf.getChannel();
//			MappedByteBuffer buffer = channel.map(
//					FileChannel.MapMode.READ_WRITE, 0, f.length());
			byte tmp;
			MappedByteBuffer buffer;
			if (type.startsWith("vcf")) {
				buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, f.length());
				for (int i = 0; i < f.length(); ++i) {
					byte rawByte = buffer.get(i);
					int j = i % password.length();
					char[] arrysStrings = password.toCharArray();
					tmp = (byte) (rawByte ^ (int) arrysStrings[j]);
					buffer.put(i, tmp);
				}
			}else if (type.startsWith("txt")) {
				buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, f.length());
//				Message msg=Message.obtain();
//				msg.what=LOCK_START;
//				msg.arg1=100;
//				msg.arg2=0;
//				handler.sendMessage(msg);
				for (int i = 0; i < f.length(); ++i) {
					byte rawByte = buffer.get(i);
					int j = i % password.length();
					char[] arrysStrings = password.toCharArray();
					tmp = (byte) (rawByte ^ (int) arrysStrings[j]);
					buffer.put(i, tmp);
//					Message msg2=Message.obtain();
//					msg2.what=LOCK_INCREACE;
//					msg2.arg1=(int)((i+1)/f.length()*100);
//					handler.sendMessage(msg2);
				}
//				Message msg3=Message.obtain();
//				msg3.what=LOCK_END;
//				handler.sendMessage(msg3);
			} else{
				if(f.length()>1024*1024*50)
				{
					buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024*1024*2);
				}
				else{
					buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, f.length());
				}
				for (int i = 0; i < 526; ++i) {
					byte rawByte = buffer.get(i);
					int j = i % password.length();
					char[] arrysStrings = password.toCharArray();
					tmp = (byte) (rawByte ^ (int) arrysStrings[j]);
					buffer.put(i, tmp);
				}
			}
			buffer.force();
			buffer.clear();
			channel.close();
			raf.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean smbencrypt(String strFile, String passString) {
		// int len = REVERSE_LENGTH;
		// int len = passString.length();
		try {
			SmbFile f = new SmbFile(strFile);
			SmbRandomAccessFile raf = new SmbRandomAccessFile(f, "rw");
			// long totalLen = raf.length();

			// if (totalLen < passString.length())
			// len = (int) totalLen;

			// FileChannel channel = raf.getChannel();
			// MappedByteBuffer buffer = channel.map(
			// FileChannel.MapMode.READ_WRITE, 0, f.length());

			byte[] b = new byte[56];
			raf.read(b, 0, 56);

			byte tmp;
			for (int i = 0; i < 56; ++i) {
				byte rawByte = b[i];
				int j = i % 5;
				char[] arrysStrings = passString.toCharArray();
				tmp = (byte) (rawByte ^ (int) arrysStrings[j]);
				b[i] = tmp;
			}
			raf.write(b);
			raf.close();
			Log.e("smb加密代码++++++++++++++++++++++++++++++++++++=============",
					"smb代码执行完毕");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 解密入口
	 * 
	 * @param fileUrl
	 *            源文件绝对路径
	 */
	public void Initdecrypt(String fileUrl, String pasString, String type,Handler handler) {
		// try {
		// if (isDecripted(fileUrl)) {
		// decrypt(fileUrl);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		decrypt(fileUrl, pasString, type,handler);
	}

	private void decrypt(String fileUrl, String pasString, String type,Handler handler) {
		encrypt(fileUrl, pasString, type,handler);
	}

	/**
	 * fileName 文件是否已经解密了
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private boolean isDecripted(String fileName) throws IOException {
		// 上次加密的文件
		File lastDecryptFile = new File(LastDecryptFile);
		if (lastDecryptFile.exists() && isClear == false) {
			String lastDecryptfilepath = getLastDecryptFilePath(LastDecryptFile);
			if (lastDecryptfilepath != null
					&& lastDecryptfilepath.equals(fileName)) {
				return false;
			} else {
				// clear();
			}
		}
		StringBufferWrite(fileName);
		return true;
	}

	/**
	 * 将需要加密的文件绝对路径写入LastDecryptFile
	 * 
	 * @param filePath
	 *            需要加密的文件绝对路径
	 * @param content
	 * @throws IOException
	 */
	private void StringBufferWrite(String filePath) throws IOException {
		File lastDecryptFile = new File(LastDecryptFile);
		if (!lastDecryptFile.exists())
			lastDecryptFile.createNewFile();
		FileOutputStream out = new FileOutputStream(lastDecryptFile, true);
		StringBuffer sb = new StringBuffer();
		sb.append(filePath);
		out.write(sb.toString().getBytes("utf-8"));
		out.close();
	}

	/**
	 * 清空加密记录
	 */

	/**
	 * public synchronized void clear() { isClear = true; File decryptTempFile =
	 * new File(LastDecryptFile); if (decryptTempFile.exists()) { try { String
	 * fileName = getLastDecryptFilePath(LastDecryptFile); decrypt(fileName);
	 * new File(LastDecryptFile).delete(); } catch (IOException e) {
	 * e.printStackTrace(); } } isClear = false; }
	 */

	/**
	 * 从LastDecryptFile中读取记录
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private String getLastDecryptFilePath(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String str = br.readLine();
		br.close();
		return str;
	}
    /**  
     * 追加文件：使用RandomAccessFile  
     *   
     * @param fileName  
     *            文件名  
     * @param content  
     *            追加的内容  
     */  
    public void setPassWordtoFile(String fileName, String password) {   
        try {   
            // 打开一个随机访问文件流，按读写方式 
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");   
            // 文件长度，字节数   
            long fileLength = randomFile.length();   
            Log.i("加密前的文件大小", String.valueOf(fileLength));
            // 将写文件指针移到文件尾。   
            randomFile.seek(fileLength);
            password = encryptPassWord(password);
            Log.i("加密密码长度", String.valueOf(password.length()));
            password+=password.length();
            Log.i("加密密码2", password);
            String s2=new String(password.getBytes("GBK"),"iso8859-1");
        	randomFile.writeBytes(s2);
        	randomFile.seek(0);
            randomFile.close();
            
            Log.i("加密完后的文件大小", String.valueOf(randomFile.length()));
            Log.i("加密密码3", "加密密码完毕");
            
        } catch (IOException e){
            e.printStackTrace();
        }   
    }
    /*字符串密码解密 MD5解密算法 刘裕发 20150113*/
    public String decryptPassWord(String password){
    	try{
    		String newpw = new String();
    		java.util.StringTokenizer st = new java.util.StringTokenizer(password,"%");
    		while(st.hasMoreElements()){
    			int asc = Integer.parseInt((String)st.nextElement())-27;
    			newpw = newpw + (char)asc;
    		}
    		return newpw;
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    /*字符串密码加密 MD5加密算法 刘裕发 20150113*/
    public String encryptPassWord(String password){
	   	try{
	   		byte[] pw = password.getBytes("iso8859-1");
	   		String newpw = new String();
	   		for(int i=0;i<pw.length;i++){
	   			int asc = pw[i];
	   			pw[i] = (byte)(asc + 27);
	   			newpw = newpw +(asc + 27)+"%";
	   		}
			return newpw;
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
   }
    public String getStringOf(File file,int size){
		String str="";
		long len=0;
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			len = raf.length();
			raf.seek(len-size);
			byte[] b=new byte[size];
			raf.read(b);
			for(int i = 0; i < b.length; i++){
				str += (char)b[i];
			}
			Log.i("getStringOf----", str);
			raf.close();
			RandomAccessFile raf1 = new RandomAccessFile(file, "rw");
			len = raf1.length();
			raf1.close();
		}
		 catch (Exception e) {
				e.printStackTrace();
		}
		return str;
	}
    public void deletePassWord(File file,int size){
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			long fileLength = raf.length();
			raf.seek(fileLength-size);
			raf.writeBytes("      ");
			raf.close();
		}
		 catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
