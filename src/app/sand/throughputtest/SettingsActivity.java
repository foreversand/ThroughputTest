package app.sand.throughputtest;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;



public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			showPreferencesPreHoneycomb();
		}
		else {
			showPreferencesFragmentStyle(savedInstanceState);
		}
	}

	private void showPreferencesFragmentStyle(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

	private void showPreferencesPreHoneycomb() {
		// TODO Auto-generated method stub
		
	}
}
