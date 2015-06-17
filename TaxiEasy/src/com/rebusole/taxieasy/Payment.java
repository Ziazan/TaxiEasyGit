package com.rebusole.taxieasy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Payment extends Activity{

	private TextView textview_money;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment);
		textview_money=(TextView) findViewById(R.id.information_account);
		showMoney();
	}
	public   void showMoney(){
    	//SharedPreferences.Editor editor=sp.edit();
    	SharedPreferences sp=this.getSharedPreferences("Inf", MODE_PRIVATE);
    	String M=sp.getString("money", null);
    	textview_money.setText(M);
    }
	
}
