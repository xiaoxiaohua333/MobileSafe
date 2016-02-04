package com.hj.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberAddressQueryUtils {

	private static String path = "data/data/com.hj.mobilesafe/files/address.db";

	/**
	 * ͨ���绰�����ѯ������
	 * 
	 * @param number
	 * @return
	 */
	public static String queryNumber(String number) {

		String address = number;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// �ֻ���������ʽ
		if (number.matches("^1[34568]\\d{9}$")) {
			// �ֻ�����
			Cursor cursor = database
					.rawQuery(
							"select location from data2 where id = (select outkey from data1 where id = ?)",
							new String[] { number.substring(0, 7) });
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
			}
			cursor.close();

		} else {
			// ��������
			switch (number.length()) {
			case 3:
				address = "�������";
				break;
			case 4:
				address = "ģ����";
				break;
			case 5:
				address = "�ͷ��绰";
				break;
			case 8:
				address = "���غ���";
				break;
			case 7:
				address = "���غ���";
				break;

			default:
				// ��;�绰
				//010-
				if (number.length() > 10 && number.startsWith("0")) {
					Cursor cursor = database.rawQuery(
							"select location from data2 where area = ?",
							new String[] { number.substring(1, 3) });

					while (cursor.moveToNext()) {
						String location = cursor.getString(0);
						address = location.substring(0, location.length() - 2);

					}
					cursor.close();
				}
				//0855-
				Cursor cursor = database.rawQuery(
						"select location from data2 where area = ?",
						new String[] { number.substring(1, 4) });

				while (cursor.moveToNext()) {
					String location = cursor.getString(0);
					address = location.substring(0, location.length() - 2);

				}
				cursor.close();
				break;
			}
		}

		return address;

	}
}
