package com.royle.beartv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import helper.DataStore;
import helper.UrlApp;

public class WebViewPayPalActivity extends Activity {

    private WebView webView;
    private ImageButton imbClose;

    private DataStore dataStore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_pay_pal);
        initWidget();

        imbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dataStore = new DataStore(this);
        userId = dataStore.LoadSharedPreference(DataStore.USER_ID, "");
        webView.loadUrl(UrlApp.PAY_PAL + userId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_bottom);
    }

    private void initWidget() {
        webView = (WebView) findViewById(R.id.webView);
        imbClose = (ImageButton) findViewById(R.id.imbClose);
    }
}
