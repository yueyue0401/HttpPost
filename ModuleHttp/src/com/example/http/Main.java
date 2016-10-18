package com.example.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.http.api.AmorServerAPI;
import com.example.http.api.AmorServerAPI.AmorServerCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Main extends Activity implements HttpPost.Callback, AmorServerCallback {

	private HttpPost httpPost;
	private TextView textView;

	private IntentServiceReceiver mReceiver;
	private AmorServerAPI a;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		a = new AmorServerAPI(getApplicationContext(), Main.this);

		textView = (TextView) findViewById(R.id.textView1);
		textView.setText("Choose a command to post.");
	}

	// Button click
	public void onCMD1Click(View v) {
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();

		try {
			jo.put("Action", "0");
			jo.put("SRVDbId", "0");
			jo.put("Time", "1463266438");
			jo.put("DeviceName", "M8");
			jo.put("SDKVer", "dddddd");
			jo.put("051", "71");
			ja.put(jo);

			jo.put("Action", "0");
			jo.put("SRVDbId", "0");
			jo.put("Time", "1463266498");
			jo.put("DeviceName", "M8");
			jo.put("SDKVer", "dddddd");
			jo.put("051", "72");
			ja.put(jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		 a.upload_HR_Data("oz_huang@leadtek.com.tw", "12345678", null, "0", "1", ja);
	}

	public void onCMD2Click(View v) {
		a.download_HR_Data("oz_huang@leadtek.com.tw", "12345678", null, "0", null, "1", "1", "0", "2016-05-15", "6",
				"+08:00");
	}

	public void onCMD3Click(View v) {
		a.ask_SRV_Version("0");
	}

	public void showText(String s) {
		textView.append("\n" + s + "\n");
	}

	// HTTP class callback
	@Override
	public void responseData(String data) {
		showText(data + "\n" + "***************");
	}

	// Amor Server Callback
	@Override
	public void AmorServerResponseData(String data) {
		showText(data + "\n" + "***************");

	}
}
