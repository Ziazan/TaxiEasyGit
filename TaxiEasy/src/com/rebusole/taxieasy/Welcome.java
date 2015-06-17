package com.rebusole.taxieasy;



import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class Welcome extends Activity {
	   @Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	 super.onCreate(savedInstanceState);  
	 //   	 getWindow().setFormat(PixelFormat.RGBA_8888);  
	//	       getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER); 
	        setContentView(R.layout.welcome);  
	     //Display the current version number  
	    //    PackageManager pm = getPackageManager();  
	       
	       new Handler().postDelayed(new Runnable() {  
	           public void run() {  
	                  /* Create an Intent that will start the Main WordPress Activity. */  
	                 Intent mainIntent = new Intent(Welcome.this, MainActivity.class);  
	                 Welcome.this.startActivity(mainIntent);  
	                Welcome.this.finish();} }, 2900); //2900 for release  

}}