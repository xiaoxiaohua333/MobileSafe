package com.hj.mobilesafe.receiver;

import com.hj.mobilesafe.LocationActivity;
import com.hj.mobilesafe.R;
import com.hj.mobilesafe.service.GPSService;
import com.hj.mobilesafe.service.LocationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.sax.StartElementListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		for (Object b : objs) {

			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);

			String sender = sms.getOriginatingAddress();
			String safenumber = sp.getString("safenumber", "");
			String body = sms.getMessageBody();
			Log.i("hehe", body);
			if ("#*location*#".equals(body)) {
				Intent i = new Intent(context, LocationActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
				player.setLooping(false);
				player.start();
				player.setVolume(1.0f, 1.0f);
				abortBroadcast();
			}
		}

	}

}
