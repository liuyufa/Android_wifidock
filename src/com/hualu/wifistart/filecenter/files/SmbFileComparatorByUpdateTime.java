package com.hualu.wifistart.filecenter.files;

import java.util.Comparator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;


public class SmbFileComparatorByUpdateTime implements Comparator<SmbFile>{

	@Override
	public int compare(SmbFile file1, SmbFile file2) {
		try {
			if(file1.isDirectory() && !file2.isDirectory()){
				return -1;
			}else if(!file1.isDirectory() && file2.isDirectory()){
				return 1;
			}else{
				long result = (file1.lastModified()-file2.lastModified());
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
