package com.hj.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hj.mobilesafe.utils.StreamTools;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int SHOW_UPDATE_DIALOG = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String description;
	private TextView tv_update_info;
	/**
	 * �°汾�����ص�ַ
	 */
	private String apkurl;
	private SharedPreferences sp;

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_splash);
	 * 
	 * tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
	 * tv_splash_version.setText("�汾��" + getVersionName());
	 * 
	 * // ������� //checkUpdate(); //����Ч������ AlphaAnimation aa = new
	 * AlphaAnimation(0.2f, 1.0f); aa.setDuration(500);
	 * findViewById(R.id.rl_root_splash).startAnimation(aa); //enterHome(); }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		
		boolean update = sp.getBoolean("update", false);

		// �������ݿ�
		copyDB("address.db");
		copyDB("antivirus.db");
		if (update) {
			// �������
			checkUpdate();
		} else {
			// �Զ������Ѿ��ر�
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// ������ҳ��
					enterHome();
				}
			}, 2000);
		}

		// ����Ч������
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);

	}

	/**
	 * �������ݿ�
	 */
	private void copyDB(String filename) {

		try {
			File file = new File(getFilesDir(), filename);
			if (file.exists()&&file.length()>0) {
				//�Ѿ����� �������¿���
			}else{
				InputStream is = getAssets().open(filename);

				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG:// ��ʾ�����ĶԻ���
				Log.i(TAG, "��ʾ�����ĶԻ���");
				showUpdateDialog();
				break;
			case ENTER_HOME:// ��ʾ��ҳ��
				enterHome();
				break;
			case URL_ERROR:// URL����
				enterHome();
				Toast.makeText(getApplicationContext(), "URL����", 0).show();
				break;
			case NETWORK_ERROR:// �����쳣
				enterHome();
				Toast.makeText(getApplicationContext(), "�����쳣", 0).show();
				break;
			case JSON_ERROR:// JSON��������
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON��������", 0).show();
				break;

			default:
				break;
			}
		}

	};

	/**
	 * ����Ƿ����°汾,����о�����
	 */
	private void checkUpdate() {

		new Thread() {
			public void run() {

				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {

					// URL http://
					URL url = new URL(getString(R.string.serverurl));
					// ����
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					// ����������ʽ
					con.setRequestMethod("GET");
					// ���ó�ʱʱ��
					con.setConnectTimeout(4000);
					// ������Ӧ��
					int code = con.getResponseCode();
					if (code == 200) {
						// �����ɹ�
						InputStream is = con.getInputStream();
						// ����ת��String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "�����ɹ���" + result);
						// json����
						JSONObject obj = new JSONObject(result);
						// �õ��������İ汾��Ϣ
						String version = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// У���Ƿ����°汾
						if (getVersionName().equals(version)) {
							// �汾һ��,û���°汾,������ҳ��
							mes.what = ENTER_HOME;
						} else {
							// ���°汾,����һ�����Ի���
							mes.what = SHOW_UPDATE_DIALOG;

						}

					}

				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					mes.what = URL_ERROR;
					e1.printStackTrace();
				} catch (IOException e) {
					mes.what = NETWORK_ERROR;
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					mes.what = JSON_ERROR;
					e.printStackTrace();
				} finally {

					long endTime = System.currentTimeMillis();
					// ����ʱ��
					long dTime = endTime - startTime;
					// 2000
					if (dTime < 2000) {
						try {
							Thread.sleep(2000 - dTime);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					handler.sendMessage(mes);
				}

			};

		}.start();
	}

	/**
	 * ���������Ի���
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��ʾ����");
		// builder.setCancelable(false); ǿ������
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// ������ҳ��
				enterHome();
				dialog.dismiss();

			}

		});
		builder.setMessage(description);
		builder.setPositiveButton("��������", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ����APK,�����滻��װ
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sdcard����
					// afinal
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilesafe2.0.ap;", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "����ʧ��", 1)
									.show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							// TODO Auto-generated method stub
							super.onLoading(count, current);
							tv_update_info.setVisibility(View.VISIBLE);
							// ��ǰ���ذٷֱ�
							int progress = (int) (current * 100 / count);

							tv_update_info.setText("���ؽ���: " + progress + " %");
						}

						@Override
						public void onSuccess(File t) {
							// TODO Auto-generated method stub
							super.onSuccess(t);
							installAPK(t);
						}

						/**
						 * ��װAPK
						 * 
						 * @param t
						 */

						private void installAPK(File t) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEN");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t),
									"application/vnd.android.package.archive");

							startActivity(intent);
						}

					});
				} else {
					Toast.makeText(getApplicationContext(), "û��sdcard,�밲װ������!",
							0).show();
					return;
				}
			}

		});
		builder.setNegativeButton("�´���˵", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();// ������ҳ��
			}

		});
		builder.show();

	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// �رյ�ǰҳ��
		finish();
	}

	/**
	 * �õ�Ӧ�ó���İ汾����
	 */

	private String getVersionName() {
		// ���������ֻ���APK
		PackageManager pm = getPackageManager();

		try {
			// �õ�֪��apk�Ĺ����嵥�ļ�
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}

	}
}
