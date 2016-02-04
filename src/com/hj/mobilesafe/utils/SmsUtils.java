package com.hj.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.hj.mobilesafe.R.integer;
import com.hj.mobilesafe.db.dao.SmsDao;
import com.hj.mobilesafe.domain.SmsInfo;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

/**
 * ���Ź�����
 * 
 * @author Administrator
 * 
 */
public class SmsUtils {
	
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public interface BackUpCallUp{
		/**
		 * ��ʼ����ʱ,���ý��ȵ����ֵ
		 * @param max
		 */
		public void beforeBackup(int max);
		
		/**
		 * ���ݹ�����,���ӽ���
		 * @param progress
		 */
		public void onSmsBackup(int progress);
		
	}

	/**
	 * �����û��Ķ���
	 * 
	 * @param context
	 * @param BackUpCallUp ���ݶ��ŵĽӿ�
	 * @throws Exception
	 */
	public static void backupSms(Context context, BackUpCallUp callBack) throws Exception {
		ContentResolver resolver = context.getContentResolver();
//		File file = new File(Environment.getExternalStorageDirectory(),
//				"backup.xml");
//		FileOutputStream fos = new FileOutputStream(file);
//		// ���û��Ķ���һ��һ������,������һ���ĸ�ʽд���ļ�
//		XmlSerializer serializer = Xml.newSerializer(); // ��ȡxml�ļ���������
//		serializer.setOutput(fos, "utf-8");
//		serializer.startDocument("utf-8", true);
//		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[] { "body", "address",
				"type", "date" }, null, null, null);
		//���ý����������ֵ
		int max = cursor.getCount();
		callBack.beforeBackup(max);
//		serializer.attribute(null, "max", max+"");
		SmsDao dao = new SmsDao(context);
		dao.delete();
		int process = 0;
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			dao.add(body, address, Integer.valueOf(type), date);
//
//			serializer.startTag(null, "sms");
//			serializer.startTag(null, "body");
//			serializer.text(body);
//			serializer.endTag(null, "body");
//
//			serializer.startTag(null, "address");
//			serializer.text(address);
//			serializer.endTag(null, "address");
//
//			serializer.startTag(null, "type");
//			serializer.text(type);
//			serializer.endTag(null, "type");
//
//			serializer.startTag(null, "date");
//			serializer.text(date);
//			serializer.endTag(null, "date");
//
//			serializer.endTag(null, "sms");
			//���ݹ�����,���ӽ���
			process++;
			callBack.onSmsBackup(process);
		}
//
//		serializer.endTag(null, "smss");
//		serializer.endDocument();
//		fos.close();
	}
	
	
	
	/**
	 * ��ԭ����
	 * @param context
	 * @param falg �Ƿ����ԭ���Ķ���
	 */
	public static void restoreSms(Context context,boolean flag){
		//1.��ȡXML�ļ�
		//Xml.newPullParser();
		//2.��max
		//3.��ȡ����
		//4.�������
		
		Uri uri = Uri.parse("content://sms/");
		if(flag){
			context.getContentResolver().delete(uri, null, null);
		}
		
		SmsDao dao = new SmsDao(context);
		List<SmsInfo> infos = new ArrayList<SmsInfo>();
		infos = dao.findAll();
		int max = dao.findNum();
		
		if(max == 0){
			Toast.makeText(context, "���ȱ��ݶ���", 0).show();
		}else{
			SmsInfo info = new SmsInfo();
			for(int i = 0 ;i<max;i++){
				info = infos.get(i);
				ContentValues values = new ContentValues();
				values.put("body", info.getBody());
				values.put("date", info.getDate());
				values.put("type", info.getType());
				values.put("address", info.getAddress());
				context.getContentResolver().insert(uri, values);
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
