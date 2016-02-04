package com.hj.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

public abstract class BaseSetupActivity extends Activity {

	// 定义手势识别器
	private GestureDetector detector;

	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			/**
			 * 当手指滑动时回调
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//屏蔽滑动过慢
				if(Math.abs(velocityX)<200){
					return true;
				}
				// 屏蔽斜滑
				if (Math.abs((e2.getRawY() - e1.getRawY())) > 100) {
					return true;
				}

				// 显示上一页面
				if ((e2.getRawX() - e1.getRawX()) > 200) {
					showPre();
					return true;
				}

				// 显示下一页面
				if ((e1.getRawX() - e2.getRawX()) > 200) {
					showNext();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}

	// 抽象方法 在子类重写
	public abstract void showNext();

	public abstract void showPre();

	// 使用手势识别器
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * 下一步的点击事件
	 * 
	 * @param view
	 */
	public void next(View view) {
		showNext();
	}

	/**
	 * 上一步
	 * 
	 * @param view
	 */
	public void pre(View view) {
		showPre();
	}
}
