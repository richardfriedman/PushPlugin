package com.plugin.gcm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class PushHandlerActivity extends Activity
{
	private static String TAG = "PushHandlerActivity"; 

	/*
	 * this activity will be started if the user touches a notification that we own. 
	 * We send it's data off to the push plugin for processing.
	 * If needed, we boot up the main activity to kickstart the application. 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		boolean isPushPluginActive = PushPlugin.isActive();
		processPushBundle(isPushPluginActive);

		GCMIntentService.cancelNotification(this);

		finish();

		if (!isPushPluginActive) {
			forceMainActivityReload();
		}
	}

	/**
	 * Takes the pushBundle extras from the intent, 
	 * and sends it through to the PushPlugin for processing.
	 */
	private void processPushBundle(boolean isPushPluginActive)
	{
		Log.v(TAG, "processPushBundle");		
		
		Bundle extras = getIntent().getBundleExtra("pushBundle");
		if ( extras == null ) { 
			extras = PushPlugin.getExtras();
		}
		
		if (extras != null)	{
			extras.putBoolean("foreground", false);
			extras.putBoolean("coldstart", !isPushPluginActive);

			PushPlugin.sendExtras(extras);
		} else { 
			Log.v(TAG, "FAILED to SEND PushPlugin.sendExtras"); 
		}
	}

	/**
	 * Forces the main activity to re-launch if it's unloaded.
	 */
	private void forceMainActivityReload()
	{
		PackageManager pm = getPackageManager();
		Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());    		
		startActivity(launchIntent);
	}

}