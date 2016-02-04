package com.hj.mobilesafe.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.R.drawable;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

import com.hj.mobilesafe.domain.AppInfo;

/**
 * ҵ�񷽷�,�ṩ�ֻ����氲װ�����е�Ӧ����Ϣ
 * 
 * @author Administrator
 * 
 */
public class AppInfoProvider {

	/**
	 * ��ȡ���еİ�װ��Ӧ�ó�����Ϣ
	 * 
	 * @param context
	 *            ������
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		// �õ����а�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packaInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packaInfo : packaInfos) {
			AppInfo appInfo = new AppInfo();
			// packaInfo �õ���Ӧ�ó���apk���嵥�ļ�
			String packagename = packaInfo.packageName;
			Drawable icon = packaInfo.applicationInfo.loadIcon(pm);
			String name = packaInfo.applicationInfo.loadLabel(pm).toString();
			// Ӧ�ó�����Ϣ�ı��
			int flags = packaInfo.applicationInfo.flags;

			// ����ϵͳ�����Ӧ�ó����һ���̶��ı�� (Ψһ)
			int uid = packaInfo.applicationInfo.uid;
			appInfo.setUid(uid);
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�����
				appInfo.setUserApp(true);
			} else {
				// ϵͳ����
				appInfo.setUserApp(false);

			}

			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// �ֻ��ڴ�
				appInfo.setInRom(true);
			} else {
				// SD card
				appInfo.setInRom(false);
			}

			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfo.setPackname(packagename);
			appInfos.add(appInfo);
		}

		return appInfos;
	}

	/**
	 * ��ȡ���еİ�װ��Ӧ�ó�����Ϣ��������Ϣ
	 * 
	 * @param context
	 *            ������
	 * @return
	 */
	public static List<AppInfo> getAppInfosAboutTraffic(Context context) {
		PackageManager pm = context.getPackageManager();
		// �õ����а�װ��ϵͳ�ϵ�Ӧ�ó������Ϣ
		List<PackageInfo> packaInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packaInfo : packaInfos) {
			AppInfo appInfo = new AppInfo();
			// packaInfo �õ���Ӧ�ó���apk���嵥�ļ�
			String packagename = packaInfo.packageName;
			Drawable icon = packaInfo.applicationInfo.loadIcon(pm);
			String name = packaInfo.applicationInfo.loadLabel(pm).toString();
			// Ӧ�ó�����Ϣ�ı��
			int flags = packaInfo.applicationInfo.flags;

			// ����ϵͳ�����Ӧ�ó����һ���̶��ı�� (Ψһ)
			int uid = packaInfo.applicationInfo.uid;
			appInfo.setUid(uid);
			// ���� �ϴ������� byte
			long tx = TrafficStats.getUidTxBytes(appInfo.getUid());
			// ���ص����� byte
			long rx = TrafficStats.getUidRxBytes(appInfo.getUid());
			// return -1 no or not accept traffic
			if(((tx+rx)/1024)/1024 == 0){
				
			} else {
				appInfo.setIcon(icon);
				appInfo.setName(name);
				appInfo.setPackname(packagename);
				appInfos.add(appInfo);
			}

		}

		return appInfos;
	}

}
