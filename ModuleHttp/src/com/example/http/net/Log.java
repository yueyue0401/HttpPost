package com.example.http.net;


public class Log {
	
	public final static String APP_TAG = "amor8Z61SyncMgr";
	
	private static boolean useSameTag = true;
	
	private static int log_Level = android.util.Log.VERBOSE;
	
	public static void setLogLevel(int level){
		log_Level = level;
	}	
	
	public static String getLineInfo(){  
        StackTraceElement ste = new Throwable().getStackTrace()[1];  
        return "F: "+ste.getFileName()+" "+ste.getMethodName() + " Line: " + ste.getLineNumber();         
    }
	
	private static String getTag(String tag){
		return (useSameTag)?APP_TAG:tag;
	}
	
	private static String getMsg(String tag,String msg){
		return (useSameTag)?tag+" "+msg:msg;
	}
	
	public static void wtf(String tag,String msg){
		android.util.Log.wtf(getTag(tag), getMsg(tag,msg));
	}
	
	public static void wtf(String tag,String msg,Throwable tr){
		android.util.Log.wtf(getTag(tag), getMsg(tag,msg),tr);
	}
	
	public static void e(boolean fordebug,String tag,String msg){
		if(fordebug){
			if(log_Level<=android.util.Log.DEBUG)
				e(tag,msg);
			return;
		}
		e(tag,msg);
	}
	
	public static void e(boolean fordebug,String tag,String msg,Throwable tr){
		if(fordebug){
			if(log_Level<=android.util.Log.DEBUG)
				e(tag,msg);
			return;
		}
		e(tag,msg,tr);
	}	
	
	public static void e(String tag,String msg){
		android.util.Log.e(getTag(tag), getMsg(tag,msg));
	}
	private static void e(String tag,String msg,Throwable tr){
		android.util.Log.e(getTag(tag), getMsg(tag,msg),tr);
	}
	
	public static void w(String tag,String msg){
		android.util.Log.w( getTag(tag), getMsg(tag,msg));
	}
	public static void w(String tag,String msg,Throwable tr){
		android.util.Log.w(getTag(tag), getMsg(tag,msg),tr);
	}
	
	public static void i(String tag,String msg){
		if(log_Level<=android.util.Log.INFO)
			android.util.Log.i(getTag(tag), getMsg(tag,msg));
	}
	public static void i(String tag,String msg,Throwable tr){
		if(log_Level<=android.util.Log.INFO)
			android.util.Log.i(getTag(tag), getMsg(tag,msg),tr);
	}
	
	public static void d(String tag,String msg){
		if(log_Level<=android.util.Log.DEBUG)
			android.util.Log.d(getTag(tag), getMsg(tag,msg));
	}
	public static void d(String tag,String msg,Throwable tr){
		if(log_Level<=android.util.Log.DEBUG)
			android.util.Log.d(getTag(tag), getMsg(tag,msg),tr);
	}
	
	public static void v(String tag,String msg){
		if(log_Level<=android.util.Log.VERBOSE)
			android.util.Log.v(getTag(tag), getMsg(tag,msg));
	}
	public static void v(String tag,String msg,Throwable tr){
		if(log_Level<=android.util.Log.VERBOSE)
			android.util.Log.v(getTag(tag), getMsg(tag,msg),tr);
	}

}
