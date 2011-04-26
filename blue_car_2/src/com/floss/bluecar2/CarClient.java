package com.floss.bluecar2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.floss.bluecar2.frwk.ClientSocketThread;
import com.floss.bluecar2.frwk.MessageUtils;
import com.floss.bluecar2.frwk.ShareObjectManager;

public class CarClient extends Activity {

	private EditText EditIpAddress;
	private TextView TViewClientStatus;
	private TextView TViewPort;
	
	public  Handler myClientHandler ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initXmlElements();
		fillElements();
		myClientHandler = new Handler() {
				public void handleMessage(Message msg) {
					
					int type=msg.getData().getInt(MessageUtils.TYPE);
					if(type==MessageUtils.MSG_DATA)
					{
						String data=msg.getData().getString(MessageUtils.DATA);
						TViewClientStatus.setText("Server OK, ping["+data+"]");
					}
					//appndtxt(msg.getData().getString(MessageUtils.DATA));
				}
			};
	}

	private void fillElements() {
		ShareObjectManager som=new ShareObjectManager(getApplicationContext());
		String addr=som.LoadConfig("server_ip");
		if ((addr!=null)&&(addr.length()>0))
		{
			EditIpAddress.setText(addr);
		}
		TViewClientStatus.setText("NOT tested yet");
		String port=som.LoadConfig("port");
		if ((port!=null)&&(port.length()>0))
		{
			TViewPort.setText(port);
		}else{
			TViewPort.setText("PORT?");
		}
	}

	private void initXmlElements() {
		setContentView(R.layout.client);
		EditIpAddress=(EditText) findViewById(R.id.cli_edit_ip);
		TViewClientStatus=(TextView) findViewById(R.id.cli_edit_serverstatus);
		TViewPort=(TextView) findViewById(R.id.cli_txt_port);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	public void sendPetition(String str)
	{
		String ipadd=EditIpAddress.getText().toString();
		ShareObjectManager som=new ShareObjectManager(getApplicationContext());
		som.SaveConfig("server_ip",ipadd);
		int port=Integer.parseInt(som.LoadConfig("port"));
		ClientSocketThread clientSocket=new ClientSocketThread(ClientSocketThread.TCP,str, ipadd, port, myClientHandler);
		clientSocket.start();
	}
	
	
	public void testServer (View Target)
	{
		sendPetition("SINC");
	}

	public void ClickUp (View Target)
	{
		sendPetition("u");
	}
	
	
	public void ClickDown (View Target)
	{
		sendPetition("d");
	}
	
	public void ClickLong (View Target)
	{
		sendPetition("t");
	}
	
	public void ClickShort (View Target)
	{
		sendPetition("s");
	}	
	
}
