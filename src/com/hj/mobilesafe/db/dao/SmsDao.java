package com.hj.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hj.mobilesafe.db.SmsDBOpenHelper;
import com.hj.mobilesafe.domain.SmsInfo;

/**
 * ���ݶ������ݿ����ɾ�Ĳ�ҵ����
 * 
 * @author Administrator
 * 
 */

public class SmsDao {

	private SmsDBOpenHelper helper;

	/**
	 * ���췽��
	 * 
	 * @param context
	 */
	public SmsDao(Context context) {
		helper = new SmsDBOpenHelper(context);
	}

	/**
	 * ��Ӻ���������
	 * 
	 * @param number
	 *            ����
	 * @param mode
	 *            ����ģʽ
	 */
	public void add(String body, String address, int type, String date) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("body", body);
		values.put("address", address);
		values.put("type", type);
		values.put("date", date);
		db.insert("sms", null, values);
		db.close();
	}

	/**
	 * ��ձ��ݶ���
	 * 
	 */
	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("sms", null, new String[] {});
		db.close();
	}

	/**
	 * ��ѯȫ�����ݶ���
	 * 
	 * @return
	 */
	public List<SmsInfo> findAll() {
		List<SmsInfo> result = new ArrayList<SmsInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select body,address,type,date from sms",
						null);
		while (cursor.moveToNext()) {
			SmsInfo info = new SmsInfo();
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			int type = cursor.getShort(2);
			String date = cursor.getString(3);

			info.setAddress(address);
			info.setBody(body);
			info.setDate(date);
			info.setType(type);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯ����
	 */
	
	public int findNum(){
		SQLiteDatabase db = helper.getReadableDatabase();
		int result = 0;
		Cursor cursor = db
				.rawQuery(
						"select count(body) from sms ",
						null);
		if (cursor.moveToNext()) {
			result = Integer.valueOf(cursor.getString(0));
		}
		cursor.close();
		db.close();
		
		return result;
	}

}
