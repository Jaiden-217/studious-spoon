/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.airtops.carbon.plane.fly;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import com.airtops.carbon.plane.adapter.LeDeviceListAdapter;
import com.airtops.carbon.plane.base.BaseActivity;
import com.airtops.carbon.plane.bluetooth.BluetoothLeClass;
import com.airtops.carbon.plane.util.DataValue;
import com.airtops.carbon.plane.util.Utils;
import com.airtops.carbon.plane.R;
/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends BaseActivity {
	private final String TAG = DataValue.TAG;
	private final String DEVICE_NAME = DataValue.DEVICE_NAME;
	private final UUID UUID_KEY_DATA = UUID.fromString(DataValue.UUID);
	private final UUID UUID_RECEIVE_DATA = UUID
			.fromString(DataValue.UUID_RECEIVE);

	private BluetoothAdapter mBluetoothAdapter;
	protected static BluetoothLeClass mBLE;
	private Handler mHandler;
	private BluetoothGattCharacteristic mGattCharacteristic;
	protected LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothDevice mDevice;
	// send rssi
	Intent rssiIntent = new Intent(DataValue.RSSI_ACTION);
	// send link state
	Intent linkIntent = new Intent(DataValue.LINK_ACTION);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mHandler = new Handler();
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		search_ble();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mBluetoothAdapter != null) {
			mLeDeviceListAdapter.clear();
			Log.e("mDebug", "adapter cleared");
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
		}

		mBLE = new BluetoothLeClass(this);
		if (!mBLE.initialize()) {
			finish();
		}

		mLeDeviceListAdapter = new LeDeviceListAdapter(this);

		registerReceiver(dataReceiver, new IntentFilter(DataValue.DATA_ACTION));
		registerReceiver(finishReceiver, new IntentFilter(
				DataValue.BROAD_FINISH));
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DataValue.MSG_SEARCHING:
				search_ble();
				break;
			case DataValue.MSG_NO_BLE_FOUND:
				no_ble_found();
				break;
			case DataValue.MSG_QUIT:
				finish();
				break;
			default:
				break;
			}
		}
	};

	private void no_ble_found() {
		dismissHttpDialog();
		showHttpDialog("无法扫描到匹配设备，请确认设备处于激活状态。程序将于3秒后退出。");
		handler.sendEmptyMessageDelayed(DataValue.MSG_QUIT, 3000);
	}

	private BroadcastReceiver finishReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataValue.BROAD_FINISH)) {
				finish();
			}
		}
	};

	@Override
	protected void onDestroy() {
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
		try {
			mBLE.disconnect();
			mBLE.close();
		} catch (NullPointerException e1) {
			Log.e(TAG, "mBLE is null; "+e1.toString());
		}
		Process.killProcess(Process.myPid());
		super.onDestroy();
	}

	protected void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, DataValue.SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/**
	 * discover callback
	 */
	private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new BluetoothLeClass.OnServiceDiscoverListener() {

		@Override
		public void onServiceDiscover(BluetoothGatt gatt) {
			displayGattServices(mBLE.getSupportedGattServices());
		}

	};

	BluetoothLeClass.OnConnectListener mOnConnectListener = new BluetoothLeClass.OnConnectListener() {
		@Override
		public void onConnect(BluetoothGatt gatt) {
			Log.e(TAG, "onConnect");
		}
	};

	BluetoothLeClass.OnDisconnectListener mOnDisconnectListener = new BluetoothLeClass.OnDisconnectListener() {

		@Override
		public void onDisconnect(BluetoothGatt gatt) {
			Log.e(TAG, "onDisconnect");
			linkIntent.putExtra("link", 0);
			sendBroadcast(linkIntent);
		}
	};

	/**
	 * data exchange callback
	 */
	private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new BluetoothLeClass.OnDataAvailableListener() {

		/**
		 * read callback
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			Log.e(TAG,
					"onCharRead " + gatt.getDevice().getName() + " read "
							+ characteristic.getUuid().toString() + " -> "
							+ Utils.bytesToHexString(characteristic.getValue()));
		}

		/**
		 * write callback
		 */
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.e(TAG, "onCharWrite " + gatt.getDevice().getName() + " write "
					+ characteristic.getUuid().toString() + " -> "
					+ bytesToHexString(characteristic.getValue()));
//				CARBONAPP.CARBON.setHeartBeat(0);
		}
	};
	
	public static String bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }

		for (int i: src ) {
			String hv = Integer.toHexString(i & 0xFF);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * rssi callback,rssi means bluetooth signal strength
	 */
	private BluetoothLeClass.OnReadRssiListener mOnReadRssiListener = new BluetoothLeClass.OnReadRssiListener() {
		@Override
		public void onReadRssi(BluetoothGatt gatt, int rssi) {
			rssiIntent.putExtra("rssi", rssi);
			sendBroadcast(rssiIntent);
		}
	};

	// Device scan callback
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (device.getName().equals(DEVICE_NAME)) {
						mDevice = device;
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						mLeDeviceListAdapter.addDevice(mDevice);
					}
				}
			});
		}
	};

	public void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;

		for (BluetoothGattService gattService : gattServices) {
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				if (gattCharacteristic.getUuid().equals(UUID_KEY_DATA)) {
					mGattCharacteristic = gattCharacteristic;
					// mBLE.readCharacteristic(mGattCharacteristic);
					// mBLE.setCharacteristicNotification(mGattCharacteristic,
					// true);
					for (final BluetoothGattCharacteristic gattCharacteristic2 : gattCharacteristics) {
						if (gattCharacteristic2.getUuid().equals(
								UUID_RECEIVE_DATA)) {
							mBLE.readCharacteristic(gattCharacteristic2);
							mBLE.setCharacteristicNotification(
									gattCharacteristic2, true);
						}
					}
				}
			}
		}
	}

	public void sendMessage(byte[] val) {
		mGattCharacteristic.setValue(val);
		// write data to characteristic
		mBLE.writeCharacteristic(mGattCharacteristic);
		mBLE.getRssi();
	}

	Thread linkThread = new Thread(new Runnable() {

		@Override
		public void run() {
			if (mBLE.connect(mDevice.getAddress())) {
				// 发现BLE终端的Service时回调
				mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);

				// 收到BLE终端数据交互的事件
				mBLE.setOnDataAvailableListener(mOnDataAvailable);

				// 收到rssi回调
				mBLE.setmOnReadRssiListener(mOnReadRssiListener);

				mBLE.setOnConnectListener(mOnConnectListener);

				mBLE.setOnDisconnectListener(mOnDisconnectListener);

				handler.removeMessages(DataValue.MSG_SEARCHING);
				Intent in = new Intent(DeviceScanActivity.this,
						ControllerActivity.class);
				startActivity(in);
			} else {
				search_ble();
			}
		}
	});

	public void search_ble() {
		if (!mBluetoothAdapter.isEnabled()) {
			// open a dialog to enable bluetooth
			Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enabler, 2);
		} else {
			if (mLeDeviceListAdapter.isEmpty()) {
				dismissHttpDialog();
				scanLeDevice(true);
				showHttpDialog("正在扫描设备...");
				handler.sendEmptyMessageDelayed(DataValue.MSG_SEARCHING, 3500);
			} else {
				dismissHttpDialog();
				linkThread.run();
				showHttpDialog("正在连接设备...");
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2) {
			if (!mBluetoothAdapter.isEnabled()) {
				DeviceScanActivity.this.finish();
			}
		}
	}

	private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataValue.DATA_ACTION)) {
				byte[] data = intent.getByteArrayExtra("data");
				if (data == null) {
					return;
				}
				try {
					sendMessage(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	@Override
	public void onBackPressed() {
		finish();
		Log.e(TAG, "Scan activity finished");
	}
}
