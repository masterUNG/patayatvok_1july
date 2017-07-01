package user;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.royle.beartv2.R;

public class MailActivity extends Activity {

	public WebView webview_mail;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.webview_mail);

        webview_mail = (WebView) findViewById(R.id.webview_mail);
        webview_mail.getSettings().setJavaScriptEnabled(true);
        webview_mail.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview_mail.loadUrl("http://form.4kmoviebox.com/view.php?id=11216");

	}


}
