package com.robotcontrol;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

public class DeviceListActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 20;
	private BluetoothDevice[] deviceArr;
	private ArrayAdapter<String> mArrayAdapter;
	public static BluetoothDevice deviceSend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		mArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_list, R.id.paired_devices);
		refreshList();
	}


	public void refreshList() {
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(DeviceListActivity.this, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
			return;
		} else if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		}
		
		ListView lv = (ListView) findViewById(R.id.paired_devices);
		
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			String[] names = new String[pairedDevices.size()];
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
			Integer i = 0;
			deviceArr = new BluetoothDevice[pairedDevices.size()];
			// Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
				mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				deviceArr[i] = device;
		        names[i] = device.getName() + "\n" + device.getAddress();
		        i++;
		    }
		    
		    lv.setAdapter(adapter);   
		    
		    lv.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        	deviceSend = deviceArr[position];
		        	finish();
				}
		      }); 
		}	
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_CANCELED && requestCode == REQUEST_ENABLE_BT) {
			//TODO
		}
		if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT) {
			refreshList();
		}
	}
	
	
	public void RefreshButton_Click(View v) {
		refreshList();
	}
	
	public void onClick(View view) {
		finish();
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		
		if (deviceSend != null) {
			intent.putExtra("returnKey1", "Selected " + deviceSend.getName() + " device"); // for Toast
			intent.putExtra("returnKey2", deviceSend.getName() + " [" + deviceSend.getAddress() + "]");
			setResult(RESULT_OK, intent);	
		} else {
			intent.putExtra("returnKey3", "deviceSend is NULL");
			setResult(RESULT_CANCELED, intent);	
		}
		
		super.finish();
	}

}
