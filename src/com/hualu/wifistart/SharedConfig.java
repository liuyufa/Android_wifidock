package com.hualu.wifistart;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedConfig {
	Context context;
	SharedPreferences shared;
	public SharedConfig(Context context){
		this.context=context;
		shared=context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public SharedPreferences GetConfig(){
		return shared;
	}
	public void ClearConfig(){
		shared.edit().clear().commit();
	}
}
