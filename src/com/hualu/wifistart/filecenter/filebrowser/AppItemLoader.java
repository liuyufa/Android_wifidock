package com.hualu.wifistart.filecenter.filebrowser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;   
import java.util.regex.Pattern;

import com.hualu.wifistart.filecenter.filebrowser.Browser.IAppLoader;
import com.hualu.wifistart.filecenter.files.FileItemForOperation;
import com.hualu.wifistart.filecenter.files.FileItemSet;
import com.hualu.wifistart.filecenter.files.SearchAdapter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class AppItemLoader extends AsyncTaskLoader<List<FileItemForOperation>> implements IAppLoader
{
	Context mContext;
	String mFilter;
	FileItemSet mdata;
	public static int issearch2 = 0;
	public static int isfileactivity = 0;
	public static String searchcm;
	private final String TAG = "AppItemLoader";
	public static final Comparator<FileItemForOperation> APP_NAME_COMPARATOR;
	private  SearchAdapter mSearchAdapter;
	static
	{
		APP_NAME_COMPARATOR = new Comparator<FileItemForOperation>()
		{
			public final int compare(FileItemForOperation file1, FileItemForOperation file2)
			{				
				return file1.getFileItem().getFileName().toLowerCase(Locale.getDefault()).compareTo(file2.getFileItem().getFileName().toLowerCase(Locale.getDefault()));
			}
		};
	}
	
	AppItemLoader(Context paramContext,SearchAdapter adapter)
	{      
		super(paramContext);
		this.mContext = paramContext;		
		mSearchAdapter = adapter;
	}
	public void updateAdapter(SearchAdapter adapter)
	{
		mSearchAdapter = adapter;
	}
	public List<FileItemForOperation> loadInBackground()
	{
		mdata = mSearchAdapter.mdata;
		Log.i(TAG,"loadInBackground " + mFilter + " " + mdata.getFileItems().size());
					
		if ((this.mFilter == null) || (this.mFilter.length() == 0)||mFilter.equals("")||mFilter.equals(" "))
		{			
			ArrayList<FileItemForOperation> files = new ArrayList<FileItemForOperation>();
			List<FileItemForOperation> list = mdata.getFileItems();
			for(int i = 0;i<list.size();i++)
			{          
				files.add(list.get(i));
			}
			Log.i(TAG,"loadInBackground " + files.size());			
			return files;
		}else{
			if(issearch2 == 0&& isfileactivity == 1){
				ArrayList<FileItemForOperation> files = new ArrayList<FileItemForOperation>();
				List<FileItemForOperation> list = mdata.getFileItems();
				 String filter = mFilter;//.replace('.', '#'); 
//				 filter = filter.replaceAll("#", "\\\\.");   
//				 filter = filter.replace('*', '#');   
//				 filter = filter.replaceAll("#", ".*");   
//				 filter = filter.replace('?', '#');   
//				 filter = filter.replaceAll("#", ".?");   
//				 filter = ".*" + filter + ".*";
				 searchcm = filter;
				for(int i = 0;i<list.size();i++)
				{          
					files.add(list.get(i));
				}			
				Log.i(TAG,"loadInBackground " + files.size());				
				return files;
			}
			else{
				ArrayList<FileItemForOperation> files = new ArrayList<FileItemForOperation>();
				List<FileItemForOperation> list = mdata.getFileItems();
				 String filter = mFilter.replace('.', '#');   
				 filter = filter.replaceAll("#", "\\\\.");   
				 filter = filter.replace('*', '#');   
				 filter = filter.replaceAll("#", ".*");   
				 filter = filter.replace('?', '#');   
				 filter = filter.replaceAll("#", ".?");    
				 filter = ".*" + filter + ".*";          
				 Log.i(TAG,"loadInBackground " + filter); 
				 searchcm = filter;
				 Pattern p = Pattern.compile(filter,Pattern.CASE_INSENSITIVE);
				 
				for(int position = 0;position<list.size();position++)
				{        
					FileItemForOperation file = mdata.getFileItems().get(position);
					String filename = file.getFileItem().getFileName();	
					int index = filename.length();
					if (index > 0)
						filename = filename.substring(0,index);
					Log.i(TAG,"" + filename + " " + position);
					if (filename!= null){						
						Matcher fMatcher = p.matcher(filename);   
			           if (fMatcher.matches()){	
			        	   files.add(file);
			           }
					}
					
				}
				Log.i(TAG,"loadInBackground " + files.size());		
				return files;
			}
		}
		
	}

	public void updateList(String filter,FileItemSet data)//SearchAdapter adapter
	{      
		Log.i(TAG,"updateList " + filter);
		mFilter = filter.toLowerCase(Locale.getDefault());
		mSearchAdapter.mdata = data;
		cancelLoad();
		onContentChanged();
	}
}
