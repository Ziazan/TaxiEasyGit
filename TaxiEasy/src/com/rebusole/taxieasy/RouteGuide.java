package com.rebusole.taxieasy;

import java.util.ArrayList;
import java.util.List;


import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RouteGuide extends Activity {

	 private BNaviPoint mStartPoint = new BNaviPoint(116.30142, 40.05087,
	            "�ٶȴ���", BNaviPoint.CoordinateType.GCJ02);
	    private BNaviPoint mEndPoint = new BNaviPoint(116.39750, 39.90882,
	            "�����찲��", BNaviPoint.CoordinateType.GCJ02);
	    private List<BNaviPoint> mViaPoints = new ArrayList<BNaviPoint>();
	    private Button mBtnAddViaPoint;
	    
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_routeguide);
			Button btnStartNavigation = (Button)findViewById(R.id.button_navigation);
			btnStartNavigation.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				    if (mViaPoints.size() == 0) {
				        launchNavigator();
				    } else {
				        launchNavigatorViaPoints();
				    }
				}
			});
			
			findViewById(R.id.start_nav2_btn).setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	                if (mViaPoints.size() == 0) {
	                    launchNavigator2();
	                } else {
	                    launchNavigatorViaPoints();
	                }
	            }
	        });
			
			mBtnAddViaPoint = (Button) findViewById(R.id.add_via_btn); 
			mBtnAddViaPoint.setOnClickListener(new OnClickListener() {

	                @Override
	                public void onClick(View arg0) {
	                    addViaPoint();
	                }
	            });
		}

	    private void addViaPoint() {
	        EditText viaXEditText = (EditText) findViewById(R.id.et_via_x);
	        EditText viaYEditText = (EditText) findViewById(R.id.et_via_y);
	        double latitude = 0, longitude = 0;
	        try {
	            latitude = Integer.parseInt(viaXEditText.getText().toString());
	            longitude = Integer.parseInt(viaYEditText.getText().toString());
	        } catch (NumberFormatException e) {
	            e.printStackTrace();
	        }
	        // Ĭ������ϵΪGCJ
	        BNaviPoint viaPoint = new BNaviPoint(longitude/1e5, latitude/1e5,
	                ";����" + (mViaPoints.size()+1));
	        mViaPoints.add(viaPoint);
	        Toast.makeText(this, "�����;���㣺" + viaPoint.getName(),
	                Toast.LENGTH_SHORT).show();
	        if (mViaPoints.size() >= 3) {
	            mBtnAddViaPoint.setEnabled(false);
	        }
	    }

		/**
		 * ����GPS����. ǰ�����������������ʼ���ɹ�
		 */
		private void launchNavigator(){
			//�������һ�����յ�ʾ����ʵ��Ӧ���п���ͨ��POI�������ⲿPOI��Դ�ȷ�ʽ��ȡ���յ�����
			BaiduNaviManager.getInstance().launchNavigator(this,
					40.05087, 116.30142,"�ٶȴ���", 
			        39.90882, 116.39750,"�����찲��",
					NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME, 		 //��·��ʽ
					true, 									   		 //��ʵ����
					BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //�����߲���
					new OnStartNavigationListener() {				 //��ת����
						
						@Override
						public void onJumpToNavigator(Bundle configParams) {
						//	Intent intent = new Intent(RouteGuide.this, BNavigator.class);
						//	intent.putExtras(configParams);
					   //     startActivity(intent);
						}
						
						@Override
						public void onJumpToDownloader() {
						}
					});
		}
		
	    /**
	     * ָ���������յ�����GPS����.���յ��Ϊ������������ϵ�ĵ������ꡣ
	     * ǰ�����������������ʼ���ɹ�
	     */
	    private void launchNavigator2(){
	        //�������һ�����յ�ʾ����ʵ��Ӧ���п���ͨ��POI�������ⲿPOI��Դ�ȷ�ʽ��ȡ���յ�����
	        BNaviPoint startPoint = new BNaviPoint(116.307854,40.055878,
	                "�ٶȴ���", BNaviPoint.CoordinateType.BD09_MC);
	        BNaviPoint endPoint = new BNaviPoint(116.403875,39.915168,
	                "�����찲��", BNaviPoint.CoordinateType.BD09_MC);
	        BaiduNaviManager.getInstance().launchNavigator(this,
	                startPoint,                                      //��㣨��ָ������ϵ��
	                endPoint,                                        //�յ㣨��ָ������ϵ��
	                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //��·��ʽ
	                true,                                            //��ʵ����
	                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //�����߲���
	                new OnStartNavigationListener() {                //��ת����
	                    
	                    @Override
	                    public void onJumpToNavigator(Bundle configParams) {
	                //        Intent intent = new Intent(RouteGuide.this, BNavigator.class);
	                //        intent.putExtras(configParams);
	                //        startActivity(intent);
	                    }
	                    
	                    @Override
	                    public void onJumpToDownloader() {
	                    }
	                });
	    }

	    /**
	     * ����һ������;���㣬����GPS����. 
	     * ǰ�����������������ʼ���ɹ�
	     */
	    private void launchNavigatorViaPoints(){
	        //�������һ�����յ�ʾ����ʵ��Ӧ���п���ͨ��POI�������ⲿPOI��Դ�ȷ�ʽ��ȡ���յ�����
//	        BNaviPoint startPoint = new BNaviPoint(113.97348, 22.53951, "��ʯ��");
//	        BNaviPoint endPoint   = new BNaviPoint(113.92576, 22.48876, "�߿�");
//	        BNaviPoint viaPoint1 = new BNaviPoint(113.94104, 22.54343, "����ͨ�Ŵ���");
//	        BNaviPoint viaPoint2 = new BNaviPoint(113.94863, 22.53873, "�й����пƼ�԰֧��");
	        List<BNaviPoint> points = new ArrayList<BNaviPoint>();
	        points.add(mStartPoint);
	        points.addAll(mViaPoints);
	        points.add(mEndPoint);
	        BaiduNaviManager.getInstance().launchNavigator(this,
	                points,                                          //·�ߵ��б�
	                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //��·��ʽ
	                true,                                            //��ʵ����
	                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //�����߲���
	                new OnStartNavigationListener() {                //��ת����
	                    
	                    @Override
	                    public void onJumpToNavigator(Bundle configParams) {
	              //          Intent intent = new Intent(RouteGuide.this, BNavigator.class);
	               //         intent.putExtras(configParams);
	               //         startActivity(intent);
	                    }
	                    
	                    @Override
	                    public void onJumpToDownloader() {
	                    }
	                });
	    }
	}
