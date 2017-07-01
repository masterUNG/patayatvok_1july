package helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class DataStore {
	//language
	public static final String APP_LANG = "app_lang";
	public static final String EN = "en";
	public static final String TH = "th";
	//user
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "username";
	public static final String PASSWORD = "password";
	public static final String USER_LEVEL = "level";
	public static final String USER_LEVEL_ID = "level_id";
	public static final String USER_EXPIRE = "expiry_date";
	public static final String USER_TOKEN = "token";
	//other
	public static final String TEXT_SLIDE = "txt_slide";
	public static final String IMG_PATH = "img_path";
	
	

	private Context context;
	private String def_value;
	private SharedPreferences prefer;
	
	public DataStore(Context context) {
		this.context = context;
	}
	public void SavedSharedPreference(String sharedkey, String value)
	{
		prefer = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefer.edit();
		editor.putString(sharedkey, value);
		editor.commit();
	}
	public String LoadSharedPreference(String sharedkey,String def_value)
	{
		prefer = PreferenceManager.getDefaultSharedPreferences(context);
		return prefer.getString(sharedkey, def_value);
	}
	public void ClearSharedPreference()
	{
		prefer = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefer.edit();
		editor.clear();
		editor.commit();
	}
	public Boolean checkUser(){
		prefer = PreferenceManager.getDefaultSharedPreferences(context);
		String user_id = prefer.getString(USER_ID, "");
		if (user_id.equals("")) {
			return false;
		}else {
			return true;
		}
		

	}



}
