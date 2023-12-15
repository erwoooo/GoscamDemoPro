package com.gocam.goscamdemopro.add;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gocam.goscamdemopro.R;
import com.gocam.goscamdemopro.utils.PermissionUtil;
import com.gocam.goscamdemopro.utils.dbg;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.camera.CameraManager;


/**
 *
 * @author wangh
 * @date 2018/11/28
 * 所有权限请求统一在外部请求
 */
public class ZxingCaptureActivity extends AppCompatActivity implements  SensorEventListener {



	Toolbar mToolbar;

	ImageView mIvLed;

	private CaptureFragment mCaptureFragment;
	boolean isInAlbum = false;
	public static final int REQUEST_IMAGE = 112;
	private final String IMAGE_TYPE = "image/*";

//	boolean isFromToolbox=false;//判断是否是从工具箱跳转过来
//	boolean isFromMineFra=false;//判断是否是从个人中心跳转过来

	int comeFrom;

	SensorManager mSensroMgr;

	public static void startActivity(Activity activity, int comeFrom) {
		Intent intent = new Intent(activity, ZxingCaptureActivity.class);

		activity.startActivityForResult(intent,comeFrom);
	}



	private void showToast(String string) {
		Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zx_capture);


		mCaptureFragment = new CaptureFragment();
		CodeUtils.setFragmentArgs(mCaptureFragment, R.layout.zxing_camera);
		mCaptureFragment.setAnalyzeCallback(analyzeCallback);
		getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, mCaptureFragment).commit();


		mSensroMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


		verifyCameraPermissions();


	}

	private void verifyCameraPermissions() {
		PermissionUtil.verifyCameraPermissions(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensroMgr.registerListener(this, mSensroMgr.getDefaultSensor(Sensor.TYPE_LIGHT),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensroMgr.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			float light_strength = event.values[0];
			dbg.D("onSensorChanged","当前光线强度为:"+light_strength);
			if(!userHandleFlashLight){
				Camera camera = CameraManager.get().getCamera();
				if (camera == null) {
					return;
				}
				try{
					Camera.Parameters parameters = camera.getParameters();
					String flashMode = parameters.getFlashMode();
					if(light_strength<=10f && !TextUtils.equals(flashMode,Camera.Parameters.FLASH_MODE_TORCH)){
						parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						camera.setParameters(parameters);
						mIvLed.setImageResource(R.mipmap.icon_light_1);
					}else if(light_strength>10f && TextUtils.equals(flashMode,Camera.Parameters.FLASH_MODE_TORCH)){
						parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						camera.setParameters(parameters);
						mIvLed.setImageResource(R.mipmap.icon_light_2);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}



	boolean userHandleFlashLight;


	public void toggleFlashLight() {
		Camera camera = CameraManager.get().getCamera();
		if (camera == null) {
			return;
		}
		try{
			Camera.Parameters parameters = camera.getParameters();
			String flashMode = parameters.getFlashMode();
			parameters.setFlashMode(TextUtils.equals(flashMode,Camera.Parameters.FLASH_MODE_TORCH) ?
					Camera.Parameters.FLASH_MODE_OFF : Camera.Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameters);
			mIvLed.setImageResource(TextUtils.equals(flashMode,Camera.Parameters.FLASH_MODE_TORCH) ?
					R.mipmap.icon_light_1 : R.mipmap.icon_light_2);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}


	CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
		@Override
		public void onAnalyzeSuccess(Bitmap mBitmap, String result) {

			mCaptureFragment.setAnalyzeCallback(null);
			dbg.D("AnalyzeCallback", "result : " + result);

			//查询绑定状态


		}

		@Override
		public void onAnalyzeFailed() {
			mCaptureFragment.setAnalyzeCallback(null);
			showToast(getString(R.string.scan_failed));
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
