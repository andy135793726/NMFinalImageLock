package com.nmfinal.nmfinalimageunlock;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nmfinal.preference.SharedPreferenceManager;
import com.nmfinal.syncaccount.SyncAccountsActivity;

public class SettingsActivity extends Activity {

	private Button								 syncAcctBtn;
	private ListView 		   					 listView;
	private SettingListAdapter 					 settingListAdapter;
	private ArrayList< HashMap<String, Object> > settingsData;
	
	private static final String HASHKEY_ID		= "id";
	private static final String HASHKEY_LABEL   = "label";
	private static final String HASHKEY_CHECKED = "checked";
	
	public static final String PREF_INITIALIZED = SharedPreferenceManager.PREF_INITIALIZED;
	public static final String PREF_NAME_SETTINGS 	 = SharedPreferenceManager.PREF_NAME_SETTINGS;
	
	
	
	
	private enum SettingsID
	{
		USE_UNLOCK_SCREEN
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		findViews();
		addListeners();
		/* listview *//*
		initSettingsListData();
		listView.setAdapter( settingListAdapter );
		*/
		
	
		
		
		
		
		//Initial preferences
		//SharedPreferences pref = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE );
		//pref.registerOnSharedPreferenceChangeListener( onSharedPreferenceChangeListener );
	}
	
	
	protected void findViews()
	{
		syncAcctBtn = (Button) findViewById( R.id.btn_syncacct );
		listView = (ListView) findViewById( R.id.listview_settings );
	}
	
	protected void addListeners()
	{
		syncAcctBtn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass( getApplicationContext(), SyncAccountsActivity.class );
				startActivity( intent );
			}
		});
	}
	
	
	private void initSettingsListData()
	{
		settingsData = new ArrayList<HashMap<String,Object>>();
		final int settingsNum = 1;
		for ( int i = 0; i < settingsNum; i++ ){
			settingsData.add( new HashMap<String, Object>() );
		}
		
		//Add the setting data : 0 
		SettingsID id = SettingsID.USE_UNLOCK_SCREEN;
		settingsData.get( 0 ).put( HASHKEY_ID,      id );
		settingsData.get( 0 ).put( HASHKEY_LABEL,   getString(R.string.setting_unlockScreenOn));
		settingsData.get( 0 ).put( HASHKEY_CHECKED, getSettingsPrefBoolean( id ) );
		
		
		settingListAdapter = new SettingListAdapter( this, settingsData );
	}
	
	
	private Boolean getSettingsPrefBoolean( SettingsID id )
	{
		SharedPreferences pref = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE );
		return pref.getBoolean( id.name(), Boolean.FALSE );
	}
	
	
	private void changeSetting( SettingsID id, Boolean isChecked )
	{
		SharedPreferences sp = getSharedPreferences( PREF_NAME_SETTINGS, MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean( id.name(), isChecked );
		editor.commit();
	}
	
	public OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = 
		new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
			{
			}
	};  
	
	private class SettingListAdapter extends BaseAdapter
	{
		private ArrayList< HashMap<String, Object> > data;
		private Context context;
		private LayoutInflater layoutInflater;  
		public SettingListAdapter( Context context, ArrayList< HashMap<String, Object> > data )
		{
			this.context = context;
			this.data = data;
			this.layoutInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			Log.i(MainActivity.TAB,"SettingListAdapter.getCount() return: "+ data.size());
			return data.size();
		}

		@Override
		public Object getItem( int position ) {
			return data.get( position );
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SettingItem setting = null;
			if (convertView == null){
	            convertView = layoutInflater.inflate( R.layout.list_settings, null ); 
				setting = new SettingItem();
				setting.checkBox 	  = (CheckBox) convertView.findViewById( R.id.checkBox_setting );
				setting.labelTextView = (TextView) convertView.findViewById( R.id.textView_setting );
				convertView.setTag( setting );
			} 
			else {
				setting = (SettingItem) convertView.getTag();
			}
			
			Log.i(MainActivity.TAB, "SettingListAdapter.getView( " + position +", " + convertView + ", " + parent + " )");
			setting.id = 				   (SettingsID) data.get(position).get(HASHKEY_ID);
			setting.labelTextView.setText( (String)     data.get(position).get(HASHKEY_LABEL) );
			setting.checkBox.setChecked(   (Boolean) 	data.get(position).get(HASHKEY_CHECKED) );
			setting.checkBox.setOnCheckedChangeListener( setting.onCheckedChangeListener );
			
			return convertView;
		}
	}
	
	private class SettingItem
	{
		public SettingsID id;
		public TextView labelTextView;
		public CheckBox checkBox;
		
		public SettingItem(){}
		
		public Boolean isChecked()
		{
			return checkBox.isChecked();
		}
		
		public OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeSetting( id, isChecked);
			}
		};
	}
}
