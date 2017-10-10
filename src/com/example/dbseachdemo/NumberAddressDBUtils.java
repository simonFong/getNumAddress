package com.example.dbseachdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class NumberAddressDBUtils {
	// �������Ϣ�ֶκ��ⲿdb�ļ�����Ϣ����һ�¡�
	public static final String DB_NAME = "numberaddress.db";
	// ����
	public static final String TABLE_METADATA = "android_metadata";
	public static final String TABLE_NUMBERADDRESS = "numberaddress";
	// �ֶ���
	public static final String COLUMN_PROVINCE = "province";
	public static final String COLUMN_CITY = "city";
	public static final String COLUMN_TELECOM = "telecom";
	public static final String COLUMN_NUMBER = "number";
	public static final String DB_PATH = "data/data/com.example.dbseachdemo/databases";
	private static SQLiteDatabase database;

	/**
	 * �����ļ������п���
	 */
	public static void copyDBFromRaw(Context context) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append("/data/data/");
			stringBuffer.append(context.getPackageName());
			stringBuffer.append("/databases");
			Log.v("simon", stringBuffer.toString());
			File dir = new File(stringBuffer.toString());
			if (!dir.exists()) {// ��ֹdatabases�ļ��в����ڣ���Ȼ���ᱨENOENT (No such file
								// or directory)���쳣
				dir.mkdirs();
			}
			stringBuffer.append("/");
			stringBuffer.append(DB_NAME);
			File file = new File(stringBuffer.toString());
			if (file == null || !file.exists()) {// ���ݿⲻ���ڣ�����п������ݿ�Ĳ�����
				inputStream = context.getResources().openRawResource(
						R.raw.numberaddress);
				outputStream = new FileOutputStream(file.getAbsolutePath());
				byte[] b = new byte[1024];
				int length;
				while ((length = inputStream.read(b)) > 0) {
					outputStream.write(b, 0, length);
				}
				// д���ˢ��
				outputStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {// �ر������ͷ���Դ
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void openDatabase() {
		database = SQLiteDatabase.openOrCreateDatabase(
				NumberAddressDBUtils.DB_PATH + "/"
						+ NumberAddressDBUtils.DB_NAME, null);
	}

	public static void closeDatabase() {
		database.close();
	}

	public static String getAddress(String number) {
		if (isMobileNO(number)) {
			String num = number.substring(0, 7);// ��ȡǰ7������
			String address = "";
			Cursor cursor = database.rawQuery(
					"SELECT province,city,telecom FROM numberaddress WHERE number = "
							+ num, null);

			while (cursor.moveToNext()) {
				String province = cursor.getString(cursor
						.getColumnIndex("province"));
				String city = cursor.getString(cursor.getColumnIndex("city"));
				String telecom = cursor.getString(cursor
						.getColumnIndex("telecom"));
				Log.v("simon", "province:" + province + "city:" + city
						+ "telecom:" + telecom);
				address = province + city;
				Log.v("simon", address);

			}
			cursor.close();
			return address;
		} else {
			return "";
		}

	}

	// ��֤�Ƿ��ֻ��ŵ�������ʽ
	public static boolean isMobileNO(String mobiles) {
		/*
		 * �ƶ���134��135��136��137��138��139��150��151��157(TD)��158��159��187��188
		 * ��ͨ��130��131��132��152��155��156��185��186 ���ţ�133��153��180��189����1349��ͨ��
		 * �ܽ��������ǵ�һλ�ض�Ϊ1���ڶ�λ�ض�Ϊ3��5��8������λ�õĿ���Ϊ0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"�����1λΪ����1��"[358]"����ڶ�λ����Ϊ3��5��8�е�һ����"\\d{9}"��������ǿ�����0��9�����֣���9λ��
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

}
