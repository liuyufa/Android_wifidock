package com.hualu.wifistart.wifisetting.utils;

public final class IPInfoValidityUtils {
	public final static boolean IPInfoValidity(String ipinfo,int id){
		String info = ipinfo;
		int countalldot=0;
		int len = info.length();
		char dot = '.'; 
		String temp[]={"","","",""};
		/*备域名服务器是否为空*/
		if(0==len&&id==4){
			return true;
		}
		/*字符串是否符合长度7-15*/
		if(6<len&&len<16){
			/*计算.符号个数*/
			for(int i=0;i<len;i++){
				if(dot == info.charAt(i)){
					/*是否首尾为空*/
					if(i==0||i==len-1){
						return false;
					}
					countalldot++;
					/*是否连续两个.符号*/
					if(len-1!=i){
						if(info.charAt(i)==info.charAt(i+1)){
							return false;
						}
					}
				}
			}
			if(3==countalldot){
				/*以.符号为标志拆分字符串*/
				temp=info.split("\\.");
				for(int i=0;i<4;i++){
					int size = temp[i].length();
					/*是否全为数字*/
					for(int j=0;j<size;j++){
						if('0'>temp[i].charAt(j)||'9'<temp[i].charAt(j)){
							return false;
						}
					}
					if(size >1){
						/*首字符不为0*/
						if('0'==temp[i].charAt(0)){
							return false;
						}
					}
					/*是否为0-255*/
					if(0> Integer.parseInt(temp[i])||255<Integer.parseInt(temp[i])){
						return false;
					}
					/*子网掩码首字符必须为255*/
					if(1==id&&255!=Integer.parseInt(temp[0])){
						return false;
					}
				}
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
		return true;
	}
}
