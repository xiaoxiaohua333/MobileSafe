package com.hj.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hj.mobilesafe.db.dao.AntivirusDao;

public class AntiVirusActivity extends Activity {

	protected static final int SCANING = 0;
	protected static final int FINISH = 1;
	private ImageView iv_scan;
	private ProgressBar progressBar;
	private PackageManager pm;
	private TextView tv_scan_status;
	private LinearLayout ll_container;
	private Button btn_virus_clean;
	List<String> deleteInfos;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("����ɨ��:" + scanInfo.name);

				TextView tv = new TextView(getApplicationContext());

				if (scanInfo.isvirus) {
					tv.setTextColor(Color.RED);
					tv.setText("���ֲ���:" + scanInfo.name);
					btn_virus_clean.setVisibility(View.VISIBLE);
				} else {
					tv.setTextColor(R.color.green);
					tv.setText("ɨ�谲ȫ:" + scanInfo.name);
				}
				ll_container.addView(tv, 0);
				break;

			case FINISH:
				tv_scan_status.setText("ɨ�����");
				iv_scan.clearAnimation();
				break;

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);

		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		btn_virus_clean = (Button) findViewById(R.id.btn_virus_clean);
		btn_virus_clean.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				cleanVirus();
				
			}
		});

		deleteInfos = new ArrayList<String>();

		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 1s ��תһ��
		ra.setDuration(1000);
		// һֱ��ת
		ra.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(ra);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		scanVirus();

	}

	/**
	 * ɨ�財��
	 */
	private void scanVirus() {
		pm = getPackageManager();
		tv_scan_status.setText("���ڳ�ʼ��ɱ������...");
		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				progressBar.setMax(infos.size());

				int progress = 0;
				for (PackageInfo info : infos) {
					// apk �ļ�����·��
					String sourcedir = info.applicationInfo.sourceDir;
					String md5 = getFileMd5(sourcedir);

					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = info.applicationInfo.loadLabel(pm)
							.toString();
					scanInfo.packname = info.packageName;
					Log.i(scanInfo.name, md5);
					// ����MD5��Ϣ ɨ�����ݿ�
					if (AntivirusDao.isVirus(md5)) {
						// ���ֲ���
						scanInfo.isvirus = true;
						deleteInfos.add(scanInfo.packname);
					} else {
						// ��ȫ
						scanInfo.isvirus = false;
					}
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANING;
					handler.sendMessage(msg);

					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					progress++;
					progressBar.setProgress(progress);
				}
				Message msg = Message.obtain();
				msg.what = FINISH;
				handler.sendMessage(msg);
			};

		}.start();

	}

	/**
	 * ɨ����Ϣ���ڲ���
	 */
	class ScanInfo {
		String packname;
		String name;
		boolean isvirus;
	}

	/**
	 * ��ȡ�ļ���Md5ֵ
	 * 
	 * @param path
	 *            �ļ�ȫ·��
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws FileNotFoundException
	 */
	private String getFileMd5(String path) {
		try {
			File file = new File(path);

			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				// ������
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * ������
	 */

	private void cleanVirus() {
		for (int i = 0; i < deleteInfos.size(); i++) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			intent.setAction("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + deleteInfos.get(i)));
			startActivityForResult(intent, 0);
		}
		btn_virus_clean.setVisibility(View.INVISIBLE);
	}
}
