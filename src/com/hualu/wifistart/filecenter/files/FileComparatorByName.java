package com.hualu.wifistart.filecenter.files;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;


public class FileComparatorByName implements Comparator<File>{

	@Override
	public int compare(File file1, File file2) {
		Collator ca = Collator.getInstance(Locale.CHINA);
		if(file1.isDirectory() && !file2.isDirectory()){
			return -1;
		}else if(file1.isDirectory() && file2.isDirectory()){
		  	return ca.compare(file1.getName().toLowerCase(Locale.getDefault()),file2.getName().toLowerCase(Locale.getDefault()));
		}else if(!file1.isDirectory() && file2.isDirectory()){
			return 1;
		}else{
			return ca.compare(file1.getName().toLowerCase(Locale.getDefault()),file2.getName().toLowerCase(Locale.getDefault()));
		}
	}
}
