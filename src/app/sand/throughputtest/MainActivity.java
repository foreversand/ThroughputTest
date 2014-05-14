package app.sand.throughputtest;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;



public class MainActivity extends Activity implements OperationView.stateListener {

	private static final String MAIN_TAG = "MainActivity";
	protected static final int SET_PROGRESS_BAR_VISIBILITY = 0;
	protected static final int PROGRESS_UPDATE = 1;
	protected static final int SHOW_RESULT = 2;
	private ProgressBar mProgressBar;
	private OperationView mOperationView;
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){

		public void handleMessage(android.os.Message msg) {
			String str = ""; 
			switch (msg.what) {
			case SET_PROGRESS_BAR_VISIBILITY: {
				str = mOperationView.getOperationResult();
				mProgressBar.setVisibility((Integer) msg.obj);
				break;
			}
			case PROGRESS_UPDATE: {
				mProgressBar.setProgress((Integer) msg.obj);
				mOperationView.setOperationResult(str + "testing......" + msg.obj + "%\n");
				mOperationView.postInvalidate();
				break;
			}
			case SHOW_RESULT: {
				mOperationView.setOperationResult(str + "testing result is: \n" );
				mOperationView.postInvalidate();
				mOperationView.setOperationState(false);
				break;
			}
			}
		}
	};
	
	public void onOperationStart(boolean state){
		if(state) {
			new Thread(new DoThroughputTask(handler)).start();
		}
	}
	
	public class DoThroughputTask implements Runnable {
		private final Handler handler;
		
		public DoThroughputTask(Handler handler) {
			this.handler = handler; 
		}
		@Override
		public void run() {
			Message msg = handler.obtainMessage(SET_PROGRESS_BAR_VISIBILITY,
					ProgressBar.VISIBLE);
			handler.sendMessage(msg);
			for (int i = 1; i < 11; i++) {
				sleep();
				msg = handler.obtainMessage(PROGRESS_UPDATE, i * 10);
				handler.sendMessage(msg);
			}

			msg = handler.obtainMessage(SHOW_RESULT);
			handler.sendMessage(msg);

			msg = handler.obtainMessage(SET_PROGRESS_BAR_VISIBILITY,
					ProgressBar.INVISIBLE);
			handler.sendMessage(msg);
			
		}
		private void sleep() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
