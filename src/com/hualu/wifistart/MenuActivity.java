package com.hualu.wifistart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.net.nntp.NewGroupsOrNewsQuery;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import com.hualu.wifistart.custom.HuzAlertDialog;
import com.hualu.wifistart.filecenter.utils.CustomListener;
import com.hualu.wifistart.filecenter.utils.Helper;
import com.hualu.wifistart.filecenter.utils.ViewEffect;
import com.hualu.wifistart.smbsrc.Singleton;
import com.hualu.wifistart.smbsrc.Helper.SmbHelper;
import com.hualu.wifistart.smbsrc.Helper.wfDiskInfo;
import com.hualu.wifistart.vcardsrc.VCardComposer;
import com.hualu.wifistart.vcardsrc.VCardConfig;
import com.hualu.wifistart.wifiset.HotSettingActivity;
import com.hualu.wifistart.wifiset.HotSpotsItem;
import com.hualu.wifistart.wifiset.WiFiInfo;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;
import com.hualu.wifistart.vcardsrc.AddressBookItem;

import android.app.Activity;
import com.hualu.wifistart.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity {

	final int copyItems[] = { R.string.copy_contact, R.string.copy_photo,R.string.recover_contact,R.string.recover_photo};// ,R.string.copy_record
	// jh- final int
	// statusItems[]={R.string.status_transfer,R.string.status_play,R.string.status_playing};
	final int statusItems[] = { R.string.status_transfer };
	// final int
	// settingItems[]={R.string.setting_wifishare,R.string.setting_file,R.string.setting_help,R.string.setting_guide,R.string.setting_about};
	// �û����� ��繦�� ·�ɹ��� �ȵ㹦�� U�̹��� ���߹��� �ֶ˷��� ��ʾ�豸 ����Web���� ����ָ�� ���� �˳�
	final int settingItems[] = { R.string.setting_customer,
			R.string.setting_power, R.string.setting_router,
			R.string.setting_hot, R.string.setting_mnt, R.string.setting_share,
			R.string.setting_local, R.string.setting_display,
			R.string.setting_share, R.string.setting_upgrade,
			R.string.setting_guide, R.string.setting_web,
			R.string.setting_about, R.string.setting_exit, };// R.string.setting_storage,
	ArrayList<String> itemsList;
	// ImageButton btnHome,btnBack;
	AddressBookItem addressBookItem;
	private List<AddressBookItem> itemlist;
	// luoronghui����home��backͼƬת����ҳ�ͷ��ع���
	ImageView btnHome, btnBack;
	TextView menu;
	// RelativeLayout rlHome,rlBack;
	String menuType;
	public SmbHelper smbHelper = new SmbHelper();
	private ActualExportThread mActualExportThread = null;
	private FileCopyThread mFileCopyThread = null;
	private SmbFileCopyThread mSmbCopyThread = null;
	private FileCopyThread mFileCopyThread2 = null;
	private SmbFileCopyThread mSmbCopyThread2 = null;
	private AddressBookThread mAddressBookThread = null;
	
	final static String TAG = "MenuActivity";
	private final int MENU_FIRST = Menu.FIRST + 100;
	private final int MENU_COPY_CONTACT_Disk0 = MENU_FIRST;
	private final int MENU_COPY_CONTACT_Disk1 = MENU_FIRST + 1;
	private final int MENU_COPY_CONTACT_Disk2 = MENU_FIRST + 2;
	private final int MENU_COPY_CONTACT_Disk3 = MENU_FIRST + 3;

	private final int MENU_COPY_PHOTO_disk0 = MENU_FIRST + 4;
	private final int MENU_COPY_PHOTO_disk1 = MENU_FIRST + 5;
	private final int MENU_COPY_PHOTO_disk2 = MENU_FIRST + 6;
	private final int MENU_COPY_PHOTO_disk3 = MENU_FIRST + 7;

	private final int MENU_COPY_RECORD_LOCAL = MENU_FIRST + 8;
	private final int MENU_COPY_RECORD_SMB1 = MENU_FIRST + 9;
	private final int MENU_COPY_RECORD_SMB2 = MENU_FIRST + 10;
	private final int MENU_COPY_RECORD_SMB3 = MENU_FIRST + 11;
	
	private final int MENU_RECOVER_CONTACT_LOCAL = MENU_FIRST + 12;
	private final int MENU_RECOVER_CONTACT_SMB1 = MENU_FIRST + 13;
	private final int MENU_RECOVER_CONTACT_SMB2 = MENU_FIRST + 14;
	private final int MENU_RECOVER_CONTACT_SMB3 = MENU_FIRST + 15;
	
	private final int MENU_RECOVER_PHOTO_LOCAL = MENU_FIRST + 16;
	private final int MENU_RECOVER_PHOTO_SMB1 = MENU_FIRST + 17;
	private final int MENU_RECOVER_PHOTO_SMB2 = MENU_FIRST + 18;
	private final int MENU_RECOVER_PHOTO_SMB3 = MENU_FIRST + 19;
	// private final int CONFIG_EXPORT_FILE_MIN_INDEX = 1;
	// private final int CONFIG_EXPORT_FILE_MAX_INDEX = 99999;

	private int[] menu_copy_contact = new int[] { MENU_COPY_CONTACT_Disk0,
			MENU_COPY_CONTACT_Disk1, MENU_COPY_CONTACT_Disk2,
			MENU_COPY_CONTACT_Disk3 };
	private int[] menu_copy_photo = new int[] { MENU_COPY_PHOTO_disk0,
			MENU_COPY_PHOTO_disk1, MENU_COPY_PHOTO_disk2, MENU_COPY_PHOTO_disk3 };
	private int[] menu_copy_record = new int[] { MENU_COPY_RECORD_LOCAL,
			MENU_COPY_RECORD_SMB1, MENU_COPY_RECORD_SMB2, MENU_COPY_RECORD_SMB3 };
	
	private int[] menu_recover_contact = new int[] { MENU_RECOVER_CONTACT_LOCAL,
			MENU_RECOVER_CONTACT_SMB1, MENU_RECOVER_CONTACT_SMB2, MENU_RECOVER_CONTACT_SMB3 };

	private int[] menu_recover_photo = new int[] { MENU_RECOVER_PHOTO_LOCAL,
			MENU_RECOVER_PHOTO_SMB1, MENU_RECOVER_PHOTO_SMB2, MENU_RECOVER_PHOTO_SMB3 };
	
	Context mContext;
	String targetFolder;
	HuzAlertDialog alertDialog;
	HuzAlertDialog passwordDialog;
	static boolean islock = false;
	static String pasString;
	public final int START=100;
	public final int INCREACE=200;
	public final int END=300;
	public final int LOCK_START=400;
	public final int LOCK_INCREACE=500;
	public final int LOCK_END=600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(1);
		setContentView(R.layout.activity_menu);
		mContext = this;
		if (getIntent().getExtras() != null)
			menuType = getIntent().getExtras().getString("menuType");
		else
			menuType = null;
		Log.i(TAG, "filetype = " + menuType);

		itemsList = new ArrayList<String>();
		menu = (TextView) findViewById(R.id.menuTitle);
		initPage1();
		btnHome = (ImageView) findViewById(R.id.homebg);

		btnHome.setOnClickListener(new btnClickListener());

		btnBack = (ImageView) findViewById(R.id.backbg);

		btnBack.setOnClickListener(new btnClickListener());

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		setResult(RESULT_OK);
		finishActivity(1);
		finish();
	}

	private void initPage1()// (View paramView)
	{
		final ListView localListView = (ListView) findViewById(R.id.copyslist);

		// ImageView icon =(ImageView)findViewById(R.id.menuIcon);
		// TextView txt = (TextView)findViewById(R.id.menuTxt);
		if (menuType.equals("copy")) {
			// icon.setImageResource(R.drawable.copybig);
			// txt.setText(getString(R.string.copysTittle));
			menu.setText(getString(R.string.copysTittle));
			registerForContextMenu(localListView);
			for (int i = 0; i < copyItems.length; i++) {
				itemsList.add(getResources().getString(copyItems[i]));
			}
			// PrefsAdapter adapter = new PrefsAdapter(this,
			// R.layout.settingsitem, settingsItems);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.settingitem, R.id.itemTitle, itemsList);
			localListView.setAdapter(adapter);

			// localListView.setOnCreateContextMenuListener(MenuLis)

			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							final View argView = arg1;
							
							if(2==arg2){
								Log.i(TAG, "arg2 ======== 2 ");
								localListView.showContextMenuForChild(argView);
								return ;
							}
							
							alertDialog = ViewEffect.createComfirDialog2(
									mContext, R.string.menu_read_lock,
									R.string.menu_read_lockcopy,
									new CustomListener() {

										@Override
										public void onListener() {
											// TODO Auto-generated method stub
											// islock=true;
											alertDialog.dismiss();
											SharedPreferences sh = mContext
													.getSharedPreferences(
															"lockedfile", 0);
											String prepassword = sh.getString(
													"rememberpassword", null);
											passwordDialog = ViewEffect
													.createPassWordDialog(
															mContext,
															R.string.set_pwd_input,
															prepassword,
															new CustomListener() {

																@Override
																public void onListener() {
																	passwordDialog
																			.dismiss();
																	// ����
																	EditText et1 = (EditText) passwordDialog
																			.findViewById(R.id.inputpassword1);
																	String passwordString1 = et1
																			.getText()
																			.toString();
																	EditText et2 = (EditText) passwordDialog
																			.findViewById(R.id.inputpassword2);
																	String passwordString2 = et2
																			.getText()
																			.toString();
																	if ("".equals(passwordString1)
																			|| "".equals(passwordString2)) {
																		alertDialog = ViewEffect
																				.createComfirDialog(
																						mContext,
																						R.string.menu_read_lock,
																						R.string.set_password_empty,
																						new CustomListener() {

																							@Override
																							public void onListener() {
																								alertDialog
																										.dismiss();
																							}
																						},
																						null);
																		alertDialog
																				.show();
																		return;
																	}
																	if (passwordString1.length()<6) {
																		alertDialog = ViewEffect.createComfirDialog(
																				mContext, R.string.menu_read_lock,
																				R.string.set_pwd_input,
																				new CustomListener() {
																					
																					@Override
																					public void onListener() {
																						alertDialog.dismiss();
																					}
																				}, null);
																		alertDialog.show();
																		return;
																	}
																	if (!passwordString1
																			.equals(passwordString2)) {
																		alertDialog = ViewEffect
																				.createComfirDialog(
																						mContext,
																						R.string.menu_read_lock,
																						R.string.menu_read_passfail,
																						new CustomListener() {

																							@Override
																							public void onListener() {
																								alertDialog
																										.dismiss();
																							}
																						},
																						null);
																		alertDialog
																				.show();
																		return;
																	}
																	// ��ס����
																	CheckBox cb = (CheckBox) passwordDialog
																			.findViewById(R.id.passwordcheckBox);
																	boolean check = cb
																			.isChecked();
																	if (check) {
																		SharedPreferences sh = mContext
																				.getSharedPreferences(
																						"lockedfile",
																						0);
																		Editor editor = sh
																				.edit();
																		editor.putString(
																				"rememberpassword",
																				passwordString2);
																		editor.commit();
																	}
																	islock = true;
																	pasString = passwordString1;
																	localListView
																			.showContextMenuForChild(argView);
																}

															},
															new CustomListener() {

																@Override
																public void onListener() {
																	passwordDialog
																			.dismiss();
																}
															});
											passwordDialog.getWindow().setSoftInputMode(
													WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
											passwordDialog.show();

										} 
									}, new CustomListener() {

										@Override
										public void onListener() {
											// TODO Auto-generated method stub
											islock = false;
											alertDialog.dismiss();
											localListView
													.showContextMenuForChild(argView);
										}
									});
							alertDialog.show();
							// localListView.showContextMenuForChild(arg1);
						}

					});
		} else if (menuType.equals("status")) {
			// icon.setImageResource(R.drawable.statusbig);
			// txt.setText(getString(R.string.statusTittle));
			// ����ҳ������
			menu.setText(getString(R.string.statusTittle));
			for (int i = 0; i < statusItems.length; i++) {
				itemsList.add(getResources().getString(statusItems[i]));
			}
			// PrefsAdapter adapter = new PrefsAdapter(this,
			// R.layout.settingsitem, settingsItems);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.settingitem, R.id.itemTitle, itemsList);
			localListView.setAdapter(adapter);

			// localListView.setOnCreateContextMenuListener(MenuLis)

			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							// localListView.showContextMenuForChild(arg1);
							if (arg2 == 0) {
								Intent intent = new Intent(MenuActivity.this,
										StatusActivity.class);
								intent.putExtra("listType", "transfer");
								MenuActivity.this.startActivity(intent);
							} else if (arg2 == 1) {
								Intent intent = new Intent(MenuActivity.this,
										StatusActivity.class);
								intent.putExtra("listType", "play");
								MenuActivity.this.startActivity(intent);
							} else {
								Intent intent = new Intent(MenuActivity.this,
										MusicActivity.class);
								MenuActivity.this.startActivity(intent);
							}
						}

					});
		} else if (menuType.equals("settings")) {
			// icon.setImageResource(R.drawable.settingbig);
			// txt.setText(getString(R.string.settingsTittle));
			for (int i = 0; i < settingItems.length; i++) {
				itemsList.add(getResources().getString(settingItems[i]));
			}
			// PrefsAdapter adapter = new PrefsAdapter(this,
			// R.layout.settingsitem, settingsItems);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.settingitem, R.id.itemTitle, itemsList);
			// settingAdapter adapter = new settingAdapter(this,itemsList);
			localListView.setAdapter(adapter);

			// localListView.setOnCreateContextMenuListener(MenuLis)

			localListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							// TODO Auto-generated method stub
							// ����ָ�� �û����� ��繦�� ·�ɹ��� �ȵ㹦�� U�̹��� ���߹��� �ֶ˷��� ��ʾ�豸
							// ����Web���� ���� �˳�
							switch (arg2) {
							case 0:
								break;
							case 1:
								break;
							case 2:
								break;
							case 3:
								break;
							case 4:
								break;
							case 5:
								break;
							case 6:
								break;
							case 7:
								break;
							case 8:
								Intent guide = new Intent(MenuActivity.this,
										GuideActivity.class);
								MenuActivity.this.startActivity(guide);
								MenuActivity.this.overridePendingTransition(0,
										0);
								break;
							case 9: {
								Intent web = new Intent(MenuActivity.this,
										WebSetting.class);
								MenuActivity.this.startActivity(web);
							}
								break;
							case 10: {
								Intent info = new Intent(MenuActivity.this,
										InfoActivity.class);
								info.putExtra("infoType", "about");
								MenuActivity.this.startActivity(info);
							}
								break;
							case 11: {
								Intent intent = new Intent(MenuActivity.this,
										MusicService.class);
								stopService(intent);
								SysApplication.getInstance().exit();
								MenuActivity.this.finish();
							}
								break;
							}
						}

					});
		}
	}

	/*
	 * class settingAdapter extends BaseAdapter{ ArrayList<String> items;
	 * private LayoutInflater mInflater; public settingAdapter(Context context,
	 * ArrayList<String> itemsList){ super(); //this.mContext = context; items =
	 * itemsList; mInflater = LayoutInflater.from(context); }
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return items.size(); }
	 * 
	 * @Override public Object getItem(int position) { // TODO Auto-generated
	 * method stub return items.get(position); }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) { // TODO Auto-generated method stub if (convertView == null) {
	 * convertView = mInflater.inflate(R.layout.settingitem, null); } TextView
	 * tittle = (TextView)convertView.findViewById(R.id.itemTitle);
	 * tittle.setText(items.get(position)); SlidButton myBtn =
	 * (SlidButton)convertView.findViewById(R.id.slipBtn);
	 * myBtn.SetOnChangedListener(new OnChangedListener(){
	 * 
	 * @Override public void OnChanged(boolean checkState) { // TODO
	 * Auto-generated method stub if (checkState){
	 * 
	 * }else{
	 * 
	 * } }
	 * 
	 * }); //myBtn.onDraw(); return convertView; }
	 * 
	 * }
	 */
	/*
	 * ListView.OnCreateContextMenuListener MenuLis=new
	 * ListView.OnCreateContextMenuListener(){
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { // TODO Auto-generated method stub //���Ӳ˵���
	 * //menu.add(Menu.NONE,Menu_Item1,0,"��ȡposition");
	 * 
	 * }
	 * 
	 * };
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {// jh+ ����Loading�豸���ƣ�TF�� ���洢 ���ֻ�3����ʾ
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		int selectedPosition = info.position;

		menu.setHeaderIcon(R.drawable.toolbar_operation);
		if (Singleton.SMB_ONLINE)
			Singleton.initDiskInfo();
		ArrayList<wfDiskInfo> disks = Singleton.instance().disks;

		if (disks.size() > 0) {
			if (selectedPosition == 0) {
				menu.setHeaderTitle(R.string.copy_contact);
				for (int i = 0; i < disks.size(); i++) {
					if (disks.get(i).des.equals(".config")) {
						Log.d(TAG, "find .config file");
						continue;
					} else {// jh+ for usb not need backup 2013.12.12
						if (disks.get(i).des.contains("USB")
								|| disks.get(i).des.contains("�ֻ�"))
							continue;
					}

					menu.add(0, menu_copy_contact[i], Menu.NONE,
							disks.get(i).des);
					// menu.addSubMenu(disks.get(i).path);
				}
				// menu.add(0, MENU_COPY_CONTACT_SMB1, Menu.NONE,
				// R.string.menu_cut_selected);
			} else if (selectedPosition == 1) {
				menu.setHeaderTitle(R.string.copy_photo);
				for (int i = 0; i < disks.size(); i++) {
					if (disks.get(i).des.equals(".config")) {
						Log.d(TAG, "find .config file");
						continue;
					} else {// jh+ for usb not need backup 2013.12.12
						if (disks.get(i).des.contains("USB")
								|| disks.get(i).des.contains("�ֻ�"))

							continue;
					}
					menu.add(0, menu_copy_photo[i], Menu.NONE, disks.get(i).des);
				}
			}
			else if(selectedPosition == 2){
				menu.setHeaderTitle(R.string.recover_contact);
				for (int i = 0; i < disks.size(); i++) {
					if (disks.get(i).des.equals(".config")) {
						Log.d(TAG, "find .config file");
						continue;
					}
					menu.add(0, menu_recover_contact[i], Menu.NONE,
							disks.get(i).des);
				}
			}
			else if (selectedPosition == 3) {
				menu.setHeaderTitle(R.string.recover_photo);
				for (int i = 0; i < disks.size(); i++) {
					if (disks.get(i).des.equals(".config")) {
						Log.d(TAG, "find .config file");
						continue;
					}
					menu.add(0, menu_recover_photo[i], Menu.NONE,
							disks.get(i).des);
				}
			}
			else if (selectedPosition == 4) {
				menu.setHeaderTitle(R.string.copy_record);
				for (int i = 0; i < disks.size(); i++) {
					if (disks.get(i).des.equals(".config")) {
						Log.d(TAG, "find .config file");
						continue;
					}
					menu.add(0, menu_copy_record[i], Menu.NONE,
							disks.get(i).des);
				}
			}
		}

		/*
		 * // TODO Auto-generated method stub super.onCreateContextMenu(menu, v,
		 * menuInfo); MenuInflater localMenuInflater = getMenuInflater();
		 * //ListView localView1 = (ListView)findViewById(R.id.copyslist);
		 * 
		 * AdapterView.AdapterContextMenuInfo localAdapterContextMenuInfo =
		 * (AdapterView.AdapterContextMenuInfo)menuInfo; if
		 * (localAdapterContextMenuInfo.position == 0)
		 * localMenuInflater.inflate(R.menu.copycontacts, menu); else if
		 * (localAdapterContextMenuInfo.position == 1)
		 * localMenuInflater.inflate(R.menu.copycphotos, menu); else if
		 * (localAdapterContextMenuInfo.position == 2)
		 * localMenuInflater.inflate(R.menu.copyrecords, menu);
		 */

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// AdapterView.AdapterContextMenuInfo localAdapterContextMenuInfo =
		// (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		Log.i(TAG, "onContextItemSelected " + item.getTitle());
		ArrayList<wfDiskInfo> disks = Singleton.instance().disks;
		switch (item.getItemId()) {
		case MENU_COPY_CONTACT_Disk0: {
			// String targetFileName =
			// getAppropriateFileName(item.getSubMenu());
			// Log.i(TAG,"copyto " + targetFileName);
			//String targetFileName = disks.get(0).path + "sdcard0/contacts/";
			String targetFileName=disks.get(0).path+"contacts/";
			// String targetFileName = Environment.getExternalStorageDirectory()
			// +"/contacts/";
			// File mFile1 = new File("/storage/emulated/0/contacts");
			// if (!mFile1.exists()) {
			// mFile1.mkdirs();
			// }
			// if (mFile1.canWrite()) {
			// targetFileName = "/storage/emulated/0/contacts/";
			// }else {
			// File mFile2 = new File("/storage/sdcard0/contacts");
			// if (!mFile2.exists()) {
			// mFile2.mkdirs();
			// }
			// if (mFile2.canWrite()) {
			// targetFileName= "/storage/sdcard0/contacts/";
			// }else {
			// File mFile3 = new File("/storage/sdcard1/contacts");
			// if (!mFile3.exists()) {
			// mFile3.mkdirs();
			// }
			// if (mFile3.canWrite()) {
			// targetFileName = "/storage/sdcard1/contacts/";
			// }else {
			// Toast.makeText(getBaseContext(),
			// R.string.effectivedisk, Toast.LENGTH_SHORT)
			// .show();
			// finish();
			// }
			// }
			// }

			Log.i("disk0  target desdir===================", targetFileName);
			mActualExportThread = new ActualExportThread(targetFileName);// "/mnt/sdcard/WifiShare/00001.vcf"
			mActualExportThread.start();
			break;
		}
		case MENU_COPY_CONTACT_Disk1: {
			String targetFileName = disks.get(1).path + "contacts/";
			Log.i("disk1  target desdir===================", targetFileName);
			mActualExportThread = new ActualExportThread(targetFileName);// "/mnt/sdcard/WifiShare/00001.vcf"
			mActualExportThread.start();
			break;

		}

		/*
		 * case MENU_COPY_CONTACT_SMB1:{ String targetFileName =
		 * disks.get(1).path + "contacts.vcf"; try {
		 * if(disks.get(1).path.contains("sdcard0")){ File file = new
		 * File(targetFileName); if (file.exists()) file.delete(); } else{
		 * if(disks.get(1).path.contains("smb")) { SmbFile file = new
		 * SmbFile(targetFileName); if (file.exists()) file.delete(); } }
		 * mActualExportThread = new
		 * ActualExportThread(targetFileName);//"/mnt/sdcard/WifiShare/00001.vcf"
		 * mActualExportThread.start(); }catch (MalformedURLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }catch
		 * (SmbException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		case MENU_COPY_CONTACT_Disk2: {
			String targetFileName = disks.get(2).path + "contacts/";
			Log.i("disk2  target desdir===================", targetFileName);
			mActualExportThread = new ActualExportThread(targetFileName);// "/mnt/sdcard/WifiShare/00001.vcf"
			mActualExportThread.start();
		}
			break;
		case MENU_COPY_CONTACT_Disk3: {
			String targetFileName = disks.get(3).path + "contacts/";
			Log.i("disk3  target desdir===================", targetFileName);
			mActualExportThread = new ActualExportThread(targetFileName);// "/mnt/sdcard/WifiShare/00001.vcf"
			mActualExportThread.start();
		}
			break;
		case MENU_COPY_PHOTO_disk0: {
			targetFolder = disks.get(0).path + "/Wifidockbackups/";
			Dialog dialog2 = new HuzAlertDialog.Builder(mContext)
					.setTitle(R.string.title_comfir_delete)
					.setMessage(
							(mContext.getResources()
									.getString(R.string.backups_choice)))
					.setPositiveButton(R.string.backups_default,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									paramDialogInterface.dismiss();
									// Ĭ�ϱ���
									String srcFolder = Environment
											.getExternalStorageDirectory()
											.getPath()
											+ File.separator
											+ getString(R.string.dcim_folder)
											+ File.separator
											+ getString(R.string.camera_folder);
									File srcFile = new File(srcFolder);
									if (!srcFile.exists()) {
										srcFolder = Environment
												.getExternalStorageDirectory()
												.getPath()
												+ File.separator
												+ getString(R.string.camera_folder);
									}
									File srcFile2 = new File(srcFolder);
									if (!srcFile2.exists()) {
										srcFile2.mkdirs();
									}
									if (islock) {
										new Lockfilethread(srcFile2).start();
									} else {

										if (targetFolder.startsWith("smb")) {
											mSmbCopyThread = new SmbFileCopyThread(
													srcFolder, targetFolder);
											mSmbCopyThread.start();

											// mSmbCopyThread2 = new
											// SmbFileCopyThread(
											// srcFolder2, targetFolder);
											// mSmbCopyThread2.start();
										} else {
											mFileCopyThread = new FileCopyThread(
													srcFolder, targetFolder);
											mFileCopyThread.start();

											// mFileCopyThread2 = new
											// FileCopyThread(
											// srcFolder2, targetFolder);
											// mFileCopyThread2.start();

										}
									}

								}
							})
					.setNegativeButton(R.string.backups_undefault,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									// �Զ��屸��
									Intent intent = new Intent(mContext,
											FileBrowserActivity.class);
									intent.putExtra("target", targetFolder);
									intent.putExtra("islock", islock);
									intent.putExtra("pasString", pasString);
									startActivity(intent);
									paramDialogInterface.dismiss();
								}
							}).create();
			dialog2.show();

			// Intent intent = new Intent(this, FileBrowserActivity.class);
			// intent.putExtra("target", targetFolder);
			// startActivity(intent);
		}
			break;
		case MENU_COPY_PHOTO_disk1: {
			targetFolder = disks.get(1).path + "/Wifidockbackups/";
			Dialog dialog2 = new HuzAlertDialog.Builder(mContext)
					.setTitle(R.string.title_comfir_delete)
					.setMessage(
							(mContext.getResources()
									.getString(R.string.backups_choice)))
					.setPositiveButton(R.string.backups_default,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									paramDialogInterface.dismiss();
									// Ĭ�ϱ���
									String srcFolder = Environment
											.getExternalStorageDirectory()
											.getPath()
											+ File.separator
											+ getString(R.string.dcim_folder)
											+ File.separator
											+ getString(R.string.camera_folder);
									File srcFile = new File(srcFolder);
									if (!srcFile.exists()) {
										srcFolder = Environment
												.getExternalStorageDirectory()
												.getPath()
												+ File.separator
												+ getString(R.string.camera_folder);
									}
									File srcFile2 = new File(srcFolder);
									if (!srcFile2.exists()) {
										srcFile2.mkdirs();
									}
									if (islock) {
										new Lockfilethread(srcFile2).start();
									} else {

										if (targetFolder.startsWith("smb")) {
											mSmbCopyThread = new SmbFileCopyThread(
													srcFolder, targetFolder);
											mSmbCopyThread.start();

											// mSmbCopyThread2 = new
											// SmbFileCopyThread(
											// srcFolder2, targetFolder);
											// mSmbCopyThread2.start();
										} else {
											mFileCopyThread = new FileCopyThread(
													srcFolder, targetFolder);
											mFileCopyThread.start();

											// mFileCopyThread2 = new
											// FileCopyThread(
											// srcFolder2, targetFolder);
											// mFileCopyThread2.start();

										}
									}
								}
							})
					.setNegativeButton(R.string.backups_undefault,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									// �Զ��屸��
									Intent intent = new Intent(mContext,
											FileBrowserActivity.class);
									intent.putExtra("target", targetFolder);
									intent.putExtra("islock", islock);
									intent.putExtra("pasString", pasString);
									startActivity(intent);
									paramDialogInterface.dismiss();
								}
							}).create();
			dialog2.show();

			// Intent intent = new Intent(this, FileBrowserActivity.class);
			// intent.putExtra("target", targetFolder);
			// startActivity(intent);
		}
			break;
		case MENU_COPY_PHOTO_disk2: {
			targetFolder = disks.get(2).path + "/Wifidockbackups/";
			Dialog dialog2 = new HuzAlertDialog.Builder(mContext)
					.setTitle(R.string.title_comfir_delete)
					.setMessage(
							(mContext.getResources()
									.getString(R.string.backups_choice)))
					.setPositiveButton(R.string.backups_default,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									paramDialogInterface.dismiss();
									// Ĭ�ϱ���
									String srcFolder = Environment
											.getExternalStorageDirectory()
											.getPath()
											+ File.separator
											+ getString(R.string.dcim_folder)
											+ File.separator
											+ getString(R.string.camera_folder);
									File srcFile = new File(srcFolder);
									if (!srcFile.exists()) {
										srcFolder = Environment
												.getExternalStorageDirectory()
												.getPath()
												+ File.separator
												+ getString(R.string.camera_folder);
									}
									File srcFile2 = new File(srcFolder);
									if (!srcFile2.exists()) {
										srcFile2.mkdirs();
									}
									if (islock) {
										new Lockfilethread(srcFile2).start();
									} else {

										if (targetFolder.startsWith("smb")) {
											mSmbCopyThread = new SmbFileCopyThread(
													srcFolder, targetFolder);
											mSmbCopyThread.start();

											// mSmbCopyThread2 = new
											// SmbFileCopyThread(
											// srcFolder2, targetFolder);
											// mSmbCopyThread2.start();
										} else {
											mFileCopyThread = new FileCopyThread(
													srcFolder, targetFolder);
											mFileCopyThread.start();

											// mFileCopyThread2 = new
											// FileCopyThread(
											// srcFolder2, targetFolder);
											// mFileCopyThread2.start();

										}
									}
								}
							})
					.setNegativeButton(R.string.backups_undefault,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									// �Զ��屸��
									Intent intent = new Intent(mContext,
											FileBrowserActivity.class);
									intent.putExtra("target", targetFolder);
									intent.putExtra("islock", islock);
									intent.putExtra("pasString", pasString);
									startActivity(intent);
									paramDialogInterface.dismiss();
								}
							}).create();
			dialog2.show();

			// Intent intent = new Intent(this, FileBrowserActivity.class);
			// intent.putExtra("target", targetFolder);
			// startActivity(intent);
		}
			break;
		case MENU_COPY_PHOTO_disk3: {
			targetFolder = disks.get(3).path + "/Wifidockbackups/";
			Dialog dialog2 = new HuzAlertDialog.Builder(mContext)
					.setTitle(R.string.title_comfir_delete)
					.setMessage(
							(mContext.getResources()
									.getString(R.string.backups_choice)))
					.setPositiveButton(R.string.backups_default,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									paramDialogInterface.dismiss();
									// Ĭ�ϱ���
									String srcFolder = Environment
											.getExternalStorageDirectory()
											.getPath()
											+ File.separator
											+ getString(R.string.dcim_folder)
											+ File.separator
											+ getString(R.string.camera_folder);
									File srcFile = new File(srcFolder);
									if (!srcFile.exists()) {
										srcFolder = Environment
												.getExternalStorageDirectory()
												.getPath()
												+ File.separator
												+ getString(R.string.camera_folder);
									}
									File srcFile2 = new File(srcFolder);
									if (!srcFile2.exists()) {
										srcFile2.mkdirs();
									}
									if (islock) {
										new Lockfilethread(srcFile2).start();
									} else {

										if (targetFolder.startsWith("smb")) {
											mSmbCopyThread = new SmbFileCopyThread(
													srcFolder, targetFolder);
											mSmbCopyThread.start();

											// mSmbCopyThread2 = new
											// SmbFileCopyThread(
											// srcFolder2, targetFolder);
											// mSmbCopyThread2.start();
										} else {
											mFileCopyThread = new FileCopyThread(
													srcFolder, targetFolder);
											mFileCopyThread.start();

											// mFileCopyThread2 = new
											// FileCopyThread(
											// srcFolder2, targetFolder);
											// mFileCopyThread2.start();

										}
									}
								}
							})
					.setNegativeButton(R.string.backups_undefault,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									// �Զ��屸��
									Intent intent = new Intent(mContext,
											FileBrowserActivity.class);
									intent.putExtra("target", targetFolder);
									intent.putExtra("islock", islock);
									intent.putExtra("pasString", pasString);
									startActivity(intent);
									paramDialogInterface.dismiss();
								}
							}).create();
			dialog2.show();

			// Intent intent = new Intent(this, FileBrowserActivity.class);
			// intent.putExtra("target", targetFolder);
			// startActivity(intent);
		}
			break;
		case MENU_COPY_RECORD_LOCAL: {
			String targetFolder = disks.get(0).path;
			String srcFolder = Environment.getExternalStorageDirectory()
					.getPath()
					+ File.separator
					+ getString(R.string.recording_folder);
			File file = new File(srcFolder);
			if (!file.exists()) {
				srcFolder = Environment.getExternalStorageDirectory().getPath()
						+ File.separator + "Recording";
				file = new File(srcFolder);
				if (!file.exists()) {
					srcFolder = Environment.getExternalStorageDirectory()
							.getPath() + File.separator + "Recordings";
					if (!file.exists()) {
						srcFolder = Environment.getExternalStorageDirectory()
								.getPath() + File.separator + "Sounds";
						if (!file.exists()) {
							ViewEffect.showToast(this,
									R.string.no_recording_folder);
							break;
						}
					}
				}

			}
			mFileCopyThread = new FileCopyThread(srcFolder, targetFolder);
			mFileCopyThread.start();
		}
			break;
		case MENU_COPY_RECORD_SMB1: {
			String targetFolder = disks.get(1).path;
			String srcFolder = Environment.getExternalStorageDirectory()
					.getPath()
					+ File.separator
					+ getString(R.string.recording_folder);
			File file = new File(srcFolder);
			if (!file.exists()) {
				srcFolder = Environment.getExternalStorageDirectory().getPath()
						+ File.separator + "Recording";
				file = new File(srcFolder);
				if (!file.exists()) {
					srcFolder = Environment.getExternalStorageDirectory()
							.getPath() + File.separator + "Recordings";
					if (!file.exists()) {
						srcFolder = Environment.getExternalStorageDirectory()
								.getPath() + File.separator + "Sounds";
						if (!file.exists()) {
							ViewEffect.showToast(this,
									R.string.no_recording_folder);
							break;
						}
					}
				}

			}

			mSmbCopyThread = new SmbFileCopyThread(srcFolder, targetFolder);
			mSmbCopyThread.start();
		}
			break;
		case MENU_RECOVER_CONTACT_LOCAL:
			if(fileIsExists(MENU_RECOVER_CONTACT_LOCAL,disks)){
				mAddressBookThread = new AddressBookThread(disks.get(0).path);
				mAddressBookThread.start();
			}
			else{
				ToastBuild.toast(MenuActivity.this,
						R.string.toast_file_not_find);
			}
			break;
		case MENU_RECOVER_CONTACT_SMB1:
			if(fileIsExists(MENU_RECOVER_CONTACT_SMB1,disks)){
				//targetFolder = disks.get(1).path + "/contacts/";
				mAddressBookThread = new AddressBookThread(disks.get(1).path);
				mAddressBookThread.start();
			}
			else{
				ToastBuild.toast(MenuActivity.this,
						R.string.toast_file_not_find);
			}
			break;
		case MENU_RECOVER_CONTACT_SMB2:
			if(fileIsExists(MENU_RECOVER_CONTACT_SMB2,disks)){
				mAddressBookThread = new AddressBookThread(disks.get(2).path);
				mAddressBookThread.start();
			}
			else{
				ToastBuild.toast(MenuActivity.this,
						R.string.toast_file_not_find);
			}
			break;
		case MENU_RECOVER_CONTACT_SMB3:
			if(fileIsExists(MENU_RECOVER_CONTACT_SMB3,disks)){
				mAddressBookThread = new AddressBookThread(disks.get(3).path);
				mAddressBookThread.start();
			}
			else{
				ToastBuild.toast(MenuActivity.this,
						R.string.toast_file_not_find);
			}
			break;
			case MENU_RECOVER_PHOTO_LOCAL:
				if(fileIsExists(MENU_RECOVER_PHOTO_LOCAL,disks)){
//					mAddressBookThread = new AddressBookThread(disks.get(0).path);
//					mAddressBookThread.start();
				}
				else{
					ToastBuild.toast(MenuActivity.this,
							R.string.toast_file_not_find);
				}
				break;
			case MENU_RECOVER_PHOTO_SMB1:
				if(fileIsExists(MENU_RECOVER_PHOTO_SMB1,disks)){
					//targetFolder = disks.get(1).path + "/contacts/";
//					mAddressBookThread = new AddressBookThread(disks.get(1).path);
//					mAddressBookThread.start();
				}
				else{
					ToastBuild.toast(MenuActivity.this,
							R.string.toast_file_not_find);
				}
				break;
			case MENU_RECOVER_PHOTO_SMB2:
				if(fileIsExists(MENU_RECOVER_PHOTO_SMB2,disks)){
//					mAddressBookThread = new AddressBookThread(disks.get(2).path);
//					mAddressBookThread.start();
				}
				else{
					ToastBuild.toast(MenuActivity.this,
							R.string.toast_file_not_find);
				}
				break;
			case MENU_RECOVER_PHOTO_SMB3:
				if(fileIsExists(MENU_RECOVER_PHOTO_SMB3,disks)){
//					mAddressBookThread = new AddressBookThread(disks.get(3).path);
//					mAddressBookThread.start();
				}
				else{
					ToastBuild.toast(MenuActivity.this,
							R.string.toast_file_not_find);
				}
				break;
		default:
			break;
		}
		return true;
	}
	/*vcf�ļ��Ƿ����*/
	public boolean fileIsExists(int flag,ArrayList<wfDiskInfo> disks){
        try{
        	String targetFileName="";
        	switch(flag){
        	case MENU_RECOVER_CONTACT_LOCAL:
        		targetFileName = disks.get(0).path + "contacts/Contacts.vcf";
        		break;
        	case MENU_RECOVER_CONTACT_SMB1:
        		targetFileName = disks.get(1).path + "contacts/Contacts.vcf";
        		break;
        	case MENU_RECOVER_CONTACT_SMB2:
        		targetFileName = disks.get(2).path + "contacts/Contacts.vcf";
        		break;
        	case MENU_RECOVER_CONTACT_SMB3:
        		targetFileName = disks.get(2).path + "contacts/Contacts.vcf";
        		break;
        	}   
    		if(!targetFileName.contains("smb")){
                File f=new File(targetFileName);
                if(!f.exists()){
                	return false;
                }
    		}
    		else{
    			SmbFile f = new SmbFile(targetFileName);
        		if(!f.exists()){
        			return false;
        		}
    		}
        }catch (Exception e) {
                // TODO: handle exception
                return false;
        }
        return true;
}
	/*
	 * private String getAppropriateFileName(final String destDirectory) { int
	 * fileNumberStringLength = 0; { // Calling Math.Log10() is costly. int tmp;
	 * for (fileNumberStringLength = 0, tmp = CONFIG_EXPORT_FILE_MAX_INDEX; tmp
	 * > 0; fileNumberStringLength++, tmp /= 10) { } } String bodyFormat =
	 * "%s%0" + fileNumberStringLength + "d%s";
	 * 
	 * if (!ALLOW_LONG_FILE_NAME) { String possibleBody =
	 * String.format(bodyFormat,mFileNamePrefix, 1, mFileNameSuffix); if
	 * (possibleBody.length() > 8 || mFileNameExtension.length() > 3) {
	 * Log.e(LOG_TAG, "This code does not allow any long file name.");
	 * mErrorReason = getString(R.string.fail_reason_too_long_filename,
	 * String.format("%s.%s", possibleBody, mFileNameExtension));
	 * showDialog(R.id.dialog_fail_to_export_with_reason); // finish() is called
	 * via the error dialog. Do not call the method here. return null; } }
	 * 
	 * // Note that this logic assumes that the target directory is case
	 * insensitive. // As of 2009-07-16, it is true since the external storage
	 * is only sdcard, and // it is formated as FAT/VFAT. // TODO: fix this. for
	 * (int i = CONFIG_EXPORT_FILE_MIN_INDEX; i <= CONFIG_EXPORT_FILE_MAX_INDEX;
	 * i++) { boolean numberIsAvailable = true; // SD Association's
	 * specification seems to require this feature, though we cannot // have the
	 * specification since it is proprietary... String body = null; for (String
	 * possibleExtension : mExtensionsToConsider) { body =
	 * String.format(bodyFormat, mFileNamePrefix, i, mFileNameSuffix); File file
	 * = new File(String.format("%s/%s.%s", destDirectory, body,
	 * possibleExtension)); if (file.exists()) { numberIsAvailable = false;
	 * break; } } if (numberIsAvailable) { return String.format("%s/%s.%s",
	 * destDirectory, body, mFileNameExtension); } }
	 * //showDialog(R.string.fail_reason_too_many_vcard); return null; }
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mActualExportThread != null) {
			// The Activity is no longer visible. Stop the thread.
			mActualExportThread.cancel();
			mActualExportThread = null;
		}
		if (mFileCopyThread != null) {
			mFileCopyThread = null;
		}
		/*
		 * if (!isFinishing()) { finish(); }
		 */
	}

	class btnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.homebg:
				MenuActivity.this.finish();
				startActivity(new Intent(MenuActivity.this,
						WifiStarActivity.class));
				break;
			case R.id.backbg:
				dispachBackKey();
				break;
			}
		}
	}

	public void dispachBackKey() {
		Log.i(TAG, "dispachBackKey");
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
				KeyEvent.KEYCODE_BACK));
		dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
	}

	// export photos,recordings
	private class FileCopyThread extends Thread {
		String mSrcFolderDir;
		String mDstFolderDir;
//		private ProgressDialog mProgressDialog;

		public FileCopyThread(String srcDir, String dstDir) {
			mSrcFolderDir = srcDir;
			mDstFolderDir = dstDir;
			Log.i(TAG, "src=" + mSrcFolderDir + " dst=" + mDstFolderDir);
//			String title = getString(R.string.exporting_data_title);
//			String message = getString(R.string.exporting_data_message,
//					mDstFolderDir);
//			mProgressDialog = new ProgressDialog(MenuActivity.this);
//			mProgressDialog.setTitle(title);
//			mProgressDialog.setMessage(message);
//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			mProgressDialog.show();
		    
		}

		public void run() {
			boolean flag = false;
			File srcFile = new File(mSrcFolderDir);
			if (!srcFile.exists()) { // Դ�ļ��в�����
				Log.i(TAG, "Դ�ļ��в�����");
				mProgressDialog.dismiss();
				return;
			}

//			String dirName = getDirName(mSrcFolderDir);
//			String destPath = mDstFolderDir + File.separator + dirName;
			String destPath = mDstFolderDir;
			// String destPath = mDstFolderDir + dirName; //jh+

			File destDirFile = new File(destPath);
			if (destDirFile.exists()) { // Ŀ��λ����һ��ͬ���ļ���
//				Log.i(TAG, "Ŀ��λ������ͬ���ļ���!");
//				File[] fileList = destDirFile.listFiles();
//				for (File temp : fileList)
//					temp.delete();

			}else {
				destDirFile.mkdirs(); // ����Ŀ¼
			}

			File[] fileList = srcFile.listFiles(); // ��ȡԴ�ļ����µ����ļ������ļ���
			if (fileList.length == 0) { // ���Դ�ļ���Ϊ��Ŀ¼��ֱ������flagΪtrue����һ���ǳ����Σ�debug�˺ܾ�
				return;
			} else {
				Message msg=Message.obtain();
				Bundle bundle=new Bundle();
				bundle.putString("dest", mDstFolderDir);
				msg.setData(bundle);
				msg.what=START;
				msg.arg1=fileList.length;
				msg.arg2=0;
				handler.sendMessage(msg);
				
//				mProgressDialog.setMax(fileList.length);
//				mProgressDialog.setProgress(0);
				for (File temp : fileList) {
					if (temp.isFile()) { // �ļ�
						flag = copyFile(temp.getAbsolutePath(), destPath);
					}
					if (!flag) {
						break;
					}
//					mProgressDialog.incrementProgressBy(1);
					Message msg2=Message.obtain();
					msg2.what=INCREACE;
					handler.sendMessage(msg2);
				}
			}
//			mProgressDialog.dismiss();
			Message msg=Message.obtain();
			msg.what=END;
			handler.sendMessage(msg);
			if (flag) {
				Log.i(TAG, "�����ļ��гɹ�!");
			}
		}
	}

	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // ����ļ���·����"//"��β������ȥ��ĩβ��"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}

	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // Դ�ļ�������
			Log.i(TAG, "Դ�ļ�������");
			return false;
		}
		// ��ȡ�������ļ����ļ���
		String fileName = srcPath
				.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;

		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // ��·�����Ѿ���һ��ͬ���ļ�
