package com.hj.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.hj.mobilesafe.R;
import com.hj.mobilesafe.domain.TaskInfo;

import android.R.drawable;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;

/**
 * �ṩ�ֻ�����Ľ�����Ϣ
 * 
 * @author Administrator
 * 
 */
public class TaskInfoProvider {

	/**
	 * ��ȡ���н��̵���Ϣ
	 * 
	 * @param context
	 *            ������
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();

		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo processInfo : processInfos) {
			TaskInfo taskInfo = new TaskInfo();
			// Ӧ�ó������
			String packname = processInfo.processName;
			taskInfo.setPackname(packname);

			// Ӧ�ó���ռ���ڴ�
			MemoryInfo[] memoryInfo = am
					.getProcessMemoryInfo(new int[] { processInfo.pid });
			long memsize = memoryInfo[0].getTotalPrivateDirty() * 1024;
			taskInfo.setMemsize(memsize);

			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packname, 0);

				// Ӧ�ó���ͼ��
				Drawable icon = applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);

				// Ӧ�ó�������
				String name = applicationInfo.loadLabel(pm).toString();
				taskInfo.setName(name);

				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// �û�����
					taskInfo.setUserTask(true);
				} else {
					// ϵͳ����
					taskInfo.setUserTask(false);
				}

			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(
						R.drawable.android));
				taskInfo.setName(packname);
			}
			taskInfos.add(taskInfo);
		}

		return taskInfos;

	}

}
