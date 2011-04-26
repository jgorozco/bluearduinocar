package com.floss.bluecar2.frwk;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class BtManager {


	// Bluetooth Stuff
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null; 
	private OutputStream outStream = null;
	private ConnectThread mConnectThread = null;
	private String deviceAddress = null;
	// Well known SPP UUID (will *probably* map to RFCOMM channel 1 (default) if not in use); 
	// see comments in onResume(). 
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public Handler myHandler;
	public Context myContext;

	// Intent request codes



	public BtManager(Context cntx,Handler myHandler) {
		myContext=cntx;
		this.myHandler=myHandler;

	}


	public void ConnectBtDevice(String macAdrr)
	{
		deviceAddress=macAdrr;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		if (mBluetoothAdapter == null) { 
			myHandler.sendMessage(MessageUtils.OnErrorMsg("No bt adapter", 0));

		} 
		// If BT is not on, request that it be enabled.
		if (!mBluetoothAdapter.isEnabled()) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg("bt disable", 0));
		}
		mConnectThread = new ConnectThread(deviceAddress);
		mConnectThread.start();
	}



	public void sendData(String data)
	{
		if (btSocket!=null)
		{

			try {
				if (outStream==null)
				{
					outStream = btSocket.getOutputStream();
				}
				outStream.write(data.getBytes());
				outStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
	}


	public void closeServer()
	{
		try {
			outStream.flush();
			outStream.close();
			btSocket.close();
		} catch (Exception e) {
			myHandler.sendMessage(MessageUtils.OnErrorMsg("error["+e.getMessage()+"]", 0));
		}

	}


	/** Thread used to connect to a specified Bluetooth Device */
	public class ConnectThread extends Thread {
		private String address;

		ConnectThread(String MACaddress) {
			address = MACaddress;
		}

		public void run() {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address); 
			try { 
				btSocket = device.createRfcommSocketToServiceRecord(MY_UUID); 

			} catch (IOException e) { 
				myHandler.sendMessage(MessageUtils.OnErrorMsg("device socket error", 0));
			} 
			mBluetoothAdapter.cancelDiscovery(); 
			try { 
				btSocket.connect(); 
				myHandler.sendMessage(MessageUtils.OnCompleteMsg("device ready", 0));
			} catch (IOException e1) { 
				try { 
					btSocket.close(); 
					myHandler.sendMessage(MessageUtils.OnErrorMsg("device close", 0));
				} catch (IOException e2) {
					myHandler.sendMessage(MessageUtils.OnErrorMsg("device close error", 0));
				} 
			} 
			try { 
				outStream = btSocket.getOutputStream(); 
			} catch (IOException e2) { 
				myHandler.sendMessage(MessageUtils.OnErrorMsg("device socket error 2", 0));
			} 
		}
	}
}
