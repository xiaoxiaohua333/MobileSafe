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
 * 业务方法,提供手机里面安装的所有的应用信息
 * 
 * @author Administrator
 * 
 */
public class AppInfoProvider {

	/**
	 * 获取所有的安装的应用程序信息
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		// 得到所有安装在系统上的应用程序包信息
		List<PackageInfo> packaInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packaInfo : packaInfos) {
			AppInfo appInfo = new AppInfo();
			// packaInfo 得到的应用程序apk的清单文件
			String packagename = packaInfo.packageName;
			Drawable icon = packaInfo.applicationInfo.loadIcon(pm);
			String name = packaInfo.applicationInfo.loadLabel(pm).toString();
			// 应用程序信息的标记
			int flags = packaInfo.applicationInfo.flags;

			// 操作系统分配给应用程序的一个固定的编号 (唯一)
			int uid = packaInfo.applicationInfo.uid;
			appInfo.setUid(uid);
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户程序
				appInfo.setUserApp(true);
			} else {
				// 系统程序
				appInfo.setUserApp(false);

			}

			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 手机内存
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
	 * 获取所有的安装的应用程序信息及流量信息
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfosAboutTraffic(Context context) {
		PackageManager pm = context.getPackageManager();
		// 得到所有安装在系统上的应用程序包信息
		List<PackageInfo> packaInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packaInfo : packaInfos) {
			AppInfo appInfo = new AppInfo();
			// packaInfo 得到的应用程序apk的清单文件
			String packagename = packaInfo.packageName;
			Drawable icon = packaInfo.applicationInfo.loadIcon(pm);
			String name = packaInfo.applicationInfo.loadLabel(pm).toString();
			// 应用程序信息的标记
			int flags = packaInfo.applicationInfo.flags;

			// 操作系统分配给应用程序的一个固定的编号 (唯一)
			int uid = packaInfo.applicationInfo.uid;
			appInfo.setUid(uid);
			// 发送 上传的流量 byte
			long tx = TrafficStats.getUidTxBytes(appInfo.getUid());
			// 下载的流量 byte
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
