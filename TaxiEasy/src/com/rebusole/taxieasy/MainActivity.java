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
//	��֤��Կ
	 private final static String ACCESS_KEY = "PihSG8L27IgQYODlIsSXX6ne";
	 private boolean mIsEngineInitSuccess = false;
//
	 private String Tag="taxi";
	private String mcity=null;
	private Context context;//context1;
	//��߲˵���
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	 private ArrayList<HashMap<String, Object>> listItems;   //������֡�ͼƬ��Ϣ  
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
	private Marker LastMarker=null;//���Ƶ����ɫ�任
	private Marker preMarker=null;//���Ƴ���ֻ��ʾһ��
	private InfoWindow mInfoWindow;
	private String fiveProbability;
	private String tenProbability;//�򳵸���
	
	// ��λ���
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
    //��ͼ��ʼ��
  	private void initView()
  	{
  		 setContentView(R.layout.drawerlayout);
  		 this.context = this;
         btn_takeTaxi=(Button) findViewById(R.id.btn_takeTaxi);
         btn_takeTaxi.setOnClickListener(ButtonOnClickListener);
         imagebtn_takeTaxi=(ImageButton) findViewById(R.id.btn_myloc);       
         imagebtn_takeTaxi.setOnClickListener(ButtonOnClickListener);
         
  	//У��ٶȵ���key
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                mNaviEngineInitListener, new LBSAuthManagerListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        String str = null;
                        if (0 == status) {
                            str = "keyУ��ɹ�!";
                        } else {
                            str = "keyУ��ʧ��, " + msg;
                        }
                        Toast.makeText(MainActivity.this, str,
                                Toast.LENGTH_LONG).show();
                    }
                });
  		//��ʼ����ͼ
  		mMapView = (MapView) findViewById(R.id.bmapView);
  		mBaiduMap = mMapView.getMap();

  		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
  		mBaiduMap.animateMapStatus(msu);
  		mBaiduMap.setOnMapLongClickListener(mOnMapLongClickListener);
  		initDrawLayout();
  		initLocation();
  	}
    //��λ
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
  		
////////�˵�
  		private void initDrawLayout(){
  	    	
  	    	
  	    	mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
  	    	mDrawerList=(ListView) findViewById(R.id.left_drawer);
  	    
  	    	 listItems = new ArrayList<HashMap<String, Object>>();     //������֡�ͼƬ��Ϣ  
  	    	  HashMap<String, Object> map = new HashMap<String, Object>();     
  	          map.put("ItemTitle", "PROFILE");     //����  
  	          map.put("ItemImage", R.drawable.user);//ͼƬ     
  	          listItems.add(map);
  	          //
  	    	  HashMap<String, Object> map2 = new HashMap<String, Object>();     

  	          map2.put("ItemTitle", "PAYMENT ");     //����  
  	          map2.put("ItemImage", R.drawable.wallet);//ͼƬ     
  	          listItems.add(map2);  
  	          //
  	    	  HashMap<String, Object> map3 = new HashMap<String, Object>();     

  	          map3.put("ItemTitle", "SHARE");     //����  
  	          map3.put("ItemImage", R.drawable.share);//ͼƬ     
  	          listItems.add(map3);  
  	          //
  	    	  HashMap<String, Object> map4 = new HashMap<String, Object>();     

  	          map4.put("ItemTitle", "SUPPORT");     //����  
  	          map4.put("ItemImage", R.drawable.support);//ͼƬ     
  	          listItems.add(map4);  
  	          ///
  	    	  HashMap<String, Object> map5 = new HashMap<String, Object>();     

  	          map5.put("ItemTitle", "ABOUT");     //����  
  	          map5.put("ItemImage", R.drawable.about);//ͼƬ     
  	          listItems.add(map5);  
  	          /////////////
  	    	  listItemAdapter = new SimpleAdapter(this,listItems,//����Դ      
  	    	             R.layout.drawer_layout_item_1,//ListItem��XML����ʵ��     
  	    	             //��̬������ImageItem��Ӧ������             
  	    	             new String[] {"ItemTitle", "ItemImage"},      
  	    	             //ImageItem��XML�ļ������һ��ImageView,����TextView ID     
  	    	             new int[] {R.id.listview_text, R.id.listview_image}   );  
  	  		mDrawerList.setAdapter(listItemAdapter);

  			mDrawerList.setOnItemClickListener(mOnItemClickListener);
  			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
  					R.drawable.ic_drawer, R.string.drawer_open,
  					R.string.drawer_close) {
  				@Override
  				public void onDrawerOpened(View drawerView) {
  					super.onDrawerOpened(drawerView);
  					getActionBar().setTitle("��ѡ��");
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
  			
  			//����ActionBar��APP ICON�Ĺ���
  			getActionBar().setDisplayHomeAsUpEnabled(true);
  			getActionBar().setHomeButtonEnabled(true);
  	    	
  	    }
 
//�������ݼӵ�
private void initNearPoint(){
	if(firstTimein)
	{
	new Thread(runnable).start();
	cardata = new CarData(mhandler);	
	firstTimein=false;
	}
}

////////////////////////////�����¼�
/////��λButton   Button ������
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

//////�����г���
private void setMarker(ArrayList<Taxi> list){
	Marker mk=null; 
	for (int i = 0; i < list.size(); i++) {
		Taxi taxi=new Taxi();
		taxi = list.get(i);
		//����Markerͼ��  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
			    .fromResource(R.drawable.taxi_icon);
		//����Maker�����  
		LatLng latlng = new LatLng(taxi.getLatitude(), taxi.getLongitude());
		//����MarkerOption�������ڵ�ͼ�����Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(latlng)  
		    .icon(bitmap)
		    .zIndex(9);  //����marker���ڲ㼶
		
		
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
				marker.setTitle("γ�ȣ�" +marker.getPosition().latitude+ "\n����"+ marker.getPosition().longitude);
				
				showPop(marker);//��ʾ����
				
				LastMarker=marker;
				return true;
			}
		});
	}
	
}
//��ʾ���ݵĺ���
private void showPop(final Marker marker) {  
	
	// ����InfoWindowչʾ��view
	View view = LayoutInflater.from(this).inflate(R.layout.pop, null); //�Զ���������״
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
//					"�����ת����ҳ��",
//					Toast.LENGTH_LONG).show();
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, driverInformation.class);
			startActivity(intent);
		}
	});
	// ����������ʾ��InfoWindow�������
	// ����InfoWindow�ĵ���¼�������
	OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
		public void onInfoWindowClick() {
			mBaiduMap.hideInfoWindow();//Ӱ������
		}
	};
	// ����InfoWindow
	mInfoWindow = new InfoWindow(view, pt,-47);
	mBaiduMap.showInfoWindow(mInfoWindow); //��ʾ����

}
/////////////////////��ͼ��������
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
	.position(llm)  //����marker��λ��
	.icon(bitmap)  //����markerͼ��
	.zIndex(9)  //����marker���ڲ㼶
	.draggable(true);  //����������ק
	//��marker��ӵ���ͼ��
	mMarkerA = (Marker) (mBaiduMap.addOverlay(options));
	preMarker=mMarkerA;
	new Thread(countProability).start();
	}
};

