package com.nmfinal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.nmfinal.nmfinalimageunlock.MainActivity;
import com.nmfinal.nmfinalimageunlock.UnlockScreenActivity;

public class UnlockScreenReceiver extends BroadcastReceiver {

	private boolean isScreenOn = true;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
	
		if ( action.equals( Intent.ACTION_SCREEN_OFF ) ) {
			Log.i( MainActivity.TAB, "UnlockScreenReceiver: ScreenOff received. Starting UnlockScreenActivity.") ;
        	isScreenOn=false;
        	
        	Intent intent11 = new Intent( context, UnlockScreenActivity.class );
        	intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	context.startActivity(intent11);
        	
        } else if ( action.equals( Intent.ACTION_SCREEN_ON ) ) {
        	isScreenOn=true;
        }
	}
}
