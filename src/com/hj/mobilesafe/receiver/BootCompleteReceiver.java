package com.hj.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {

		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);

		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			//开启防盗保护后执行检查是否sim卡变化
			
			tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			// 读取之前保存的sim卡信息;
			String saveSim = sp.getString("sim", "");

			// 读取当前sim卡信息
			String simNow = tm.getSimSerialNumber();

			// 比较是否一样
			if (simNow.equals(saveSim)) {
				// sim卡没有没有变更
			} else {
				// sim卡变更 发一个短信到安全号码
				
				SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""),
						null, "sim卡已经变更", null, null);
			}
		}

	}
}
