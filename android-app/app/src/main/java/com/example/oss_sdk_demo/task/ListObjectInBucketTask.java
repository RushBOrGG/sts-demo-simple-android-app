package com.example.oss_sdk_demo.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.example.oss_sdk_demo.activity.FileExplorerActivity;
import com.example.oss_sdk_demo.adapter.FileListAdapter;
import com.example.oss_sdk_demo.model.FileObject;
import com.example.oss_sdk_demo.model.FileObject.FileType;
import com.example.oss_sdk_demo.util.AppUtil;

public class ListObjectInBucketTask extends AsyncTask<Object, Integer, ListObjectsResult> {

	private FileListAdapter adapter;
	private FileExplorerActivity inWhich;
	private String queryPath;

	public ListObjectInBucketTask(FileListAdapter adapter, FileExplorerActivity which) {
		super();
		this.adapter = adapter;
		this.inWhich = which;
	}

	@Override
	protected ListObjectsResult doInBackground(Object... params) {
		ListObjectsRequest listObject = (ListObjectsRequest) params[0];
		this.queryPath = listObject.getPrefix();
		try {
			return AppUtil.oss.listObjects(listObject);
		} catch (ClientException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ListObjectsResult result) {
		if (result == null) {
			Toast.makeText(inWhich.getApplicationContext(), "操作失败！", Toast.LENGTH_SHORT).show();
			return ;
		}
		this.inWhich.setCurrentPath(queryPath);
		List<FileObject> newList = new ArrayList<FileObject>();
		newList.add(new FileObject("..", FileType.FOLDER));
		for (String dirName : result.getCommonPrefixes()) {
		    OSSLog.logD("dirName: " + dirName);
		    dirName = dirName.substring(0, dirName.length() - 1);
		    dirName = dirName.substring(dirName.lastIndexOf("/") + 1);
		    dirName += "/";
		    newList.add(new FileObject(dirName, FileType.FOLDER));
		}
		for (OSSObjectSummary object : result.getObjectSummaries()) {
			if (object.getKey().endsWith("/") && !object.getKey().equals(result.getPrefix())) {
				newList.add(new FileObject(object.getKey(), FileType.FOLDER));
			}
		}
		for (OSSObjectSummary object : result.getObjectSummaries()) {
			OSSLog.logD(object.getKey());
			if (!object.getKey().endsWith("/")) {
				newList.add(new FileObject(object.getKey().substring(object.getKey().lastIndexOf("/") + 1), FileType.FILE));
			}
		}
		adapter.moveToNewDataSource(newList);
		adapter.notifyDataSetChanged();
	}
}