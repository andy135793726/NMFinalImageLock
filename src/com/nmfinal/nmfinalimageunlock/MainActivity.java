package com.nmfinal.nmfinalimageunlock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.nmfinal.nmfinalimageunlock.R.drawable;
import com.nmfinal.preference.SharedPreferenceManager;
import com.nmfinal.syncaccount.LinkAccountDialog;

public class MainActivity extends Activity {

	public static final String TAB = "NMFinal";
	
	private Button setImageButton;
	private Button settingsButton;
	
	private ImageButton switchUnlockImgBtn;
	private ImageButton takePhotoImgBtn;
	private ImageButton settingsImgBtn;
	
	private Boolean unlockOn = true;
	
	public static final String PREF_NAME_SETTINGS 	 = SharedPreferenceManager.PREF_NAME_SETTINGS;

	public static final String PREF_INITIALIZED 	 = SharedPreferenceManager.PREF_INITIALIZED;
	public static final String UNLOCKSCREEN_SWITCH	 = "unlockScreenSwitch";
	
	protected enum MenuItemID
	{
		NONE,
		ITEM_UNLOCKED,
		ITEM_LOCKED,
		ITEM_TAKEPHOTO,
		ITEM_SETTINGS
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		addListeners();
		

		
		
		//Initial preferences
		SharedPreferences pref = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE );
		Boolean initialized = pref.getBoolean( PREF_INITIALIZED, Boolean.FALSE );
		if (!initialized){
			initSharedPref( pref );
		}

		unlockOn =  pref.getBoolean( UNLOCKSCREEN_SWITCH, Boolean.TRUE );
		enableUnlockScreen( unlockOn );
		
		if ( !UnlockScreenService.isSomeInstanceRunning( this ) ) {
			Log.i( MainActivity.TAB, "MainActivity: Creating UnlockScreenService.");
			
			Intent i 	  = new Intent();
			i.setClass( this, UnlockScreenService.class );
			startService( i );
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public void onResume()
	{
		SharedPreferences pref = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE );
		Boolean enableUS = pref.getBoolean( UNLOCKSCREEN_SWITCH, true);
		enableUnlockScreen( enableUS );
		
		if (enableUS){
			switchUnlockImgBtn.setImageDrawable( getResources().getDrawable( R.drawable.menuitem_locked ) );
		}
		else {
			switchUnlockImgBtn.setImageDrawable( getResources().getDrawable( R.drawable.menuitem_unlocked ) );
		}
	
		
		super.onResume();
	}

	protected void findViews()
	{
		//setImageButton = ( Button ) findViewById( R.id.btn_setImage );
		//settingsButton = ( Button ) findViewById( R.id.btn_settings );
		
		switchUnlockImgBtn  = ( ImageButton ) findViewById( R.id.imgbtn_switchLock );
		takePhotoImgBtn     = ( ImageButton ) findViewById( R.id.imgbtn_takePhoto );
		settingsImgBtn 		= ( ImageButton ) findViewById( R.id.imgbtn_settings );
	}
	
	protected void addListeners()
	{
		//setImageButton.setOnClickListener( setImageButtonListener );
		//settingsButton.setOnClickListener( settingsButtonListener );
		
		switchUnlockImgBtn.setOnClickListener( switchUnlockImgBtnListener );
		takePhotoImgBtn.   setOnClickListener( takePhotoImgBtnListener );
		settingsImgBtn.	   setOnClickListener( settingsImgBtnListener );
		
		switchUnlockImgBtn.setOnTouchListener( new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MenuItemID id = ( unlockOn ? MenuItemID.ITEM_LOCKED : MenuItemID.ITEM_UNLOCKED );
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 1) );
				}
				else if (event.getAction() == MotionEvent.ACTION_UP ){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 0) );
				}
				return false;
			}
		});
		
		takePhotoImgBtn.setOnTouchListener( new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MenuItemID id = MenuItemID.ITEM_TAKEPHOTO;
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 1) );
				}
				else if (event.getAction() == MotionEvent.ACTION_UP ){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 0) );
				}
				return false;
			}
		} );
		
		settingsImgBtn.setOnTouchListener( new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				MenuItemID id = MenuItemID.ITEM_SETTINGS;
				if (event.getAction() == MotionEvent.ACTION_DOWN){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 1) );
				}
				else if (event.getAction() == MotionEvent.ACTION_UP ){
					((ImageButton)v).setImageResource( getMenuItemDrawableId( id, 0) );
				}
				return false;
			}
		} );
	}
	
	protected void initSharedPref( SharedPreferences prefInit ) 
	{
		SharedPreferences.Editor editor = prefInit.edit();
		
		//*****Add Settings Initializations
		editor.putBoolean( UNLOCKSCREEN_SWITCH, true );
		//*****
		
		editor.putBoolean( PREF_INITIALIZED, Boolean.TRUE );
		editor.commit();
	}
	
	
	protected void enableUnlockScreen( Boolean enable )
	{		
		UnlockScreenService.enableUnlockScreenStatic(enable);
		
		SharedPreferences pref = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE );
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean( UNLOCKSCREEN_SWITCH, enable );
		editor.commit();
	}
	
	protected int getMenuItemDrawableId( MenuItemID itemId, int state )
	{
		int id = -1;
		if (state == 0){           // not touched
			switch (itemId){
			case ITEM_LOCKED:
				id = R.drawable.menuitem_locked;
				break;
			case ITEM_UNLOCKED:
				id = R.drawable.menuitem_unlocked;
				break;
			case ITEM_TAKEPHOTO:
				id = R.drawable.menuitem_takephoto;
				break;
			case ITEM_SETTINGS:
				id = R.drawable.menuitem_settings;
				break;
			}
		}
		else if (state == 1) {
			switch (itemId){
			case ITEM_LOCKED:
				id = R.drawable.menuitem_locked_touched;
				break;
			case ITEM_UNLOCKED:
				id = R.drawable.menuitem_unlocked_touched;
				break;
			case ITEM_TAKEPHOTO:
				id = R.drawable.menuitem_takephoto_touched;
				break;
			case ITEM_SETTINGS:
				id = R.drawable.menuitem_settings_touched;
			}
		}
		return id;
	}
	
	
	private OnClickListener switchUnlockImgBtnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if ( unlockOn ){
				switchUnlockImgBtn.setImageDrawable( getResources().getDrawable( R.drawable.menuitem_unlocked ) );
				enableUnlockScreen( false );
				unlockOn = false;
			}
			else {
				switchUnlockImgBtn.setImageDrawable( getResources().getDrawable( R.drawable.menuitem_locked ) );
				enableUnlockScreen( true );
				unlockOn = true;
			}
			
		}
	};
	
	private OnClickListener takePhotoImgBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass( MainActivity.this, SetImageActivity.class);
			startActivity( intent );
		}
	};
	
	private OnClickListener settingsImgBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass( MainActivity.this, SettingsActivity.class);
			startActivity( intent );
		}
	};
	
	
	
}
