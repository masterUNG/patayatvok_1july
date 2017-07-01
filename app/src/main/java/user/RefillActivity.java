package user;

import com.royle.beartv2.R;
import com.royle.beartv2.WebViewPayPalActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.DataStore;
import helper.PortalServices;

public class RefillActivity extends Activity {
	//widget
	private DataStore dataStore;
	private String username;
	private String password;
	private TextView txtExpire;
	public PortalServices portalServices;
	private AlertDialog alertDialog;

	private Button btnPaypal;
	private Button btnTrueMoney;
	private Button btnPinCode;
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_refill);
		
		initWidget();

		//progressDialog = new ProgressDialog(this);
		portalServices = new PortalServices();
		dataStore = new DataStore(getBaseContext());

		username = dataStore.LoadSharedPreference(DataStore.USER_NAME, "");
		password = dataStore.LoadSharedPreference(DataStore.PASSWORD, "");
		txtExpire = (TextView) findViewById(R.id.txtExpire_main);

		

		
		btnTrueMoney.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent   intent = new Intent(RefillActivity.this,CheckTrueMoneyActivity.class);
				startActivity(intent);
				finish();
			}
		});

		btnPaypal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (true) {
					intent = new Intent(RefillActivity.this, WebViewPayPalActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
				} else {
					startActivity(new Intent(RefillActivity.this, LoginActivity.class));
				}
			}
		});

		btnPinCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (true) {
					dialogCode();
				} else {
					startActivity(new Intent(RefillActivity.this, LoginActivity.class));
				}
			}
		});
	}
	
	public void initWidget(){
		btnPaypal = (Button) findViewById(R.id.btnPaypal);
		btnTrueMoney = (Button) findViewById(R.id.btnTruemoney);
		btnPinCode = (Button) findViewById(R.id.btnPincode);
	}

	private void dialogCode() {
		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_code, null);
		dialogBuilder.setView(dialogView);
		dialogBuilder.setCancelable(false);
		final EditText editText = (EditText) dialogView.findViewById(R.id.edtCode);
		dialogBuilder.setPositiveButton("ตกลง", null)
				.setNegativeButton("ยกเลิก", null);
		alertDialog = dialogBuilder.create();
		alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String code = editText.getText().toString().trim();
						DataStore dataStore = new DataStore(getApplicationContext());
						String userId = dataStore.LoadSharedPreference(DataStore.USER_ID, "");
						new RefillCodeTask(userId,code).execute();
					}
				});
			}
		});

		alertDialog.show();
	}

	public class RefillCodeTask extends AsyncTask<Void, Void, String> {
		private String code;
		private String userId;

		public RefillCodeTask(String code, String userId) {
			this.code = code;
			this.userId = userId;
		}

		@Override
		protected String doInBackground(Void... params) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("id", code));
			list.add(new BasicNameValuePair("code", userId));
			String resultData = portalServices.makePortalCall(null, "http://saleilike.4kmoviestar.com/code/re",
					PortalServices.POST, list);
			Log.e("refill_code", resultData);
			return resultData;
		}


		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			try {
				JSONObject jsonObject = new JSONObject(s);
				int status = jsonObject.getInt("status");
				if (status == 0) { //incorrect
					String txt = jsonObject.getString("txt");
					Toast.makeText(RefillActivity.this, txt, Toast.LENGTH_SHORT).show();
				} else {
					alertDialog.dismiss();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
