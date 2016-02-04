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
			 * 当文本发生变化的时候回调
			 */
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && s.length() >= 3) {
					// 当输入号码大于3后开始查询
					String address = NumberAddressQueryUtils.queryNumber(s
							.toString());
					tv_result.setText(address);
				}

			}

			/**
			 * 当文本发生变化之前回调
			 */
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			/**
			 * 当文本发生变化之后
			 */
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 查询归属地
	 * 
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		String phone = et_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			// 号码为空
			Toast.makeText(this, "号码为空", 0).show();
			return;
		} else {
			// 数据库查询号码归属地
			String address = NumberAddressQueryUtils.queryNumber(phone);
			tv_result.setText(address);
		}
	}

}
