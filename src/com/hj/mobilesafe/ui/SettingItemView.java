package com.hj.mobilesafe.ui;

import com.hj.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的组合控件,里面有两个TextView,还有一个CheckBox , 还有一个View
 * 
 * @author Administrator
 * 
 */
public class SettingItemView extends RelativeLayout {

	private CheckBox cb_status;
	private TextView tv_title;
	private TextView tv_desc;

	private String desc_off;
	private String desc_on;

	/**
	 * 初始化布局文件
	 * 
	 * @param context
	 */
	private void iniView(Context context) {

		// 把一个布局文件 ---> View 并加载在 SettingItemView
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) this.findViewById(R.id.cb_status);
		tv_desc = (TextView) this.findViewById(R.id.tv_desc);
		tv_title = (TextView) this.findViewById(R.id.tv_title);

	}

	/**
	 * 带有两个参数的构造方法,布局文件使用的时候调用
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
		String title = attrs
				.getAttributeValue(
						"http://schemas.android.com/apk/res/com.hj.mobilesafe",
						"settitle");
		desc_on = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.hj.mobilesafe",
				"desc_on");
		desc_off = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.hj.mobilesafe",
				"desc_off");
		tv_title.setText(title);
		setDesc(desc_off);
	}

	public SettingItemView(Context context) {
		super(context);
		iniView(context);
	}

	/**
	 * 校验组合控件是否选中
	 */

	public boolean isChecked() {

		return cb_status.isChecked();
	}

	/**
	 * 设置组合控件的状态
	 */

	public void setChecked(boolean checked) {
		if (checked) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}
		cb_status.setChecked(checked);
	}

	/**
	 * 设置组合控件的描述信息
	 */
	public void setDesc(String text) {
		tv_desc.setText(text);
	}

}
