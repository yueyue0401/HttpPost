package com.example.http;

import com.example.http.IntentServiceReceiver.Receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class HttpPost implements IntentServiceReceiver.Receiver{

	public static final String RESULT = "result";
	public static final String URL = "url";
	public static final String REQUEST_DATA = "requestData";
	public static final String HOSTNAME = "hostname";
	
	private String url, requestData;
	private String[] hostname;
	private IntentServiceReceiver mReceiver;
	private Context context;
	private Callback callback;
	
	public interface Callback{
		void responseData(String data);
	}
	
	/**
	 * Initial and Send HTTP Post.
	 * 
	 * @param callback
	 * @param context
	 * @param url
	 * @param data the data of request.
	 * @param hostname
	 */
	public HttpPost(Callback callback, Context context, String url, String data, String[] hostname){
		this.url = url;
		this.requestData = data;
		this.hostname = hostname;
		this.context = context;
		this.callback = callback;
		
		mReceiver = new IntentServiceReceiver(new Handler());
		mReceiver.setReceiver(this);
		
		sendRequest();
	}
	
	/**
	 * Start the IntentService.
	 */
	private void sendRequest() {
		Intent intent = new Intent(context, HttpIntentService.class);
		intent.putExtra(URL, url);
		intent.putExtra(REQUEST_DATA, requestData);
		intent.putExtra(HOSTNAME, hostname);
		intent.putExtra("receiver", mReceiver);
		
		context.startService(intent);
	}

	@Override
	public void httpResultCallback(String data) {
		callback.responseData(data);
	}

}
