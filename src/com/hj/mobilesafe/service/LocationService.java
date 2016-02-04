package com.hj.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.hj.mobilesafe.R;
import com.hj.mobilesafe.utils.LocationApplication;

public class LocationService extends Service{

	
	//百度定位
		private LocationClient mLocationClient;
		private LocationMode tempMode = LocationMode.Hight_Accuracy;
		private String tempcoor="gcj02";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
		TextView textView = new TextView(getApplicationContext());
		((LocationApplication)getApplication()).mLocationResult = textView;
		
		InitLocation();
		mLocationClient.start();
		
		
		String gps = textView.getText().toString().trim();
		Log.i("GPS", "-----------------" + gps);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("lastlocation", gps);
		editor.commit();
		mLocationClient.stop();
		SendSms();
	}
	
	/**
	 * 百度定位方法
	 */
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=1000;
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();
	}
	
	private void SendSms(){
		
		SharedPreferences sp = getSharedPreferences(
				"config", Context.MODE_PRIVATE);
		String sender = sp.getString("safenumber", "15086882355");
		String lastlocation = sp.getString("lastlocation", null);
		if (TextUtils.isEmpty(lastlocation)) {
			Log.i("hehe", "location = 0");
			// λ��û�еõ�
			SmsManager.getDefault().sendTextMessage(sender, null,
					"Huahua is acquiring the location...", null, null);
		} else {
			Log.i("hehe", "get location");
			SmsManager.getDefault().sendTextMessage(sender, null,
					"手机当前位置:"+lastlocation, null, null);
		}
	}

}
