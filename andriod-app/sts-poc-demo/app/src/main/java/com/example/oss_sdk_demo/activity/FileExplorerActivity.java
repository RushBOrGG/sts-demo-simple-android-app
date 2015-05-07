package com.example.oss_sdk_demo.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.ListObjectOption;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSLog;
import com.example.oss_sdk_demo.R;
import com.example.oss_sdk_demo.adapter.FileListAdapter;
import com.example.oss_sdk_demo.model.FileObject;
import com.example.oss_sdk_demo.model.FileObject.FileType;
import com.example.oss_sdk_demo.task.ListObjectInBucketTask;
import com.example.oss_sdk_demo.util.AppUtil;

public class FileExplorerActivity extends Activity implements OnClickListener, OnItemClickListener {

    private ListView fileListView;
    private Button uploadBtn;
    private FileListAdapter adapter;
    private String currentPath = "";
    private OSSBucket bucket;
    final private String DEFAULT_FILE_SAVE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sts_poc_file/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer_activity);

        bucket = AppUtil.ossService.getOssBucket(AppUtil.bucketName);

        File file = new File(DEFAULT_FILE_SAVE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }

        fileListView = (ListView) findViewById(R.id.file_list);
        uploadBtn = (Button) findViewById(R.id.button_upload);
        adapter = new FileListAdapter(this);

        uploadBtn.setOnClickListener(this);
        uploadBtn.setEnabled(false);
        fileListView.setOnItemClickListener(this);
        initListView(fileListView);
    }

    public void initListView(ListView listView) {
        List<FileObject> data = new ArrayList<FileObject>();
        data.add(new FileObject("userA/", FileType.FOLDER));
        data.add(new FileObject("userB/", FileType.FOLDER));
        adapter.moveToNewDataSource(data);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void downloadFile(String objectKey) {
        OSSFile ossFile = AppUtil.ossService.getOssFile(bucket, objectKey);
        String filePath = DEFAULT_FILE_SAVE_PATH + objectKey.substring(objectKey.lastIndexOf("/") + 1);
        ossFile.downloadToInBackground(filePath, new GetFileCallback() {
            @Override
            public void onProgress(String arg0, int arg1, int arg2) {
            }
            @Override
            public void onFailure(String arg0, OSSException arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FileExplorerActivity.this.getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onSuccess(String arg0, String arg1) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(FileExplorerActivity.this.getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == 1) {
            Uri uri = data.getData();
            final String path = getRealPathFromURI(this.getApplicationContext(), uri);
            final String fileName = path.substring(path.lastIndexOf("/") + 1);
            final String objectKey = currentPath + fileName;
            OSSFile ossFile = AppUtil.ossService.getOssFile(bucket, objectKey);
            try {
                ossFile.setUploadFilePath(path, "file");
                ossFile.uploadInBackground(new SaveCallback() {
                    @Override
                    public void onProgress(String arg0, int arg1, int arg2) {
                    }
                    
                    @Override
                    public void onFailure(String objectKey, OSSException ossException) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(FileExplorerActivity.this.getApplicationContext(), "上传失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    
                    @Override
                    public void onSuccess(String objectKey) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.getDataSource().add(new FileObject(fileName, FileType.FILE));
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.button_upload:
            showFileChooser();
            break;
        default:
            break;
        }
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
		if (currentPath.length() != 0) {
		    uploadBtn.setEnabled(true);
		} else {
		    uploadBtn.setEnabled(false);
		}
    }

    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long row) {
		FileObject fo = adapter.getDataSource().get(position);
		if (fo.getFileType() == FileType.FOLDER) {
		    if (fo.getFileName().equals("..")) {
                // 返回上一级目录
		        String upperFolder = currentPath.substring(0, currentPath.lastIndexOf("/"));
		        upperFolder = upperFolder.substring(0, upperFolder.lastIndexOf("/") + 1);
		        setCurrentPath(upperFolder);
		        adapter.traceBackToPreviousDataSource();
		        adapter.notifyDataSetChanged();
		    } else {
                // 发起网络请求，读取下一级文件列表
		       	ListObjectOption loo = new ListObjectOption();
		       	loo.setDelimiter("/");
		       	loo.setPrefix(currentPath + fo.getFileName());
		       	loo.setMaxKeys(1000);
		       	new ListObjectInBucketTask(adapter, this).execute(bucket, loo);
		    }
		} else if (fo.getFileType() == FileType.FILE) {
		    final String fileName = currentPath + fo.getFileName();
		    AlertDialog.Builder builder = new Builder(FileExplorerActivity.this);
		    builder.setMessage("是否下载文件？");
		    builder.setTitle("文件下载");
		    builder.setPositiveButton("开始下载", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		            downloadFile(fileName);
		        }
		    });
		    builder.setNegativeButton("取消下载", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		    builder.create().show();
		}
	}
}
