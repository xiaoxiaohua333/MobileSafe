package com.hj.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hj.mobilesafe.utils.MD5Utils;

public class HomeActivity extends Activity {

	protected static String TAG = "HomeActivity";
	private GridView list_home;
	private MyAdapter adapter;
	private ImageView iv_home_setting;

	private SharedPreferences sp;
	private static String[] names = { "�ֻ�����", "ͨѶ��ʿ", "�������", "���̹���", "����ͳ��",
			"�ֻ�ɱ��", "��������", "�߼�����" };

	private static int[] ids = { R.drawable.home_1, R.drawable.home_2,
			R.drawable.home_3, R.drawable.home_4, R.drawable.home_5,
			R.drawable.home_6, R.drawable.home_7, R.drawable.home_8

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		list_home = (GridView) findViewById(R.id.list_home);

		iv_home_setting = (ImageView) findViewById(R.id.iv_home_setting);
		iv_home_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent = new Intent(HomeActivity.this, SettingActivity.class);
				startActivity(intent);

			}
		});

		adapter = new MyAdapter();
		list_home.setAdapter(adapter);
	}

	protected void showLostFindDialog() {
		// �ж��Ƿ����ù�����
		if (isSetupPwd()) {
			// �Ѿ���������,��������Ի���
			showEnterDialog();
		} else {
			// û����������,�����Ƿ���������
			showSetupPwdDialog();
		}

	}

	private EditText et_setup_pwd;
	private EditText et_setup_confirm;
	private Button btn_ok;
	private Button btn_cancel;
	private AlertDialog dialog;

	/**
	 * ��������Ի���
	 */
	private void showSetupPwdDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// �Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_setup_password, null);

		et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
		et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
		btn_ok = (Button) view.findViewById(R.id.btn_ok);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ��
				dialog.dismiss();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ȡ������
				String password = et_setup_pwd.getText().toString().trim();
				String password_confirm = et_setup_confirm.getText().toString()
						.trim();
				if (TextUtils.isEmpty(password)
						|| TextUtils.isEmpty(password_confirm)) {

					Toast.makeText(HomeActivity.this, "����Ϊ��", 0).show();
					return;
				}
				// �ж��Ƿ�һ�� ��������
				if (password.equals(password_confirm)) {
					// ����һ��,�������벢�����ֻ�����ҳ��
					Editor editor = sp.edit();
					// ������ܺ������
					editor.putString("password", MD5Utils.md5Password(password));
					editor.commit();
					dialog.dismiss();
					Log.i(TAG, "����һ��,�������벢�����ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {

					Toast.makeText(HomeActivity.this, "���벻һ��", 0).show();
					return;
				}

			}
		});
		dialog = builder.create();
		// ���ü��
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	private EditText et_enter_password;

	/**
	 * ��������Ի���
	 */
	private void showEnterDialog() {

		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		// �Զ���һ�������ļ�
		View view = View.inflate(HomeActivity.this,
				R.layout.dialog_enter_password, null);

		et_enter_password = (EditText) view
				.findViewById(R.id.et_enter_password);
		btn_ok = (Button) view.findViewById(R.id.btn_ok);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// �ѶԻ���ȡ��
				dialog.dismiss();
			}
		});

		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String password = et_enter_password.getText().toString().trim();
				// ȡ�����ܺ������
				String savePassword = sp.getString("password", "");
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "����Ϊ��", 0).show();
					return;
				}
				if (MD5Utils.md5Password(password).equals(savePassword)) {
					// ��������������������ͬ
					// �ѶԻ���������������ҳ��
					dialog.dismiss();
					Log.i(TAG, "�ѶԻ��������������ֻ�����ҳ��");
					Intent intent = new Intent(HomeActivity.this,
							LostFindActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(HomeActivity.this, "�������", 1).show();
					et_enter_password.setText("");
					return;
				}

			}
		});
		dialog = builder.create();
		// ���ü��
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

	}

	/**
	 * �ж��Ƿ����ù�����
	 * 
	 * @return
	 */
	private boolean isSetupPwd() {
		String password = sp.getString("password", null);
		// if(TextUtils.isEmpty(password)){
		// return false;
		// }else{
		// return true;
		// }
		return !TextUtils.isEmpty(password);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(HomeActivity.this,
					R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

			LinearLayout ll_item_home = (LinearLayout) view
					.findViewById(R.id.ll_item_home);

			final int location = position;
			ll_item_home.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent;
					switch (location) {
					case 0:// �����ֻ�����

						showLostFindDialog();
						break;

					case 1:// ����ͨѶ��ʿ
						intent = new Intent(HomeActivity.this,
								CallSmsSafeActivity.class);
						startActivity(intent);
						break;

					case 2:// �������
						intent = new Intent(HomeActivity.this,
								AppManagerActivity.class);
						startActivity(intent);
						break;
					case 3:// ���̹�����
						intent = new Intent(HomeActivity.this,
								TaskManagerActivity.class);
						startActivity(intent);
						break;

					case 4:// ����ͳ��
						intent = new Intent(HomeActivity.this,
								TrafficManagerActivity.class);
						startActivity(intent);
						break;

					case 5:// �ֻ�ɱ��
						intent = new Intent(HomeActivity.this,
								AntiVirusActivity.class);
						startActivity(intent);
						break;

					case 6:// ��������
						intent = new Intent(HomeActivity.this,
								CleanCacheActivity.class);
						startActivity(intent);
						break;

					case 7: // ����߼�����
						intent = new Intent(HomeActivity.this,
								AtoolsActivity.class);
						startActivity(intent);
						break;

					default:
						break;
					}

				}
			});

			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			return view;
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

	}

}
