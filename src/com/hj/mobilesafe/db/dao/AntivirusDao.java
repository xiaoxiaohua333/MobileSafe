package com.hj.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hj.mobilesafe.db.BlackNumberDBOpenHelper;
import com.hj.mobilesafe.domain.BlackNumberInfo;

/**
 * �������ݿ����ɾ�Ĳ�ҵ����
 * 
 * @author Administrator
 * 
 */
public class AntivirusDao {

	/**
	 * ��ѯһ�� MD5 �Ƿ����
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		String path = "/data/data/com.hj.mobilesafe/files/antivirus.db";
		boolean result =false;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);


		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[] {md5});
		if (cursor.moveToNext()) {
			result = true;
		}
		
		cursor.close();
		db.close();
		return result;
	}
}
