package com.hualu.wifistart.filecenter.files;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


public class SmbFileComparatorByName implements Comparator<SmbFile>{

	@Override
	public int compare(SmbFile file1, SmbFile file2) {
		Collator ca = Collator.getInstance(Locale.CHINA);
		try {
			if(file1.isDirectory() && !file2.isDirectory()){
				return -1;
			}else if(file1.isDirectory() && file2.isDirectory()){
				return ca.compare(file1.getName().toLowerCase(Locale.getDefault()),file2.getName().toLowerCase(Locale.getDefault()));
			}else if(!file1.isDirectory() && file2.isDirectory()){
				return 1;
			}else{
				return ca.compare(file1.getName().toLowerCase(Locale.getDefault()),file2.getName().toLowerCase(Locale.getDefault()));
			}
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1; 
	}
}
