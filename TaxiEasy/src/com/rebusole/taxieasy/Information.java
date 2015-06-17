package com.rebusole.taxieasy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Information extends Activity
{

	private TextView textview_account;
	private Button button_edit_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
 		 setContentView(R.layout.user_information);
 		 textview_account=(TextView) findViewById(R.id.information_account);
 		 button_edit_login=(Button) findViewById(R.id.information_cancle);
 		 button_edit_login.setOnClickListener(ButtonOnClickListener);
 		 

	}
/////定位Button   Button 监听器
  OnClickListener ButtonOnClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) {
			
			case R.id.information_cancle:
				ExitLogin();
				StartMain();
			default:
				break;
			}
		}
	};
	private void StartMain() {
		startActivity(new Intent(Information.this, MainActivity.class));
		Information.this.finish();
	}
	
	
	private void ExitLogin(){
		SharedPreferences sp=this.getSharedPreferences("Inf", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit(); 
		editor.putBoolean("state", false);
		editor.putString("money", "0");
		editor.commit(); 
		Toast.makeText(this, "已退出！" , 
		Toast.LENGTH_LONG).show(); 
	}
}
