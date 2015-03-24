package com.hualu.wifistart.db;

/**
 * 音乐属性类
 * @author Administrator
 *
 */
public class FileColumn {
	/**
	 * 表名
	 */
	public static final String TABLE="File_Table";
	
	/**
	 * 列名
	 */

	public static final String ID="fileId";
	public static final String NAME="fileName";
	public static final String PATH="path";
	public static final String ALBUM="album";
	public static final String SINGER="singer";
	public static final String SIZE="size";
	public static final String TIME="time";
	public static final String TITTLE="tittle";
	public static final String TYPE="type";
	public static final String SORT="sortPId";
	
	
	public static final int ID_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int PATH_COLUMN = 2;
	
	public static final int ALBUM_COLUMN = 3;
	public static final int SINGER_COLUMN = 4;
	public static final int SIZE_COLUMN = 5;
	public static final int TIME_COLUMN = 6;
	public static final int TITTLE_COLUMN = 7;
	
	public static final int TYPE_COLUMN = 8;
	public static final int SORT_COlUMN = 9;
	/**
	 * 获取列名
	 * @return 列名数组
	 */
	public static String[] getColumArray(){
		return new String[]{ID,NAME,PATH,TYPE,SORT};
	}
	
}
