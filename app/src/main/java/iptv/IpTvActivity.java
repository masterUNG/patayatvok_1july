package iptv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;

import java.util.ArrayList;

import helper.DataStore;

public class IpTvActivity extends Activity {

	// widget
	private Gallery gallery;
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
//	private Button btnSearch;
	private TextView txtSlide;

	private ArrayList<IpTvData> arrDataTv = new ArrayList<IpTvData>();
	private ProgressDialog progressDialog;

	private int widthImg;

	private int heightImg;
	
	DataStore dataStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.iptv_activity_ip_tv);
		
		//Initial View
		initialView();
		
		dataStore = new DataStore(getBaseContext());

		// Show Process Load
		progressDialog = new ProgressDialog(this);

		//Run Download
		new DonwloadIpTvTask().execute();
		
//		btnSearch.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(IpTvActivity.this,SearchActivity.class);
//				startActivity(intent);
//			}
//		});


	}	// Main Class

	private void initialView() {
		txtUsername = (TextView) findViewById(R.id.txtUsername_iptv);
		txtLevel = (TextView) findViewById(R.id.txtLevel_iptv);
		txtExpire = (TextView) findViewById(R.id.txtExpire_iptv);
//		btnSearch = (Button) findViewById(R.id.btnSearch_main);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkUser();
	}
	
	public void checkUser(){
		if (true) {
			txtUsername.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtUsername.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
			txtLevel.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
			txtExpire.setText("หมดอายุyut : "+dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, ""));

		}else {
			txtUsername.setVisibility(View.GONE);
			txtExpire.setVisibility(View.GONE);
			txtLevel.setVisibility(View.GONE);
		}
	}

	public void showContent() {

		//For Text Slide on Botton
		txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));

		//For Calculate Display on Screen
		MainActivity mainActivity = new MainActivity();
		widthImg = mainActivity.getWidth(getBaseContext());
		widthImg = widthImg/4;
		heightImg = (int) (widthImg*1.2);
		
		Log.d("1Julyv1", "screen ==> "+widthImg+"/"+heightImg);


		//About Gallery
		gallery = (Gallery) findViewById(R.id.gallery_iptv);
		gallery.setFocusable(true);
		gallery.setFocusableInTouchMode(true);
		gallery.requestFocus();
		gallery.setAdapter(new IpTvAdapter(getBaseContext(),
				R.layout.gallery_row, arrDataTv, widthImg, heightImg));
		if (arrDataTv.size()!=0) {
			gallery.setSelection(1);
		}
		
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(IpTvActivity.this,TvCategoryActivity.class);
				intent.putExtra("id", arrDataTv.get(position).getTv_id());
				startActivity(intent);
			}
		});
		gallery.setOnItemSelectedListener(new SelectListener(this));

	}

	public class DonwloadIpTvTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			IpTvData ipTvData = new IpTvData(getBaseContext());
			arrDataTv = ipTvData.getCategory("2");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			showContent();
		}

	}
	private class SelectListener implements AdapterView.OnItemSelectedListener {

        private Animation grow     = null;
        private View      lastView = null; 

        public SelectListener(Context c) {
             grow = AnimationUtils.loadAnimation(c, R.anim.grow);
        }

        @Override
		public void  onItemSelected(AdapterView<?>  parent, View  v, int position, long id)         {
        	
            // Shrink the view that was zoomed 
            try { if (null != lastView) lastView.clearAnimation();
            v.clearAnimation();
            lastView.animate().scaleX(1).scaleY(1).start();
            } catch (Exception clear) { }

            // Zoom the new selected view
            try {v.animate().scaleX((float) 1.1).scaleY((float) 1.1).start();} catch (Exception animate) {}

            // Set the last view so we can clear the animation 
            lastView = v;
        	
        }

        @Override
		public void onNothingSelected(AdapterView<?>  parent) {
        }

   }



}
