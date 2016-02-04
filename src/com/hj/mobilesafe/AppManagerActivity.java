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
	 * ���е�Ӧ�ó������Ϣ
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
	 * ���������Ŀ
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
				.setText("SD�����ÿռ�:" + Formatter.formatFileSize(this, sdsize));
		tv_avail_rom.setText("�ڴ���ÿռ�:"
				+ Formatter.formatFileSize(this, romsize));

		fillData();

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		final String pwd = sp.getString("password", null);

		// ��ӹ�������
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			// ����ʱ���õķ���
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				dismissPopwindow();
				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem > userAppInfos.size()) {
						tv_status.setText("ϵͳ����:" + systemAppInfos.size() + "��");
					} else {
						tv_status.setText("�û�����:" + userAppInfos.size() + "��");
					}
				}
			}
		});

		/**
		 * ����listview�ĵ���¼�
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
					// �û�����
					int newposition = position - 1;
					appInfo = userAppInfos.get(newposition);
				} else {
					// ϵͳ����
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

				// ����Ч���Ĳ�����ӱ�����ɫ
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
		 * ������������¼�
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
							// �û�����
							int newposition = position - 1;
							appInfo = userAppInfos.get(newposition);
						} else {
							// ϵͳ����
							int newposition = position - 1
									- userAppInfos.size() - 1;
							appInfo = systemAppInfos.get(newposition);
						}

						ViewHolder holder = (ViewHolder) view.getTag();

						if (pwd.equals("")) {
							Toast.makeText(getApplicationContext(), "δ�����ֻ�����,�����ֻ�������������", 0).show();
						} else {
							// �ж��Ƿ������ APPlock���ݿ�
							if (dao.find(appInfo.getPackname())) {
								// �������
								dao.delete(appInfo.getPackname());
								holder.iv_lock_status
										.setImageResource(R.drawable.no_lock);

								Toast.makeText(getApplicationContext(), "�������", 0).show();
								
							} else {
								// ����
								dao.add(appInfo.getPackname());
								holder.iv_lock_status
										.setImageResource(R.drawable.lock);
								Toast.makeText(getApplicationContext(), "��������", 0).show();
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
		// �رվɵ�pop
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
				tv.setText("�û�����:" + userAppInfos.size() + "��");
				return tv;
			} else if (position == userAppInfos.size() + 1) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setText("ϵͳ����:" + systemAppInfos.size() + "��");
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
				holder.tv_location.setText("�ֻ��ڴ�");
			} else {
				holder.tv_location.setText("�ⲿ�洢");
			}
			if (dao.find(appInfo.getPackname())) {
				// ����
				holder.iv_lock_status.setImageResource(R.drawable.lock);
			} else {
				// ����
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
	 * ��ȡĳ��Ŀ¼�Ŀ��ÿռ�
	 * 
	 * @param path
	 * @return
	 */
	private long getAvailSpace(String path) {
		StatFs statFs = new StatFs(path);

		// ��ȡ�����ĸ���
		statFs.getBlockCount();
		// ��ȡ�����Ĵ�С
		long size = statFs.getBlockSize();
		// ��ȡ���õ�����ĸ���
		long count = statFs.getAvailableBlocks();

		return size * count;

	}

	@Override
	protected void onDestroy() {

		dismissPopwindow();
		super.onDestroy();
	}

	/**
	 * ���ֶ�Ӧ�ĵ���¼�
	 */

	@Override
	public void onClick(View v) {
		dismissPopwindow();
		switch (v.getId()) {
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				uninstallApplication();
			} else {
				Toast.makeText(getApplicationContext(), "ϵͳӦ��ֻ�л�ȡrootȨ�޲ſ���ж��!",
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
	 * ����Ӧ�ó���
	 */
	private void shareApplication() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"�Ƽ���ʹ��һ�������õ����Ŷ,���ֽ�" + appInfo.getName());
		startActivity(intent);
	}

	/**
	 * ж��Ӧ�ó���
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
		// ˢ�½���
		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ����Ӧ�ó���
	 */
	private void startApplication() {
		// ��ѯӦ�ó�������activity ������
		PackageManager pm = getPackageManager();
		// Intent intent = new Intent();
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// //��ѯ�������ֻ��Ͼ����������ܵ�activity
		// List<ResolveInfo> infos = pm.queryIntentActivities(intent,
		// PackageManager.GET_INTENT_FILTERS);
		Log.i(TAG, appInfo.getName());
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "�Բ���,����������Ӧ��!", 0).show();
		}

	}

}
