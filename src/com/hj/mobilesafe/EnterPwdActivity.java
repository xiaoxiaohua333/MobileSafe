package com.hj.mobilesafe;

import com.hj.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPwdActivity extends Activity {

	private EditText et_password;
	private String packname;
	private TextView tv_name;
	private ImageView iv_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);

		et_password = (EditText) findViewById(R.id.et_password);
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");

		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);

		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {	
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

	public void click(View view) {
		SharedPreferences sp =getSharedPreferences("config", MODE_PRIVATE);
		String password = sp.getString("password", null);
		
		String pwd = et_password.getText().toString().trim();
		if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
			return;
		}

		if (password.equals(MD5Utils.md5Password(pwd))) {
			// 自定义广播 临时停止保护
			Intent intent = new Intent();
			intent.setAction("com.hj.mobilesafe.tempstop");
			intent.putExtra("packname", packname);
			sendBroadcast(intent);

			finish();

		} else {
			Toast.makeText(getApplicationContext(), "密码错误", 0).show();
		}
	}


	@Override
	public void onBackPressed() {
		// 返回桌面

		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);

	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

}
