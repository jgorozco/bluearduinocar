package com.floss.bluecar2;

import com.floss.bluecar2.frwk.ShareObjectManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class blueCardSelect extends Activity {
	/** Called when the activity is first created. */

	private EditText editPort;
	private EditText editBtDevice;	
	public ShareObjectManager som;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		som=new ShareObjectManager(getApplicationContext());
		initXmlElements();
	}

	private void initXmlElements() {
		editBtDevice=(EditText) findViewById(R.id.Edit_bt_addr);
		editBtDevice.setCursorVisible(false);
		editPort=(EditText) findViewById(R.id.edt_port); 	
		// TODO Auto-generated method stub

	}



	@Override
	protected void onResume() {
		super.onResume();
		if ((som.LoadConfig("bt_addrs")!=null)
				&&(som.LoadConfig("bt_addrs").length()>0)
				&&(!som.LoadConfig("bt_addrs").equals("None")))
		{
			editBtDevice.setText(som.LoadConfig("bt_addrs"));
		}
		else
		{
			editBtDevice.setText("None");
		}
		if ((som.LoadConfig("port")!=null)&&(som.LoadConfig("port").length()>0))
		{
			editPort.setText(som.LoadConfig("port"));
		}
		else
		{
			editPort.setText("12345");
		}		

	}



	@Override
	protected void onPause() {
		super.onPause();
		String port=editPort.getText().toString();
		String btdevice=editBtDevice.getText().toString();
		som.SaveConfig("port", port);
		som.SaveConfig("bt_addrs", btdevice);
	}

	public void LaunchClient(View target)
	{
		Context context = getApplicationContext();
		CharSequence text = "Hello LaunchClient!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	public void LaunchServer(View target)
	{
		Context context = getApplicationContext();
		CharSequence text = "Hello LaunchServer!";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}    

	public void SelectBtDevice(View target)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editBtDevice.getWindowToken(), 0);
		Intent myIntent = new Intent(getApplicationContext(),DeviceListActivity.class);
		startActivity(myIntent);
	}   
}