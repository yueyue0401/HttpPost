package com.example.http.net;

public class Version {
	
	private static boolean CN_VER=false;
	private static boolean API_VER2=true;

	static public 
	boolean isCNMode(){
		return (CN_VER);
	}
	static public
	boolean isAPI2(){
		return (API_VER2);
	}
}
