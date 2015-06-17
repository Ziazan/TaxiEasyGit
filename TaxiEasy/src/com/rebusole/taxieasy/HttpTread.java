package com.rebusole.taxieasy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Handler;
import android.webkit.WebView;

public class HttpTread  extends Thread{

	
	private String url;
	private WebView webview;
	private Handler handle;
	public HttpTread(String url,WebView webview,Handler handle){
		this.url=url;
		this.webview=webview;
		this.handle=handle;
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//super.run();
		
		try {
			URL httpurl=new URL(url);
			try {
				HttpURLConnection conn=(HttpURLConnection) httpurl.openConnection();
				conn.setReadTimeout(5000);
				conn.setRequestMethod("GET");
				final StringBuffer sb=new StringBuffer();
				BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String str;
				while((str=reader.readLine())!=null){
					sb.append(str);
				}
				handle.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					webview.loadData(sb.toString(), "text/html;charset=utf-8", null);	
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	
}
