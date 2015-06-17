package com.rebusole.taxieasy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
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
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
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
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.util.verify.BNKeyVerifyListener;
import com.example.bean.Taxi;
import com.example.util.CarData;
import com.example.util.CustomDialog;
import com.example.util.HttpConnectionUtils;
import com.example.util.HttpHandler;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends Activity {
//	验证密钥
	 private final static String ACCESS_KEY = "PihSG8L27IgQYODlIsSXX6ne";
	 private boolean mIsEngineInitSuccess = false;
//
	 private String Tag="taxi";
	private String mcity=null;
	private Context context;//context1;
	//侧边菜单栏
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	 private ArrayList<HashMap<String, Object>> listItems;   //存放文字、图片信息  
	    private SimpleAdapter listItemAdapter;  
	private ArrayList<String> menuLists;
	private ArrayAdapter<String> adapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;
	private ImageButton imagebtn_takeTaxi;
	private Button btn_takeTaxi;
	//private Button button_marker;
	//private Button button_heat;
	
	//////marker
	BitmapDescriptor mCurrentMarker;
	private Marker mMarkerA;
	private MapView mMapView;
	private LocationMode mCurrentMode=LocationMode.NORMAL;
	private BaiduMap mBaiduMap;
	private HeatMap heatmap=null;
	
	//zzf
	private boolean firstTimein=true;
	private Taxi mTaxi=new Taxi();
	private ArrayList<Taxi> mList;
	private  CarData cardata;
	String url="192.168.253.3";
	private Marker LastMarker=null;//控制点击颜色变换
	private Marker preMarker=null;//控制长按只显示一个
	private InfoWindow mInfoWindow;
	private String fiveProbability;
	private String tenProbability;//打车概率
	
	// 定位相关
		private LocationClient mLocationClient;
		private MyLocationListener mLocationListener;
		private boolean isFirstIn = true;
		private double mLatitude;
		private double mLongitude;
		double nowlat,nowlong;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());  

        initView();
        initLocation();
        initDrawLayout();
        initNearPoint();
        mTitle = (String) getTitle();
        
    }
    //视图初始化
  	private void initView()
  	{
  		 setContentView(R.layout.drawerlayout);
  		 this.context = this;
         btn_takeTaxi=(Button) findViewById(R.id.btn_takeTaxi);
         btn_takeTaxi.setOnClickListener(ButtonOnClickListener);
         imagebtn_takeTaxi=(ImageButton) findViewById(R.id.btn_myloc);       
         imagebtn_takeTaxi.setOnClickListener(ButtonOnClickListener);
         
  	//校验百度导航key
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String str = null;
                        if (0 == status) {
                            str = "key校验成功!";
                        } else {
                            str = "key校验失败, " + msg;
                        }
                        Toast.makeText(MainActivity.this, str,
                                Toast.LENGTH_LONG).show();
                    }
                });
  		//初始化地图
  		mMapView = (MapView) findViewById(R.id.bmapView);
  		mBaiduMap = mMapView.getMap();

  		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
  		mBaiduMap.animateMapStatus(msu);
  		mBaiduMap.setOnMapLongClickListener(mOnMapLongClickListener);
  		initDrawLayout();
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
  					mCurrentMode, true, null));
  			LocationClientOption option = new LocationClientOption();
  			option.setCoorType("bd09ll");
  			option.setIsNeedAddress(true);
  			option.setOpenGps(true);
  			option.setScanSpan(1000);
  			mLocationClient.setLocOption(option);
  		}
  		
