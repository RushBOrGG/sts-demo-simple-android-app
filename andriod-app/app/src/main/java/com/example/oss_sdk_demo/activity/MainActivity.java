package com.example.oss_sdk_demo.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.model.AuthenticationType;
import com.alibaba.sdk.android.oss.util.OSSLog;
import com.example.oss_sdk_demo.R;
import com.example.oss_sdk_demo.model.FederationToken;
import com.example.oss_sdk_demo.model.FederationTokenGetter;
import com.example.oss_sdk_demo.util.AppUtil;


public class MainActivity extends Activity implements OnClickListener {

	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initConfig();

		Button buttonA = (Button) findViewById(R.id.button_a_login);
		buttonA.setOnClickListener(this);
		Button buttonB = (Button) findViewById(R.id.button_b_login);
		buttonB.setOnClickListener(this);

		// 初始化OSSService
		AppUtil.ossService = OSSServiceProvider.getService();
		AppUtil.ossService.setApplicationContext(this.getApplicationContext());
		AppUtil.ossService.setGlobalDefaultHostId(AppUtil.endPoint);

		// 打开调试log
		OSSLog.enableLog(true);
	}

	// 从Manifest.xml的Meta-data中获得加签服务器地址
	private void initConfig() {
	    try {
            ApplicationInfo appInfo = this.getApplicationContext().getPackageManager().
                    getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            AppUtil.serverAddress = appInfo.metaData.getString("ServerAddress");
			AppUtil.bucketName = appInfo.metaData.getString("BucketName");
			AppUtil.endPoint = appInfo.metaData.getString("EndPoint");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
			finish();
        }
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_a_login:
			Log.d("OSS_", "[onclick] - button_a_login");
			userId = "userA";
			break;
		case R.id.button_b_login:
			Log.d("OSS_", "[onclick] - button_b_login");
			userId = "userB";
			break;
		default:
			break;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 为指定的用户拿取服务其授权需求的FederationToken
				FederationToken token = FederationTokenGetter.getToken(AppUtil.serverAddress, userId);
				if (token == null) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setMessage("获取FederationToken失败!!!");
							builder.setTitle("警告");
							builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
						}
					});
					return;
				}
				// 将FederationToken设置到OSSService中
				AppUtil.ossService.setOrUpdateFederationToken(token.getAccessKeyId(), token.getAccessKeySecret(), token.getSecurityToken());
				AppUtil.ossService.setAuthenticationType(AuthenticationType.FEDERATION_TOKEN);
			}
		}).start();

		Intent intent = new Intent();
		intent.setClass(this, FileExplorerActivity.class);
		startActivity(intent);
	}
}
