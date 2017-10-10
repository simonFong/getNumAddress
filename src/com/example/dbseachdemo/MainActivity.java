package com.example.dbseachdemo;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	private SQLiteDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		NumberAddressDBUtils.copyDBFromRaw(this);

		NumberAddressDBUtils.openDatabase();
		NumberAddressDBUtils.getAddress("13929907242");
		NumberAddressDBUtils.getAddress("13794067626");
		String address = NumberAddressDBUtils.getAddress("23154134");
		Log.v("simon", "ad" + address);

		NumberAddressDBUtils.closeDatabase();

	}

}
