package com.hualu.wifistart;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Bitmap mIcon1;
	private Bitmap mIcon2;
	private Bitmap mIcon3;
	private Bitmap mIcon4;
	private List<String> items;
	private List<String> paths;
	private Map<Integer, Boolean> isSelected;
	private Map<Integer, Integer> isVisibility;
	public Map<Integer, Integer> getIsVisibility() {
		return isVisibility;
	}

	private List<ViewHolder> VHs = new ArrayList<ViewHolder>();
	private Context context;

	public Map<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Map<Integer, Boolean> isSelected) {
		this.isSelected = isSelected;
	}

	public FileAdapter(Context context,List<String> it,List<String> pa){
		
		this.context = context;
		mInflater = LayoutInflater.from(context);
		if( it != null && it.size() > 0){
			isSelected = new HashMap<Integer, Boolean>();
			isVisibility = new HashMap<Integer, Integer>();
			for (int i = 0; i < it.size(); i++) {    
				isSelected.put(i, false);
				isVisibility.put(i, View.GONE);
			}
		}
		items = it;
		paths = pa;
		mIcon1 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.disk);
		mIcon2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.disk);
		mIcon3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.foldericon);
		mIcon4 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.doc);
	}

	@Override
	public int getCount(){
		return items.size();
	}

	@Override
	public Object getItem(int position){
		return items.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		ViewHolder holder;

		if(convertView == null){
			convertView = mInflater.inflate(R.layout.file_row, null);
			holder = new ViewHolder();
			VHs.add(holder);
			holder.text = (TextView) convertView.findViewById(R.id.filetext);
			holder.text.setTextColor(context.getResources().getColor(R.color.green));
			holder.icon = (ImageView) convertView.findViewById(R.id.fileicon);
			holder.cBox = (CheckBox) convertView.findViewById(R.id.file_check);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		File f=new File(paths.get(position).toString());
		if(items.get(position).toString().equals("goroot")){
			holder.text.setText("返回根目录");
			holder.icon.setImageBitmap(mIcon1);
			holder.cBox.setChecked(isSelected.get(position));
			holder.cBox.setVisibility(isVisibility.get(position));
		}else if(items.get(position).toString().equals("goparent")){
			holder.text.setText("返回上一级");
			holder.icon.setImageBitmap(mIcon2);
			holder.cBox.setChecked(isSelected.get(position));
			holder.cBox.setVisibility(isVisibility.get(position));
		}else{
			holder.text.setText(f.getName());
			if(f.isDirectory()){
				holder.icon.setImageBitmap(mIcon3);
				holder.cBox.setChecked(isSelected.get(position));
				holder.cBox.setVisibility(isVisibility.get(position));
			}else{
				holder.icon.setImageBitmap(mIcon4);
				holder.cBox.setChecked(isSelected.get(position));
				holder.cBox.setVisibility(isVisibility.get(position));
			}
		}
		return convertView;
	}

	public List<ViewHolder> getVHs() {
		return VHs;
	}

	public final class ViewHolder
	{
		public TextView text;
		public ImageView icon;
		public CheckBox cBox;
	}
}