//			destFile.delete();
			return true;
		}

		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
			Log.i(TAG, "�����ļ��ɹ�!");
			;
		}
		return flag;
	}

	// export photos,recordings
	private class SmbFileCopyThread extends Thread {
		String mSrcFolderDir;
		String mDstFolderDir;
//		private ProgressDialog mProgressDialog;

		public SmbFileCopyThread(String srcDir, String dstDir) {
			mSrcFolderDir = srcDir;
			mDstFolderDir = dstDir;
			Log.i(TAG, "src=" + mSrcFolderDir + " dst=" + mDstFolderDir);
//			String title = getString(R.string.exporting_data_title);
//			String message = getString(R.string.exporting_data_message,
//					mDstFolderDir);
//			mProgressDialog = new ProgressDialog(MenuActivity.this);
//			mProgressDialog.setTitle(title);
//			mProgressDialog.setMessage(message);
//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			mProgressDialog.show();
		}

		public void run() {
			boolean flag = false;
			File srcFile = new File(mSrcFolderDir);
			if (!srcFile.exists()) { // Դ�ļ��в�����
				Log.i(TAG, "Դ�ļ��в�����");
				return;
			}

//			String dirName = getDirName(mSrcFolderDir);
//			String destPath = mDstFolderDir + dirName + File.separator;
			String destPath = mDstFolderDir;

			try {
				SmbFile destDirFile = new SmbFile(destPath);

				if (destDirFile.exists()) { // Ŀ��λ����һ��ͬ���ļ���

//					for(SmbFile sm:destDirFile.listFiles()){
//						sm.delete();
//					}
					// Log.i(TAG, "Ŀ��λ������ͬ���ļ���!");
					// destDirFile.connect();
					// SmbFile[] fileList = destDirFile.listFiles();
					// for (SmbFile temp : fileList)
					// temp.delete();
					// // return false;
					// Log.i(TAG, "delete ok!");
				}else {
					destDirFile.mkdirs();
				}
				// else
				// destDirFile.mkdirs(); // ����Ŀ¼
				Log.i(TAG, "mkdirs ok!");
				// xuwu update 20140523
				File[] fileList1 = srcFile.listFiles(); // ��ȡԴ�ļ����µ����ļ������ļ���
				File[] fileList = new File[fileList1.length];
				int j = 0;
				for (int i = 0; i < fileList1.length; i++) {
					String nameString = fileList1[i].getName();
					Log.i("==================extraName", nameString);
					if (nameString.endsWith(".jpg")
							|| nameString.endsWith(".gif")
							|| nameString.endsWith(".bmp")
							|| nameString.endsWith(".png")
							|| nameString.endsWith(".jpeg")
							|| nameString.endsWith(".tif")) {
						fileList[j] = fileList1[i];
						j++;
					}
				}
				if (fileList.length == 0) { // ���Դ�ļ���Ϊ��Ŀ¼��ֱ������flagΪtrue����һ���ǳ����Σ�debug�˺ܾ�
					return;
				} else {
					
//					mProgressDialog.setMax(j);
//					mProgressDialog.setProgress(0);
					Message msg=Message.obtain();
					Bundle bundle=new Bundle();
					bundle.putString("dest", mDstFolderDir);
					msg.setData(bundle);
					msg.what=START;
					msg.arg1=fileList.length;
					msg.arg2=0;
					handler.sendMessage(msg);
					for (int i = 0; i < j; i++) {
						File temp = fileList[i];
						// �ļ�
						// flag = copyFileToSmb(temp.getAbsolutePath(),
						// destPath);
						String srcPath = temp.getAbsolutePath();
						String fileName = srcPath.substring(srcPath
								.lastIndexOf(File.separator));
						Log.i(TAG, "smbcopy " + srcPath + " " + destPath
								+ fileName);
						// 2014/10/27 for g81
						 smbHelper.copyFile(srcPath, destPath + fileName);
//						Intent intent = new Intent(mContext,
//								DownloadService.class);
//						intent.putExtra("srcDir", srcPath);
//						intent.putExtra("toDir", mDstFolderDir);
//						intent.putExtra("CopyOperation", "copy");
//						intent.putExtra("isCut", false);
//						startService(intent);

						Message msg2=Message.obtain();
						msg2.what=INCREACE;
						handler.sendMessage(msg2);
					}
				}
				Message msg=Message.obtain();
				msg.what=END;
				handler.sendMessage(msg);
				if (flag) {
					Log.i(TAG, "�����ļ��гɹ�!");
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class AddressBookThread extends Thread implements
		DialogInterface.OnCancelListener {
			private PowerManager.WakeLock mWakeLock;
			private ProgressDialog mProgressDialog;
			private boolean mCanceled = false;
			String mExportingFileName;
			private String spath;
			
			@SuppressWarnings("deprecation")
			public AddressBookThread(String path) {
				spath = path+"contacts/Contacts.vcf";
				mExportingFileName = getString(R.string.shouji);
				Log.i(TAG, "exporting " + mExportingFileName);
				PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
				mWakeLock = powerManager.newWakeLock(
						PowerManager.SCREEN_DIM_WAKE_LOCK
								| PowerManager.ON_AFTER_RELEASE, TAG);
				{
					String title = getString(R.string.exporting_contact_list_title);
					String message = getString(
							R.string.exporting_contact_list_message,
							mExportingFileName);
					mProgressDialog = new ProgressDialog(MenuActivity.this);
					mProgressDialog.setTitle(title);
					mProgressDialog.setMessage(message);
					mProgressDialog
							.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					mProgressDialog.setOnCancelListener(this);
					mProgressDialog.setCanceledOnTouchOutside(false);
					mProgressDialog.show();
				}
			}

			@Override
			public void run() {
				mWakeLock.acquire();
				//String file = Environment.getExternalStorageDirectory() + "/contacts/Contacts.vcf";
				String cfile = Environment.getExternalStorageDirectory() + "/wcontactscash/Contacts.vcf";
				try {
					if(spath.contains("smb")){
						File removeFile = new File(Environment
								.getExternalStorageDirectory().getPath()
								+ File.separator + "wcontactscash/");
						if (!removeFile.isDirectory()) {
							removeFile.mkdir();
						}
						removeFile = new File(Environment
								.getExternalStorageDirectory().getPath()
								+ File.separator
								+ "wcontactscash/"
								+ "Contacts.vcf");
						if (removeFile.exists()) {
							removeFile.delete();
						}
						smbGetVcf(spath,cfile);
					}
					else{
						cfile = Environment.getExternalStorageDirectory() + "/contacts/Contacts.vcf";
					}
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(cfile), "UTF-8"));
						String vcardString = "";
						String line;
						while((line = reader.readLine()) != null) {
							vcardString += line + "\n";
						}
						reader.close();
						Log.i("vCard", vcardString);
						parasecontent(vcardString);
						int length = itemlist.size();
						
						mProgressDialog.setMax(length);
						mProgressDialog.setProgress(0);
						for(int i=0;i<length;i++){
						    ContentValues values = new ContentValues();
						    Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
						    long rawContactId = ContentUris.parseId(rawContactUri);
							if(itemlist.get(i).firstname!=null){
								Log.i("111", itemlist.get(i).firstname);
							}
							if(itemlist.get(i).lastname!=null){
								Log.i("222", itemlist.get(i).lastname);
							}
							if(itemlist.get(i).compositename!=null){
							    values.clear();
							    values.put(Data.RAW_CONTACT_ID, rawContactId);
							    values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
							    values.put(StructuredName.GIVEN_NAME, itemlist.get(i).compositename);
							    getContentResolver().insert(Data.CONTENT_URI, values);
								Log.i("333", itemlist.get(i).compositename);
							}
							if(itemlist.get(i).personphone!=null){
							    values.clear();
							    values.put(Data.RAW_CONTACT_ID, rawContactId);
							    values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
							    values.put(Phone.NUMBER, itemlist.get(i).personphone);
							    values.put(Phone.TYPE, Phone.TYPE_HOME);
							    getContentResolver().insert(Data.CONTENT_URI, values);
								Log.i("444", itemlist.get(i).personphone);
							}
							mProgressDialog.incrementProgressBy(1);
						}
					} catch (FileNotFoundException e) {
						return;
					} catch (SmbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
					mWakeLock.release();
					Log.i(TAG, "contacts copy success");
					mProgressDialog.dismiss();
				}
			}

			@Override
			public void finalize() {
				if (mWakeLock != null && mWakeLock.isHeld()) {
					mWakeLock.release();
				}
			}

			public void cancel() {
				mCanceled = true;
			}

			public void onCancel(DialogInterface dialog) {
				cancel();
			}
	}
	// export vard
	private class ActualExportThread extends Thread implements
			DialogInterface.OnCancelListener {
		private PowerManager.WakeLock mWakeLock;
		private ProgressDialog mProgressDialog;
		private boolean mCanceled = false;
		String mExportingFileName;

		@SuppressWarnings("deprecation")
		public ActualExportThread(String fileName) {
			mExportingFileName = fileName;
			Log.i(TAG, "exporting " + mExportingFileName);
			PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = powerManager.newWakeLock(
					PowerManager.SCREEN_DIM_WAKE_LOCK
							| PowerManager.ON_AFTER_RELEASE, TAG);
			// if (mProgressDialog == null)
			{
				String title = getString(R.string.exporting_contact_list_title);
				String message = getString(
						R.string.exporting_contact_list_message,
						mExportingFileName);
				mProgressDialog = new ProgressDialog(MenuActivity.this);
				mProgressDialog.setTitle(title);
				mProgressDialog.setMessage(message);
				mProgressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setOnCancelListener(this);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.show();
			}
		}

		@Override
		public void run() {
			// boolean shouldCallFinish = true;
			mWakeLock.acquire();
			VCardComposer composer = null;
			try {
				OutputStream outputStream = null;
				try {
					// xw2014/10/31
					// if (mExportingFileName.startsWith("smb")) {
					// Log.i(TAG, "new smb " + mExportingFileName);
					// SmbFile remoteFile = new SmbFile(mExportingFileName);
					// remoteFile.connect();
					// Log.i(TAG, "smb connect");
					// if (!remoteFile.isDirectory()) {
					// remoteFile.mkdir();
					// }
					// remoteFile = new SmbFile(mExportingFileName
					// + "contacts.vcf");
					// if (remoteFile.exists()) {
					// Log.i(TAG, "delete smb ");
					// remoteFile.delete();
					// }
					// remoteFile.createNewFile();
					//
					// outputStream = new BufferedOutputStream(
					// new SmbFileOutputStream(remoteFile));
					// } else {
					// File remoteFile = new File(mExportingFileName);
					// if (!remoteFile.isDirectory()) {
					// remoteFile.mkdir();
					// }
					// remoteFile = new File(mExportingFileName
					// + "contacts.vcf");
					// if (remoteFile.exists()) {
					// Log.i(TAG, "delete smb ");
					// remoteFile.delete();
					// }
					// remoteFile.createNewFile();
					// mExportingFileName = mExportingFileName
					// + "contacts.vcf";
					// outputStream = new FileOutputStream(mExportingFileName);
					// }

					File remoteFile = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator + "wcontactscash/");
					if (!remoteFile.isDirectory()) {
						remoteFile.mkdir();
					}
					remoteFile = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf");
					if (remoteFile.exists()) {
						Log.i(TAG, "delete smb ");
						remoteFile.delete();
					}
					remoteFile.createNewFile();
					// mExportingFileName = mExportingFileName
					// + "contacts.vcf";
					outputStream = new FileOutputStream(Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf");

				} catch (FileNotFoundException e) {
					// shouldCallFinish = false;
					return;
				} catch (SmbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// composer = new VCardComposer(ExportVCardActivity.this,
				// mVCardTypeStr, true);
				int vcardType = VCardConfig.VCARD_TYPE_V30_GENERIC_UTF8;
				composer = new VCardComposer(MenuActivity.this, vcardType, true);
				Log.i(TAG, "addHandler ");
				composer.addHandler(composer.new HandlerForOutputStream(
						outputStream));
				Log.i(TAG, "addHandler ok");
				if (!composer.init()) {
					final String errorReason = composer.getErrorReason();
					Log.e(TAG, "initialization of vCard composer failed: "
							+ errorReason);
					// shouldCallFinish = false;
					return;
				}

				int size = composer.getCount();

				if (size == 0) {
					// (R.string.fail_reason_no_exportable_contact)
					// shouldCallFinish = false;
					return;
				}

				// mProgressDialog.setProgressNumberFormat(getString(R.string.exporting_contact_list_progress));
				mProgressDialog.setMax(size);
				mProgressDialog.setProgress(0);
				// mProgressDialog.show();
				while (!composer.isAfterLast()) {
					if (mCanceled) {
						return;
					}
					if (!composer.createOneEntry()) {
						final String errorReason = composer.getErrorReason();
						Log.e(TAG, "Failed to read a contact: " + errorReason);
						// final String translatedErrorReason =
						// translateComposerError(errorReason);
//						 mHandler.post(new ErrorReasonDisplayer(
						// getString(R.string.fail_reason_error_occurred_during_export,
						// translatedErrorReason)));
						// shouldCallFinish = false;
						return;
					}
					mProgressDialog.incrementProgressBy(1);
				}
				
			} finally {
				if (composer != null) {
					composer.terminate();
				}
				mWakeLock.release();
				Log.i(TAG, "contacts copy success");
				mProgressDialog.dismiss();
				// if (shouldCallFinish && !isFinishing()) {
				// finish();
				// }
				// if (mExportingFileName.startsWith("smb")){
				if (islock) {
					FileEnDecryptManager.getInstance().Initdecrypt(
							Environment.getExternalStorageDirectory().getPath()
									+ File.separator + "wcontactscash/"
									+ "Contacts.vcf", pasString,"vcf",null);
					FileEnDecryptManager.getInstance().setPassWordtoFile(Environment.getExternalStorageDirectory().getPath()
							+ File.separator + "wcontactscash/"
							+ "Contacts.vcf", pasString);
					File srcFile = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf");
					File srcFile2 = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf.lockfile.vcf");
					srcFile.renameTo(srcFile2);
				}
				Intent intent = new Intent(mContext, DownloadService.class);
				if (islock) {
					intent.putExtra("srcDir", Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf.lockfile.vcf");
				} else {
					intent.putExtra("srcDir", Environment
							.getExternalStorageDirectory().getPath()
							+ File.separator
							+ "wcontactscash/"
							+ "Contacts.vcf");
				}
				intent.putExtra("toDir", mExportingFileName);
				Log.i("====================111", mExportingFileName);
				intent.putExtra("CopyOperation", "COVER");
				intent.putExtra("isCut", true);
				startService(intent);
				// }

			}
		}

		@Override
		public void finalize() {
			if (mWakeLock != null && mWakeLock.isHeld()) {
				mWakeLock.release();
			}
		}

		public void cancel() {
			mCanceled = true;
		}

		public void onCancel(DialogInterface dialog) {
			cancel();
		}
	}

	private class Lockfilethread extends Thread {
		File srcFile2;

		public Lockfilethread(File srcfile) {
			srcFile2 = srcfile;
		}

		@Override
		public void run() {
			final String lockdir = Environment.getExternalStorageDirectory()
					.getPath() + "/lockcache";
			String dstString = lockdir;
			File desFile = new File(dstString);
			if (desFile.exists()) {
				File [] files=desFile.listFiles();
				for(File file:files){
					if (file.isFile()) {
						file.delete();
					}
				}
			}
			if (!desFile.exists()) {
				desFile.mkdirs();
			}
			File[] files1 = srcFile2.listFiles();
			Message msg=Message.obtain();
			msg.what=LOCK_START;
			msg.arg1=files1.length;
			msg.arg2=0;
			handler.sendMessage(msg);
			for (int i = 0; i < files1.length; i++) {
				copyFile(files1[i].getAbsolutePath(), dstString);
				Message msg2=Message.obtain();
				msg2.what=LOCK_INCREACE;
				handler.sendMessage(msg2);
			}
			File[] files2 = desFile.listFiles();
			for (File f : files2) {
				if (f.isFile()) {
					FileEnDecryptManager.getInstance().InitEncrypt(f.getPath(),
							pasString, "image",null);
					File lf = new File(f.getAbsolutePath()
							+ ".lockfile"
							+ f.getName().substring(
									f.getName().lastIndexOf(".")));
					f.renameTo(lf);
					FileEnDecryptManager.getInstance().setPassWordtoFile(lf.getPath(), pasString);
				}
			}
			Message msg3=Message.obtain();
			msg3.what=LOCK_END;
			handler.sendMessage(msg3);
			try {
				if (targetFolder.startsWith("smb")) {
					SmbFile sbf = new SmbFile(targetFolder);
					if (!sbf.exists()) {
						sbf.mkdirs();
					}
				} else {
					File file = new File(targetFolder);
					if (!file.exists()) {
						file.mkdirs();
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (targetFolder.startsWith("smb")) {
				mSmbCopyThread = new SmbFileCopyThread(lockdir, targetFolder);
				mSmbCopyThread.start();

				// mSmbCopyThread2 = new
				// SmbFileCopyThread(
				// srcFolder2, targetFolder);
				// mSmbCopyThread2.start();
			} else {
				mFileCopyThread = new FileCopyThread(lockdir, targetFolder);
				mFileCopyThread.start();

				// mFileCopyThread2 = new
				// FileCopyThread(
				// srcFolder2, targetFolder);
				// mFileCopyThread2.start();

			}
		}
	}
	
	private ProgressDialog mProgressDialog;
	private ProgressDialog mLockProgressDialog;
	Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START:
				String title = getString(R.string.exporting_data_title);
				String message = getString(R.string.exporting_data_message,
						msg.getData().get("dest"));
				mProgressDialog = new ProgressDialog(MenuActivity.this);
				mProgressDialog.setTitle(title);
				mProgressDialog.setMessage(message);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setMax(msg.arg1);
				mProgressDialog.setProgress(msg.arg2);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.show();
				break;
           case INCREACE :
        	  mProgressDialog.incrementProgressBy(1);
        	   break;
           case END :
        	   mProgressDialog.dismiss();
        	   break;
           case LOCK_START :
        	   mLockProgressDialog = new ProgressDialog(MenuActivity.this);
				mLockProgressDialog.setTitle(R.string.menu_read_lock);
				mLockProgressDialog.setMessage(getString(R.string.locking));
				mLockProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mLockProgressDialog.setMax(msg.arg1);
				mLockProgressDialog.setProgress(msg.arg2);
				mProgressDialog.setCanceledOnTouchOutside(false);
				mLockProgressDialog.show();
        	   break;
           case LOCK_INCREACE :
        	   mLockProgressDialog.incrementProgressBy(1);
        	   break;
           case LOCK_END :
        	   mLockProgressDialog.dismiss();
        	   break;
			default:
				break;
			}
		};
	};

	public void parasecontent(String str){
		int tt = 10;//ASCII�뻻��
		char key = (char)tt;
		String strs [] = str.split(key+"");
		itemlist = new ArrayList<AddressBookItem>();
		for(int i=0;i<strs.length;i++){
			Log.i("strs", strs[i]);
			if(strs[i].contains("BEGIN")){
				addressBookItem = new AddressBookItem();
				Log.i("Parse start", "1111");
			}
			else if(strs[i].contains("END")){
				Log.i("Parse end", "222");
				itemlist.add(addressBookItem); 
				addressBookItem = null;	
			}
			else if(strs[i].contains("VERSION:")){
				continue;
			}
			else if(strs[i].contains("FN:")){
				String strn[] = strs[i].split(":");
				if(strn.length>1){
					String compositename = strn[1];
					Log.i("compositename", compositename);
					addressBookItem.setCompositename(compositename);
				}
			}
			else if(strs[i].contains("N:")){
				String strn[] = strs[i].split(":");
				if(strn.length>1){
					String strnn[] = strn[1].split(";");
					String firstname = strnn[0];
					String lastname="";
					if(strnn.length>1){
						lastname = strnn[1];
						addressBookItem.setLastname(lastname);
					}
					Log.i("firstname", firstname);
					Log.i("lastname", lastname);
					addressBookItem.setFirstname(firstname);
				}
			}
			else if(strs[i].contains("TEL;")){
				String strn[] = strs[i].split(":");
				if(strn.length>1){
					String personphone = strn[1];
					Log.i("personphone", personphone);
					addressBookItem.setPersonphone(personphone);
				}
			}
		}
	}
	private  void smbGetVcf(String remoteUrl, String localDir) {
		   InputStream in = null;
		   OutputStream out = null ;
		   try  {
		          SmbFile remoteFile = new SmbFile(remoteUrl);   
		          File localFile = new File(localDir);
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
}
