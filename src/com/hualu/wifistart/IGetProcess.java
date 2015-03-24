package com.hualu.wifistart;

import java.util.ArrayList;

import com.hualu.wifistart.StatusActivity.TransferItemInfo;

public interface IGetProcess
{
	ArrayList<TransferItemInfo> getProcessList();
	void removeProcess(TransferItemInfo item);
}
