package com.nmfinal.nmfinalimageunlock;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

public class SetImageActivity extends Activity {
	private Uri fileUri;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setimage);
		findViews();
		addListeners();
		fileUri = getOutputMediaUri( MEDIA_TYPE_IMAGE );
		Intent pictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		pictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult( pictureIntent , CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE )
		{
		   if ( resultCode == RESULT_OK )
		   {
		   	try
		   	{
		   		Log.i("haha","unlocked");
		   		finish();
		   	}
		   	catch ( Exception e)
		   	{
		   		e.printStackTrace();
		   	}
		   }
		}
	}
	
	protected void findViews()
	{
	}
	
	protected void addListeners()
	{
	}
	
	
	private File getOutputMediaFile( int type )
	{		
		File mediaStorageDirectory = new File( getApplicationContext().getExternalFilesDir( Environment.DIRECTORY_PICTURES ), "PictureLock" );
		
		if ( !mediaStorageDirectory.exists() )
		{
			if ( !mediaStorageDirectory.mkdirs() )
			{
				Log.d( "PictureLock", "failed to create directory" );
				return null;
			}
		}
		
		//String timeStamp = new SimpleDateFormat( "yyMMdd_HHmmss").format( new Date() );
		File mediaFile = null;
		
		if ( type == MEDIA_TYPE_IMAGE ){
				mediaFile = new File( mediaStorageDirectory.getPath() + File.separator + "password.jpg" );
			
		}
			
		
		return mediaFile;
	}
	private Uri getOutputMediaUri( int type )
	{
		return Uri.fromFile( getOutputMediaFile( type ) );
	}
	
}
