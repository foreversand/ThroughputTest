package app.sand.throughputtest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;


//preference fragment
public class SettingsActivity extends PreferenceActivity {
	private static final String SETTING_TAG = "SettingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			Log.d(SETTING_TAG, "an old version setting activity created\n");

			showPreferencesPreHoneycomb();
		}
		else {
			Log.d(SETTING_TAG, "a new version setting activity created\n");
			showPreferencesFragmentStyle(savedInstanceState);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	private void showPreferencesFragmentStyle(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(savedInstanceState == null) {
			FragmentTransaction transcacion = getFragmentManager().beginTransaction();
			Fragment fragment = new TestPreferenceFragment();
			transcacion.replace(android.R.id.content, fragment);
			transcacion.commit();
		}
	}

	@SuppressWarnings("deprecation")
	private void showPreferencesPreHoneycomb() {
		// TODO Auto-generated method stub
		addPreferencesFromResource(R.xml.test_prefs);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
	public static class TestPreferenceFragment extends PreferenceFragment{
		

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			Log.d(SETTING_TAG, "attach a fragment to an activity!\n");
		}
		
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//        		Bundle savedInstanceState) {
//        	// TODO Auto-generated method stub
//        	this.addPreferencesFromResource(R.xml.test_prefs);
//        	return super.onCreateView(inflater, container, savedInstanceState);
//        }
		@Override
		public void onCreate(Bundle savedInstanceState) {
			
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.test_prefs);
		}
	}
}
