package com.rebusole.taxieasy;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams.RGLocationMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.comapi.setting.SettingParams;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.model.RGCacheStatus;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.common.PreferenceHelper;
import com.baidu.navisdk.util.common.ScreenUtil;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;

public class TaxiTake extends Activity {
	private RoutePlanModel mRoutePlanModel = null;
	private MapGLSurfaceView mMapView = null;
	
	int start_Longitude,start_Latitude,end_Latitude,end_Longitude;
     private EditText startXEditText,startYEditText,endXEditText,endYEditText;
     
	
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.taxi_take);
		
		start_Longitude=(int) (this.getIntent().getExtras().getDouble("start_Longitude")* 1e5);
        start_Latitude=(int) (this.getIntent().getExtras().getDouble("start_Latitude")* 1e5);
        end_Latitude=(int) (this.getIntent().getExtras().getDouble("end_Latitude")* 1e5);
        end_Longitude=(int) (this.getIntent().getExtras().getDouble("end_Longitude")* 1e5);
        System.out.println("==>start_Longitude"+start_Longitude);
        System.out.println("==>start_Latitude"+start_Latitude);
         startXEditText = (EditText) findViewById(R.id.et_start_x);
		 startYEditText = (EditText) findViewById(R.id.et_start_y);
		 endXEditText = (EditText) findViewById(R.id.et_end_x);
		 endYEditText = (EditText) findViewById(R.id.et_end_y);
		 startXEditText.setText(""+start_Latitude);
	     startYEditText.setText(""+start_Longitude);
	     startXEditText.setText(""+end_Latitude);
	     startYEditText.setText(""+end_Longitude);
		findViewById(R.id.online_calc_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startCalcRoute(NL_Net_Mode.NL_Net_Mode_OnLine);
			}
		});

		findViewById(R.id.simulate_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startNavi(false);
					}
				});

		findViewById(R.id.real_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PreferenceHelper.getInstance(getApplicationContext())
						.putBoolean(SettingParams.Key.SP_TRACK_LOCATE_GUIDE,
								false);
				startNavi(true);
			}
		});
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		BNRoutePlaner.getInstance().setRouteResultObserver(null);
		((ViewGroup) (findViewById(R.id.mapview_layout))).removeAllViews();
		BNMapController.getInstance().onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		initMapView();
		((ViewGroup) (findViewById(R.id.mapview_layout))).addView(mMapView);
		BNMapController.getInstance().onResume();
	}

    private void initMapView() {
        if (Build.VERSION.SDK_INT < 14) {
            BaiduNaviManager.getInstance().destroyNMapView();
        }
        
        System.out.println(start_Longitude);
         System.out.println(start_Latitude);
       int DmLongtitude=(int)(start_Longitude* 1e5);
        int DmLatitude=(int)(start_Latitude* 1e5);
        mMapView = BaiduNaviManager.getInstance().createNMapView(this);
        BNMapController.getInstance().setLevel(14);
        BNMapController.getInstance().setLayerMode(
                LayerMode.MAP_LAYER_MODE_BROWSE_MAP);
        updateCompassPosition();
        BNMapController.getInstance().locateWithAnimation(
        		DmLongtitude ,DmLatitude );
    }
	
	/**
	 * ����ָ����λ��
	 */
	private void updateCompassPosition(){
		int screenW = this.getResources().getDisplayMetrics().widthPixels;
		BNMapController.getInstance().resetCompassPosition(
				screenW - ScreenUtil.dip2px(this, 30),
					ScreenUtil.dip2px(this, 126), -1);
	}

	private void startCalcRoute(int netmode) {
		//��ȡ���������		
		int sX = 0, sY = 0, eX = 0, eY = 0;
		try {
			sX = Integer.parseInt(startXEditText.getText().toString());
			sY = Integer.parseInt(startYEditText.getText().toString());
			eX = Integer.parseInt(endXEditText.getText().toString());
			eY = Integer.parseInt(endYEditText.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//���
		RoutePlanNode startNode = new RoutePlanNode(sX, sY,
				RoutePlanNode.FROM_MAP_POINT, "�人��", "�人��");
		//�յ�
		RoutePlanNode endNode = new RoutePlanNode(eX, eY,
				RoutePlanNode.FROM_MAP_POINT, "�人��", "�人��");
		//�����յ���ӵ�nodeList
		ArrayList<RoutePlanNode> nodeList = new ArrayList<RoutePlanNode>(2);
		nodeList.add(startNode);
		nodeList.add(endNode);
		BNRoutePlaner.getInstance().setObserver(new RoutePlanObserver(this, null));
		//������·��ʽ
		BNRoutePlaner.getInstance().setCalcMode(NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME);
		// ������·����ص�
		BNRoutePlaner.getInstance().setRouteResultObserver(mRouteResultObserver);
		// �������յ㲢��·
		boolean ret = BNRoutePlaner.getInstance().setPointsToCalcRoute(
				nodeList,NL_Net_Mode.NL_Net_Mode_OnLine);
		if(!ret){
			Toast.makeText(this, "�滮ʧ��", Toast.LENGTH_SHORT).show();
		}
	}

	private void startNavi(boolean isReal) {
		if (mRoutePlanModel == null) {
			Toast.makeText(this, "������·��", Toast.LENGTH_LONG).show();
			return;
		}
		// ��ȡ·�߹滮������
		RoutePlanNode startNode = mRoutePlanModel.getStartNode();
		// ��ȡ·�߹滮����յ�
		RoutePlanNode endNode = mRoutePlanModel.getEndNode();
		if (null == startNode || null == endNode) {
			return;
		}
		// ��ȡ·�߹滮��·ģʽ
		int calcMode = BNRoutePlaner.getInstance().getCalcMode();
		Bundle bundle = new Bundle();
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_VIEW_MODE,
				BNavigator.CONFIG_VIEW_MODE_INFLATE_MAP);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_DONE,
				BNavigator.CONFIG_CLACROUTE_DONE);
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_X,
				startNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_START_Y,
				startNode.getLatitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_X, endNode.getLongitudeE6());
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_END_Y, endNode.getLatitudeE6());
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_START_NAME,
				mRoutePlanModel.getStartName(this, false));
		bundle.putString(BNavConfig.KEY_ROUTEGUIDE_END_NAME,
				mRoutePlanModel.getEndName(this, false));
		bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_CALCROUTE_MODE, calcMode);
		if (!isReal) {
			// ģ�⵼��
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
					RGLocationMode.NE_Locate_Mode_RouteDemoGPS);
		} else {
			// GPS ����
			bundle.putInt(BNavConfig.KEY_ROUTEGUIDE_LOCATE_MODE,
					RGLocationMode.NE_Locate_Mode_GPS);
		}
		
		//Intent intent = new Intent(RoutePlanDemo.this, BNavigatorActivity.class);
		//intent.putExtras(bundle);
		//startActivity(intent);
	}

	private IRouteResultObserver mRouteResultObserver = new IRouteResultObserver() {

		@Override
		public void onRoutePlanYawingSuccess() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanYawingFail() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutePlanSuccess() {
			// TODO Auto-generated method stub
			BNMapController.getInstance().setLayerMode(
					LayerMode.MAP_LAYER_MODE_ROUTE_DETAIL);
			mRoutePlanModel = (RoutePlanModel) NaviDataEngine.getInstance()
					.getModel(ModelName.ROUTE_PLAN);
		}

		@Override
		public void onRoutePlanFail() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanCanceled() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onRoutePlanStart() {
			// TODO Auto-generated method stub

		}

	};
}