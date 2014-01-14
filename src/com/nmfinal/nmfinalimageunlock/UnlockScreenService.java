package com.nmfinal.nmfinalimageunlock;


import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils.TruncateAt;
import android.util.Log;

import com.nmfinal.receiver.UnlockScreenReceiver;

public class UnlockScreenService extends Service {

	private UnlockScreenReceiver unlockScreenReceiver;
	private KeyguardManager.KeyguardLock keyguardLock;
	
	public static UnlockScreenService instance;
	private static Boolean enabled = true;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.i( MainActivity.TAB, "UnlockScreenService.onCreate() called");

	    instance = this;
	    super.onCreate();
	}
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.i(MainActivity.TAB, "Service:onStartCommand: enable = " + enabled);
		
		enableUnlockScreen(enabled);
		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
		Log.i( MainActivity.TAB, "UnlockScreenService onDestroy called");
		enableUnlockScreen( false );
		super.onDestroy();
	}
	
	
	public static boolean isSomeInstanceRunning( Context context ) 
	{
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {
	        if ( UnlockScreenService.class.getName().equals( service.service.getClassName() ) ) {
	            return true;
	        }
	    }
	    return false;   
	}
	
	public static UnlockScreenService getRunningInstance( Context context )
	{
		if ( instance == null ){
			Intent i = new Intent();
			i.setClass( context, UnlockScreenService.class );
			context.startService( i );  //this will create an instance in onCreate()
		}
		return instance;
	}
	

	public void enableUnlockScreen( Boolean enable )
	{	
		if ( keyguardLock == null ){
			KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		    keyguardLock = km.newKeyguardLock( "IN" );
		}
	    
		if ( unlockScreenReceiver == null ) {
			unlockScreenReceiver = new UnlockScreenReceiver();
		}
		
		if ( enable ){
		    IntentFilter filter = new IntentFilter( Intent.ACTION_SCREEN_ON );
			filter.addAction( Intent.ACTION_SCREEN_OFF );
			registerReceiver( unlockScreenReceiver, filter );
			keyguardLock.disableKeyguard();
		}
		else {
			try {
				unregisterReceiver( unlockScreenReceiver );
			} 
			catch ( IllegalArgumentException e ) {
				// if the receiver is not registered, this exception will be caught.
				// do nothing. pass this.
			}
			keyguardLock.reenableKeyguard();
		}
	}
	
	public static void enableUnlockScreenStatic( Boolean enable )
	{
		enabled = enable;
		if (instance != null){
			instance.enableUnlockScreen( enabled );
		}
	}
 
		
}
