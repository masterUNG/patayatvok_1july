package user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;
import com.royle.beartv2.ServiceCheck;

import org.json.JSONObject;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;

public class UserProfileActivity extends Activity {
	
	//widget
	private TextView txtUsername;
	private TextView txtLevel;
	private TextView txtExpire;
	private Button btnLogin;
	private Button btnRegister;
	private Button btnChangePass;
	private Button btnLogout;
	private TextView txtSlide;
	private TextView txtMacAddress;
	private TextView txtIp;
	
	private DataStore dataStore;
	private String username;
	private String expire;
	private MCrypt mcrypt = new MCrypt();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_activity_user_profile);
		
		initWidget();
		dataStore = new DataStore(getBaseContext());
		username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");
		expire = dataStore.LoadSharedPreference(DataStore.USER_EXPIRE, "");
		new getIpTask().execute();
		
		txtUsername.setText("สมาชิก : " + username);
		txtLevel.setText("สถานะ : "+dataStore.LoadSharedPreference(DataStore.USER_LEVEL, ""));
		txtExpire.setText("วันที่หมดอายุ : "+ expire);
		
		
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
				startActivity(intent);
			}
		});
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserProfileActivity.this,RegisterActivity.class);
				startActivity(intent);
			}
		});
		
		btnChangePass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent  intent = new Intent(UserProfileActivity.this,ChangePassActivity.class);
				startActivity(intent);
			}
		});

		btnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logout(UserProfileActivity.this);
				Intent intent = new Intent(UserProfileActivity.this,ServiceCheck.class);
				stopService(intent);
				finish();

				Toast.makeText(getApplicationContext(), "ออกจากระบบเรียบร้อย", Toast.LENGTH_LONG).show();
				Thread timerThread = new Thread() {
					public void run() {
						try {
							sleep(0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							android.os.Process.killProcess(android.os.Process.myPid());
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);


						}
					}
				};
				timerThread.start();

			}

		});

		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (true) {
			txtUsername.setVisibility(View.VISIBLE);
			txtExpire.setVisibility(View.VISIBLE);
			txtLevel.setVisibility(View.VISIBLE);
			btnLogin.setVisibility(View.GONE);
			btnRegister.setVisibility(View.GONE);
		} else {
			txtUsername.setVisibility(View.GONE);
			txtExpire.setVisibility(View.GONE);
			txtLevel.setVisibility(View.GONE);
			btnLogin.setVisibility(View.VISIBLE);
			btnRegister.setVisibility(View.VISIBLE);
		}
	}
	public void initWidget(){
		txtUsername = (TextView) findViewById(R.id.txtUserName_userProfile);
		txtExpire = (TextView) findViewById(R.id.txtExpire_userProfile);
		txtLevel = (TextView) findViewById(R.id.txtLevel_userProfile);
		btnLogin = (Button) findViewById(R.id.btnLogin_userProfile);
		btnRegister = (Button) findViewById(R.id.btnRegister_userProfile);
		btnChangePass = (Button) findViewById(R.id.btnChangePass_userProfile);
		btnLogout = (Button) findViewById(R.id.btnLogout_userProfile);
		txtSlide = (TextView) findViewById(R.id.txtSlide);
		txtIp = (TextView) findViewById(R.id.txtIp);
		txtSlide.setSelected(true);
		txtMacAddress = (TextView) findViewById(R.id.txtMacAdress);
		txtMacAddress.setText("Mac Address : "+MainActivity.getMacAddress(UserProfileActivity.this));
	}
	
	public static void logout(Context context){
		DataStore dataStore = new DataStore(context);
		dataStore.SavedSharedPreference(DataStore.USER_ID, "");
		dataStore.SavedSharedPreference(DataStore.USER_NAME, "");
		dataStore.SavedSharedPreference(DataStore.PASSWORD, "");
		dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "");
		dataStore.SavedSharedPreference(DataStore.USER_LEVEL_ID, "");
		dataStore.SavedSharedPreference(DataStore.USER_EXPIRE, "");
		dataStore.SavedSharedPreference(DataStore.USER_TOKEN, "");
		dataStore.ClearSharedPreference();
	}

	private class getIpTask extends AsyncTask<Void,Void,Void>{

		private String ip;

		@Override
		protected Void doInBackground(Void... params) {
			PortalServices portalServices = new PortalServices();
			String result = portalServices.makePortalCall(null, UrlApp.GET_IP,PortalServices.GET);
			try {
				String decrypted = new String(mcrypt.decrypt(result));
				JSONObject jsonObject = new JSONObject(decrypted);
				ip = jsonObject.getString("ip");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			txtIp.setText("IP : "+ip);
		}
	}



}
