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
 * 备份短信数据库的增删改查业务类
 * 
 * @author Administrator
 * 
 */

public class SmsDao {

	private SmsDBOpenHelper helper;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public SmsDao(Context context) {
		helper = new SmsDBOpenHelper(context);
	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            号码
	 * @param mode
	 *            拦截模式
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
	 * 清空备份短信
	 * 
	 */
	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("sms", null, new String[] {});
		db.close();
	}

	/**
	 * 查询全部备份短信
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
	 * 查询条数
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
