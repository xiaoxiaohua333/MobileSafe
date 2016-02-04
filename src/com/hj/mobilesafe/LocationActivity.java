package com.hj.mobilesafe;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.hj.mobilesafe.utils.LocationApplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class LocationActivity extends Activity {

	private TextView tv_location_info;

	// 百度定位
	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		tv_location_info = (TextView) findViewById(R.id.tv_location_info);
		
		mLocationClient = ((LocationApplication)getApplication()).mLocationClient;
		((LocationApplication)getApplication()).mLocationResult = tv_location_info;
		
		InitLocation();
		mLocationClient.start();
		
		new Thread(){
			
			public void run() {
				try {
					Thread.sleep(5000);
					String gps = tv_location_info.getText().toString().trim();
					if(gps.equals("")||gps.equals("no")){
						
						SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putString("lastlocation", "");
						editor.commit();
					}else{
						SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putString("lastlocation", gps);
						editor.commit();
						
					}
					
					mLocationClient.stop();
					SendSms();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			};
			
		}.start();
		
		
		

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
			Log.e("hehe", "location = 0");
			// λ��û�еõ�
			SmsManager.getDefault().sendTextMessage(sender, null,
					"Ood Show is acquiring the location...", null, null);
		} else {
			Log.e("hehe", "get location");
			SmsManager.getDefault().sendTextMessage(sender, null,
					lastlocation+" get location", null, null);
		}
		finish();
	}
}
