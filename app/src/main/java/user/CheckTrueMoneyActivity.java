package user;

import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.royle.beartv2.MainActivity;
import com.royle.beartv2.R;


public class CheckTrueMoneyActivity extends Activity {
	//widget
	private EditText edtTrueMoney;
	private Button btnConfirm;
	
	private PortalServices portalServices;
	private DataStore dataStore;
	private ProgressDialog dialog;
	
	private String user_id;
	private String code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_true_money);
		
		dataStore = new DataStore(getApplicationContext());
		portalServices = new PortalServices();
		dialog = new ProgressDialog(this);

		initWidget();
		

		
		btnConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user_id = dataStore.LoadSharedPreference(DataStore.USER_ID, "");
				code = edtTrueMoney.getText().toString();
				if (true) {
					if (user_id.length()>0&&code.length()>0) {
						sentTrueMoney(user_id, code);
					}else {
						Toast.makeText(getApplicationContext(), "กรุณากรอก", Toast.LENGTH_SHORT).show();
					}
				}else {
					Intent intent = new Intent(CheckTrueMoneyActivity.this,LoginActivity.class);
					startActivity(intent);
				}
				
			}
		});
		
	}



	private void initWidget(){
		edtTrueMoney = (EditText) findViewById(R.id.edtTrueMoney_checkTrue);
		btnConfirm = (Button) findViewById(R.id.btnConfirm_checkTrue);
	}
	
	private void sentTrueMoney(String user_id,String code){
		new SentTrueMonetTask().execute(user_id,code);
	}
	public class SentTrueMonetTask extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}
		@Override
		protected Void doInBackground(String... params) {
			String resultData = portalServices.makePortalCall(null, UrlApp.CHECK_MONEY+params[0]+"/"+params[1], PortalServices.GET);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), "เรียบร้อย", Toast.LENGTH_SHORT).show();
			finish();
			Intent intent = new Intent(CheckTrueMoneyActivity.this,MainActivity.class);
			startActivity(intent);
		}
		
	}
}
