/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hualu.wifistart;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.hualu.wifistart.custom.HuzAlertDialog;


public class VideoViewDemo extends Activity implements OnCompletionListener, OnErrorListener{

	/**
	 * TODO: Set the path variable to a streaming video URL or a local media file
	 * path.
	 */
	//private String path = "";
	
	static public String musicdir=Environment.getExternalStorageDirectory().getPath() + "/localVideo/";
	private Context mContext;
	private String path ;//= "/mnt/sdcard/Movies/[Œﬁ”«”∞ ”5u.cc]King2hearts_01.rmvb";
//	private String uri;
	private String path2;
	private VideoView mVideoView;
	protected static String TAG = "VIDEO";
	private ProgressDialog progDailog;
	private MediaController mMediaCtrl;
	private OnErrorListener mOnErrorListener;
	private boolean errorFlag = false;
	private boolean smbFlag = false;
	private SurfaceHolder holder ;
	
	 int videoWidth=0;  
	 int videoHeight=0;
	 private Display mDisplay;  
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.videoview);
		mVideoView = (VideoView) findViewById(R.id.surface_view);
		mDisplay=getWindowManager().getDefaultDisplay();
		
		if (getIntent().getExtras() != null ){
			path = getIntent().getExtras().getString("path");
			path2 = getIntent().getExtras().getString("path2");
			Log.e("zhangjiany", "path="+ path2);
		}
			
		else
			path = "";
		Log.i(TAG,"video path = "+path);
		if (path == "") {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoViewDemo.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			
//			mVideoView.setVideoPath(path);
			if (path.startsWith("http:")){
				mVideoView.setVideoURI(Uri.parse(path));
				smbFlag = true;
			}
			else
				mVideoView.setVideoPath(path);
			
			mVideoView.setBufferSize(2048);
			mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
			mVideoView.setOnCompletionListener(this);
			mVideoView.setOnErrorListener(this);
//			mVideoView.setOnPreparedListener(this);
			
			mMediaCtrl = new MediaController(this);
	        mMediaCtrl.setAnchorView(mVideoView);
	        mVideoView.setMediaController(mMediaCtrl);
	        
//			mVideoView.setMediaController(new MediaController(this));
			mVideoView.requestFocus();
			
			progDailog  = ProgressDialog.show(this, "Please wait ...", 
			           "Retrieving data ...", true);
			progDailog.setCancelable(true);

			

			

			mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					// optional need Vitamio 4.0
					mediaPlayer.setPlaybackSpeed(1.0f);
					progDailog.dismiss();
				}
			});
			
			mVideoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					// TODO Auto-generated method stub
					
				}
			});
		}

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		super.onPause();
		getWindow().getDecorView().setKeepScreenOn(false);
		if(null != mVideoView) {
            mVideoView.pause();
        }
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		
		super.onResume();
		progDailog.show();
		 getWindow().getDecorView().setKeepScreenOn(true);
	        if(null != mVideoView) {
	            mVideoView.resume();
	        }
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(null != mVideoView) {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
		if(errorFlag){
			progDailog.dismiss();
		}
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) { 
        System.out.println("onCompletion");
        Log.e("onCompletion", "onCompletion");
        if(null != mVideoView) {
            mVideoView.seekTo(0);
            mVideoView.stopPlayback();
            
        }
        this.finish();
    }
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if (mOnErrorListener != null) {
			if (mOnErrorListener.onError(mp, what, extra))
				return true;
		} else {
			if(smbFlag){
				Log.e("play", "play");
				final Dialog dialog = new HuzAlertDialog.Builder(
						VideoViewDemo.this)
						.setTitle(R.string.title_comfir_delete)
						.setMessage(
								Html.fromHtml(getResources().getString(
										R.string.errorMsg)))
						.setPositiveButton(R.string.set_done,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										paramDialogInterface.dismiss();
										errorFlag = true;
										mVideoView.stopPlayback();

										Intent intent = new Intent(MApplication
												.getmContext(),
												DownloadService.class);
										Log.e("MApplication", "path" + path2);
										intent.putExtra("srcDir", path2);
										intent.putExtra("toDir", musicdir);
										intent.putExtra("CopyOperation",
												"APPEND2");
										intent.putExtra("isCut", false);

										startService(intent);
										VideoViewDemo.this.finish();

									}
								})
						.setNegativeButton(R.string.set_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface paramDialogInterface,
											int paramInt) {
										paramDialogInterface.dismiss();
										mVideoView.stopPlayback();

										VideoViewDemo.this.finish();

									}
								}).create();
				dialog.show();

				return true;
			}else{
				return false;
			}

		}

		return false;
	}
	
	
	
}
