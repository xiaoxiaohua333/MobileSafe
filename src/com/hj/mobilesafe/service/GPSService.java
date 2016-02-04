package com.hj.mobilesafe.service;

import java.io.IOException;
import java.io.InputStream;

import com.hj.mobilesafe.utils.LocationApplication;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class GPSService extends Service {

	// 用到位置服务
	// private LocationManager lm;
	// private MyLocationListener listener;

	private LocationClient mLocationClient;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor = "gcj02";

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
		mLocationClient.start();
		InitLocation();
		
		
		String gps = textView.getText().toString().trim();
		Log.i("GPS", "-----------------" + gps);
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("lastlocation", gps);
		editor.commit();
		mLocationClient.stop();

		// Log.i("hehe", "GPS sevice");
		//
		// lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		// listener = new MyLocationListener();
		// // 注册监听位置服务
		// // 给位置提供者设置条件
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// // String proveder =
		// lm.getBestProvider(criteria, true);
		// lm.requestLocationUpdates("gps", 0, 0, listener);
	}

	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();
		// 取消监听位置服务
		// lm.removeUpdates(listener);
		// listener = null;
	}

	private void onstop() {
		// TODO Auto-generated method stub

		mLocationClient.stop();

	}

	class MyLocationListener implements LocationListener {

		/**
		 * 当位置改变的时候回调
		 */

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			String accuracy = "a" + location.getAccuracy() + "\n";
			// 发送短信给安全号码

			// 把标准的GPS坐标转换为火星坐标
			// InputStream is;
			// try {
			// is = getAssets().open("axisoffset.dat");
			// ModifyOffset offset = ModifyOffset.getInstance(is);
			// PointDouble double1 = offset.s2c(new PointDouble(location
			// .getLongitude(), location.getLatitude()));
			// longitude = "j:"+offset.X+"\n";
			// latitude = "w:"+offset.Y+"\n";
			//
			//
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}

		/**
		 * 当状态发生改变的时候回调 开启--关闭 ；关闭--开启
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		/**
		 * 某一个位置提供者可以使用了
		 */
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		/**
		 * 某一个位置提供者不可以使用了
		 */
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}

}
