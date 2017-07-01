package user;

import com.royle.beartv2.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ErrorActivity extends Activity {
	//widget

	private ImageButton btnerror;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_activity_error);
		
		initWidget();



		btnerror.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}
	
	public void initWidget(){

		btnerror = (ImageButton) findViewById(R.id.btnerror);
	}

}