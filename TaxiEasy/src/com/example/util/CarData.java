package com.example.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.bean.Driver;
import com.example.bean.Taxi;

import android.os.Handler;

public class CarData {
	Handler mHandler;
	
	public CarData(Handler mHandler) {
		this.mHandler = mHandler;
	}
	
	//获得出租车的消息
	public  ArrayList<Taxi> getCarData(String str) {
		//向服务器请求数据
		ArrayList<Taxi> list = parser(str);
		return list;
	}
	
	//解析json 数据
	private ArrayList<Taxi> parser(String str){	
		ArrayList<Taxi> list =new ArrayList<Taxi>() ;
		
		try {
			    JSONObject json = new JSONObject(str);
            	boolean code = json.getBoolean("success");
            	if(code)
            	{
				JSONArray arr = json.getJSONArray("cardata");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject dataJSON = arr.getJSONObject(i);
					Taxi t = new  Taxi();
					Driver d=new Driver();
					d.setPhoneNumber(dataJSON.getString("plateNumber"));					
					t.setDriver(d);
					double latitude = Double.parseDouble(dataJSON.getString("latitude"));
					double longitude = Double.parseDouble(dataJSON.getString("longitude"));
					t.setLatitude(latitude);
					t.setLongitude(longitude);
					System.out.println("==>taxi[i]:"+t.getLatitude()+";"+t.getLongitude());
					list.add(t);
					}
            	}
		} catch (JSONException e) {
			e.printStackTrace();			
		}
		System.out.println("==>return list:"+list.toString());
		return list;
	}
}

