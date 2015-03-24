package com.hualu.wifistart.update;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.hualu.wifistart.R;
import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.filebrowser.SearchDialog;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

public class UpdateNew {
	private Context mContext;
	private UpdateManager mUpdateManager;
	/* liuyufa change for netdialog 20140115 */
	// public static ProgressDialog progressDialog;
	public static SearchDialog progressDialog;
	/* liuyufa change for netdialog 20140115 */
	// private static Toast mToast;
	private Thread updateThread;
	public static int newVerCode = 0;
	public static String newVerName = "";
	public static final int CONNECT_FAIL = 0;
	// public static final int DOWN_UPDATE = 1;
	// public static final int DOWN_OVER = 2;
	public static final int CHECK_OVER = 3;
	public static final int UPDATE_ABLE = 4;
	public static final int UPDATE_UNABLE = 5;
	public boolean mUnupdateDialog;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CONNECT_FAIL:
				if (progressDialog != null) {
					/* liuyufa change for toast 20140115 start */
					ToastBuild.toast(mContext, R.string.no_server);
					/*
					 * mToast = Toast.makeText( mContext, "无法连接到服务器，更新失败",
					 * Toast.LENGTH_LONG); mToast.setGravity(Gravity.CENTER, 0,
					 * 0); mToast.show();
					 */
					/* liuyufa change for toast 20140115 end */
				}
				// progressDialog.dismiss();
				// break;
			case CHECK_OVER:
				if (progressDialog != null)
					progressDialog.dismiss();
				break;
			case UPDATE_ABLE:
				showNoticeDialog();
				break;
			case UPDATE_UNABLE:
				if (mUnupdateDialog)
					notNewVersionShow();
				break;
			default:
				break;
			}
		};
	};

	public UpdateNew(Context context) {
		this.mContext = context;
		mUpdateManager = new UpdateManager(mContext);
	}

	public void updateRun(boolean dialogFlag) {
		mUnupdateDialog = dialogFlag;
		updateThread = new Thread(mdownApkRunnable);
		updateThread.start();
	}

	public void checkDailog() {
		/* liuyufa change for dialog 20140115 start */
		// progressDialog = ProgressDialog.show(mContext, "正在连接网络...",
		// "请耐心等待...", true, false);
		progressDialog = new SearchDialog(mContext, R.layout.setnet_dialog,
				R.style.Theme_dialog);
		progressDialog.show();
		/* liuyufa change for dialog 20140115 end */
	}

	private void showNoticeDialog() {
		int verCode = mUpdateManager.getVerCode(mContext);
		String verName = mUpdateManager.getVerName(mContext);
		StringBuffer sb = new StringBuffer();
//		sb.append(R.string.current_version);
		sb.append(mContext.getResources().getString(R.string.current_version));
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
//		sb.append(R.string.find_new);
		sb.append(mContext.getResources().getString(R.string.find_new));
		sb.append(newVerName);
		sb.append(" Code:");
		sb.append(newVerCode);
//		sb.append(R.string.isupdate);
		sb.append(mContext.getResources().getString(R.string.isupdate));
		// AlertDialog.Builder builder = new Builder(mContext);
		Dialog dialog = new HuzAlertDialog.Builder(mContext)
				.setTitle(R.string.solftware_update)
				.setMessage(sb.toString())
				// 设置内容
				.setPositiveButton(R.string._update,// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								mUpdateManager.showDownloadDialog();
							}

						})
				.setNegativeButton(R.string.later_update,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后
								dialog.dismiss();
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	public void notNewVersionShow() {
		int verCode = mUpdateManager.getVerCode(mContext);
		String verName = mUpdateManager.getVerName(mContext);
		StringBuffer sb = new StringBuffer();
		sb.append(R.string.current_version);
		sb.append(verName);
		sb.append(" Code:");
		sb.append(verCode);
		sb.append(R.string.already_lastest);
		Dialog dialog = new HuzAlertDialog.Builder(mContext)
				.setTitle(R.string.solftware_update).setMessage(sb.toString())// 设置内容
				.setPositiveButton(R.string.set_done,// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			// 需要花时间计算的方法
			// Calculation.calculate(4);
			if (mUpdateManager.getServerVerCode(mHandler)) {
				int vercode = mUpdateManager.getVerCode(mContext);
				if (newVerCode > vercode) {
					mUpdateManager.NoticeDialog();
				} else {
					mUpdateManager.NewVersionShow();
				}
			}
			// 向handler发消息
			// mHandler.sendEmptyMessage(0);
		}
	};
}
