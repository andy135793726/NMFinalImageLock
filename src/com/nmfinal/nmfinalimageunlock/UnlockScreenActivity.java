package com.nmfinal.nmfinalimageunlock;


import java.util.Stack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UnlockScreenActivity extends Activity {

	private Button unlockButton;
	private static int IsRunningFlag = 0;
	private static Stack<UnlockScreenActivity> activityStack = new Stack<UnlockScreenActivity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( IsRunningFlag > 0 ){
			Log.i(MainActivity.TAB, "UnlockScreenActivity: onCreate(): IsRunningFlag > 0");
			finish();
			return;
		}
		
		setContentView( R.layout.activity_unlockscreen );
		findViews();
		addListeners();
		
		IsRunningFlag ++;
		activityStack.push( this );
	}
	
	@Override
	protected void onDestroy()
	{
		/*while ( !activityStack.isEmpty() ){
			UnlockScreenActivity activity = activityStack.pop();
			activity.finish();
		}*/
		IsRunningFlag --;
		super.onDestroy();
	}
	
	protected void findViews()
	{
		unlockButton = ( Button ) findViewById( R.id.btn_UnlockScreenTest );
	}
	
	protected void addListeners()
	{
		unlockButton.setOnClickListener( unlockButotnListener );
	}
	
	private OnClickListener unlockButotnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.i(MainActivity.TAB, "UnlockButton clicked. Finishing screen lock!");
			finish();
		}
	};
	
    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }
    
	
	
	public static Boolean isRunning()
	{
		return (IsRunningFlag != 0);
	}
}
