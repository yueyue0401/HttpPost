package com.example.http.net;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class LrX509TrustManager implements X509TrustManager {
	private X509TrustManager standardTrustManager = null;

	public LrX509TrustManager(KeyStore keystore) throws 
				NoSuchAlgorithmException, KeyStoreException {
		super();
		TrustManagerFactory factory = TrustManagerFactory
		       .getInstance(TrustManagerFactory.getDefaultAlgorithm());
		factory.init(keystore);
		TrustManager[] trustmanagers = factory.getTrustManagers();
		if(trustmanagers.length == 0){
			throw new NoSuchAlgorithmException("no trust manager found");
		}
		standardTrustManager = (X509TrustManager) trustmanagers[0];
	}

	public void checkClientTrusted(X509Certificate[] certificates,String authType) 
			throws CertificateException {
		standardTrustManager.checkClientTrusted(certificates, authType);
	}

	public void checkServerTrusted(X509Certificate[] certificates,String authType) 
			throws CertificateException {
		if((certificates != null) && (certificates.length == 1)) {
			certificates[0].checkValidity();
		}
		else{
			standardTrustManager.checkServerTrusted(certificates, authType);
		}
	}

	public X509Certificate[] getAcceptedIssuers(){
		return standardTrustManager.getAcceptedIssuers();
	}
}

/**example-------------------------------------------**/
//void test(){
//	String encodion = "UTF-8";
//	try {
//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		schemeRegistry.register(new Scheme("https", new LrSSLSocketFactory(), 443));
//		HttpParams params = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
//        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
//		ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
//		HttpClient httpClient = new DefaultHttpClient(connManager, params);
//		HttpPost httppost = new HttpPost("https://oz-01.leadtek.com.tw/srv_echo?data=yyyyyyyyyyyyyyyyyyyyyy");
//		//https://oz-01.leadtek.com.tw/srv_echo?data=yyyyyyyyyyyyyyyyyyyyyy
//		
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//	    nameValuePairs.add(new BasicNameValuePair("Connection", "Keep-Alive"));
//	    nameValuePairs.add(new BasicNameValuePair("Charset", encodion));
//	    nameValuePairs.add(new BasicNameValuePair("Content-Type", "application/json"));	   
//	    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
//
//        // Execute HTTP Post Request
//        HttpResponse response = httpClient.execute(httppost);
//        
//        if (response.getStatusLine().getStatusCode() == 200){
//        	byte[] encBuf = EntityUtils.toByteArray(response.getEntity()).clone();
//        	String strResult = new String(encBuf);
//        	Log.d(TAG, "response message = "+strResult);
//        }
//        else{
//	    	Log.w(TAG, "respose id error : "+response.getStatusLine().getStatusCode());
//	    }	
//        
//       httpClient.getConnectionManager().shutdown();	  
//	} 
//	catch (MalformedURLException e) {
//		Log.e(false,TAG,Log.getLineInfo(),e);
//	}
//	catch (ProtocolException e){
//		Log.e(false,TAG,Log.getLineInfo(),e);
//	}
//	catch (IOException e){
//		Log.e(false,TAG,Log.getLineInfo(),e);
//		if(e.toString().contains("Timeout")||e.toString().contains("SocketTimeoutException")){
//		}
//	}
//}




