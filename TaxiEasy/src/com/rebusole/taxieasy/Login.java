package com.rebusole.taxieasy;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;





import android.net.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private Button login;
	private Button register;
	private EditText login_account;
	private EditText login_password;
	

	private ProgressDialog mpDialog;
	private static String url_ip ="http://192.168.253.3/callCar/user/login.php";
    private static final String TAG_MESSAGE = "message";
    
    final int LOGINSUCCESS_MSG = 0;
	final int LOGINFAIL_MSG = 1;
	
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		//ExitApplication.getInstance().addActivity(this);

	

//		if (app.getLogin()) {
//			StartMain();
//		}

		// 初始化加载对话框
    	mpDialog = new ProgressDialog(this);
    	mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	mpDialog.setTitle(R.string.loading_data);
        mpDialog.setIcon(android.R.drawable.ic_dialog_info);
    	mpDialog.setMessage(getString(R.string.waiting));
    	mpDialog.setIndeterminate(false);
		mpDialog.setCancelable(true);

		login = (Button) findViewById(R.id.login_confirm);
		register = (Button) findViewById(R.id.login_register);

		login_account= (EditText) findViewById(R.id.login_account);
		login_password= (EditText) findViewById(R.id.login_password);

		login.setOnClickListener(new OnClick());
		register.setOnClickListener(new OnClick());
	}

	class OnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_confirm:
				LoginMeth();
				break;
			case R.id.login_register:
				startActivityForResult(new Intent(Login.this, Register.class),1);
				break;
			}
		}
	}

	private void LoginMeth() {
		final String username = login_account.getText().toString().trim();
		final String password = login_password.getText().toString().trim();

		

        if (username.equals("")) {
 		Toast.makeText(Login.this, "用户名为空", Toast.LENGTH_SHORT).show();		
			return;
		}

		if (password.equals("")) {
     		ShowToast(R.string.noPwdErr);
    		return;		}

		mpDialog.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
		 			// Building Parameters
		 			List<NameValuePair> params = new ArrayList<NameValuePair>();
		 			params.add(new BasicNameValuePair("username",username));
		 			params.add(new BasicNameValuePair("password", password));

		 			// getting JSON Object
		 			// Note that create product url accepts POST method
		 			try {
		 				JSONObject json = jsonParser.makeHttpRequest(url_ip,"POST",params);
		 						
		 				String message = json.getString(TAG_MESSAGE);
		 				if ("login success".equals(message)) 
		 				{
		 					Message message1 = new Message();
							message1.what = LOGINSUCCESS_MSG;
							message1.obj = username; 
							mhandler.sendMessage(message1);
		 				}
		 				else {
							Message message1 = new Message();
							message1.what = LOGINFAIL_MSG;
							mhandler.sendMessage(message1);
						}

		 			} catch (Exception e) {
		 				Message message = new Message();
						message.what = LOGINFAIL_MSG;
						mhandler.sendMessage(message);
		 			}	
			}
		}).start();
	}

	// 线程处理
    private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mpDialog.dismiss();
			switch (msg.what) {
			case LOGINSUCCESS_MSG:
				/*app.SaveBegin("username", "18013398197");*/
			    //	app.SaveLogin(true);
				if(msg.obj!=null)
		    	{
					    try {
							JSONObject json = new JSONObject(msg.obj.toString());
							String mmoney=json.getString("money");
							GetMoney(mmoney);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		
		    	}
				SaveState();
				StartMain();
				break;
			case LOGINFAIL_MSG:
			//	ShowToast(R.string.loginFail);
				break;
			}
		}
	};

	private void GetMoney(String string)
	{
		SharedPreferences sp_m=this.getSharedPreferences("Inf", MODE_PRIVATE);
		SharedPreferences.Editor editor_m = sp_m.edit(); 
		editor_m.putString("money", string);
		editor_m.commit(); 
	}
	private void SaveState(){
		SharedPreferences sp=this.getSharedPreferences("Inf", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit(); 
		editor.putBoolean("state", true);
		editor.commit(); 
		Toast.makeText(this, "已登录！" , 
		Toast.LENGTH_LONG).show(); 
	}
	  public int  CheckLoginState(){
	    	//SharedPreferences.Editor editor=sp.edit();
	    	SharedPreferences sp=this.getSharedPreferences("Inf", MODE_PRIVATE);
	    	boolean M=sp.getBoolean("state", false);
	    	if(M)
	    	{
	    		java.lang.System.out.println("OK");
	    		return 1;
	    	}
	    	else
	    	{
	    		java.lang.System.out.println("Flase");
	    		return 2;
	    	}
	    }
	private void StartMain() {
		startActivity(new Intent(Login.this, MainActivity.class));
		Login.this.finish();
	}
	
	private void ShowToast(int res) {
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}
}