package com.hj.mobilesafe;

import com.hj.mobilesafe.service.AddressService;
import com.hj.mobilesafe.service.CallSmsSafeService;
import com.hj.mobilesafe.service.WatchDogService;
import com.hj.mobilesafe.ui.SettingItemView;
import com.hj.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {

	// 设置是否开启自动更新
	private SettingItemView siv_update;
	private SharedPreferences sp;

	// 设置是否开启显示归属地
	private SettingItemView siv_show_address;
	private Intent showAddress;

	// 设置是否开启黑名单拦截
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	//设置是否开启程序锁
	private SettingItemView siv_watchdog;
	private Intent watchDogIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
//		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
		siv_callsms_safe = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		siv_watchdog = (SettingItemView) findViewById(R.id.siv_watchdog);

//		boolean update = sp.getBoolean("update", false);
//		if (update) {
//			// 自动升级已经开启
//			siv_update.setChecked(true);
//		} else {
//			// 自动升级已经关闭
//			siv_update.setChecked(false);
//		}

		// 设置自动更新
//		siv_update.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Editor editor = sp.edit();
//				// 判断是否选中
//				// 已经打开自动升级
//				if (siv_update.isChecked()) {
//					siv_update.setChecked(false);
//					editor.putBoolean("update", false);
//
//				} else {
//					// 没有打开自动升级
//					siv_update.setChecked(true);
//					editor.putBoolean("update", true);
//				}
//				editor.commit();
//			}
//		});
		// 设置显示归属地
		showAddress = new Intent(this, AddressService.class);
		boolean isRunning = ServiceUtils.isServiceRunning(this,
				"com.hj.mobilesafe.service.AddressService");
		if (isRunning) {
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}
		siv_show_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_show_address.isChecked()) {
					// 非选中
					siv_show_address.setChecked(false);
					stopService(showAddress);
				} else {
					// 选中
					siv_show_address.setChecked(true);
					startService(showAddress);
				}

			}
		});

		// 设置黑名单拦截
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// 变为非选中状态
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// 选择状态
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}

			}
		});
		
		// 设置程序锁
		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watchdog.isChecked()) {
					// 变为非选中状态
					siv_watchdog.setChecked(false);
					stopService(watchDogIntent);
				} else {
					// 选择状态
					siv_watchdog.setChecked(true);
					startService(watchDogIntent);
				}

			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showAddress = new Intent(this, AddressService.class);
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hj.mobilesafe.service.AddressService");

		if (isServiceRunning) {
			// 监听来电的服务是开启的
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hj.mobilesafe.service.CallSmsSafeService");
		// 监听黑名单拦截的服务是开启的
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
		boolean iswatchdogServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hj.mobilesafe.service.WatchDogService");
		// 监听程序锁的服务是开启的
		siv_watchdog.setChecked(iswatchdogServiceRunning);
	}
}
