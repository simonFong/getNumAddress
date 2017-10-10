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
	// 这里的信息字段和外部db文件中信息保持一致。
	public static final String DB_NAME = "numberaddress.db";
	// 表名
	public static final String TABLE_METADATA = "android_metadata";
	public static final String TABLE_NUMBERADDRESS = "numberaddress";
	// 字段名
	public static final String COLUMN_PROVINCE = "province";
	public static final String COLUMN_CITY = "city";
	public static final String COLUMN_TELECOM = "telecom";
	public static final String COLUMN_NUMBER = "number";
	public static final String DB_PATH = "data/data/com.example.dbseachdemo/databases";
	private static SQLiteDatabase database;

	/**
	 * 利用文件流进行拷贝
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
			if (!dir.exists()) {// 防止databases文件夹不存在，不然，会报ENOENT (No such file
								// or directory)的异常
				dir.mkdirs();
			}
			stringBuffer.append("/");
			stringBuffer.append(DB_NAME);
			File file = new File(stringBuffer.toString());
			if (file == null || !file.exists()) {// 数据库不存在，则进行拷贝数据库的操作。
				inputStream = context.getResources().openRawResource(
						R.raw.numberaddress);
				outputStream = new FileOutputStream(file.getAbsolutePath());
				byte[] b = new byte[1024];
				int length;
				while ((length = inputStream.read(b)) > 0) {
					outputStream.write(b, 0, length);
				}
				// 写完后刷新
				outputStream.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {// 关闭流，释放资源
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
			String num = number.substring(0, 7);// 截取前7个数字
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

	// 验证是否手机号的正则表达式
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

}
