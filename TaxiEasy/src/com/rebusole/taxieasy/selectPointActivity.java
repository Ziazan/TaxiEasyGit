package com.rebusole.taxieasy;

import android.app.Activity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class selectPointActivity extends Activity implements OnClickListener {
	 private String Tag="taxi";

	private Button btn_GoNavi,btn_myloc;
	EditText edt_startLat,edt_startLong,edt_endLat,edt_endLong;
	//////marker
	BitmapDescriptor mCurrentMarker;
	private Marker mMarkerA;
	//////
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private Context context;
	// 定位相关
		private LocationClient mLocationClient;
		private MyLocationListener mLocationListener;
		private boolean isFirstLoc = true;
		private double mLatitude;
		private double mLongitude;
		int randomPlatnum,random_Lat,random_Long;
		double start_Longitude,start_Latitude,end_Longitude,end_Latitude;
		
		String url="192.168.251.3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());  
      setContentView(R.layout.select_point_activity);
        //获取地图控件引用  
       this.context = this;      
        initView();
    }
    ////////////////////////////监听事件
 /////定位Button   Button 监听器

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_GoNavi:
			Intent it=new Intent(selectPointActivity.this,TaxiTake.class);
			it.putExtra("start_Latitude", start_Latitude);
			it.putExtra("start_Longitude", start_Longitude);
			it.putExtra("end_Latitude", end_Latitude);
			it.putExtra("end_Longitude", end_Longitude);
			System.out.println("==>mLatitude"+mLatitude);
		    System.out.println("==>mLongitude"+mLongitude);
			startActivity(it);
			break;
case R.id.btn_myloc:
	      centerToMyLocation();
			break;

		default:
			break;
		}
		
	}
	/////////////////////地图长按监听
	 OnMapLongClickListener mOnMapLongClickListener = new OnMapLongClickListener() {
		
		@Override
		public void onMapLongClick(LatLng latLng) {
			// TODO Auto-generated method stub
			      // clearOverlay();
			    	BitmapDescriptor bdA = BitmapDescriptorFactory
		    		.fromResource(R.drawable.icon_marka);		        
		        	clearOverlay();
		        	end_Latitude=latLng.latitude;
		        	end_Longitude=latLng.longitude;
		        	LatLng llm = new LatLng(end_Latitude, end_Longitude);
		        	new MarkerOptions().position(llm).icon(bdA)
		    				.zIndex(9).draggable(true);
		        	OverlayOptions options = new MarkerOptions()
		            .position(llm)  //设置marker的位置
		            .icon(bdA)  //设置marker图标
		            .zIndex(9)  //设置marker所在层级
		           .draggable(true);  //设置手势拖拽
		       //将marker添加到地图上
		        	mMarkerA = (Marker) (mBaiduMap.addOverlay(options));
		        	Toast.makeText(
							selectPointActivity.this,
							"拖拽结束，新位置：" + latLng.latitude + ", "
									+ latLng.longitude,
							Toast.LENGTH_LONG).show();
					edt_endLat.setText(""+end_Latitude);
					edt_endLong.setText(""+end_Longitude);
		}
	};
	/////////////// marker 监听
	  OnMarkerClickListener mOnMarkerClickListener =new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {
			// TODO Auto-generated method stub
			//return false;
			mMarkerA.remove();
			
			mBaiduMap.hideInfoWindow();
			Toast.makeText(
					selectPointActivity.this,
					"位置：" + marker.getPosition().latitude + ", "
							+ marker.getPosition().longitude,
					Toast.LENGTH_LONG).show();
		
			return true;
		}
	};

    ////////清除地图
    public void clearOverlay() {
		mBaiduMap.clear();
	}
 
//////////////////绘制地图
	private void initView()
	{
		start_Longitude=(int) (this.getIntent().getExtras().getDouble("Longitude")* 1e5);
        start_Latitude=(int) (this.getIntent().getExtras().getDouble("Latitude")* 1e5);
        System.out.println("==>start_Longitude"+start_Longitude);
        System.out.println("==>start_Latitude"+start_Latitude);
		 btn_GoNavi=(Button) findViewById(R.id.btn_GoNavi);
		 btn_GoNavi.setOnClickListener(this);
	        btn_myloc=(Button) findViewById(R.id.btn_myloc);
	        btn_myloc.setOnClickListener(this);
	        edt_startLat =(EditText) findViewById(R.id.edt_startLat);
	        edt_startLong =(EditText) findViewById(R.id.edt_startLong);
	        edt_endLat =(EditText) findViewById(R.id.edt_endLat);
	        edt_endLong =(EditText) findViewById(R.id.edt_endLong);
	        edt_startLat.setText(""+start_Latitude);   
	        edt_startLong.setText(""+start_Longitude); 
	    	//初始化地图
	  		mMapView = (MapView) findViewById(R.id.bmapView);
	  		mBaiduMap = mMapView.getMap();
	  		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
	  		mBaiduMap.animateMapStatus(msu);
	  		mBaiduMap.setOnMapLongClickListener(mOnMapLongClickListener);
	  		initLocation();
	}

	   //定位
			private void initLocation() {
				// TODO Auto-generated method stub
				mLocationClient = new LocationClient(this);
				mLocationListener = new MyLocationListener();
				mLocationClient.registerLocationListener(mLocationListener);
				mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						LocationMode.NORMAL, true, null));
				LocationClientOption option = new LocationClientOption();
				option.setCoorType("bd09ll");
				option.setIsNeedAddress(true);
				option.setOpenGps(true);
				option.setScanSpan(1000);
				mLocationClient.setLocOption(option);
				mLocationClient.start();
			}
	
	/////////////////地图监听事件
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	Log.d(Tag,"onStart");
    	super.onStart();
    	// 开启定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		centerToMyLocation();
    }
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(Tag, "onStop");  
		super.onStop();
		// 停止定位
				mBaiduMap.setMyLocationEnabled(false);
				mLocationClient.stop();
	}
    @Override  
    protected void onDestroy() { 
    	Log.d(Tag, "onDestroy");
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    

    @Override  
    protected void onResume() {  
    	Log.d(Tag, "onResume"); 
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
    Log.d(Tag, "onPause");
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
  
    }
    protected void onRestart() {  
        // TODO Auto-generated method stub  
        super.onRestart();  
        
        Log.d(Tag, "Restart");  
    }  

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//需要将ActionDrawerToggle与DrawerLayout的状态同步
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onBackPressed() {
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myUid());
	}

    //重定位
	private void centerToMyLocation()
	{
		if(mLatitude==0.0&&mLongitude==0.0)
		{
			mLatitude=30.444089;
			mLongitude=114.27109;
		}
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				mLatitude=location.getLatitude();
				mLongitude=location.getLongitude();
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}


}
