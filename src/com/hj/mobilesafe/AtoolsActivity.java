package com.hj.mobilesafe;

import com.hj.mobilesafe.utils.SmsUtils;
import com.hj.mobilesafe.utils.SmsUtils.BackUpCallUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	
	private ProgressDialog pd;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
	}

	/**
	 * 号码归属地查询
	 * 
	 * @param view
	 */
	public void numberQuery(View view) {

		Intent intent = new Intent(this, NumberAddressQueryActivity.class);
		startActivity(intent);

	}

	/**
	 * 短信备份
	 * 
	 * @param view
	 */
	public void smsBackup(View view) {
		
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在备份短信");
		pd.show();
		new Thread(){
			@Override
			public void run() {
				try {
					SmsUtils.backupSms(getApplicationContext(),new BackUpCallUp() {
						
						@Override
						public void onSmsBackup(int progress) {
							pd.setProgress(progress);
							
						}
						
						@Override
						public void beforeBackup(int max) {
							pd.setProgress(max);
							
						}
					});
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份成功", 0).show();
						}
					});
				} catch (Exception e) {

					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(AtoolsActivity.this, "备份失败", 0).show();
						}
					});
					
				}finally{
				pd.dismiss();
				}
			};
		}.start();
	}

	/**
	 * 短信还原
	 * 
	 * @param view
	 */
	public void smsRestore(View view) {

		boolean flag = false;
		SmsUtils.restoreSms(this,true);
		Toast.makeText(AtoolsActivity.this, "还原成功", 0).show();
		
	}
}
