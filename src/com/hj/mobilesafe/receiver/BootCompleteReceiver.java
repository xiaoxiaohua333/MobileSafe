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
			//��������������ִ�м���Ƿ�sim���仯
			
			tm = (TelephonyManager) context
					.getSystemService(context.TELEPHONY_SERVICE);
			// ��ȡ֮ǰ�����sim����Ϣ;
			String saveSim = sp.getString("sim", "");

			// ��ȡ��ǰsim����Ϣ
			String simNow = tm.getSimSerialNumber();

			// �Ƚ��Ƿ�һ��
			if (simNow.equals(saveSim)) {
				// sim��û��û�б��
			} else {
				// sim����� ��һ�����ŵ���ȫ����
				
				SmsManager.getDefault().sendTextMessage(sp.getString("safenumber", ""),
						null, "sim���Ѿ����", null, null);
			}
		}

	}
}
