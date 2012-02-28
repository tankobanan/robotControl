package com.robotcontrol;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.ListView;

public class ConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
	private UUID MY_UUID;
	private TransferData testTransfer;
 
    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
        	MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
		// Cancel discovery because it will slow down the connection
       // mBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
        	mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();    
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
       // manageConnectedSocket(mmSocket);
    }
 
  /*  private void manageConnectedSocket(BluetoothSocket mmSocket2) {
    	testTransfer = new TransferData(mmSocket2);
    	byte[] commandByte = new byte[3];
    	commandByte[0] = (byte) (110 & 0xFF);
    	commandByte[1] = (byte) (49 & 0xFF);
    	commandByte[2] = (byte) (49 & 0xFF);
    	testTransfer.write(commandByte); 
	} */
  
    
	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
    
    public BluetoothSocket ret() {
		return mmSocket;
    }
}