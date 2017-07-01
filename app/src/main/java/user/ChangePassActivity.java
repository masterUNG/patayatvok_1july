package user;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ChangePassActivity extends Activity {
	//widget
	private EditText edtNewpass;
	private EditText edtNewPassAgain;
	private Button btnSave;

	private String newPass;
	private String newPassAgain;
	
	private DataStore dataStore;
	private PortalServices portalServices;
	private MCrypt mcrypt = new MCrypt();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pass);
		
		initWidget();
		dataStore = new DataStore(getApplicationContext());
		portalServices = new PortalServices();
		
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newPass = edtNewpass.getText().toString();
				newPassAgain = edtNewPassAgain.getText().toString();

				if (edtNewpass.length() < 4 ||
						edtNewpass.length() > 10) {

					edtNewpass.setError("กรุณาใส่ตัวเลข 4-10 ตัวเลข");

					edtNewpass.requestFocus();
				} else {
					if (newPass.equals(newPassAgain)) {
						new changePassTask().execute();
					} else {
						Toast.makeText(getApplicationContext(), "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
					}
				}
			}

		});

		
	}
	private void initWidget(){
		edtNewpass = (EditText) findViewById(R.id.edtNewPass);
		edtNewPassAgain = (EditText) findViewById(R.id.edtNewPassAgain);
		btnSave = (Button) findViewById(R.id.btnSave_changePass);
	}
	
	private class changePassTask extends AsyncTask<Void, Void, Void>{
		Boolean check;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("user_id", dataStore.LoadSharedPreference(DataStore.USER_ID, "")));
			list.add(new BasicNameValuePair("password", newPass));
			String resultData = portalServices.makePortalCall(null, UrlApp.CHANGEPASS, PortalServices.POST, list);
			try {
				String decrypted = new String(mcrypt.decrypt(resultData));
				JSONObject jsonObject = new JSONObject(decrypted);
				if (jsonObject.has("status")) {
					String status = jsonObject.getString("status");
					if (status.equals("1")) {
						check = true;
					}else {
						check = false;
					}
					
				}else {
					check = false;
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
			if (check) {
				Toast.makeText(getApplicationContext(), "เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
				dataStore.SavedSharedPreference(DataStore.USER_ID, "");
				dataStore.SavedSharedPreference(DataStore.USER_NAME, "");
				dataStore.SavedSharedPreference(DataStore.PASSWORD, "");
				dataStore.SavedSharedPreference(DataStore.USER_LEVEL, "");
				dataStore.SavedSharedPreference(DataStore.USER_EXPIRE, "");
				Intent intent = new Intent(ChangePassActivity.this,LoginActivity.class);
				startActivity(intent);
				finish();
			}else {
				Toast.makeText(getApplicationContext(), "ไม่สามารถเปลี่ยนรหัสผ่าน", Toast.LENGTH_SHORT).show();
			}
		}
		
	}



}
