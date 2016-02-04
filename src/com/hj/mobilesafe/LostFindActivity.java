package com.hj.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {

	private SharedPreferences sp;

	private TextView tv_safenumber;
	private ImageView iv_protecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 判断是否做过设置向导
		boolean configed = sp.getBoolean("configed", false);
		if (configed) {
			// 已完成设置向导
			setContentView(R.layout.activity_lost_find);
			tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
			iv_protecting = (ImageView) findViewById(R.id.iv_protecting);

			// 得到config中的设置
			String safenumber = sp.getString("safenumber", "");
			tv_safenumber.setText(safenumber);

			boolean protecting = sp.getBoolean("protecting", false);
			if (protecting) {
				// 已经开启
				iv_protecting.setImageResource(R.drawable.lock);
			} else {
				// 没有开启
				iv_protecting.setImageResource(R.drawable.no_lock);
			}

		} else {
			// 没有设置向导
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}

	}

	/**
	 * 重新进入手机防盗页面
	 * 
	 * @param view
	 */
	public void reEnterSetup(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
}
