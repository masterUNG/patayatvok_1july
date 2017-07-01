package ebook;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.royle.beartv2.R;

public class WebViewEbookActivity extends Activity {
	
	private WebView webview;
	
	String eBook_id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ebook_activity_web_view);

		eBook_id = getIntent().getStringExtra("eBook_id");
		
		webview = (WebView) findViewById(R.id.webView_ebook);
		webview.getSettings().setJavaScriptEnabled(true); 
	    webview.getSettings().setPluginState(PluginState.ON);
//		String pdf = "http://4kmoviestar.com/private/src/Main/ThirdParty/uploads/7g58pnr1g60w84k0c.pdf";
		Log.e("ebook","http://4kmoviestar.com/api/readpdf?id=" + eBook_id);
		webview.loadUrl("http://4kmoviestar.com/api/readpdf?id=" + eBook_id);
	}


}
