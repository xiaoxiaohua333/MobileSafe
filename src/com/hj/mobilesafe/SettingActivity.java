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

	// �����Ƿ����Զ�����
	private SettingItemView siv_update;
	private SharedPreferences sp;

	// �����Ƿ�����ʾ������
	private SettingItemView siv_show_address;
	private Intent showAddress;

	// �����Ƿ�������������
	private SettingItemView siv_callsms_safe;
	private Intent callSmsSafeIntent;
	
	//�����Ƿ���������
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
//			// �Զ������Ѿ�����
//			siv_update.setChecked(true);
//		} else {
//			// �Զ������Ѿ��ر�
//			siv_update.setChecked(false);
//		}

		// �����Զ�����
//		siv_update.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Editor editor = sp.edit();
//				// �ж��Ƿ�ѡ��
//				// �Ѿ����Զ�����
//				if (siv_update.isChecked()) {
//					siv_update.setChecked(false);
//					editor.putBoolean("update", false);
//
//				} else {
//					// û�д��Զ�����
//					siv_update.setChecked(true);
//					editor.putBoolean("update", true);
//				}
//				editor.commit();
//			}
//		});
		// ������ʾ������
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
					// ��ѡ��
					siv_show_address.setChecked(false);
					stopService(showAddress);
				} else {
					// ѡ��
					siv_show_address.setChecked(true);
					startService(showAddress);
				}

			}
		});

		// ���ú���������
		callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);
		siv_callsms_safe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_callsms_safe.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_callsms_safe.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					// ѡ��״̬
					siv_callsms_safe.setChecked(true);
					startService(callSmsSafeIntent);
				}

			}
		});
		
		// ���ó�����
		watchDogIntent = new Intent(this, WatchDogService.class);
		siv_watchdog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_watchdog.isChecked()) {
					// ��Ϊ��ѡ��״̬
					siv_watchdog.setChecked(false);
					stopService(watchDogIntent);
				} else {
					// ѡ��״̬
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
			// ��������ķ����ǿ�����
			siv_show_address.setChecked(true);
		} else {
			siv_show_address.setChecked(false);
		}

		boolean iscallSmsServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hj.mobilesafe.service.CallSmsSafeService");
		// �������������صķ����ǿ�����
		siv_callsms_safe.setChecked(iscallSmsServiceRunning);
		
		boolean iswatchdogServiceRunning = ServiceUtils.isServiceRunning(
				SettingActivity.this,
				"com.hj.mobilesafe.service.WatchDogService");
		// �����������ķ����ǿ�����
		siv_watchdog.setChecked(iswatchdogServiceRunning);
	}
}
