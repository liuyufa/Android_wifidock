package com.hualu.wifistart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;  
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
//zz import android.annotation.SuppressLint;
import android.content.Context;  
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;  
import android.media.MediaRecorder;
import android.util.Log;  
import android.view.SurfaceHolder;  
import android.view.SurfaceView;
import android.widget.Toast;

public class RecorderSurfacePreview extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceHolder mHolder;  
    public Camera mCamera;
	private String curDir;
	MediaRecorder mediaRecorder;
	private String filePath;
	Context mContext;
    @SuppressWarnings("deprecation")
	public RecorderSurfacePreview(Context context,String path) {
        super(context);  
        mContext = context;
        // Install a SurfaceHolder.Callback so we get notified when the  
        // underlying surface is created and destroyed.  
        mHolder = getHolder();  
        mHolder.addCallback(this);  
        // deprecated setting, but required on Android versions prior to 3.0  
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
        this.curDir = path;
    }  
  
    public void surfaceCreated(SurfaceHolder holder) {            
       
        try {  
            // Open the Camera in preview mode
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(mHolder);  
        } catch (IOException e) {  
            Log.d("Dennis", "Error setting camera preview: " + e.getMessage());  
        }  
    }  
      
    @SuppressWarnings("deprecation")
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {  
          
        try {  
        	//设置Camera的参数
        	Camera.Parameters parameters = mCamera.getParameters();
        	parameters.setPictureFormat(PixelFormat.JPEG);  
        	parameters.setFocusMode("continuous-video");//Parameters.FOCUS_MODE_INFINITY
        	//parameters.setFocusMode("auto");
        	mCamera.setParameters(parameters);
        	if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
        	{
	        	//如果是竖屏
	        	parameters.set("orientation", "portrait");
	        	//在2.2以上可以使用
	        	mCamera.setDisplayOrientation(90);
        	}
        	else
        	{
	        	parameters.set("orientation", "landscape");
	        	//在2.2以上可以使用
	        	mCamera.setDisplayOrientation(0);
        	}
        	
//        	//竖屏
//        	mCamera.setDisplayOrientation(90);
//        	//横屏
//        	mCamera.setDisplayOrientation(0);
            mCamera.startPreview();  
  
        } catch (Exception e){  
        	e.printStackTrace();
        }
    }  
  
    public void surfaceDestroyed(SurfaceHolder holder) { 
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		// stopCamera();
		if (mCamera != null) {
			mCamera.lock();
		}
        if (mCamera != null) {  
            mCamera.stopPreview();  
            mCamera.release();  
            mCamera = null;  
        }  
    } 
		/*
		videosize and bitrate setting:
		1920x1088, 20000000
		1280x720, 14000000
		720x480,  2000000
		864x480,  2000000
		800x480,  2000000
		640x480,  2000000
		432x240,  720000
		352x288,  720000
		320x240,  512000
		176x144,  192000
		*/ 
    public boolean startRecording() {
		try {
			mCamera.unlock();
			mediaRecorder = new MediaRecorder();
			mediaRecorder.reset();
			mediaRecorder.setCamera(mCamera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setPreviewDisplay(mHolder.getSurface());
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			mediaRecorder.setVideoEncodingBitRate(2000000);
			mediaRecorder.setVideoSize(640, 480);
			mediaRecorder.setVideoFrameRate(30);
//			mediaRecorder.setMaxDuration(maxDurationInMs);
			if(curDir.startsWith("smb"))
				filePath = MApplication.VIDEO_RECORD_PATH + "VID_" + getTime() + ".3gp";				
			else
				//filePath = MApplication.VIDEO_RECORD_PATH + "VID_" + getTime() + ".3gp";
				filePath = curDir + "VID_" + getTime() + ".3gp";
			// xuw add 03/20
			File file = new File(filePath);
			if (file.isDirectory()) {
				file.mkdirs();
			}
			mediaRecorder.setOutputFile(filePath);
			
				
//			mediaRecorder.setMaxFileSize(maxFileSizeInBytes);
			mediaRecorder.prepare();
			mediaRecorder.start();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 停止录制
	public void stopRecording() throws Exception {
		if (curDir.startsWith("smb")) { // cpy file to smb
			// xuw add 03/20
			if (mediaRecorder != null) {
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
			}
			// stopCamera();
			if (mCamera != null) {
				mCamera.lock();
			}
			Toast.makeText(mContext, R.string.saving, Toast.LENGTH_LONG).show();
			File tmpFile = new File(filePath);
			String fileName = tmpFile.getName();
			SmbFile remoteFile = new SmbFile(curDir + File.separator + fileName);
			int byteread = 0;
			if (tmpFile.exists()) {
				InputStream inStream = new BufferedInputStream(
						new FileInputStream(tmpFile)); // 读入原文件
				OutputStream outStream = new BufferedOutputStream(
						new SmbFileOutputStream(remoteFile));
				byte[] buffer = new byte[1024*10];
				while ((byteread = inStream.read(buffer)) != -1) {
																
					outStream.write(buffer, 0, byteread);
				}
				inStream.close();
				outStream.flush();
				if (outStream != null){
					outStream.close();
					}
			}
			 tmpFile.delete();
			 Toast.makeText(mContext, R.string.finish_rec, Toast.LENGTH_SHORT).show();
		}else{
		Toast.makeText(mContext, R.string.saving, Toast.LENGTH_LONG).show();
		if (mediaRecorder != null) {
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
		}
		// stopCamera();
		if (mCamera != null) {
			mCamera.lock();
		}
		Toast.makeText(mContext, R.string.finish_rec, Toast.LENGTH_SHORT).show();
	}}
	//zz @SuppressLint("SimpleDateFormat")
	public String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");// 设置日期格式
		return df.format(new Date());// new Date()为获取当前系统时间
	}
}
