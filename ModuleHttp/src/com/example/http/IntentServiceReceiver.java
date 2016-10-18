package com.example.http;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class IntentServiceReceiver extends ResultReceiver {

	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);
		String data = resultData.getString(HttpPost.RESULT);
		mReceiver.httpResultCallback(data);
	}

	private Receiver mReceiver;
	
	public interface Receiver{
		void httpResultCallback(String data);
	}
	
	public IntentServiceReceiver(Handler handler) {
		super(handler);
	}
	
	public void setReceiver(Receiver receiver){
		mReceiver = receiver;
	}

}
