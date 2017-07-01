package iptv;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;

public class IpTvData {
	
	private Context context;
	private PortalServices portalServices;
	
	private ArrayList<IpTvData> arrDataTvMain = new ArrayList<IpTvData>();
	private ArrayList<IpTvData> arrDataTvCat = new ArrayList<IpTvData>();
	private ArrayList<IpTvData> arrDataTvList = new ArrayList<IpTvData>();
	
	private String tv_id;
	private String tv_name;
	private String tv_img;
	private String tv_color;
	private String tv_link;
	private MCrypt mcrypt = new MCrypt();
	
	public IpTvData(Context context) {
		super();
		this.context = context;
		portalServices = new PortalServices();
	}
	
	public IpTvData(String tv_id,String tv_name,String tv_img) {
		this.tv_id = tv_id;
		this.tv_name =tv_name;
		this.tv_img = tv_img;
	}
	
	public IpTvData(String tv_id,String tv_name,String tv_img,String tv_color) {
		this.tv_id = tv_id;
		this.tv_name =tv_name;
		this.tv_img = tv_img;
		this.tv_color = tv_color;
	}
	public IpTvData(String tv_id,String tv_name,String tv_img,String tv_color,String tv_link) {
		this.tv_id = tv_id;
		this.tv_name =tv_name;
		this.tv_img = tv_img;
		this.tv_color = tv_color;
		this.tv_link  = tv_link;
	}

	public String getTv_id() {
		return tv_id;
	}

	public void setTv_id(String tv_id) {
		this.tv_id = tv_id;
	}

	public String getTv_name() {
		return tv_name;
	}

	public void setTv_name(String tv_name) {
		this.tv_name = tv_name;
	}

	public String getTv_img() {
		return tv_img;
	}

	public void setTv_img(String tv_img) {
		this.tv_img = tv_img;
	}

	public String getTv_color() {
		return tv_color;
	}

	public void setTv_color(String tv_color) {
		this.tv_color = tv_color;
	}
	
	public String getTv_link() {
		return tv_link;
	}

	public void setTv_link(String tv_link) {
		this.tv_link = tv_link;
	}

	public ArrayList<IpTvData> getMain(){
		String resultData = portalServices.makePortalCall(null, UrlApp.MAIN_TV, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				tv_id = jData.getString("id");
				tv_name = jData.getString("name");
				tv_img = jData.getString("img");
				tv_color = jData.getString("colors");
				
				arrDataTvMain.add(new IpTvData(tv_id, tv_name, tv_img, tv_color));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataTvMain;
		
	}
	
	public ArrayList<IpTvData> getCategory(String main_id){
		String resultData = portalServices.makePortalCall(null, UrlApp.CATEGORY_TV+"?main_id="+main_id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				tv_id = jData.getString("id");
				tv_name = jData.getString("name");
				tv_img = jData.getString("imgs");
				tv_color = jData.getString("colors");
				
				arrDataTvCat.add(new IpTvData(tv_id, tv_name, tv_img, tv_color));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataTvCat;
		
	}
	public ArrayList<IpTvData> getList(String id){
		String resultData = portalServices.makePortalCall(null, UrlApp.CATEGORY_TV+"?id="+id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				tv_id = jData.getString("id");
				tv_name = jData.getString("name");
				tv_img = jData.getString("logo");
				tv_color = "";
				tv_link = jData.getString("link");
				
				arrDataTvList.add(new IpTvData(tv_id, tv_name, tv_img,tv_color,tv_link));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataTvList;
		
	}


}
