package com.hualu.wifistart;

import java.io.File;

import android.app.Activity;  
import android.os.Bundle;  
import android.os.Environment;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.FrameLayout;  
import android.widget.ImageButton;
import android.widget.TextView;
  
public class RecorderActivity extends Activity implements OnClickListener{
    private RecorderSurfacePreview mRecSurPreview = null;  
    private ImageButton btnRec;  
    //private ImageView photoView;
    private TextView txtRec;
    //private String TAG = "recorder";  
    private enum RecStatus {REC,STOP};
    private RecStatus recStatus = RecStatus.STOP;
    private String currFolder = null;    
    public static String CACHE_VIDEO_IMAG = "/wifidock/VideoImgcache";
    //String cachePATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mediacenter/imgcache/";
    String cachePATH = Environment.getExternalStorageDirectory().getPath() + CACHE_VIDEO_IMAG + File.separator;
    @Override    
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.activity_recorder);  
        
        if (getIntent().getExtras() != null)
        	currFolder = getIntent().getExtras().getString("currFolder");
		else
			currFolder = cachePATH;  
        // Create our Preview view and set it as the content of our activity.  
        FrameLayout preview = (FrameLayout) findViewById(R.id.preview);  
        mRecSurPreview = new RecorderSurfacePreview(this,currFolder);  
        preview.addView(mRecSurPreview);    
        
        
        
     // Add a listener to the Capture button  
        btnRec = (ImageButton) findViewById(R.id.btnRec);  

        btnRec.setOnClickListener(this);   
        //photoView = (ImageView)findViewById(R.id.photoView); 
        txtRec = (TextView)findViewById(R.id.txtRec);
    } 
    /*
    public boolean saveCapture(){
    	boolean ret = false;
    	if(currFolder != null){
    		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    		String path = currFolder + "IMAGE_"+ timeStamp + ".jpg";
    		Log.i(TAG,"filedir = " + path);
    		if(currFolder.startsWith("smb")){
    			try {
					OutputStream out = new BufferedOutputStream(new SmbFileOutputStream(path));
					out.write(jpgData);
					out.close();
					ret = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}else{
    			try {  
    				
    				File pictureFile = new File(currFolder + "IMAGE_"+ timeStamp + ".jpg");//+ File.separator 
    	            FileOutputStream fos = new FileOutputStream(pictureFile);  
    	            fos.write(jpgData);  
    	            fos.close();
    	            ret = true;
    	        } catch (FileNotFoundException e) {  
    	            Log.d(TAG, "File not found: " + e.getMessage());  
    	        } catch (IOException e) {  
    	            Log.d(TAG, "Error accessing file: " + e.getMessage());  
    	        }
    		}
    	}    	
    	return ret;
    }
    */
    @Override
	public void onBackPressed() {
    	System.gc();
    	setResult(RESULT_OK);
		finishActivity(1);
		finish();
    }
    @Override  
    public void onClick(View v) { 
    	if(recStatus == RecStatus.REC){
    		//stop recording
    		try{
    		mRecSurPreview.stopRecording();
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
    		recStatus = RecStatus.STOP;
    		btnRec.setBackgroundResource(R.drawable.btn_bg_camera_capture);
    		txtRec.setText(R.string.start_rec);
    	}else{
    		//start recording
    		if(mRecSurPreview.startRecording()){
	    		recStatus = RecStatus.REC;
	    		btnRec.setBackgroundResource(R.drawable.btn_bg_camera_confirm);
	    		txtRec.setText(R.string.stop_rec);
    		}
    	}
    	
    }  
}  