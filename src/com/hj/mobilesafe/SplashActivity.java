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
	 * 新版本的下载地址
	 */
	private String apkurl;
	private SharedPreferences sp;

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * setContentView(R.layout.activity_splash);
	 * 
	 * tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
	 * tv_splash_version.setText("版本号" + getVersionName());
	 * 
	 * // 检查升级 //checkUpdate(); //渐变效果动画 AlphaAnimation aa = new
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

		// 拷贝数据库
		copyDB("address.db");
		copyDB("antivirus.db");
		if (update) {
			// 检查升级
			checkUpdate();
		} else {
			// 自动升级已经关闭
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// 进入主页面
					enterHome();
				}
			}, 2000);
		}

		// 渐变效果动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		findViewById(R.id.rl_root_splash).startAnimation(aa);

	}

	/**
	 * 拷贝数据库
	 */
	private void copyDB(String filename) {

		try {
			File file = new File(getFilesDir(), filename);
			if (file.exists()&&file.length()>0) {
				//已经存在 不需重新拷贝
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
			case SHOW_UPDATE_DIALOG:// 显示升级的对话框
				Log.i(TAG, "显示升级的对话框");
				showUpdateDialog();
				break;
			case ENTER_HOME:// 显示主页面
				enterHome();
				break;
			case URL_ERROR:// URL错误
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", 0).show();
				break;
			case NETWORK_ERROR:// 网络异常
				enterHome();
				Toast.makeText(getApplicationContext(), "网络异常", 0).show();
				break;
			case JSON_ERROR:// JSON解析出错
				enterHome();
				Toast.makeText(SplashActivity.this, "JSON解析出错", 0).show();
				break;

			default:
				break;
			}
		}

	};

	/**
	 * 检查是否有新版本,如果有就升级
	 */
	private void checkUpdate() {

		new Thread() {
			public void run() {

				Message mes = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {

					// URL http://
					URL url = new URL(getString(R.string.serverurl));
					// 联网
					HttpURLConnection con = (HttpURLConnection) url
							.openConnection();
					// 设置联网方式
					con.setRequestMethod("GET");
					// 设置超时时间
					con.setConnectTimeout(4000);
					// 设置响应码
					int code = con.getResponseCode();
					if (code == 200) {
						// 联网成功
						InputStream is = con.getInputStream();
						// 把流转成String
						String result = StreamTools.readFromStream(is);
						Log.i(TAG, "联网成功了" + result);
						// json解析
						JSONObject obj = new JSONObject(result);
						// 得到服务器的版本信息
						String version = (String) obj.get("version");

						description = (String) obj.get("description");
						apkurl = (String) obj.get("apkurl");

						// 校验是否有新版本
						if (getVersionName().equals(version)) {
							// 版本一致,没有新版本,进入主页面
							mes.what = ENTER_HOME;
						} else {
							// 有新版本,弹出一升级对话框
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
					// 所花时间
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
	 * 弹出升级对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示升级");
		// builder.setCancelable(false); 强制升级
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// 进入主页面
				enterHome();
				dialog.dismiss();

			}

		});
		builder.setMessage(description);
		builder.setPositiveButton("立刻升级", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载APK,并且替换安装
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					// sdcard存在
					// afinal
					FinalHttp finalhttp = new FinalHttp();
					finalhttp.download(apkurl, Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/mobilesafe2.0.ap;", new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							t.printStackTrace();
							Toast.makeText(getApplicationContext(), "下载失败", 1)
									.show();
							super.onFailure(t, errorNo, strMsg);
						}

						@Override
						public void onLoading(long count, long current) {
							// TODO Auto-generated method stub
							super.onLoading(count, current);
							tv_update_info.setVisibility(View.VISIBLE);
							// 当前下载百分比
							int progress = (int) (current * 100 / count);

							tv_update_info.setText("下载进度: " + progress + " %");
						}

						@Override
						public void onSuccess(File t) {
							// TODO Auto-generated method stub
							super.onSuccess(t);
							installAPK(t);
						}

						/**
						 * 安装APK
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
					Toast.makeText(getApplicationContext(), "没有sdcard,请安装上再试!",
							0).show();
					return;
				}
			}

		});
		builder.setNegativeButton("下次再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				enterHome();// 进入主页面
			}

		});
		builder.show();

	}

	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 关闭当前页面
		finish();
	}

	/**
	 * 得到应用程序的版本名称
	 */

	private String getVersionName() {
		// 用来管理手机的APK
		PackageManager pm = getPackageManager();

		try {
			// 得到知道apk的功能清单文件
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}

	}
}