////////菜单
  		private void initDrawLayout(){
  	    	
  	    	
  	    	mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
  	    	mDrawerList=(ListView) findViewById(R.id.left_drawer);
  	    
  	    	 listItems = new ArrayList<HashMap<String, Object>>();     //存放文字、图片信息  
  	    	  HashMap<String, Object> map = new HashMap<String, Object>();     
  	          map.put("ItemTitle", "PROFILE");     //文字  
  	          map.put("ItemImage", R.drawable.user);//图片     
  	          listItems.add(map);
  	          //
  	    	  HashMap<String, Object> map2 = new HashMap<String, Object>();     

  	          map2.put("ItemTitle", "PAYMENT ");     //文字  
  	          map2.put("ItemImage", R.drawable.wallet);//图片     
  	          listItems.add(map2);  
  	          //
  	    	  HashMap<String, Object> map3 = new HashMap<String, Object>();     

  	          map3.put("ItemTitle", "SHARE");     //文字  
  	          map3.put("ItemImage", R.drawable.share);//图片     
  	          listItems.add(map3);  
  	          //
  	    	  HashMap<String, Object> map4 = new HashMap<String, Object>();     

  	          map4.put("ItemTitle", "SUPPORT");     //文字  
  	          map4.put("ItemImage", R.drawable.support);//图片     
  	          listItems.add(map4);  
  	          ///
  	    	  HashMap<String, Object> map5 = new HashMap<String, Object>();     

  	          map5.put("ItemTitle", "ABOUT");     //文字  
  	          map5.put("ItemImage", R.drawable.about);//图片     
  	          listItems.add(map5);  
  	          /////////////
  	    	  listItemAdapter = new SimpleAdapter(this,listItems,//数据源      
  	    	             R.layout.drawer_layout_item_1,//ListItem的XML布局实现     
  	    	             //动态数组与ImageItem对应的子项             
  	    	             new String[] {"ItemTitle", "ItemImage"},      
  	    	             //ImageItem的XML文件里面的一个ImageView,两个TextView ID     
  	    	             new int[] {R.id.listview_text, R.id.listview_image}   );  
  	  		mDrawerList.setAdapter(listItemAdapter);

  			mDrawerList.setOnItemClickListener(mOnItemClickListener);
  			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
  					R.drawable.ic_drawer, R.string.drawer_open,
  					R.string.drawer_close) {
  				@Override
  				public void onDrawerOpened(View drawerView) {
  					super.onDrawerOpened(drawerView);
  					getActionBar().setTitle("请选择");
  					invalidateOptionsMenu(); // Call onPrepareOptionsMenu()
  				}

  				@Override
  				public void onDrawerClosed(View drawerView) {
  					super.onDrawerClosed(drawerView);
  					getActionBar().setTitle(mTitle);
  					invalidateOptionsMenu();
  				}
  			};
  		mDrawerLayout.setDrawerListener(mDrawerToggle);
  			
  			//开启ActionBar上APP ICON的功能
  			getActionBar().setDisplayHomeAsUpEnabled(true);
  			getActionBar().setHomeButtonEnabled(true);
  	    	
  	    }
 
//请求数据加点
private void initNearPoint(){
	if(firstTimein)
	{
	new Thread(runnable).start();
	cardata = new CarData(mhandler);	
	firstTimein=false;
	}
}

////////////////////////////监听事件
/////定位Button   Button 监听器
  OnClickListener ButtonOnClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) {
			case R.id.btn_myloc:
				centerToMyLocation();
				break;
			case R.id.btn_takeTaxi:
				Intent intentTaxi=new Intent(MainActivity.this,selectPointActivity.class);
				 intentTaxi.putExtra("Latitude", mLatitude);
				 intentTaxi.putExtra("Longitude", mLongitude);
				 System.out.println("==>mLatitude"+mLatitude);
			        System.out.println("==>mLongitude"+mLongitude);
	            startActivity(intentTaxi); 
	            break;
			default:
				break;
			}
		}
	};

