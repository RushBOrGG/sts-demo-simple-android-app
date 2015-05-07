package com.example.oss_sdk_demo.adapter;

import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.util.OSSLog;
import com.example.oss_sdk_demo.R;
import com.example.oss_sdk_demo.model.FileObject;
import com.example.oss_sdk_demo.model.FileObject.FileType;

public class FileListAdapter extends BaseAdapter {

	private List<FileObject> dataSource;
	private Stack<List<FileObject>> historyPath = new Stack<>();
	private Context attachTo;

	public FileListAdapter(Context ctx) {
		this.attachTo = ctx;
	}


	@Override
	public int getCount() {
		return dataSource.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataSource.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FileObject thisFile = dataSource.get(position);
		LayoutInflater inflater = LayoutInflater.from(this.attachTo);
		if (thisFile.getFileType() == FileType.FILE) {
			convertView = inflater.inflate(R.layout.file_item, null);
			TextView name = (TextView) convertView.findViewById(R.id.file_name);
			name.setText(thisFile.getFileName());
		} else {
			convertView = inflater.inflate(R.layout.file_folder_item, null);
			TextView name = (TextView) convertView.findViewById(R.id.folder_name);
			name.setText(thisFile.getFileName());
		}
		return convertView;
	}

	public List<FileObject> getDataSource() {
	    return this.dataSource;
	}

	public void moveToNewDataSource(List<FileObject> dataSource) {
		this.dataSource = dataSource;
		historyPath.push(dataSource);
	}

	public void traceBackToPreviousDataSource() {
	    historyPath.pop();
	    this.dataSource = historyPath.peek();
	    OSSLog.logD("size: " + dataSource.size());
	}
}
