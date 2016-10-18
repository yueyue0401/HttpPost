1.	HttpPost.java: new此class就會send HTTP Post.
	HttpPost httpPost = new HttpPost(Callback callback, Context context, String url, String data, String[] hostname);

	I.	Callback: set callback
	II.	Context: the launching activity
	III.	url: the url to send post
	IV.	data: the data of request (JSONObject.toString)
	V.	hostname

2.	HttpIntentService.java: new HttpPost後會StartService此IntentService.

3.	IntentServiceReceiver.java: IntentService send Post後, 會send bundle到此ResultReciever.

5.	Net folder: Https加密解密的Class.

6.	Main.java: 用三個Button測試三個method.
