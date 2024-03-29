package com.robotcontrol;

import java.util.Date;


import com.robotcontrol.R;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RobotControl extends Activity implements OnTouchListener {// ���������� ��� Activity ����� ��������� ��������� OnTouchListener
	private static final int REQUEST_CODE = 10;
	private ConnectThread blueConnect;
	Button btnConnect;
	TransferData testTransfer;
	byte[] commandByte = new byte[3];
//	Date currentDate = new Date();
	long msec_old;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//setContentView(R.layout.device_list);
		
		Resources res = getResources();
        Drawable shape = res. getDrawable(R.drawable.oval);
        
		btnConnect = (Button) findViewById(R.id.button2);
		LinearLayout ll =(LinearLayout)this.findViewById(R.id.ll);//������� ������ View 
      //  ll.setOnTouchListener(this);// ������������� ������ ����� � �������� ��������� MotionEvent'�� ��� ������ LinearLayout
        ll.setBackgroundDrawable(shape);
        
        
	}

	public void ScanButton_Click(View v) {
		/*Intent ii = new Intent(this, DeviceListActivity.class);
		ii.putExtra("Value1", "This value one for ActivityTwo ");
		ii.putExtra("Value2", "This value two ActivityTwo");
		// Set the request code to any code you like, you can identify the
		// callback via this code
		startActivityForResult(ii, REQUEST_CODE);*/
		
		Intent intent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED && requestCode == REQUEST_CODE) {
			//Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
			if (data.hasExtra("returnKey3")) {
				Toast.makeText(this, data.getExtras().getString("returnKey3"),
						Toast.LENGTH_SHORT).show();
			}
		}
		
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("returnKey1")) {
				Toast.makeText(this, data.getExtras().getString("returnKey1"),
						Toast.LENGTH_SHORT).show();
			}
			if (data.hasExtra("returnKey2")) {
				TextView displayText = (TextView) findViewById(R.id.textView1);
	        	displayText.setText(data.getExtras().getString("returnKey2"));
			}
		}
	}	
	
	
	
	public void ConnectButton_Click(View v) {
		blueConnect = new ConnectThread(DeviceListActivity.deviceSend);
		blueConnect.run();
		
		LinearLayout ll =(LinearLayout)this.findViewById(R.id.ll);//������� ������ View 
        ll.setOnTouchListener(this);// ������������� ������ ����� � �������� ��������� MotionEvent'�� ��� ������ LinearLayout
		
		testTransfer = new TransferData(blueConnect.ret());

	}
	
	 @Override 
     public boolean onTouch(View v, MotionEvent event)// ���, ����������, �����, ������� � ����� ������������ MotionEvent'�.
 { 
         int Action=event.getAction(); 
         int x;
         int y;
 // � ������� ������ getAction() �������� ��� ��������(ACTION_DOWN,ACTION_MOVE ��� ACTION_UP)
         StringBuilder str=new StringBuilder(); 
         str.append("\nActrion type: "); 
 //������ ��� ������� ����������(�.�. ��������� ACTION_DOWN,ACTION_MOVE � ACTION_UP ��������)
         //�������� switch �� ���������� Action � ��������� � ��� StringBuilder �������� ��������� 
         switch(Action) 
         { 
             case MotionEvent.ACTION_DOWN: str.append("ACTION_DOWN\n");break; 
             case MotionEvent.ACTION_MOVE: str.append("ACTION_MOVE\n");break; 
             case MotionEvent.ACTION_UP: str.append("ACTION_UP\n");break; 
         } 
 //� ������� ������� getX() � getY() �������� ���������� �� ��� x � y �������������� 
         //������� ��������, ��� ����� 0 ������������� � ����� ������� ���� ������. 
         //��� x ���������� ������ 
         //��� y ���������� ����(��� ����, ��� ������ ����������). 
         str.append("Location: ").append(event.getX()).append(" x ").append(event.getY()).append("\n");//������ ���������� 
         str.append("Edge flags: ").append(event.getEdgeFlags()).append("\n");// ����� getEdgeFlags ���������� ���������� � ����������� ����� ������
         str.append("Pressure: ").append(event.getPressure()).append("\n");// ������ �������� 
         str.append("Size: ").append(event.getSize()).append("\n"); // ������ ������ ���������(����� ��������������� ������ � �������)
         str.append("Down time: ").append(event.getDownTime()).append("ms\n");// ������ �����, ����� ����� ��� ������ �� ����� � �������������
         str.append("Event time: ").append(event.getEventTime()).append("ms");//������ ������� �����(��������������� ��������������� MotionEvent'�) � ������������� 
         str.append(" Elapsed: ").append(event.getEventTime()-event.getDownTime());//������ ������� ������� ������ � ������� ��������� ������, �� �������� MotionEvent'�  
         //Log.v("Mytag", str.toString());//��� ����, ����� ����� ���� ����������� ��� ��������, ���������� ��� ���������� � ��� � ���. 
         
         //�������������� ���������
         x = (int)event.getX() - 240;
         if (event.getY() > 240) {
        	 y = 240 - (int)event.getY();
         } else {
        	 y = Math.abs((int)event.getY() - 240);
         }
         
         TextView coordinates = (TextView) findViewById(R.id.textView2);
         //coordinates.setText("X = [" + Float.toString(event.getX()) + "] Y = [" + Float.toString(event.getY()) + "]" );
         coordinates.setText("X = [" + Integer.toString(x) + "] Y = [" + Integer.toString(y) + "]" );
         
         if (MotionEvent.ACTION_MOVE == Action) {
        	 //���� ������ ������ 100 ��
        	 if (delta(10)) {
        		 testTransfer.write(cord(x,y));
        	 }
         } else if (MotionEvent.ACTION_UP == Action) {
        	 //testTransfer.write(cord(x,y));
         } else if (MotionEvent.ACTION_DOWN == Action) {         
        	 testTransfer.write(cord(x,y));
         }  
         return true;// ������ �� ���������� true ����� ����������� ����� 
     }	
	 
	 public boolean delta(long d) {
		 Date currentDate = new Date();
		 long msec_new = currentDate.getTime();
		 if (msec_new - msec_old > d) {
			 msec_old = msec_new;
			 //Log.v("nikTag_true", Long.toString(msec_new));
			 return true;
		 } else {
			 //Log.v("nikTag_false", Long.toString(msec_new));
			return false;
		 }
	 }
	 
	 
	public byte[] cord(float x, float y) {
		byte[] commandByte2 = new byte[5];
		int speed = 0;
		int steer = 0;
		int maxSpeed = 90;
		int lSpeed = 0;
		int rSpeed = 0;
		int lDir = 0;
		int rDir = 0;
		final int deadX = 55;
		final int deadY = 35;

		if (x > 240) {
			x = 240;
		}
		if (x < -240) {
			x = -240;
		}
		if (y > 240) {
			y = 240;
		}
		if (y < -240) {
			y = -240;
		}

		speed = (int) ((y / 240) * 254); // to -254..254
		steer = (int) ((-x / 240) * 126); // to -126..126

		speed = applyDeadZone(speed, deadY);
		steer = applyDeadZone(steer, deadX);
		if (speed > maxSpeed) {
			speed = maxSpeed;
		}
		if (speed < -maxSpeed) {
			speed = -maxSpeed;
		}

		if (speed > 0) {
			// Forward
			// Left/Right turn
			lSpeed = speed - steer;
			rSpeed = speed + steer;
			if (lSpeed < 0) {
				lSpeed = 0;
			}
			if (rSpeed < 0) {
				rSpeed = 0;
			}
			if (lSpeed > maxSpeed) {
				lSpeed = maxSpeed;
			}
			if (rSpeed > maxSpeed) {
				rSpeed = maxSpeed;
			}
		} else /*if (0 == speed) {
			//nothing
		} else */ {
			// Backward
			// Left/Right turn
			lSpeed = speed + steer;
			rSpeed = speed - steer;
			if (lSpeed > 0) {
				lSpeed = 0;
			}
			if (rSpeed > 0) {
				rSpeed = 0;
			}
			if (lSpeed < (-maxSpeed)) {
				lSpeed = -maxSpeed;
			}
			if (rSpeed < (-maxSpeed)) {
				rSpeed = -maxSpeed;
			}
		}
		

		  //prepare rDir, lDir data based on tracks speed
		  if (0 == lSpeed) {
			  lDir = 0; //stop
		  } else if ((lSpeed >= 1)&&(lSpeed <= 254)) {
			  lDir = 1; //forward
		  } else if ((lSpeed >= -254)&&(lSpeed <= -1)) {
			  lDir = 2; //backward
		  }
		  
		  if (0 == rSpeed) {
			  rDir = 0; //stop
		  } else if ((rSpeed >= 1)&&(rSpeed <= 254)) {
			  rDir = 1; //forward
		  } else if ((rSpeed >= -254)&&(rSpeed <= -1)) {
			  rDir = 2; //backward
		  }
		 
		commandByte2[0] = (byte) (255);
		commandByte2[1] = (byte) (lDir);
	    commandByte2[2] = (byte) (rDir);
		commandByte2[3] = (byte) (Math.abs(lSpeed));
		commandByte2[4] = (byte) (Math.abs(rSpeed));

		
		return commandByte2;
	}
	 
	public int applyDeadZone(int param, int dead) {
	  if (Math.abs(param) < dead) {
		  return 0;
	  } else {
		  return param;
	  }
	}
	
	
}