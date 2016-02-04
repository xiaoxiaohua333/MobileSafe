package com.hj.mobilesafe;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CleanCacheActivity extends Activity {

	private ProgressBar pb_clean;
	private TextView tv_scan_status;
	private LinearLayout ll_clean_container;

	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		pb_clean = (ProgressBar) findViewById(R.id.pb_clean);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_clean_container = (LinearLayout) findViewById(R.id.ll_clean_container);

		
	}
	
	@Override
	protected void onStart() {
		
		scanCache();
		super.onStart();
	}

	/**
	 * 扫描手机里所有应用程序的缓存信息
	 */
	private void scanCache() {
		pm = getPackageManager();
		new Thread() {
			public void run() {
				Method getPackageSizeInfo = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfo = method;
					}
				}
				List<PackageInfo> packInfos = pm.getInstalledPackages(0);
				pb_clean.setMax(packInfos.size());
				int progress = 0;
				for (PackageInfo packInfo : packInfos) {
					try {
						Method myUserId = UserHandle.class.getDeclaredMethod("myUserId");  
						int userID = (Integer) myUserId.invoke(pm,null);  
						getPackageSizeInfo.invoke(pm, packInfo.packageName,userID,
								new MyDataObserver());
						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb_clean.setProgress(progress);

				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_scan_status.setText("扫描完毕");
					}
				});
			}

		}.start();
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			long code = pStats.codeSize;
			long data = pStats.dataSize;
			final String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_scan_status.setText("正在扫描:" + appInfo.loadLabel(pm));
						if (cache > 0) {
							// 有缓存
							View view = View.inflate(getApplicationContext(),
									R.layout.list_item_cacheinfo, null);
							TextView tv_cache = (TextView) view
									.findViewById(R.id.tv_cache_size);
							tv_cache.setText("缓存大小:"
									+ Formatter.formatFileSize(
											getApplicationContext(), cache));
							TextView tv_name = (TextView) view
									.findViewById(R.id.tv_app_name);
							tv_name.setText(appInfo.loadLabel(pm));

							ImageView iv_delete = (ImageView) view
									.findViewById(R.id.iv_cache_delete);
							iv_delete.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									try {
									Intent intent = new Intent();  
									intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");  
									intent.addCategory("android.intent.category.DEFAULT");  
									intent.setData(Uri.parse("package:" + appInfo.packageName));  
									startActivity(intent);  
									finish();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
							ll_clean_container.addView(view, 0);
						}
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class MypackDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {

		}

	}

	/**
	 * 清理手机的全部缓存
	 * 
	 * @param view
	 */
	public void clearAll(View view) {
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE, new MyDataObserver());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

		}
	}
	
	
	//网上的方法
	
	public  void clearCache(View view) {  
		Toast.makeText(getApplicationContext(), "test", 0).show();

        try {  
            
            Method localMethod =  PackageManager.class.getClass().getMethod("freeStorageAndNotify", Long.TYPE,  
                    IPackageDataObserver.class);  
            Long localLong = Long.valueOf(getEnvironmentSize() - 1L);  
            Object[] arrayOfObject = new Object[2];  
            arrayOfObject[0] = localLong;  
            localMethod.invoke(pm, localLong, new IPackageDataObserver.Stub() {  

                @Override  
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {  
                }  
            });  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  

    private static long getEnvironmentSize() {  
        File localFile = Environment.getDataDirectory();  
        long l1;  
        if (localFile == null)  
            l1 = 0L;  
        while (true) {  
            String str = localFile.getPath();  
            StatFs localStatFs = new StatFs(str);  
            long l2 = localStatFs.getBlockSize();  
            l1 = localStatFs.getBlockCount() * l2;  
            return l1;  
        }  

    }  
}
