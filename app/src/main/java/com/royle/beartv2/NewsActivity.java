package com.royle.beartv2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class NewsActivity extends Activity {
	//widget
	private ImageView img;
	private TextView txtnews; 
	
	private String img_path;
	private String news;
	

	private ImageLoader imageLoader;
	private DisplayImageOptions displayImageOptions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_news);
		
		img_path = getIntent().getStringExtra("img_path");
		news = getIntent().getStringExtra("news");
		
		img = (ImageView) findViewById(R.id.img_news);
		txtnews = (TextView) findViewById(R.id.txt_news);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getBaseContext()));
		displayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_default)
				.showImageForEmptyUri(R.drawable.ic_default).cacheOnDisc(true)
				.cacheInMemory(true).imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		
		txtnews.setText(Html.fromHtml(news));
		imageLoader.displayImage(img_path, img, displayImageOptions);
		
	}




}
