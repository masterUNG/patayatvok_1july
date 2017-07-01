package com.royle.beartv2;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import helper.DataStore;
import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;

public class OtherData {
	
	private Context context;
	private PortalServices portalServices;
	private DataStore dataStore;
	
	private String imgShow;
	private String news_text;
	private MCrypt mcrypt = new MCrypt();
	
	private ArrayList<OtherData> arrData = new ArrayList<OtherData>();
	
	public String getImgShow() {
		return imgShow;
	}

	public void setImgShow(String imgShow) {
		this.imgShow = imgShow;
	}

	public String getNews_text() {
		return news_text;
	}

	public void setNews_text(String news_text) {
		this.news_text = news_text;
	}

	public OtherData(Context context) {
		super();
		this.context = context;
		portalServices = new PortalServices();
		dataStore = new DataStore(context);
	}
	public OtherData(String imgShow,String news_text) {
		this.imgShow = imgShow;
		this.news_text = news_text;
				
	}
	
	public void getOther(){
		String resultData = portalServices.makePortalCall(null, UrlApp.OTHER, PortalServices.GET);


		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			String imgPath = jsonObject.getString("img_path");
			JSONObject jData = jsonObject.getJSONObject("data");
			String txtSlide = jData.getString("text_silde");
			dataStore.SavedSharedPreference(DataStore.TEXT_SLIDE, txtSlide);
			dataStore.SavedSharedPreference(DataStore.IMG_PATH, imgPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<OtherData> getSlide() {
		String resultData = portalServices.makePortalCall(null, UrlApp.SLIDE, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONArray jsonArray = new JSONArray(decrypted);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				imgShow = jsonObject.getString("imgs_show");
				news_text = jsonObject.getString("news_text");
				arrData.add(new OtherData(imgShow, news_text));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrData;
	}




}
