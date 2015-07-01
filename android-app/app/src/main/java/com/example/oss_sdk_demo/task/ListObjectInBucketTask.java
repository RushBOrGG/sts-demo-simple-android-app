package com.example.oss_sdk_demo.task;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.model.ListObjectOption;
import com.alibaba.sdk.android.oss.model.ListObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectResult.ObjectInfo;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.util.OSSLog;
import com.example.oss_sdk_demo.activity.FileExplorerActivity;
import com.example.oss_sdk_demo.adapter.FileListAdapter;
import com.example.oss_sdk_demo.model.FileObject;
import com.example.oss_sdk_demo.model.FileObject.FileType;

public class ListObjectInBucketTask extends AsyncTask<Object, Integer, ListObjectResult> {

	private FileListAdapter adapter;
	private FileExplorerActivity inWhich;
	private String queryPath;

	public ListObjectInBucketTask(FileListAdapter adapter, FileExplorerActivity which) {
		super();
		this.adapter = adapter;
		this.inWhich = which;
	}

	@Override
	protected ListObjectResult doInBackground(Object... params) {
		OSSBucket bucket = (OSSBucket) params[0];
		ListObjectOption opt = (ListObjectOption) params[1];
		this.queryPath = opt.getPrefix();
		try {
			return bucket.listObjectsInBucket(opt);
		} catch (OSSException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(ListObjectResult result) {
		if (result == null) {
			Toast.makeText(inWhich.getApplicationContext(), "操作失败！", Toast.LENGTH_SHORT).show();
			return ;
		}
		this.inWhich.setCurrentPath(queryPath);
		List<FileObject> newList = new ArrayList<FileObject>();
		newList.add(new FileObject("..", FileType.FOLDER));
		for (String dirName : result.getCommonPrefixList()) {
		    OSSLog.logD("dirName: " + dirName);
		    dirName = dirName.substring(0, dirName.length() - 1);
		    dirName = dirName.substring(dirName.lastIndexOf("/") + 1);
		    dirName += "/";
		    newList.add(new FileObject(dirName, FileType.FOLDER));
		}
		for (ObjectInfo entry : result.getObjectInfoList()) {
			if (entry.getObjectKey().endsWith("/") && !entry.getObjectKey().equals(result.getPrefix())) {
				newList.add(new FileObject(entry.getObjectKey(), FileType.FOLDER));
			}
		}
		for (ObjectInfo entry : result.getObjectInfoList()) {
		    OSSLog.logD(entry.getObjectKey());
			if (!entry.getObjectKey().endsWith("/")) {
				newList.add(new FileObject(
				        entry.getObjectKey().substring(entry.getObjectKey().lastIndexOf("/") + 1),
				        FileType.FILE));
			}
		}
		adapter.moveToNewDataSource(newList);
		adapter.notifyDataSetChanged();
	}
}