//////标所有车辆
private void setMarker(ArrayList<Taxi> list){
	Marker mk=null; 
	for (int i = 0; i < list.size(); i++) {
		Taxi taxi=new Taxi();
		taxi = list.get(i);
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    .fromResource(R.drawable.taxi_icon);
		//定义Maker坐标点  
		LatLng latlng = new LatLng(taxi.getLatitude(), taxi.getLongitude());
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(latlng)  
		    .icon(bitmap)
		    .zIndex(9);  //设置marker所在层级
		
		
		mk = (Marker) (mBaiduMap.addOverlay(option));
		
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener(){
			
			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub
				if(LastMarker!=null)
				{
					BitmapDescriptor bitmap = BitmapDescriptorFactory
				    		.fromResource(R.drawable.taxi_icon);
					LastMarker.setIcon(bitmap);
				}
				BitmapDescriptor bitmap = BitmapDescriptorFactory
			    		.fromResource(R.drawable.taxi_icon_focus);
				marker.setIcon(bitmap);
				marker.setTitle("纬度：" +marker.getPosition().latitude+ "\n经度"+ marker.getPosition().longitude);
				
				showPop(marker);//显示气泡
				
				LastMarker=marker;
				return true;
			}
		});
	}
	
}
//显示气泡的函数
private void showPop(final Marker marker) {  
	
	// 创建InfoWindow展示的view
	View view = LayoutInflater.from(this).inflate(R.layout.pop, null); //自定义气泡形状
	Button	btn_pop = (Button) view.findViewById(R.id.bt_pop);
	LatLng pt = null;
	double latitude, longitude;
	latitude = marker.getPosition().latitude;
	longitude = marker.getPosition().longitude;
	pt = new LatLng(latitude + 0.0004, longitude + 0.00005);
	btn_pop.setText(marker.getTitle());
	btn_pop.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
//			Toast.makeText(
//					MainActivity.this,
//					"点击跳转到打车页面",
//					Toast.LENGTH_LONG).show();
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, driverInformation.class);
			startActivity(intent);
		}
	});
	// 定义用于显示该InfoWindow的坐标点
	// 创建InfoWindow的点击事件监听者
	OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
		public void onInfoWindowClick() {
			mBaiduMap.hideInfoWindow();//影藏气泡
		}
	};
	// 创建InfoWindow
	mInfoWindow = new InfoWindow(view, pt,-47);
	mBaiduMap.showInfoWindow(mInfoWindow); //显示气泡

}
/////////////////////地图长按监听
OnMapLongClickListener mOnMapLongClickListener = new OnMapLongClickListener() {
	@Override
	public void onMapLongClick(LatLng latLng) {
	// TODO Auto-generated method stub

		if(preMarker!=null)
		{
			preMarker.remove();
		}
	BitmapDescriptor bitmap = BitmapDescriptorFactory
	.fromResource(R.drawable.icon_marka);		        
	//clearOverlay();
	double latitude=latLng.latitude;
	double longitude=latLng.longitude;
	nowlat=latitude;
	nowlong=longitude;
	LatLng llm = new LatLng(latitude, longitude);
	OverlayOptions oo = new MarkerOptions().position(llm).icon(bitmap)
			.zIndex(9).draggable(true);
	OverlayOptions options = new MarkerOptions()
	.position(llm)  //设置marker的位置
	.icon(bitmap)  //设置marker图标
	.zIndex(9)  //设置marker所在层级
	.draggable(true);  //设置手势拖拽
	//将marker添加到地图上
	mMarkerA = (Marker) (mBaiduMap.addOverlay(options));
	preMarker=mMarkerA;
	new Thread(countProability).start();
	}
};

