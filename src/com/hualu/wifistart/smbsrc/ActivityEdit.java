package com.hualu.wifistart.smbsrc;

import java.io.File;

import com.hualu.wifistart.R;
import com.hualu.wifistart.wifisetting.utils.ToastBuild;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ActivityEdit extends PreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener
{
	private File nowSelectFile;
	private Preference infoPreference;
	private EditTextPreference infoEditTextPreference,softEditTextPreference,deleEditTextPreference;
	private ListPreference listPreference;
	private CheckBoxPreference isDeleBoxPreference,isAlertBoxPreference;
	private EditTextPreference alertInfoEditTextPreference;
	
	@SuppressWarnings("deprecation")
	@Override
 	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.edit_activity);
		getPreferenceManager().setSharedPreferencesName("temp");
		
		Intent intent = getIntent();
		File file = (File) intent.getSerializableExtra("nowSelectFile");
		nowSelectFile = file;
		
		infoPreference = findPreference(getString(R.string.edit_activity_key_name)); //name
		infoEditTextPreference = (EditTextPreference) findPreference(getString(R.string.edit_activity_key_info));//info
		softEditTextPreference = (EditTextPreference) findPreference(getString(R.string.edit_activity_key_soft));//soft
		deleEditTextPreference = (EditTextPreference) findPreference(getString(R.string.edit_activity_key_dele));//dele
		listPreference = (ListPreference)findPreference(getString(R.string.edit_activity_key_index)); //index
		isDeleBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.edit_activity_key_oneKey_isDele)); //isDele
		isAlertBoxPreference = (CheckBoxPreference) findPreference(getString(R.string.edit_activity_key_oneKey_isAlert));//isAlert
		alertInfoEditTextPreference = (EditTextPreference) findPreference(getString(R.string.edit_activity_key_oneKey_alertInfo));//alertInfo
		
		infoPreference.setTitle(nowSelectFile.getName());
		infoPreference.setSummary(nowSelectFile.getParent());
		
		// 设置显示信息
		String infoEditSummaryString = ServiceDatabaseManager.getInfoString(nowSelectFile.getAbsolutePath(), ActivityEdit.this);
		if (infoEditSummaryString != null && !infoEditSummaryString.equals(getString(R.string.string_null)))
		{
			infoEditTextPreference.setSummary(infoEditSummaryString);
			infoEditTextPreference.setText(infoEditSummaryString);
		}
		infoEditTextPreference.setOnPreferenceChangeListener(this);
		
		// 生成软件
		String softEditSummaryString = ServiceDatabaseManager.getSoftString(nowSelectFile.getAbsolutePath(), this);
		if (!ServiceDatabaseManager.checkInfoExist(nowSelectFile.getAbsolutePath(), this))
			softEditSummaryString = ServiceDatabaseManager.getSoftString(nowSelectFile.getParent(), this);
		else 
			softEditSummaryString = ServiceDatabaseManager.getSoftString(nowSelectFile.getAbsolutePath(), this);
		softEditTextPreference.setSummary(softEditSummaryString);
		softEditTextPreference.setText(softEditSummaryString);
		softEditTextPreference.setOnPreferenceChangeListener(this);
		
		String deleSummaryString  = ServiceDatabaseManager.getDeleInfoString(nowSelectFile.getAbsolutePath(), this);
		if (deleSummaryString != null && !deleSummaryString.equals(getString(R.string.string_null)) )
		{
			deleEditTextPreference.setSummary(deleSummaryString);
			deleEditTextPreference.setText(deleSummaryString);
		}
		deleEditTextPreference.setOnPreferenceChangeListener(this);
		
		Integer nowSelectInteger = new Integer(ServiceDatabaseManager.getDeleIndexString(nowSelectFile.getAbsolutePath(), this));
		listPreference.setValueIndex(nowSelectInteger-1);
		listPreference.setSummary(getResources().getStringArray(R.array.edit_activity_spinner_index)[nowSelectInteger-1]) ;
		listPreference.setOnPreferenceChangeListener(this);
		
		Integer isDeleInteger = new Integer(ServiceDatabaseManager.getDeleisDeteString(nowSelectFile.getAbsolutePath(), this));
		setIsDeleCheck(isDeleInteger == 1 ? true : false);
		isDeleBoxPreference.setOnPreferenceChangeListener(this);
		
		Integer isAlertInteger = new Integer(ServiceDatabaseManager.getDeleisAlertString(nowSelectFile.getAbsolutePath(), this));
		setIsAlertCheck(isAlertInteger == 1 ? true : false);
		isAlertBoxPreference.setOnPreferenceChangeListener(this);
		
		String alertInfoSummaryString = ServiceDatabaseManager.getDeleAlertInfoString(nowSelectFile.getAbsolutePath(), this);
		if (alertInfoSummaryString != null && !alertInfoSummaryString.equals(getString(R.string.string_null)))
		{
			alertInfoEditTextPreference.setSummary(alertInfoSummaryString);
			alertInfoEditTextPreference.setText(alertInfoSummaryString);
		}
		alertInfoEditTextPreference.setOnPreferenceChangeListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// 信息
		if (preference.getKey().equals(getString(R.string.edit_activity_key_info)))
		{
			infoEditTextPreference.setSummary(newValue.toString());
		}
		// 生成软件
		else if (preference.getKey().equals(getString(R.string.edit_activity_key_soft)))
		{
			softEditTextPreference.setSummary(newValue.toString());
		}
		// 强制删除
		else if (preference.getKey().equals(getString(R.string.edit_activity_key_dele)))
		{
			deleEditTextPreference.setSummary(newValue.toString());
			alertInfoEditTextPreference.setText(softEditTextPreference.getText().toString() + newValue.toString());
			alertInfoEditTextPreference.setSummary(softEditTextPreference.getText().toString() + newValue.toString());
		}
		 // 删除指数
		else if (preference.getKey().equals(getString(R.string.edit_activity_key_index)))
		{
			int newValue1 = new Integer(newValue.toString())-1;
			String listSummary =  getResources().getStringArray(R.array.edit_activity_spinner_index)[newValue1];
	        //系统文件  备份文件 大多数用户数据 大多数软件数据 无用文件 未知用处
			listPreference.setSummary(listSummary);
			
			setIsDeleCheck(newValue1 >= 4 ? true : false);
		}else if (preference.getKey().equals(getString(R.string.edit_activity_key_oneKey_isDele)))
		{
			setIsDeleCheck(newValue.toString().equals("true") ? true : false);
		}else if (preference.getKey().equals(getString(R.string.edit_activity_key_oneKey_isAlert)))
		{
			setIsAlertCheck(newValue.toString().equals("true") ? true : false);
		}else if (preference.getKey().equals(getString(R.string.edit_activity_key_oneKey_alertInfo)))
		{
			alertInfoEditTextPreference.setSummary(newValue.toString());
		}
		
		return true;
	}
	
	private void setIsDeleCheck(boolean checked) {
		isDeleBoxPreference.setChecked(checked);
		isAlertBoxPreference.setEnabled(checked);
		setIsAlertCheck(checked);
	}
	
	private void setIsAlertCheck(boolean checked) {
		isAlertBoxPreference.setChecked(checked);
		alertInfoEditTextPreference.setEnabled(checked);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(R.string.edit_activity_button_save_lable).setIcon(android.R.drawable.ic_menu_edit); //保存<
		menu.add(R.string.edit_activity_button_cancel_lable).setIcon(android.R.drawable.ic_menu_close_clear_cancel); //取消
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		try
		{
			if (item.toString().equals(getString(R.string.edit_activity_button_save_lable)))
			{
				if (ServiceDatabaseManager.checkInfoExist(nowSelectFile.getAbsolutePath(), this))
					ServiceDatabaseManager.updatePathInfo(	
						nowSelectFile.getAbsolutePath(),
						nowSelectFile.getName(), 
						noEmptyString(infoEditTextPreference.getText().toString()),
						noEmptyString(softEditTextPreference.getText().toString()),
						noEmptyString(deleEditTextPreference.getText().toString()),
						listPreference.getValue(), 
						isDeleBoxPreference.isChecked() ? "1" : "0",
						isAlertBoxPreference.isChecked() ? "1" : "0",
						noEmptyString(alertInfoEditTextPreference.getText().toString()),
						this);
				else
				ServiceDatabaseManager.addNewPathInfo(
						nowSelectFile.getAbsolutePath(),
						nowSelectFile.getName(), 
						noEmptyString(infoEditTextPreference.getText().toString()),
						noEmptyString(softEditTextPreference.getText().toString()),
						noEmptyString(deleEditTextPreference.getText().toString()),
						listPreference.getValue(), 
						isDeleBoxPreference.isChecked() ? "1" : "0",
						isAlertBoxPreference.isChecked() ? "1" : "0",
						noEmptyString(alertInfoEditTextPreference.getText().toString()),
						this);
			}
			finish();
		} catch (Exception e)
		{
			/*liuyufa change for taost start*/
			ToastBuild.toast(this, R.string.dialog_empty);
			//Toast.makeText(this, "保存失败，理由：有输入框为空", 1).show();
			/*liuyufa change for taost end*/
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String noEmptyString(String string) {
		
		if (string == null || string.length() < 1 )
		{
			return getString(R.string.string_null);
		}
		return string;
	}
	
}
