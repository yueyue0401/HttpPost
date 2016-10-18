package com.example.http.net;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class LrHostnameVerifier implements HostnameVerifier{
	private final String TAG = "LrHostnameVerifier";
	private List<String> trustHostName = new ArrayList<String>();	
	public LrHostnameVerifier(String[] hostnames){
		setTrustHostname(hostnames);
	}
	public void setTrustHostname(String[] hostnames){
		if(hostnames==null)
			return;
		for(int i=0;i<hostnames.length;i++){
			if(hostnames[i]!=null)
				trustHostName.add(hostnames[i]);
		}
	}
    @Override
    public boolean verify(String hostname, SSLSession session) {
    	Log.d(TAG,"verify() hostname =  "+hostname);
    	for(int i=0;i<trustHostName.size();i++){
    		if(trustHostName.get(i).equals(hostname))
    			return true;
    	}
        return true;
    }
}
