package com.hj.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 数据库创建的构造方法
	 * 
	 * @param context
	 * @param name
	 *            applock.db
	 * @param factory
	 * @param version
	 */
	public ApplockDBOpenHelper(Context context) {
		super(context, "applock.db", null, 1);

	}

	// 初始化
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}




