//请求附件车辆
Runnable runnable=new Runnable() {
	@Override
	public void run() {			
		while(true){
	    	try {
	    		Thread.sleep(1000);
	    		 ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  
				 params.add(new BasicNameValuePair("latitude",""+mLatitude));  
				 params.add(new BasicNameValuePair("longitude", ""+mLongitude));
				 System.out.println("==>mLatitude"+mLatitude);
				 System.out.println("==>mLongitude"+mLongitude);
				String urlString ="http://"+url+"/callCar/driver/driverLocation.php";   
				new HttpConnectionUtils(mhandler).post(urlString, params);
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
};
private Handler mhandler = new HttpHandler(this) {
    @Override  
    protected void succeed(JSONObject jObject) { //自己处理成功后的操作  
        super.succeed(jObject);		        
    } //也可以在这重写start() failed()方法  
	protected void otherHandleMessage(Message message){
		//数据在这里处理
    	if(message.obj!=null)
    	{   		
    		System.out.println("==>mMessage.obj"+message.obj.toString());
            mList=cardata.getCarData(message.obj.toString());
    		System.out.println("==>mlist"+mList.toString());
		    setMarker(mList);
			System.out.println("==>setMarker(mList);");
    	}
	}
}; 
//请求当前点的打车概率
Runnable countProability=new Runnable() {
	@Override
	public void run() {			
	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();  	    
	    params.add(new BasicNameValuePair("latitude",""+nowlat));  
	    params.add(new BasicNameValuePair("longitude", ""+nowlong));
	    String urlString ="http://"+url+"/callCar/driver/taxiProbability.php";   
	    new HttpConnectionUtils(mhandler2).post(urlString, params);
	}
};
private Handler mhandler2 = new HttpHandler(this) {
    @Override  
    protected void succeed(JSONObject jObject) { //自己处理成功后的操作  
        super.succeed(jObject);		        
    } //也可以在这重写start() failed()方法  
	protected void otherHandleMessage(Message message){
		DecimalFormat df = new DecimalFormat("#.00");
		//数据在这里处理
    	if(message.obj!=null)
    	{
			    try {
					JSONObject json = new JSONObject(message.obj.toString());
					fiveProbability=df.format(json.getDouble("fiveProbability")*100);
					tenProbability=df.format(json.getDouble("tenProbability")*100);
					System.out.println("==>fiveProbability"+fiveProbability);
					System.out.println("==>tenProbability"+tenProbability);
					showAlertDialog("此地的打到车概率：\n5分钟："+fiveProbability+"%\n10分钟："+tenProbability+"%");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
	}
}; 
    //////////导航
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};

    
	  private BNKeyVerifyListener mKeyVerifyListener = new BNKeyVerifyListener() {
			
			@Override
			public void onVerifySucc() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "key校验成功", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onVerifyFailed(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "key校验失败", Toast.LENGTH_LONG).show();
			}
		};
    /////////侧边菜单
    OnItemClickListener mOnItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
		    String text=mDrawerList.getItemAtPosition(position)+"";
			switch(position)
			{
			case 0:
				if(CheckLoginState()==1)
				{ Intent intent=new Intent(MainActivity.this,Information.class);
				startActivity(intent);
				}
				else
				{
					Intent intent=new Intent(MainActivity.this,Login.class);
					startActivity(intent);
				}
				break;
			case 1:
				//mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				Toast.makeText(MainActivity.this,"POSITON"+position+ "TEXT"+text, Toast.LENGTH_SHORT).show();
				Intent intent=new Intent(MainActivity.this,Payment.class);
				startActivity(intent);
				break;
			case 2:
				if (mBaiduMap.isTrafficEnabled())
				{
					mBaiduMap.setTrafficEnabled(false);
				
				} else
				{
					mBaiduMap.setTrafficEnabled(true);
				
				}
				Toast.makeText(MainActivity.this,"POSITON"+position+ "TEXT"+text, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
				Toast.makeText(MainActivity.this,"POSITON"+position+ "TEXT"+text, Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Intent it=new Intent(MainActivity.this,aboutActivity.class);
				startActivity(it);
				break;
				default:
					break;
			
			}
		}
	};
  
	
	
	///////////拖拽监听
//	OnMarkerDragListener mOnMarkerDragListenre = new OnMarkerDragListener() {
//		
//		@Override
//		public void onMarkerDragStart(Marker marker) {
//			// TODO Auto-generated method stub
//			System.out.println("==>onMarkerDragStart");
//		}
//		
//		@Override
//		public void onMarkerDragEnd(Marker marker) {
//			// TODO Auto-generated method stub
//			System.out.println("==>onMarkerDragEnd");
//		}
//		
//		@Override
//		public void onMarkerDrag(Marker arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//	};
//	//弹出提示框
	public void showAlertDialog(String msg) {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(msg);
		builder.setTitle("提示");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//设置你的操作事项
			}
		});

		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}
    ///////自定义函数
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
    
    
    //////////热力图
    private void heat(){
    	//设置渐变颜色值
    	int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225,  0), Color.rgb(255, 0, 0) };
    	//设置渐变颜色起始值
    	float[] DEFAULT_GRADIENT_START_POINTS = { 0.2f, 1f };
    	//构造颜色渐变对象
    	Gradient gradient = new Gradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
    	List<LatLng> randomList = new ArrayList<LatLng>();
    	Random r = new Random();
    	for (int i = 0; i < 500; i++) {
    	    // 116.220000,39.780000 116.570000,40.150000
    	    int rlat = r.nextInt(370000);
    	    int rlng = r.nextInt(370000);
    	   
    	    int lat = 30476972 + rlat;
    	    int lng = 114273560 + rlng;
    	    LatLng ll = new LatLng(lat / 1E6, lng / 1E6);
    	    randomList.add(ll);
    	}
    	HeatMap heatmap = new HeatMap.Builder()
        .data(randomList)
        .gradient(gradient)
        .build();
    //在地图上添加热力图
    mBaiduMap.addHeatMap(heatmap);
    }
 
    ////////清除地图
    public void clearOverlay() {
		mBaiduMap.clear();
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
		//将ActionBarDrawerToggle中的drawer图标，设置为ActionBar中的Home-Button的Icon
		mDrawerToggle.syncState();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	@Override
	public void onBackPressed() {
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myUid());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//将ActionBar上的图标与Drawer结合起来
		if (mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_websearch:
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri uri = Uri.parse("http://www.rebusole.com");
			intent.setData(uri);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    ///menu_slidingmenu
 /* @Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.id_map_common:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			break;

		case R.id.id_map_site:
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			break;

		case R.id.id_map_traffic:
			if (mBaiduMap.isTrafficEnabled())
			{
				mBaiduMap.setTrafficEnabled(false);
				item.setTitle("实时交通(off)");
			} else
			{
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("实时交通(on)");
			}
			break;
		case R.id.id_map_location:
			centerToMyLocation();
			break;
	
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	*/
    //重定位
	private void centerToMyLocation()
	{
		LatLng latLng = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

    private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			MyLocationData data = new MyLocationData.Builder()//
					
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			mBaiduMap.setMyLocationData(data);
			// 设置自定义图标
	

			// 更新经纬度
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			if (isFirstIn)
			{
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
				mBaiduMap.animateMapStatus(msu);
				isFirstIn = false;
				Toast.makeText(context, location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
				  mcity=location.getCity();
			
				
			}

		}
	}

}
