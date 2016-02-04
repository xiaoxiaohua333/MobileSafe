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

	// ��������ʶ����
	private GestureDetector detector;

	protected SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			/**
			 * ����ָ����ʱ�ص�
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				//���λ�������
				if(Math.abs(velocityX)<200){
					return true;
				}
				// ����б��
				if (Math.abs((e2.getRawY() - e1.getRawY())) > 100) {
					return true;
				}

				// ��ʾ��һҳ��
				if ((e2.getRawX() - e1.getRawX()) > 200) {
					showPre();
					return true;
				}

				// ��ʾ��һҳ��
				if ((e1.getRawX() - e2.getRawX()) > 200) {
					showNext();
					return true;
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}

	// ���󷽷� ��������д
	public abstract void showNext();

	public abstract void showPre();

	// ʹ������ʶ����
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	/**
	 * ��һ���ĵ���¼�
	 * 
	 * @param view
	 */
	public void next(View view) {
		showNext();
	}

	/**
	 * ��һ��
	 * 
	 * @param view
	 */
	public void pre(View view) {
		showPre();
	}
}
