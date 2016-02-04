package com.hj.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.hj.mobilesafe.AppManagerActivity.ViewHolder;
import com.hj.mobilesafe.domain.AppInfo;
import com.hj.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrafficManagerActivity extends Activity {


	private ListView lv_traffic_manager;
	private LinearLayout ll_traffic_manager_loading;

	private TrafficManagerAdapter adapter;

	/**
	 * 所有的应用程序的信息
	 */
	private List<AppInfo> appInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);


		lv_traffic_manager = (ListView) findViewById(R.id.lv_traffic_manager);
		ll_traffic_manager_loading = (LinearLayout) findViewById(R.id.ll_traffic_manager_loading);

		// 总共上传
		long totaltx = TrafficStats.getTotalTxBytes();
		// 总共下载
		long totalrx = TrafficStats.getTotalRxBytes();

		// tv_sndall_traffic.setText("总共上传流量:"+(totaltx/1024)/1024+"M");
		// tv_rcvall_traffic.setText("总共下载流量:"+(totalrx/1024)/1024+"M");

		fillData();

		// PackageManager pm = getPackageManager();
		// List<ApplicationInfo> applicationInfos =
		// pm.getInstalledApplications(0);
		// for (ApplicationInfo applicationInfo : applicationInfos) {
		// int uid = applicationInfo.uid;
		// // 发送 上传的流量 byte
		// long tx = TrafficStats.getUidTxBytes(uid);
		// // 下载的流量 byte
		// long rx = TrafficStats.getUidRxBytes(uid);
		// // return -1 no or not accept traffic
		// }
	}

	private void fillData() {
		ll_traffic_manager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider
						.getAppInfosAboutTraffic(TrafficManagerActivity.this);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new TrafficManagerAdapter();
							lv_traffic_manager.setAdapter(adapter);

						} else {
							adapter.notifyDataSetChanged();
						}
						ll_traffic_manager_loading
								.setVisibility(View.INVISIBLE);

					}
				});
			};
		}.start();
	}

	private class TrafficManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			AppInfo appInfo = appInfos.get(position);

			View view;
			ViewHolder holder;

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_trafficinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_traffic_icon);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_traffic_name);
				holder.tv_rcv = (TextView) view
						.findViewById(R.id.tv_traffic_rcv);
				holder.tv_snd = (TextView) view
						.findViewById(R.id.tv_traffic_snd);
				holder.tv_all = (TextView) view
						.findViewById(R.id.tv_traffic_all);
				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());

			// 发送 上传的流量 byte
			long tx = TrafficStats.getUidTxBytes(appInfo.getUid());
			// 下载的流量 byte
			long rx = TrafficStats.getUidRxBytes(appInfo.getUid());
			// return -1 no or not accept traffic
			//
			// if(tx == -1 && rx ==-1){
			//
			// }else{
			// if (tx != -1) {
			holder.tv_snd.setText("上传:" + (tx / 1024) / 1024 + "M");
			// } else {
			// holder.tv_snd.setText("上传:0M");
			// }

			// if (rx != -1) {
			holder.tv_rcv.setText("下载:" + (rx / 1024) / 1024 + "M");
			// } else {
			// holder.tv_rcv.setText("下载:0M");
			// }
			// }

			holder.tv_all.setText(((rx + tx) / 1024) / 1024 + "M");
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_snd;
		TextView tv_rcv;
		TextView tv_all;
		ImageView iv_icon;

	}

}
