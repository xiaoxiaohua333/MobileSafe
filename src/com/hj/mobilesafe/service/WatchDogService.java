package com.hj.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.hj.mobilesafe.EnterPwdActivity;
import com.hj.mobilesafe.db.dao.ApplockDao;

/**
 * 用来监视系统程序的运行状态
 * 
 * @author Administrator
 * 
 */
public class WatchDogService extends Service {
	private ActivityManager am;
	private boolean flag;
	private ApplockDao dao;
	private InnerReceiver innerReceiver;
	private String tempStopProtectPackname;
	private ScreenOffReceiver offreceiver;
	private DataChangeReceiver changeReceiver;

	private List<String> protectPacknames;
	private Intent intent;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = null;
		}
	}

	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPackname = intent.getStringExtra("packname");
		}

	}

	private class DataChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			protectPacknames = dao.findAll();
		}

	}

	@Override
	public void onCreate() {
		offreceiver = new ScreenOffReceiver();
		registerReceiver(offreceiver,
				new IntentFilter(Intent.ACTION_SCREEN_OFF));

		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter(
				"com.hj.mobilesafe.tempstop"));

		changeReceiver = new DataChangeReceiver();
		registerReceiver(changeReceiver, new IntentFilter(
				"com.hj.mobilesafe.applockchange"));

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new ApplockDao(this);
		protectPacknames = dao.findAll();
		flag = true;
		intent = new Intent();
		intent.setClass(getApplicationContext(), EnterPwdActivity.class);
		// server don't have infos of ZHAN, open activity in
		// server , need make the activiyt flag
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					if (protectPacknames.contains(packname)) {
						// 判断应用是否需要临时保护
						if (packname.equals(tempStopProtectPackname)) {

						} else {
							// 保护 输入密码
							// 设置要保护程序的包名
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offreceiver);
		offreceiver = null;
		unregisterReceiver(changeReceiver);
		changeReceiver = null;
		super.onDestroy();
	}
}
