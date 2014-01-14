package com.nmfinal.nmfinalimageunlock;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class UnlockScreenActivity extends Activity {

	private Button unlockButton;
	private static int IsRunningFlag = 0;
	private static Stack<UnlockScreenActivity> activityStack = new Stack<UnlockScreenActivity>();
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
		
		if ( IsRunningFlag > 0 ){
			Log.i(MainActivity.TAB, "UnlockScreenActivity: onCreate(): IsRunningFlag > 0");
			finish();
			return;
		}
		path = new File( getApplicationContext().getExternalFilesDir( Environment.DIRECTORY_PICTURES ), "PictureLock" ).getPath();
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
		pp = (ProgressBar) findViewById(R.id.progress);
		unlockButton = ( Button ) findViewById( R.id.btn_UnlockScreenTest );
		pp.setVisibility(View.INVISIBLE);
	}
	
	protected void addListeners()
	{
		unlockButton.setOnClickListener( unlockButotnListener );
	}
	public void onResume()
	   {
	       super.onResume();
	       OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	   }
	private OnClickListener unlockButotnListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.i(MainActivity.TAB, "UnlockButton clicked. Finishing screen lock!");
			fileUri = getOutputMediaUri( MEDIA_TYPE_IMAGE );
			Intent pictureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
			pictureIntent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
			startActivityForResult( pictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE );
		}
	};
	final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
            super.handleMessage(msg);
			if(msg.what == -2){
				Log.i("fuck", "fuck");
				pp.setVisibility(View.INVISIBLE);
			}
			if(msg.what == -1){
				Log.i("fuck","ya!!");
				finish();
			}
        }
	};
    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE )
		{
		   if ( resultCode == RESULT_OK )
		   {
		   	try
		   	{
		   		pp.setVisibility(View.VISIBLE);
		   		new Thread(){
					public void run(){
						if(imageDetection(path + File.separator + "testing.jpg",path + File.separator + "password.jpg")){
							Message msg = new Message();
							msg.what = -1;
							handler.sendMessage(msg);
						}
						else {
							Message msg = Message.obtain();
							msg.what = -2;
							handler.sendMessage(msg);
						}
					}
				}.start();
		   	}
		   	catch ( Exception e)
		   	{
		   		e.printStackTrace();
		   	}
		   }
		}
	}
	
	public static Boolean isRunning()
	{
		return (IsRunningFlag != 0);
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
				mediaFile = new File( mediaStorageDirectory.getPath() + File.separator + "testing.jpg" );
			
		}
			
		
		return mediaFile;
	}
	private Uri getOutputMediaUri( int type )
	{
		return Uri.fromFile( getOutputMediaFile( type ) );
	}
	
	private boolean imageDetection( String proposal , String password)
	{
		boolean isPass = false;
		Log.i("abcdef", "imagedetect" );
		long StartTime = System.currentTimeMillis();
		MatOfKeyPoint kpR = new MatOfKeyPoint();
		MatOfKeyPoint kpS = new MatOfKeyPoint();
		ArrayList<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
		
		//MatOfDMatch matches = new MatOfDMatch();
		MatOfDMatch goodMatches = new MatOfDMatch();
		Mat descR = new Mat();
		Mat descS = new Mat();
		Size dst_size = new Size();
		float scale = 0.3f;
		Mat reference = Highgui.imread( password, Highgui.IMREAD_GRAYSCALE );
		Mat sample = Highgui.imread( proposal, Highgui.IMREAD_GRAYSCALE );
		dst_size.width = reference.size().width*scale;
		dst_size.height = reference.size().height*scale;
		Imgproc.resize(reference, reference, dst_size);
		
		dst_size.width = sample.size().width*scale;
		dst_size.height = sample.size().height*scale;
		
		Imgproc.resize(sample, sample, dst_size);
		
		FeatureDetector detector = FeatureDetector.create( FeatureDetector.FAST );
		DescriptorExtractor descriptor = DescriptorExtractor.create( DescriptorExtractor.FREAK );
		DescriptorMatcher matcher = DescriptorMatcher.create( DescriptorMatcher.BRUTEFORCE_HAMMING );
		
		Imgproc.equalizeHist(reference, reference);
		Imgproc.equalizeHist(sample, sample);
		
		Rect objectBoundary = new Rect(reference.width()/4, reference.height()/4, reference.width()/2, reference.height()/2);
		detector.detect( reference, kpR );
		{
			ArrayList<KeyPoint> temp = new ArrayList<KeyPoint>();
			KeyPoint[] kk = kpR.toArray(); 
			for(int i=0;i<kk.length;i++){
				if(kk[i].pt.inside(objectBoundary)){
					temp.add(kk[i]);
				}
			}
			kpR.fromList(temp);
		}
		descriptor.compute( reference, kpR, descR );
		detector.detect( sample, kpS );
		descriptor.compute( sample, kpS, descS );
		matcher.knnMatch( descS, descR, matches, 2 );
		Log.i("metches", Integer.toString(matches.size()));
		
	
		
		
		ArrayList<DMatch> gmList = new ArrayList<DMatch>(); 
			 

	   	getGoodMatchesK( matches, descS.rows(), gmList );
		goodMatches.fromList( gmList );
		
		
		
		ArrayList<Point> objectList = new ArrayList<Point>();
		ArrayList<Point> sceneList = new ArrayList<Point>();
		KeyPoint[] kpRArray = kpR.toArray();
		KeyPoint[] kpSArray = kpS.toArray();
		
		for ( int i = 0; i < gmList.size(); i++ )
		{
			//if(kpRArray[ gmList.get( i ).trainIdx].pt.inside(objectBoundary)){
				sceneList.add( kpSArray[ gmList.get( i ).queryIdx ].pt );
				objectList.add( kpRArray[ gmList.get( i ).trainIdx ].pt );
			/*}else{
				Log.i("HOMO","outside");
			}*/
			
		}
		
		MatOfPoint2f object = new MatOfPoint2f();
		MatOfPoint2f scene = new MatOfPoint2f();
		MatOfDMatch finalMatches = new MatOfDMatch();
		object.fromList( objectList );
		scene.fromList( sceneList );
		if(objectList.size() >= 4){
			Mat status = new Mat();
			Calib3d.findHomography( object, scene, Calib3d.RANSAC, 3, status );
			
			
			ArrayList<DMatch> finalList = new ArrayList<DMatch>();		
			
			for ( int i = 0; i < objectList.size(); i++ )
			{
				if ( status.get( i, 0 )[ 0 ] != 0)
					finalList.add( gmList.get( i ) );
			}
			if(finalList.size() >= 0.4 * gmList.size() || finalList.size() > 30){
				isPass = true;
			}else{
				isPass = false;
			}
			finalMatches.fromList( finalList );
			Log.i("final","size: " + Integer.toString(finalMatches.rows()));
		}
		else{
			Log.i("final","not pass");
		}
		Mat imageMatches = new Mat();
		File mediaStorageDirectory = new File( getApplicationContext().getExternalFilesDir( Environment.DIRECTORY_PICTURES ), "VVDPictureLock" );
		String timeStamp = new SimpleDateFormat( "yyMMdd_HHmmss").format( new Date() );
		
		
		Features2d.drawMatches( sample, kpS, reference, kpR, goodMatches, imageMatches, Scalar.all( -1 ), Scalar.all( -1 ), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS );
		Highgui.imwrite(  mediaStorageDirectory.getPath() + File.separator + "OUT_GOOD_" + timeStamp + ".jpg", imageMatches );
		
		Core.rectangle(reference,objectBoundary.tl(),objectBoundary.br(),Scalar.all(-1));
		Features2d.drawMatches( sample, kpS, reference, kpR, finalMatches, imageMatches, Scalar.all( -1 ), Scalar.all( -1 ), new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS );
		Highgui.imwrite(  mediaStorageDirectory.getPath() + File.separator + "OUT_FINAL_" + timeStamp + ".jpg", imageMatches );		
		
		
		
		long EndTime = System.currentTimeMillis();
	   	long ExecutionTime = EndTime - StartTime;
	   	Log.i("abcdef", "Time: " + ExecutionTime );
	   	
	   	System.gc();
	   	return isPass;
	}
	
	private void getGoodMatchesK( ArrayList<MatOfDMatch> matches, int rows, ArrayList<DMatch> gmList )
	{
      DMatch[] mArray;
		
		for ( int i = 0; i < rows; i++ )
		{
			if ( matches.get( i ).rows() >= 2 )
			{
				mArray = matches.get( i ).toArray();
				DMatch m1 = mArray[ 0 ];
				DMatch m2 = mArray[ 1 ];
				
				if ( m1.distance < 0.7 * m2.distance )
				   gmList.add( m1 );
				/*else
					Log.i("GMK","fail");*/
			}
		}
		Log.i("GMK","Size: " + Integer.toString(gmList.size()));
	}
}
