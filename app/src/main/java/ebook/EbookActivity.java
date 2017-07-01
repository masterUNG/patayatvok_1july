package ebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;
import com.royle.beartv2.SearchActivity;

import java.util.ArrayList;

import helper.DataStore;
import user.LoginActivity;

public class EbookActivity extends Activity {

	// widget
	private Gallery gallery;
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private ImageButton btnSearch;
	private TextView txtSlide;

	private ArrayList<EbookData> arrDataEbook = new ArrayList<EbookData>();
	private ProgressDialog progressDialog;

	private int widthImg;

	private int heightImg;
	
	DataStore dataStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ebook_activity);
		

		txtUsername = (TextView) findViewById(R.id.txtUsername_ebook);
		txtLevel = (TextView) findViewById(R.id.txtLevel_ebook);
		txtExpire = (TextView) findViewById(R.id.txtExpire_ebook);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch_main);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtSlide.setSelected(true);
		
		dataStore = new DataStore(getBaseContext());


		progressDialog = new ProgressDialog(this);
		new DonwloadIpTvTask().execute();
		
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EbookActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		

	}
	@Override
	protected void onResume() {
		super.onResume();
		checkUser();
	}
	
	public void checkUser(){
		if (dataStore.checkUser()) {
			txtUsername.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtUsername.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_NAME, ""));
			txtLevel.setText(": "+dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
			txtExpire.setText("หมดอายุ : "+dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, ""));
		}else {
			txtUsername.setVisibility(View.GONE);
			txtExpire.setVisibility(View.GONE);
			txtLevel.setVisibility(View.GONE);
		}
	}

	public void showContent() {
		txtSlide.setText(Html.fromHtml(dataStore.LoadSharedPreference(DataStore.TEXT_SLIDE, "")));
		MainActivity mainActivity = new MainActivity();
		widthImg = mainActivity.getWidth(getBaseContext());
		widthImg = widthImg/4;
		heightImg = (int) (widthImg*1.2);
		
		Log.d("screen", "screen"+widthImg+"/"+heightImg);
		
		gallery = (Gallery) findViewById(R.id.gallery_ebook);
		gallery.setFocusable(true);
		gallery.setFocusableInTouchMode(true);
		gallery.requestFocus();
		gallery.setAdapter(new EbookAdapter(getBaseContext(),
				R.layout.gallery_row, arrDataEbook, widthImg, heightImg));
		if (arrDataEbook.size()!=0) {
			gallery.setSelection(0);
		}
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (dataStore.checkUser()) {
					int user_level_id = Integer.valueOf(dataStore.LoadSharedPreference(DataStore.USER_LEVEL_ID, "0"));
					int level_access = Integer.valueOf(arrDataEbook.get(position).getLevel_access());
					if (user_level_id>=level_access) {
						Intent intent = new Intent(EbookActivity.this,EbookCategoryActivity.class);
						intent.putExtra("main_id", arrDataEbook.get(position).getId_ebook());
						startActivity(intent);
					}else {
						if (level_access==1) {
							Toast.makeText(getApplicationContext(), "Level Member up", Toast.LENGTH_LONG).show();
						}else if (level_access==2) {
							Toast.makeText(getApplicationContext(), "Level  Silver up", Toast.LENGTH_LONG).show();
						}else if (level_access==3) {
							Toast.makeText(getApplicationContext(), "Level Gold up", Toast.LENGTH_LONG).show();
						}else if (level_access==4) {
							Toast.makeText(getApplicationContext(), "Level Platinum up", Toast.LENGTH_LONG).show();
						}
						
					}
				}else {
					Intent intent = new Intent(EbookActivity.this,LoginActivity.class);
					intent.putExtra("id", arrDataEbook.get(position).getId_ebook());
					startActivity(intent);
				}
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
			EbookData ebookData = new EbookData();
			arrDataEbook = ebookData.getMain_Ebook();
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
