package com.royle.beartv2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

import helper.DataStore;
import movie.MovieCategoryAdapter;
import movie.MovieData;
import movie.MovieDetailActivity;

public class SearchActivity extends Activity {
    //widget
    private EditText edtSearch;
    private GridView gridView;
    private Button btnEnter;

    private ProgressDialog progressDialog;
    private ArrayList<MovieData> arrData = new ArrayList<MovieData>();

    DataStore dataStore;

    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        dataStore = new DataStore(getBaseContext());
        progressDialog = new ProgressDialog(this);
        initWidget();

        edtSearch.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    name = edtSearch.getText().toString();
                    new SearchTask().execute();
                    return true;
                }
                return false;
            }
        });
        btnEnter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                name = edtSearch.getText().toString();
                new SearchTask().execute();
            }
        });

    }

    public void showContent(){
        MovieCategoryAdapter adapter = new MovieCategoryAdapter(getBaseContext(), R.layout.movie_row, arrData);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            Intent intent;
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                intent = new Intent(SearchActivity.this,MovieDetailActivity.class);
                intent.putExtra("name", arrData.get(position).getMovie_name());
                intent.putExtra("pic", arrData.get(position).getMovie_img());
                intent.putExtra("link", arrData.get(position).getMovie_link());
                intent.putExtra("link_hd", arrData.get(position).getMovie_link_hd());
                intent.putExtra("link_mobile", arrData.get(position).getMovie_link_mobile());
                intent.putExtra("link_4k", arrData.get(position).getMovie_link_4k());
                intent.putExtra("level_access", arrData.get(position).getLevel_access());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void initWidget() {
        edtSearch = (EditText) findViewById(R.id.editSearch);
        gridView = (GridView) findViewById(R.id.gridView_search);
        gridView.setFocusable(true);
        gridView.setFocusableInTouchMode(true);
        gridView.requestFocus();
        btnEnter = (Button) findViewById(R.id.btnEnter_search);
    }

    public class SearchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            MovieData data = new MovieData();
            name = name.replace(" ", "%20");
            arrData = data.getSearch(name);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            showContent();
            progressDialog.dismiss();
        }

    }


}
