package com.nmfinal.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

	private Context context;
	
	public static final String PREF_NAME_SETTINGS = "pref_settings"; 
	public static final String PREF_INITIALIZED = "pref_initialized";
	
	
	public SharedPreferenceManager( Context context )
	{
		this.context = context;
	}
	
	public static SharedPreferences.Editor getEditor( Context context, String prefName )
	{
		//default mode = MODE_PRIVATE
		SharedPreferences pref = context.getSharedPreferences( prefName, Context.MODE_PRIVATE );
		return pref.edit();
	}
	
	
	
	/*
	public Object getSharedPreference( String key, String prefName, int mode )
	{
		Object value = null;
		
		SharedPreferences pref = context.getSharedPreferences( prefName, mode );
		
		
		return value;
	}
	
	public void setSharedPreference( String key, Object value )
	{
		//SharedPreferences pref = context.getSharedPreferences( prefName, mode );
		//SharedPreferences.Editor editor = pref.edit();
	}
	*/
	
	
}
