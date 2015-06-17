package com.rebusole.taxieasy;


import java.util.ArrayList;
import java.util.List;




import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import net.DialogUtil;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {
	private Button register_button;
	private EditText register_account;
	private EditText register_password;
	private ProgressDialog mpDialog;
	private static String url_up = "http://192.168.253.3/callCar/user/register.php";
//	private static String url_up = "http://1.chengjunapp.sinaapp.com/register.php";
	
    private static final String TAG_MESSAGE = "message";
	
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		register_button=(Button) findViewById(R.id.register_confirm);
        register_account=(EditText) findViewById(R.id.register_account);
	    register_password=(EditText) findViewById(R.id.register_password);
		register_button.setOnClickListener(new OnClick());
		
	}

	class OnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.register_confirm:
		          if(checkValidate()){                
		          new Call().execute(); }           
				break;
			}
		}
	}

	private boolean checkValidate() {
		final String username = register_account.getText().toString().trim();
		final String password = register_password.getText().toString().trim();

		if (username == "") {
			DialogUtil.showDialog(this, "�������û���", false);
			return false;
		}
		if (password == "") {
			DialogUtil.showDialog(this, "����������", false);
			return false;
		}
		
		return true;
	}
	
	class Call extends AsyncTask<String, String, String> {
		
		 protected void onPreExecute() {
	            super.onPreExecute();
	            mpDialog = new ProgressDialog(Register.this);
	            mpDialog.setMessage("����ע��..");
	            mpDialog.setIndeterminate(false);
	            mpDialog.setCancelable(true);
	            mpDialog.show();
	        }

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			final String username = register_account.getText().toString().trim();
			final String password = register_password.getText().toString().trim();
			
			 List<NameValuePair> params1 = new ArrayList<NameValuePair>();
		     params1.add(new BasicNameValuePair("username", username));
		     params1.add(new BasicNameValuePair("password", password)); 
		     try{ 
		            JSONObject json = jsonParser.makeHttpRequest(url_up,"POST", params1);  
		            String message = json.getString(TAG_MESSAGE);
		            return message;     
		           }catch(Exception e){
		               e.printStackTrace(); 
		               return "";
		           }
		}
		
		 protected void onPostExecute(String message) {
	        	
	        	//mpDialog.setMessage("ע��ɹ�"); 
	            mpDialog.dismiss();
	            if ("ע��ɹ�!".equals(message)) {
	 				Toast.makeText(Register.this,"ע��ɹ�!", 3000).show();
	            	
	            	Intent intent = new Intent();
	            	intent.setClass(Register.this,Login.class);
	 				// ����intent��Ӧ��Activity
	 				startActivity(intent);		
	 			}
	            else
	            {
	            	Toast.makeText(Register.this, "���û���ע�ᣬ������ע��", 1000).show();
	            }
				  	                
         }	
	
     }
}
