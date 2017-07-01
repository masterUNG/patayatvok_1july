package user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;


public class LoginActivity extends Activity {

	final String PREF_NAME = "LoginPreferences";
	final String KEY_USERNAME = "Username";
	final String KEY_PASSWORD = "Password";
	final String KEY_REMEMBER = "RememberUsername";

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	CheckBox cbRemember;

	//widget
	private EditText edtUsername;
	private EditText edtPassword;
	private Button btnLogin;
	private Button btnRegister;

	private String username;
	private String password;

	private PortalServices portalServices;

	private ProgressDialog dialog;
	private DataStore dataStore;

	private Context context;
	private MCrypt mcrypt = new MCrypt();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_activity_login);

		portalServices = new PortalServices();
		dataStore = new DataStore(getApplicationContext());
		dialog = new ProgressDialog(this);

		initWidget();

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				username = edtUsername.getText().toString();
				password = edtPassword.getText().toString();
				if (username.length() == 0 || password.length() == 0) {
					Toast.makeText(getApplicationContext(), "กรุณากรอกให้ครบทุกช่อง", Toast.LENGTH_LONG).show();
				} else {
					dataStore.SavedSharedPreference(DataStore.USER_NAME, username);
					dataStore.SavedSharedPreference(DataStore.PASSWORD, password);
					new LoginTask().execute(username, password);
				}
			}
		});
//		btnRegister.setOnClickListener(new OnClickListener() {

//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//				startActivity(intent);
//			}
//		});


	}



	private void initWidget() {
		sp = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		editor = sp.edit();

		edtUsername = (EditText) findViewById(R.id.edtUsername_login);
		edtPassword = (EditText) findViewById(R.id.edtPassword_login);
		btnLogin = (Button) findViewById(R.id.btnLogin_login);
//		btnRegister = (Button) findViewById(R.id.btnRegister_login);
		edtUsername.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				editor = sp.edit();
				editor.putString(KEY_USERNAME, s.toString());
				editor.commit();
			}
		});

		edtPassword.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				editor = sp.edit();
				editor.putString(KEY_PASSWORD, s.toString());
				editor.commit();
			}
		});

		cbRemember = (CheckBox) findViewById(R.id.cbRemember);
		cbRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				editor.putBoolean(KEY_REMEMBER, isChecked);
				editor.commit();
			}
		});

		boolean isRemember = sp.getBoolean(KEY_REMEMBER, false);
		cbRemember.setChecked(isRemember);

		if (isRemember) {
			String username = sp.getString(KEY_USERNAME, "");
			String password = sp.getString(KEY_PASSWORD, "");
			edtUsername.setText(username);
			edtPassword.setText(password);
		}
	}


	public void LoginApp(String username,String password){
		new LoginTask().execute(username, password);
	}


	private class LoginTask extends AsyncTask<String, Void, Void>{
		char check ;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		@Override
		protected Void doInBackground(String... params) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("username", params[0]));
			list.add(new BasicNameValuePair("password", params[1]));
			list.add(new BasicNameValuePair("macAddress", MainActivity.getAndroidId(LoginActivity.this)));
			list.add(new BasicNameValuePair("secretCode", "tk055xs0orm2xqy"));

			String resultData = portalServices.makePortalCall(null, UrlApp.LOGIN, PortalServices.POST, list);
			Log.e("login", resultData);
			try {

				String decrypted = new String(mcrypt.decrypt(resultData));
				JSONObject jsonObject = new JSONObject(decrypted);
				if (jsonObject.has("status")) {
					String status = jsonObject.getString("status");
					if (status.equals("success")) {
						JSONObject jData = jsonObject.getJSONObject("data");
						String user_id = jData.getString("id");
						String username = jData.getString("username");
						String expiry_date = jData.getString("expiry_date");
						String level = jData.getString("level_id");
						String token = jData.getString("token");
						DataStore dataStore = new DataStore(getApplicationContext());
						dataStore.SavedSharedPreference(DataStore.USER_ID, user_id);
						dataStore.SavedSharedPreference(DataStore.USER_NAME, username);
						dataStore.SavedSharedPreference(DataStore.USER_EXPIRE, expiry_date);
						dataStore.SavedSharedPreference(DataStore.USER_LEVEL_ID, level);
						dataStore.SavedSharedPreference(DataStore.USER_TOKEN, token);
						if (level.equals("1")) {
							dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "Member");
						}else if (level.equals("2")) {
							dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "Silver");
						}else if (level.equals("3")) {
							dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "Gold");
						}else if (level.equals("4")) {
							dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "Platinum");
						}
						check = '1';
					}else if(status.equals("duplicate")){
						check = '2';
					}else if(status.equals("sessionstill")){
						check = '3';
					}else{
						check = '4';
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				check = '4';
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (check == '1') {

				Toast.makeText(getApplicationContext(), "เข้าสู่ระบบแล้ว", Toast.LENGTH_SHORT).show();
				finish();

				Intent keepalive = new Intent(LoginActivity.this, meklib.KeepAlive.class);
				startService(keepalive);
				finish();

//				startActivity(new Intent(LoginActivity.this, MainActivity.class));






			}else if (check == '2') {
				Toast.makeText(getApplicationContext(), "มีผู้ใช้งาน User นี้อยู่ อนุญาติให้ใช้งานได้เพียง 1 เครื่องในเวลาเดียวกัน กรุณากดปิดแอพที่เครื่องเก่าก่อนแล้วค่อย Login ใหม่อีกครั้ง", Toast.LENGTH_LONG).show();

			}else if (check == '3') {
				Toast.makeText(getApplicationContext(), "มีเซสชั่นค้างโปรดรอ 3 นาที", Toast.LENGTH_LONG).show();

			}else {
				Toast.makeText(getApplicationContext(), "ไม่สามารถเข้าสู่ระบบได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_LONG).show();

			}
			dialog.dismiss();
		}

	}


	@Override
	public void onBackPressed() {
		//finish();
		//System.exit(0);

		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);

	}

}