//���󸽼�����
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
    protected void succeed(JSONObject jObject) { //�Լ�����ɹ���Ĳ���  
        super.succeed(jObject);		        
    } //Ҳ����������дstart() failed()����  
	protected void otherHandleMessage(Message message){
		//���������ﴦ��
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
//����ǰ��Ĵ򳵸���
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
    protected void succeed(JSONObject jObject) { //�Լ�����ɹ���Ĳ���  
        super.succeed(jObject);		        
    } //Ҳ����������дstart() failed()����  
	protected void otherHandleMessage(Message message){
		DecimalFormat df = new DecimalFormat("#.00");
		//���������ﴦ��
    	if(message.obj!=null)
    	{
			    try {
					JSONObject json = new JSONObject(message.obj.toString());
					fiveProbability=df.format(json.getDouble("fiveProbability")*100);
					tenProbability=df.format(json.getDouble("tenProbability")*100);
					System.out.println("==>fiveProbability"+fiveProbability);
					System.out.println("==>tenProbability"+tenProbability);
					showAlertDialog("�˵صĴ򵽳����ʣ�\n5���ӣ�"+fiveProbability+"%\n10���ӣ�"+tenProbability+"%");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
	}
}; 
    //////////����
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
				Toast.makeText(MainActivity.this, "keyУ��ɹ�", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onVerifyFailed(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "keyУ��ʧ��", Toast.LENGTH_LONG).show();
			}
		};
    /////////��߲˵�
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
  
	
	
	///////////��ק����
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
//	//������ʾ��
	public void showAlertDialog(String msg) {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setMessage(msg);
		builder.setTitle("��ʾ");
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//������Ĳ�������
			}
		});

		builder.setNegativeButton("ȡ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}
    ///////�Զ��庯��
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
    
    
    //////////����ͼ
    private void heat(){
    	//���ý�����ɫֵ
    	int[] DEFAULT_GRADIENT_COLORS = {Color.rgb(102, 225,  0), Color.rgb(255, 0, 0) };
    	//���ý�����ɫ��ʼֵ
    	float[] DEFAULT_GRADIENT_START_POINTS = { 0.2f, 1f };
    	//������ɫ�������
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
    //�ڵ�ͼ���������ͼ
    mBaiduMap.addHeatMap(heatmap);
    }
 
    ////////�����ͼ
    public void clearOverlay() {
		mBaiduMap.clear();
	}

	/////////////////��ͼ�����¼�
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	Log.d(Tag,"onStart");
    	super.onStart();
    	// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted())
			mLocationClient.start();
    }
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(Tag, "onStop");  
		super.onStop();
		// ֹͣ��λ
				mBaiduMap.setMyLocationEnabled(false);
				mLocationClient.stop();
	}
    @Override  
    protected void onDestroy() { 
    	Log.d(Tag, "onDestroy");
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
   
    @Override  
    protected void onResume() {  
    	Log.d(Tag, "onResume"); 
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    protected void onPause() {  
    Log.d(Tag, "onPause");
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
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
		//��Ҫ��ActionDrawerToggle��DrawerLayout��״̬ͬ��
		//��ActionBarDrawerToggle�е�drawerͼ�꣬����ΪActionBar�е�Home-Button��Icon
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
		//��ActionBar�ϵ�ͼ����Drawer�������
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
				item.setTitle("ʵʱ��ͨ(off)");
			} else
			{
				mBaiduMap.setTrafficEnabled(true);
				item.setTitle("ʵʱ��ͨ(on)");
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
    //�ض�λ
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
			// �����Զ���ͼ��
	

			// ���¾�γ��
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
