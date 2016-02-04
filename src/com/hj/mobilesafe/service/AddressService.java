package com.hj.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hj.mobilesafe.R;
import com.hj.mobilesafe.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {

	/**
	 * 窗体管理者
	 */
	private WindowManager wm;
	private View view;

	/**
	 * 电话服务
	 */
	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;

	private OutCallReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	// 服务里面的内部类
	// 广播接收者的生命周期和服务一样
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 这就是我们拿到的播出去的电话号码
			String phone = getResultData();
			// 查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);

			Toast.makeText(context, address, 1).show();
			
		}

	}

	/**
	 * 监听铃声响起状态
	 * 
	 * @author Administrator
	 * 
	 */

	private class MyListenerPhone extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state：状态，incomingNumber：来电号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				// 查询数据库的操作
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);

				Toast.makeText(getApplicationContext(), address, 1).show();

				break;

			case TelephonyManager.CALL_STATE_IDLE:// 电话的空闲状态：挂电话、来电拒绝

				if (view != null) {
					wm.removeView(view);
				}

				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);

		// 代码注册广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);

		// 实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;

		// 代码取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;

	}

}
