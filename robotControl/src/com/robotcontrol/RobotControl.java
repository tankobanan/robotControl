package com.robotcontrol;

import java.util.Date;
import java.util.Set;

import com.robotcontrol.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class RobotControl extends Activity implements OnTouchListener {// Заставляем наш Activity класс воплощать интерфейс OnTouchListener
	private EditText edtext;
	private int REQUEST_ENABLE_BT;
	private ArrayAdapter<String> mArrayAdapter;
	private BluetoothDevice deviceSend;
	private ConnectThread blueConnect;
	private BluetoothDevice[] deviceArr;
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
        
        
		
		mArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_list, R.id.paired_devices);
		btnConnect = (Button) findViewById(R.id.button2);
		LinearLayout ll =(LinearLayout)this.findViewById(R.id.ll);//Достаем нужный View 
      //  ll.setOnTouchListener(this);// Устанавливаем данный класс в качестве слушателя MotionEvent'ов для нашего LinearLayout
        ll.setBackgroundDrawable(shape);
        
        
	}

	public void ScanButton_Click(View v) {
		setContentView(R.layout.device_list);
		ListView lv = (ListView) findViewById(R.id.paired_devices);
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			edtext.setText("Device does not support Bluetooth");
		} else if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

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
		        	setContentView(R.layout.main);		        	
		        	TextView displayText = (TextView) findViewById(R.id.textView1);
		        	displayText.setText(deviceSend.getName() + " [" + deviceSend.getAddress() + "]");
				}
		      }); 
		}	
	}
	
	
	
	

	public void RefreshButton_Click(View v) {
		ScanButton_Click(v);
	}
	
	public void ConnectButton_Click(View v) {
		blueConnect = new ConnectThread(deviceSend);
		blueConnect.run();
		
		LinearLayout ll =(LinearLayout)this.findViewById(R.id.ll);//Достаем нужный View 
        ll.setOnTouchListener(this);// Устанавливаем данный класс в качестве слушателя MotionEvent'ов для нашего LinearLayout
		
		testTransfer = new TransferData(blueConnect.ret());

	}
	
	 @Override 
     public boolean onTouch(View v, MotionEvent event)// Вот, собственно, метод, который и будет обрабатывать MotionEvent'ы.
 { 
         int Action=event.getAction(); 
         int x;
         int y;
 // С помощью метода getAction() получаем тип действия(ACTION_DOWN,ACTION_MOVE или ACTION_UP)
         StringBuilder str=new StringBuilder(); 
         str.append("\nActrion type: "); 
 //Дальше для лучшего восприятия(т.к. константы ACTION_DOWN,ACTION_MOVE и ACTION_UP числовые)
         //проводим switch по переменной Action и добавляем в наш StringBuilder название константы 
         switch(Action) 
         { 
             case MotionEvent.ACTION_DOWN: str.append("ACTION_DOWN\n");break; 
             case MotionEvent.ACTION_MOVE: str.append("ACTION_MOVE\n");break; 
             case MotionEvent.ACTION_UP: str.append("ACTION_UP\n");break; 
         } 
 //С помощью методов getX() и getY() получаем координаты по оси x и y соответственно 
         //Следует отметить, что точка 0 располагается в левом верхнем углу экрана. 
         //Ось x направлена вправо 
         //Ось y направлена вниз(чем ниже, тем больше координата). 
         str.append("Location: ").append(event.getX()).append(" x ").append(event.getY()).append("\n");//Узнаем координаты 
         str.append("Edge flags: ").append(event.getEdgeFlags()).append("\n");// Метод getEdgeFlags возвращает информацию о пересечении краев экрана
         str.append("Pressure: ").append(event.getPressure()).append("\n");// Узнаем давление 
         str.append("Size: ").append(event.getSize()).append("\n"); // Узнаем размер указателя(места соприкосновения пальца с экраном)
         str.append("Down time: ").append(event.getDownTime()).append("ms\n");// Узнаем время, когда палец был опущен на экран в миллисекундах
         str.append("Event time: ").append(event.getEventTime()).append("ms");//узнаем текущее время(соответствующее обрабатываемому MotionEvent'у) в миллисекундах 
         str.append(" Elapsed: ").append(event.getEventTime()-event.getDownTime());//Узнаем сколько времени прошло с момента опускания пальца, до текущего MotionEvent'а  
         //Log.v("Mytag", str.toString());//Для того, чтобы можно было отслеживать эти действия, записываем всю информацию о них в лог. 
         
         //Преобразование координат
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
        	 //если прошло больше 100 мс
        	 if (delta(10)) {
        		 testTransfer.write(cord(x,y));
        	 }
         } else if (MotionEvent.ACTION_UP == Action) {
        	 //testTransfer.write(cord(x,y));
         } else if (MotionEvent.ACTION_DOWN == Action) {         
        	 testTransfer.write(cord(x,y));
         }  
         return true;// Почему мы возвращаем true будет рассмотрено потом 
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