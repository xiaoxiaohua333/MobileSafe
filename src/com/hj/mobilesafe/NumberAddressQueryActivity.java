package com.hj.mobilesafe;

import com.hj.mobilesafe.db.dao.NumberAddressQueryUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressQueryActivity extends Activity {

	private EditText et_phone;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_address_query);
		et_phone = (EditText) findViewById(R.id.et_phone);
		tv_result = (TextView) findViewById(R.id.tv_result);
		et_phone.addTextChangedListener(new TextWatcher() {

			/**
			 * ���ı������仯��ʱ��ص�
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && s.length() >= 3) {
					// ������������3��ʼ��ѯ
					String address = NumberAddressQueryUtils.queryNumber(s
							.toString());
					tv_result.setText(address);
				}

			}

			/**
			 * ���ı������仯֮ǰ�ص�
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			/**
			 * ���ı������仯֮��
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * ��ѯ������
	 * 
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			// ����Ϊ��
			Toast.makeText(this, "����Ϊ��", 0).show();
			return;
		} else {
			// ���ݿ��ѯ���������
			String address = NumberAddressQueryUtils.queryNumber(phone);
			tv_result.setText(address);
		}
	}

}
