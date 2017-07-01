package user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;


public class RegisterActivity extends Activity {
	
	//widget
	private EditText edtUsername;
	private EditText edtPassword;
	private EditText edtPasswordAgain;
	private EditText edtEmail;
	private Button btnRegister;
	private Button btnRegisterCancel;
	
	private ProgressDialog dialog;
	private PortalServices portalServices;
	
	private String username;
	private String password;
	private String password_again;
	private String email;
	private MCrypt mcrypt = new MCrypt();

	private TextView txtMacAddress;
	private TextView txtANDROID_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_activity_register);


		dialog = new ProgressDialog(RegisterActivity.this);
		portalServices = new PortalServices();

		initWidget();

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				username = edtUsername.getText().toString();
				password = edtPassword.getText().toString();
				password_again = edtPasswordAgain.getText().toString();
				//email = edtEmail.getText().toString();

				if (edtUsername.length() < 10 || edtUsername.length() > 10) {
					edtUsername.setError("กรุณาใส่เบอร์โทรศัพท์");
					edtUsername.requestFocus();
				} else {

					if (edtPassword.length() < 4 ||
							edtPassword.length() > 10) {
						edtPassword.setError("กรุณาใส่ตัวเลขรหัสผ่าน 4-10 ตัวเลข");
						edtPassword.requestFocus();
					} else {

						if (password.equals(password_again)) {
							//if (username.length() == 0 || password.length() == 0 || email.length() == 0) {
							if (username.length() == 0 || password.length() == 0) {
								Toast.makeText(getApplicationContext(), "กรุณากรอกให้ครบทุกช่อง", Toast.LENGTH_LONG).show();
							} else {
								new registerTask().execute();
							}
						} else {
							Toast.makeText(getApplicationContext(), "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_LONG).show();
						}


					}

				}
			}
		});

		btnRegisterCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
//				MainActivity.activity=true;

				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				startActivity(intent);


			}
		});



	}
	private void initWidget(){
		edtUsername = (EditText) findViewById(R.id.edtUsername_register);
		edtPassword = (EditText) findViewById(R.id.edtPassword_register);
		edtPasswordAgain = (EditText) findViewById(R.id.edtPassword_again);
		//edtEmail = (EditText) findViewById(R.id.edtEmail_register);
		btnRegister = (Button) findViewById(R.id.btnRegister_register);
		btnRegisterCancel = (Button) findViewById(R.id.btnRegister_cancel);
		txtMacAddress = (TextView) findViewById(R.id.txtMacAdress);
		txtMacAddress.setText("Mac Address : "+MainActivity.getMacAddress(RegisterActivity.this));
		txtANDROID_ID = (TextView) findViewById(R.id.txtANDROID_ID);
		txtANDROID_ID.setText("ANDROID_ID : "+MainActivity.getAndroidId(RegisterActivity.this));
	}

	private class registerTask extends AsyncTask<Void, Void, Void>{
		Boolean check;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("username", username));
			list.add(new BasicNameValuePair("password", password));
			list.add(new BasicNameValuePair("email", email));
			list.add(new BasicNameValuePair("macAddress", MainActivity.getMacAddress(RegisterActivity.this)));
			list.add(new BasicNameValuePair("getAndroidId", MainActivity.getAndroidId(RegisterActivity.this)));
			String resultdata= portalServices.makePortalCall(null, UrlApp.REGISTER, PortalServices.POST, list);
			try {
				String decrypted = new String(mcrypt.decrypt(resultdata));
				JSONObject jsonObject = new JSONObject(decrypted);
				if (jsonObject.has("status")) {
					String status = jsonObject.getString("status");
					if (status.equals("success")) {
						check = true;
					}else {

						check = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				check = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (check) {
				finish();
				Toast.makeText(getApplicationContext(), "สมัครสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_LONG).show();
//				MainActivity.activity=true;
				getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
				Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
				startActivity(intent);

			}else {
				Toast.makeText(getApplicationContext(), "ไม่สามารถสมัครได้ อาจเพราะมีเบอร์โทรนี้ในระบบแล้ว", Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onBackPressed() {
		//finish();
		//System.exit(0);


		Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
		startActivity(intent);

	}



}
