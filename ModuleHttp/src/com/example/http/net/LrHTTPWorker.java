package com.example.http.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

public class LrHTTPWorker {
	private final String TAG = "LrHTTPWorker";
	static public class HttpResponseData implements Serializable{
		private static final long serialVersionUID = 1L;
		public int code;
		public boolean result;
		public String reason = "";
		public String data = "";
	}
	
	public HttpClient createHttpsClient(){
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);  
		HttpConnectionParams.setSoTimeout(params,60 * 1000);  
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", new LrSSLSocketFactory(), 443));
		if(Version.isCNMode()){
			registry.register(new Scheme("https", new LrSSLSocketFactory(), 8443));
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8000));
		}
		else{
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", new LrSSLSocketFactory(), 443));
		}
		//registry.register(new Scheme("https", new LrSSLSocketFactory(), 8443));
		
		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
		return new DefaultHttpClient(ccm, params);
	}
	
	synchronized public 
	HttpResponseData sendPostRequestAndUploadFile(String httpUrl,
			String requestData,String filePath,String fileName,boolean isSecret){
		Log.d(TAG, "sendPostRequestAndUploadFile() "+httpUrl);
		if(TextUtils.isEmpty(httpUrl)||TextUtils.isEmpty(requestData)||
				TextUtils.isEmpty(filePath)||TextUtils.isEmpty(fileName)){
			Log.wtf(TAG, "sendPostRequestAndUploadFile() some thing error");
			return null;
		}
		File fSource = new File(filePath);
		if(!fSource.exists()||!fSource.isFile()){
			android.util.Log.d(TAG,"exists ="+fSource.exists()+" isFile"+fSource.isFile());
			Log.wtf(TAG, "sendPostRequestAndUploadFile() no file");
			return null;
		}
		
		BasicHttpParams params = new BasicHttpParams();  
		HttpConnectionParams.setConnectionTimeout(params, 15 * 1000);  
		HttpConnectionParams.setSoTimeout(params, 10 * 1000);  		
		HttpResponseData res = new HttpResponseData();
		res.result = true;
		try {
			HttpClient client = (isSecret)? createHttpsClient():new DefaultHttpClient(params); 			
			
			HttpPost post = new HttpPost(httpUrl);   
			MultipartEntity multipartContent = new MultipartEntity();
			multipartContent.addPart("json",new StringBody(requestData, Charset.forName("UTF-8")));
			multipartContent.addPart("file", new FileBody(fSource));
			post.setEntity(multipartContent); 
			HttpResponse resp = client.execute(post);  			
			
			res.code = resp.getStatusLine().getStatusCode();
			android.util.Log.d(TAG,"StatusCode:" + res.code); 
			if(res.code==200){
				res.data = EntityUtils.toString(resp.getEntity());
				android.util.Log.d(TAG,"sendPostRequestAndUploadFile() Response:" + res.data); 
				android.util.Log.d(TAG,"sendPostRequestAndUploadFile() Response:" +resp.getStatusLine().getReasonPhrase());
			}
			else{
				res.result = false;
				res.reason =  resp.getStatusLine().getReasonPhrase();	
				Log.w(TAG, "sendPostRequestAndUploadFile() code="+res.code+" reason="+res.reason);
				Log.w(TAG, "sendPostRequestAndUploadFile() "+((resp.getEntity()!=null)?EntityUtils.toString(resp.getEntity()):""));
			}
		} 
		catch (UnsupportedEncodingException e) {			
			res.result = false;
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
		} 
		catch (ClientProtocolException e) {
			res.result = false;
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
		} 
		catch (IOException e) {
			res.result = false;
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			if(e.toString().contains("Timeout")||e.toString().contains("SocketTimeoutException")){
				if(e.toString().contains("Timeout")||e.toString().contains("SocketTimeoutException")){
					res.code = 403;
					res.reason = "connect server timeout";
				}
			}
		}
		android.util.Log.d(TAG,"sendPostRequestAndUploadFile() code="+res.code);
		return res;
	}	

	synchronized public 
	HttpResponseData sendPostRequest(String httpUrl,boolean isSecret,String requestData,String[] hostname){	
		android.util.Log.d(TAG, "sendRequestToSRV() "+httpUrl+" "+requestData);
		if(TextUtils.isEmpty(httpUrl)||TextUtils.isEmpty(requestData)){
			Log.wtf(TAG, "sendPostRequest() no url");
			return null;
		}
		URL url = null;
		String encodion = "UTF-8";
		HttpResponseData result = new HttpResponseData();
		result.result = true;
		try{	
			URL url1=new URL(httpUrl);	
			url = new URL(url1.getProtocol(),url1.getHost(),(isSecret&&Version.isCNMode())?8443:url1.getDefaultPort(),url1.getFile());
			if(isSecret){
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, new TrustManager[]{new LrX509TrustManager(null)}, new SecureRandom());	
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(new LrHostnameVerifier(hostname));
			}
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();			
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");	
		    conn.setRequestProperty("Connection", "Keep-Alive");  
		    conn.setRequestProperty("Charset", encodion); 
		    conn.setRequestProperty("Content-Type","application/json");
		    conn.setUseCaches(false); 
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    conn.connect();	    
		    android.util.Log.d(TAG,"sendRequestToSRV()"+requestData);
		    DataOutputStream os = new DataOutputStream(conn.getOutputStream());				    
		    os.write(requestData.getBytes(encodion));
		    os.flush();
		    os.close();	
		    
		    //------------------------------------------
		    result.code = conn.getResponseCode();
		    android.util.Log.d(TAG,"sendRequestToSRV() encoding = "+conn.getContentEncoding());
		    android.util.Log.d(TAG,"sendRequestToSRV() resposeCode = "+result.code);		    
		    if(result.code==200){
		    	//App.sendBroadcastToLauncher(false);
		    	InputStreamReader is = null;
		    	if(conn.getContentEncoding()!=null&&conn.getContentEncoding().equals("gzip")){	
		    		GZIPInputStream zis =new GZIPInputStream(conn.getInputStream());  
		    		is = new InputStreamReader(zis);
		    	}
		    	else{
		    		is =  new InputStreamReader(conn.getInputStream());
		    	}
		    
			    BufferedReader rd = new BufferedReader(is);
			    String line;
			    StringBuffer response = new StringBuffer(); 
			    while((line = rd.readLine()) != null) {
			        response.append(line);
			    }
			    rd.close();
			    is.close();
			    
			    android.util.Log.d(TAG,"sendRequestToSRV()response message = "+response.toString());
			    result.data = response.toString();			    
		    }
		    else{
		    	Log.w(TAG, "respose id error : "+result.code);
		    	if(result.code==404){
		    		//App.sendBroadcastToLauncher(true);
		    	}
		    	result.result = false;
		    	result.reason = conn.getResponseMessage();
		    }	
		    conn.disconnect();	
		    return result;
		} 
		catch (KeyManagementException e) {
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
		}
		catch (MalformedURLException e) {
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
		}
		catch (ProtocolException e){
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
		}
		catch (UnknownHostException e) {
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
			//App.sendBroadcastToLauncher(true);
		}
		catch (ConnectTimeoutException e){
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
    		//App.sendBroadcastToLauncher(true);
		}
		catch (IOException e){
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			if(e.toString().toLowerCase().contains("timeout")||
				e.toString().toLowerCase().contains("sockettimeoutexception")){
				result.code = -2;
				result.result = false;
		    	result.reason = "connect server timeout";
		    	return result;
			}
			else{
				result.reason = e.toString();
			}
		} 
		catch (NoSuchAlgorithmException e) {
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
		} 
		catch (KeyStoreException e) {
			Log.e(false,TAG,Log.getLineInfo()+" "+e.toString());
			result.reason = e.toString();
		}
		
		result.result = false;
    	result.code = -1;
		return result;
	}
	


}
