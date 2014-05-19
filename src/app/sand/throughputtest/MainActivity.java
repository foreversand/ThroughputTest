package app.sand.throughputtest;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;



public class MainActivity extends Activity implements OperationView.stateListener {

	private static final String MAIN_TAG = "MainActivity";
	protected static final int OPERATION_BEGIN = 0;
	protected static final int GENERATE_RESULT = 1;
	protected static final int PROGRESS_UPDATE = 2;
	protected static final int UPDATE_RESULT = 3;
	private static final int SHOW_RESULT = 4;
	protected static final int OPERATION_COMPLETE = 5;
	
	
	private ProgressBar mProgressBar;
	private OperationView mOperationView;
	private String mTestResult;
	private int mTestTime;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case OPERATION_BEGIN: {
				mProgressBar.setVisibility((Integer) msg.obj);
				mOperationView.setOperationResult("testing......");
				mOperationView.postInvalidate();
				mTestResult = "";
				break;
			}
			case PROGRESS_UPDATE: {
				mTestTime = 1 + (Integer) msg.obj / 10;
				mProgressBar.setProgress((Integer) msg.obj);
				mOperationView.setOperationResult("testing......" + msg.obj + "%\n");
				mOperationView.postInvalidate();
				break;
			}
			case GENERATE_RESULT: {
				mTestResult = (String) msg.obj;
				break;
			}
			case UPDATE_RESULT: {
				mTestResult += "Do the throughput test: " + mTestTime + "\n";
				mTestResult += msg.obj;				
				Log.d(MAIN_TAG, "update result\n");
				//mOperationView.postInvalidate();
				break;
			}
			case SHOW_RESULT: {
				File path;
				String filename;
				generateResultFilename();

				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
					path = Environment.getExternalStoragePublicDirectory("result");
				}
				else {
					path = Environment.getDataDirectory();
				}
				filename = generateResultFilename();
				File file = new File(path, filename);
				try {
					path.mkdirs();
					BufferedWriter os = new BufferedWriter(new FileWriter(file));
					os.write(mTestResult);
					os.close();
				} catch (Exception e) {
					Log.w("ExternalStorage", "Error writing " + file, e);
				}	
				
				mOperationView.setOperationResult("TESK OK!\n\n\nThe result is put in directory \"" + path.toString() + "\".\n" + mTestResult);
				mOperationView.postInvalidate();
				break;
			}
			case OPERATION_COMPLETE: {

				mOperationView.setOperationState(false);
				mProgressBar.setVisibility(ProgressBar.INVISIBLE);
				mOperationView.postInvalidate();
				
				
				break;
			}
			}
		}

		private String generateResultFilename() {
			Time t=new Time(); 
			t.setToNow();
			int year = t.year;
			int month = t.month;
			int date = t.monthDay;
			int hour = t.hour; 
			int minute = t.minute;
			int second = t.second;
			return String.valueOf(year) + String.valueOf(month) + String.valueOf(date)
				   + String.valueOf(hour) + String.valueOf(minute)+ String.valueOf(second) + ".txt";
		}
	};
	
	
	
	public void onOperationStart(boolean state){
		if(state) {
			new Thread(new doThroughputTask(handler)).start();
		}
	}
	
	public class doThroughputTask implements Runnable {
		private static final int TEST_COUNT = 10;
		
		private final Handler handler;
		private DiskOperation diskOp = new DiskOperation();
		
		public doThroughputTask(Handler handler) {
			this.handler = handler; 
		}
		@Override
		public void run() {
			Message msg = handler.obtainMessage(OPERATION_BEGIN,
					ProgressBar.VISIBLE);
			handler.sendMessage(msg);
			
			int src = mOperationView.getSourceDisk();
			int dest = mOperationView.getDestDisk();
			Long buffersize;
			String result = "";
			try {
				buffersize = Long.valueOf(mOperationView.getBufferSize());
			} catch (Exception e) {
				buffersize = (long) 65536;
			}
			result = diskOp.generateResultHeander(src, dest, buffersize);
			msg = handler.obtainMessage(GENERATE_RESULT, result);
			handler.sendMessage(msg);
			
			for (int i = 1; i < TEST_COUNT + 1; i++) {
				result = diskOp.operationThroughputTest(src, dest, buffersize);
				//Log.d(MAIN_TAG, result);
				msg = handler.obtainMessage(UPDATE_RESULT, result);
				handler.sendMessage(msg);
				msg = handler.obtainMessage(PROGRESS_UPDATE, i * 10);
				handler.sendMessage(msg);
			}

			msg = handler.obtainMessage(SHOW_RESULT);
			handler.sendMessage(msg);
			
			msg = handler.obtainMessage(OPERATION_COMPLETE,
					ProgressBar.INVISIBLE);
			handler.sendMessage(msg);
			
		}
		
//		private void sleep() {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mOperationView = (OperationView) findViewById(R.id.operation);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mOperationView.registerStatelistener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.parameters_settings) {
			Log.d(MAIN_TAG, "select a setting to activate the setting activity\n");
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
