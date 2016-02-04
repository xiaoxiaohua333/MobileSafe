package com.hj.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hj.mobilesafe.db.dao.ApplockDao;
import com.hj.mobilesafe.domain.AppInfo;
import com.hj.mobilesafe.engine.AppInfoProvider;
import com.hj.mobilesafe.utils.DensityUtil;

public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";
	private TextView tv_avail_rom;
	private TextView tv_avail_sd;

	private ListView lv_app_manager;
	private LinearLayout ll_app_manager_loading;

	/**
	 * 所有的应用程序的信息
	 */
	private List<AppInfo> appInfos;

	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;

	private TextView tv_status;

	private PopupWindow popupWindow;

	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_shared;

	/**
	 * 被点击的条目
	 */
	private AppInfo appInfo;

	private AppManagerAdapter adapter;

	private ApplockDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		dao = new ApplockDao(this);

		tv_status = (TextView) findViewById(R.id.tv_status);

		tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
		tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);

		ll_app_manager_loading = (LinearLayout) findViewById(R.id.ll_app_manager_loading);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);

		long sdsize = getAvailSpace(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		long romsize = getAvailSpace(Environment.getDataDirectory()
				.getAbsolutePath());

		tv_avail_sd
				.setText("SD卡可用空间:" + Formatter.formatFileSize(this, sdsize));
		tv_avail_rom.setText("内存可用空间:"
				+ Formatter.formatFileSize(this, romsize));

		fillData();

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		final String pwd = sp.getString("password", null);

		// 添加滚动监听
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			// 滚动时调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				dismissPopwindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("系统程序:" + systemAppInfos.size() + "个");
					} else {
						tv_status.setText("用户程序:" + userAppInfos.size() + "个");
					}
				}
			}
		});

		/**
		 * 设置listview的点击事件
		 */
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (position == 0) {
					return;
				} else if (position == (userAppInfos.size() + 1)) {
					return;
				} else if (position <= userAppInfos.size()) {
					// 用户程序
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {
					// 系统程序
					int newposition = position - 1 - userAppInfos.size() - 1;
					appInfo = systemAppInfos.get(newposition);
				}

				dismissPopwindow();

				View contentView = View.inflate(getApplicationContext(),
						R.layout.pop_app_item, null);

				ll_uninstall = (LinearLayout) contentView
						.findViewById(R.id.ll_uninstall);
				ll_start = (LinearLayout) contentView
						.findViewById(R.id.ll_start);
				ll_shared = (LinearLayout) contentView
						.findViewById(R.id.ll_shared);

				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_shared.setOnClickListener(AppManagerActivity.this);
				ll_start.setOnClickListener(AppManagerActivity.this);

				int[] location = new int[2];
				view.getLocationInWindow(location);

				popupWindow = new PopupWindow(contentView, -2, -2);

				// 动画效果的播放添加背景颜色
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						Color.TRANSPARENT));

				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);

				popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP,
						px, location[1]);

				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0.5f);
				sa.setDuration(300);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(sa);
				set.addAnimation(aa);
				contentView.startAnimation(set);

			}

		});

		/**
		 * 程序锁长点击事件
		 */
		lv_app_manager
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {

						if (position == 0) {
							return true;
						} else if (position == (userAppInfos.size() + 1)) {
							return true;
						} else if (position <= userAppInfos.size()) {
							// 用户程序
							int newposition = position - 1;
							appInfo = userAppInfos.get(newposition);
						} else {
							// 系统程序
							int newposition = position - 1
									- userAppInfos.size() - 1;
							appInfo = systemAppInfos.get(newposition);
						}

						ViewHolder holder = (ViewHolder) view.getTag();

						if (pwd.equals("")) {
							Toast.makeText(getApplicationContext(), "未开启手机防盗,请在手机防盗设置密码", 0).show();
						} else {
							// 判断是否存在于 APPlock数据库
							if (dao.find(appInfo.getPackname())) {
								// 解除锁定
								dao.delete(appInfo.getPackname());
								holder.iv_lock_status
										.setImageResource(R.drawable.no_lock);

								Toast.makeText(getApplicationContext(), "程序解锁", 0).show();
								
							} else {
								// 锁定
								dao.add(appInfo.getPackname());
								holder.iv_lock_status
										.setImageResource(R.drawable.lock);
								Toast.makeText(getApplicationContext(), "程序上锁", 0).show();
							}
						}

						return true;
					}
				});
	}

	private void fillData() {
		ll_app_manager_loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo info : appInfos) {
					if (info.isUserApp()) {
						userAppInfos.add(info);
					} else {
						systemAppInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (adapter == null) {
							adapter = new AppManagerAdapter();
							lv_app_manager.setAdapter(adapter);

						} else {
							adapter.notifyDataSetChanged();
						}
						ll_app_manager_loading.setVisibility(View.INVISIBLE);

					}
				});
			};
		}.start();
	}

	private void dismissPopwindow() {
		// 关闭旧的pop
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
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

			AppInfo appInfo;

			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("用户程序:" + userAppInfos.size() + "个");
				return tv;
			} else if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("系统程序:" + systemAppInfos.size() + "个");
				return tv;
			} else if (position <= userAppInfos.size()) {
				int newposition = position - 1;
				appInfo = userAppInfos.get(newposition);
			} else {
				int newposition = position - 1 - userAppInfos.size() - 1;
				appInfo = systemAppInfos.get(newposition);
			}

			View view;
			ViewHolder holder;

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_appinfo, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.iv_lock_status = (ImageView) view
						.findViewById(R.id.iv_lock_status);
				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("手机内存");
			} else {
				holder.tv_location.setText("外部存储");
			}
			if (dao.find(appInfo.getPackname())) {
				// 上锁
				holder.iv_lock_status.setImageResource(R.drawable.lock);
			} else {
				// 解锁
				holder.iv_lock_status.setImageResource(R.drawable.no_lock);
			}

			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
		ImageView iv_lock_status;

	}

	/**
	 * 获取某个目录的可用空间
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);

		// 获取分区的个数
		statFs.getBlockCount();
		// 获取分区的大小
		long size = statFs.getBlockSize();
		// 获取可用的区块的个数
		long count = statFs.getAvailableBlocks();

		return size * count;

	}

	@Override
	protected void onDestroy() {

		dismissPopwindow();
		super.onDestroy();
	}

	/**
	 * 布局对应的点击事件
	 */

	@Override
	public void onClick(View v) {
		dismissPopwindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				uninstallApplication();
			} else {
				Toast.makeText(getApplicationContext(), "系统应用只有获取root权限才可以卸载!",
						0).show();
			}

			break;
		case R.id.ll_start:
			startApplication();
			break;
		case R.id.ll_shared:

			shareApplication();
			break;
		}

	}

	/**
	 * 分享应用程序
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"推荐您使用一个超好用的软件哦,名字叫" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 卸载应用程序
	 */
	private void uninstallApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 刷新界面
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 开启应用程序
	 */
	private void startApplication() {
		// 查询应用程序的入口activity 并开启
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //查询出所有手机上具有启动功能的activity
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		Log.i(TAG, appInfo.getName());
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "对不起,不能启动该应用!", 0).show();
		}

	}

}
