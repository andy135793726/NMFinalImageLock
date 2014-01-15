package com.nmfinal.syncaccount;

import java.net.URL;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.nmfinal.nmfinalimageunlock.MainActivity;
import com.nmfinal.nmfinalimageunlock.R;

public class LoginWebviewActivity extends Activity {
	
	private String accountType;
	private String username;
	private String password;
	
	private WebView webView;
	public final static String TAG = "myWeb";
	private Uri fileUri;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private String path;
	private ProgressBar pp;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

	       @Override
	       public void onManagerConnected(int status) {
	           switch (status) {
	               case LoaderCallbackInterface.SUCCESS:
	               {
	                   Log.i("PictureLock", "OpenCV loaded successfully");

	               } break;
	               default:
	               {
	                   super.onManagerConnected(status);
	               } break;
	           }
	       }
	   };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginweb);

		webView = (WebView) findViewById( R.id.webview );
		webView.getSettings().setJavaScriptEnabled( true );
		
	
		accountType = getIntent().getExtras().getString( getResources().getString(R.string.accountType) );
		username = getIntent().getExtras().getString( getResources().getString(R.string.username) );
		password = getIntent().getExtras().getString( getResources().getString(R.string.password) );
		
		Log.i(MainActivity.TAB, "starting web activity. acctType=" + accountType + "  username=" + username);
		
		startTestingImage();
	}

	private void startTestingImage()
	{
		//this is for testing, delete this if using picture lock
		startConnectingService();
		
		/*
		fileUri = getOutputMediaUri( MEDIA_TYPE_IMAGE );
		Intent pictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
		pictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
		startActivityForResult( pictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE );
		*/
	}
	
	
	
	
	// called (by) the callback of image-recognization (if it returns passed)
	private void startConnectingService()
	{
		String url = getUrlByAccountType( this, accountType );
		final String un =  this.username;
		final String pwd = this.password;
		
		try {
			webView.loadUrl( url );
			webView.setWebViewClient( new WebViewClient(){
				Boolean alreadyLogin = false;
				public void onPageFinished( WebView view, String url ){
					Log.i(MainActivity.TAB, "onPageFinished!");
					if ( alreadyLogin ){
						Log.i(MainActivity.TAB, "Not loginning in because having logged in ");
						return;
					}
					
					view.clearCache( true );
					
					if ( accountType.equals( getResources().getString(R.string.acctType_ntumail) )){
						view.loadUrl( "javascript:document.getElementById('username').value='" + un + "';"
									+ "document.getElementById('password').value='" + pwd + "';" 
									+ "document.getElementById('password').focus();" );
						triggerEnterKey();
						alreadyLogin = true;
					}
					else if ( accountType.equals( getResources().getString(R.string.acctType_dropbox) ) ){
						view.loadUrl( "javascript:document.getElementById('login_email').value='" + un + "';"
									+ "document.getElementById('login_password').value='" + pwd + "';"
									+ "document.getElementById('login_password').focus();" );		
						triggerEnterKey();
						alreadyLogin = true;
					}
					
				}
			});
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
	}
	
	
	private void triggerEnterKey()
	{
		// Programmically trigger the ENTER KEY to login
		new Thread(){
			public void run(){
				Log.i(MainActivity.TAB, "new thread run");
				Instrumentation inst = new Instrumentation();
				inst.sendKeyDownUpSync( KeyEvent.KEYCODE_ENTER);
			}
		}.start();
	}
	
	private static String getUrlByAccountType( Context context, String accountType )
	{
		String url = null;
		Resources res = context.getResources();
		if ( accountType.equals( res.getString(R.string.acctType_ntumail) )){
			url = res.getString( R.string.url_ntuwebmail );
		}
		else if ( accountType.equals( res.getString(R.string.acctType_dropbox) ) ){
			url = res.getString( R.string.url_dropbox );
		}
		return url;
	}
	
	
}
