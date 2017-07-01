package com.royle.beartv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class SportActivity extends Activity {

	public WebView webview_sport;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_sport);

		webview_sport = (WebView) findViewById(R.id.webview_sport);
		webview_sport.getSettings().setJavaScriptEnabled(true);
		webview_sport.getSettings().setPluginState(WebSettings.PluginState.ON);
		webview_sport.loadUrl("http://android.livescore.com");

	}


}
