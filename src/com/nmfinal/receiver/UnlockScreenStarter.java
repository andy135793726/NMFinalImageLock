package com.nmfinal.receiver;

import com.nmfinal.nmfinalimageunlock.MainActivity;
import com.nmfinal.nmfinalimageunlock.UnlockScreenService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UnlockScreenStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.i( MainActivity.TAB, "UnlockScreenStarter.onReceive() called with \"Intent.ACTION_BOOT_COMPLETED\" ");
			
			Intent i = new Intent();
			i.setClass( context, UnlockScreenService.class );
			context.startService( i );
		}
	}

}
