package com.hj.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SmsDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法
	 * 
	 * @param context
	 * @param name
	 *            applock.db
	 * @param factory
	 * @param version
	 */
	public SmsDBOpenHelper(Context context) {
		super(context, "sms.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table sms (_id integer primary key autoincrement,body varchar(200),address varchar(20),type integer,date varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
