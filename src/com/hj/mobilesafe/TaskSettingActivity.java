package com.hj.mobilesafe;

import com.hj.mobilesafe.service.AutoCleanService;
import com.hj.mobilesafe.utils.ServiceUtils;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));

		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		cb_auto_clean.setChecked(sp.getBoolean("autoclean", false));

		cb_show_system
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sp.edit();
						editor.putBoolean("showsystem", isChecked);
						editor.commit();

					}
				});

		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 锁屏广播事件是一特殊广播事件
				Intent intent = new Intent(TaskSettingActivity.this,
						AutoCleanService.class);
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
			}
		});
	}

	@Override
	protected void onStart() {
		boolean running = ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.hj.mobilesafe.service.AutoCleanService");
		cb_auto_clean.setChecked(running);
		super.onStart();
	}

}
