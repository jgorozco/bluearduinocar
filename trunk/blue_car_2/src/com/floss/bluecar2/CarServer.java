package com.floss.bluecar2;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.floss.bluecar2.frwk.BtManager;
import com.floss.bluecar2.frwk.MessageUtils;
import com.floss.bluecar2.frwk.NetworkUtils;
import com.floss.bluecar2.frwk.ServerSocketThread;
import com.floss.bluecar2.frwk.ShareObjectManager;

public class CarServer extends Activity {
	public TextView tViewIpaddr;
	public TextView tViewServerStatus;
	public TextView tViewBtStatus;
	public TextView tViewDetails;

	public ServerSocketThread myServerSocket;
	public  Handler myServerHandler ;
	public BtManager myBtManager;
	public  Handler myBtHandler ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initXMLElements();
		ShareObjectManager somManager=new ShareObjectManager(getApplicationContext());
		appndtxt("\n port:"+somManager.LoadConfig("port"));
		appndtxt("\n bt mac:"+somManager.LoadConfig("bt_addrs"));
		deactivatedBT();
		String ipadd=NetworkUtils.getLocalIpAddress();
		if ((ipadd!=null)&&(ipadd.length()>1))
		{
			tViewIpaddr.setText(ipadd);
			tViewIpaddr.setTextColor(Color.GREEN);
		}else
		{
			tViewIpaddr.setText("Not internet conection");
			tViewIpaddr.setTextColor(Color.RED);
		}
		setServeStatus("DISCONNECTED",Color.RED);
		myServerHandler = new Handler() {
			public void handleMessage(Message msg) {

				int type=msg.getData().getInt(MessageUtils.TYPE);
				//appndtxt("\n__newhandleMessage["+String.valueOf(type)+"]");
				if (type==MessageUtils.MSG_DATA)
				{
					String data=msg.getData().getString(MessageUtils.DATA);

				}else if (type==MessageUtils.MSG_PROGRESS)
				{
					int status=msg.getData().getInt(MessageUtils.PROGRESS);
					//appndtxt("\n__new status["+String.valueOf(status)+"]");
					if (status<20)
					{
						setServeStatus("DISCONECTED", Color.RED);
					}else if (status<40)
					{
						setServeStatus("LISTENING", Color.YELLOW);							
					}else if (status<60)
					{
						setServeStatus("RECIVING", Color.BLUE);	
					}else if (status<80)
					{
						setServeStatus("SENDING", Color.BLUE);								
					}else if (status<90)
					{
						setServeStatus("CLOSSING", Color.GREEN);
					}
				}else if (type==MessageUtils.MSG_COMPLETE)
				{
					appndtxt("PING["+msg.getData().getString(MessageUtils.DATA)+"]");
				}

				appndtxt(msg.getData().getString(MessageUtils.DATA));

			}
		};
		myBtHandler = new Handler() {
			public void handleMessage(Message msg) {
				int type=msg.getData().getInt(MessageUtils.EVENT);
				if (type==MessageUtils.MSG_COMPLETE)
				{
					activatedBT();
				}else//es un error
				{
					String data=msg.getData().getString(MessageUtils.DATA);
					Toast.makeText(getApplicationContext(), data, Toast.LENGTH_LONG).show();
				}
			}
		};

	//s	myBtManager=new BtManager(getApplicationContext(), myBtHandler);
	//	initBtDevice();

	}

	private void initXMLElements() {
		setContentView(R.layout.server);
		tViewIpaddr=(TextView) findViewById(R.id.srv_txt_ipaddr);
		tViewServerStatus=(TextView) findViewById(R.id.srv_txt_serverstatus);
		tViewBtStatus=(TextView) findViewById(R.id.srv_txt_btstatus);
		tViewDetails=(TextView) findViewById(R.id.srv_txt_details);
	}


	public void deactivatedBT() {
		tViewBtStatus.setText("BT DEACTIVATED");
		tViewBtStatus.setTextColor(Color.RED);

	}

	public void activatedBT() {
		tViewBtStatus.setText("BT ACTIVATED");
		tViewBtStatus.setTextColor(Color.BLUE);
		initBtDevice();

	}


	public void setServeStatus(String status,int color)
	{
		appndtxt("\n change socket status["+status+"]");
		tViewServerStatus.setText(status);
		tViewServerStatus.setTextColor(color);
	}

	public void appndtxt(String string) {
		tViewDetails.append(string);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (myServerSocket!=null)
		{
			while(myServerSocket.isAlive())
			{
				myServerSocket.closeServer();
				myServerSocket.stop();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	public void LaunchServer(View target)
	{
		initSocketServer();
	}

	public void TestBluetooth(View target)
	{

		try {
			sendBtCommand("l");
			sendBtCommand("u");
			Thread.sleep(1500);
			sendBtCommand("d");
			sendBtCommand("a");
		} catch (InterruptedException e) {		
			e.printStackTrace();
		}

	}

	public void sendBtCommand(String command)
	{
		tViewDetails.append("send bt ["+command+"] \n");
		myBtManager.sendData(command);
	}

	public void initBtDevice()
	{
		ShareObjectManager som=new ShareObjectManager(getApplicationContext());
		String addr=som.LoadConfig("bt_addrs");
		if ((addr!=null)&&(addr.length()>1))
		{
			myBtManager.ConnectBtDevice(addr);
			tViewBtStatus.setText("BT CONNECTED");
			tViewBtStatus.setTextColor(Color.BLUE);			
		}else
		{
			tViewBtStatus.setText("BT NOT CONNECTED");
			tViewBtStatus.setTextColor(Color.RED);	
		}
	}

	public void initSocketServer()
	{
		ShareObjectManager som=new ShareObjectManager(getApplicationContext());
		int port=Integer.parseInt(som.LoadConfig("port"));
		myServerSocket =new ServerSocketThread(ServerSocketThread.TCP, port, myServerHandler);
		myServerSocket.start();
	}
}
