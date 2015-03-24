package com.hualu.wifistart.filecenter.files;

import java.util.Comparator;
import java.util.Locale;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class SmbFileComparatorBySize implements Comparator<SmbFile>{
	
	@Override
	public int compare(SmbFile file1, SmbFile file2) {
		try {
			if(file1.isDirectory() && !file2.isDirectory()){
				return -1;
			}else if(file1.isDirectory() && file2.isDirectory()){
				return file1.getName().toLowerCase(Locale.getDefault()).compareTo(file2.getName().toLowerCase(Locale.getDefault()));
			}else if(!file1.isDirectory() && file2.isDirectory()){
				return 1;
			}else{
				long result = (file1.length()-file2.length());
				if(result>=0){
					return 1;
				}else{
					return -1;
				}
			}
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}
