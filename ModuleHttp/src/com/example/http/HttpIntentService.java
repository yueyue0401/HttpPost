package com.example.http;

import com.example.http.net.LrHTTPWorker;
import com.example.http.net.LrHTTPWorker.HttpResponseData;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class HttpIntentService extends IntentService {

	public HttpIntentService() {
		super(HttpIntentService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String url = intent.getStringExtra(HttpPost.URL);
		String requestData = intent.getStringExtra(HttpPost.REQUEST_DATA);
		String hostname[] = intent.getStringArrayExtra(HttpPost.HOSTNAME);
		boolean isSecret = url.contains("https");
		
		HttpResponseData res= new HttpResponseData();
		res = new LrHTTPWorker().sendPostRequest(url, isSecret, requestData, hostname);
		
		Bundle bundle = new Bundle();
		bundle.putString(HttpPost.RESULT, res.data);
		
		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		receiver.send(0, bundle);
	}

}
