package series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;

public class SeriesData {
	
	private PortalServices portalServices;
	
	private ArrayList<SeriesData> arrDataSeriesMain = new ArrayList<SeriesData>();
	private ArrayList<SeriesData> arrDataSeriesCategory = new ArrayList<SeriesData>();
	private ArrayList<SeriesData> arrDataSeriesSeason = new ArrayList<SeriesData>();
	private ArrayList<SeriesData> arrDataSeriesList = new ArrayList<SeriesData>();
	private ArrayList<SeriesData> arrDataSeriesSearch = new ArrayList<SeriesData>();

	
	
	private String series_id;
	private String series_name;
	private String series_level_access;
	private String series_img;
	private String series_color;
	private String series_link;
	private String type;
	private MCrypt mcrypt = new MCrypt();
	
	public SeriesData() {
		portalServices = new PortalServices();
	}
	
	public SeriesData(String series_id,String series_name,String series_img) {
		this.series_id = series_id;
		this.series_name = series_name;
		this.series_img = series_img;
	}
	
	public SeriesData(String series_id,String series_name,String series_img,String series_color) {
		this.series_id = series_id;
		this.series_name = series_name;
		this.series_img = series_img;
		this.series_color = series_color;
	}
	
	
	public String getseries_id() {
		return series_id;
	}
	public void setseries_id(String series_id) {
		this.series_id = series_id;
	}
	public String getseries_name() {
		return series_name;
	}
	public void setseries_name(String series_name) {
		this.series_name = series_name;
	}
	public String getseries_img() {return series_img;}
	public void setseries_img(String series_img) {
		this.series_img = series_img;
	}
	public String getseries_color() {
		return series_color;
	}
	public void setseries_color(String series_color) {
		this.series_color = series_color;
	}
	public String getseries_link() {
		return series_link;
	}
	public void setseries_link(String series_link) {
		this.series_link = series_link;
	}

	public ArrayList<SeriesData> getMainSeries(){
		String resultData = portalServices.makePortalCall(null, UrlApp.MAIN_Series, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				series_id = jData.getString("id");
				series_name = jData.getString("main_cat_series_name");
				series_img = jData.getString("img");
				series_color = jData.getString("main_cat_series_color");
				if (series_color.equals("null")) {
					series_color = "";
				}
				
				arrDataSeriesMain.add(new SeriesData(series_id, series_name, series_img, series_color));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataSeriesMain;
		
	}
	public ArrayList<SeriesData> getCategorySeries(String main_id){
		String resultData = portalServices.makePortalCall(null, UrlApp.CATEGORY_Series+"?main_id="+main_id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				series_id = jData.getString("id");
				series_name = jData.getString("name");
				series_img = jData.getString("imgs");
				
				arrDataSeriesCategory.add(new SeriesData(series_id, series_name, series_img));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataSeriesCategory;
		
	}
	
	public ArrayList<SeriesData> getSeasonSeries(String main_id){
		String resultData = portalServices.makePortalCall(null, UrlApp.SEASON_Series+"?id="+main_id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				series_id = jData.getString("id");
				series_name = jData.getString("name");
				series_img = jData.getString("imgs");
				
				arrDataSeriesSeason.add(new SeriesData(series_id, series_name, series_img));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataSeriesSeason;
		
	}
	
	public ArrayList<SeriesData> getSeries(String main_id){
		String resultData = portalServices.makePortalCall(null, UrlApp.Series+"?id="+main_id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				series_id = jData.getString("id");
				series_name = jData.getString("name");
				series_img = jData.getString("link");
				
				arrDataSeriesList.add(new SeriesData(series_id, series_name, series_img));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataSeriesList;
		
	}

	public ArrayList<SeriesData> getSRSearch(String name){
		String result = portalServices.makePortalCall(null, UrlApp.SEARCH+"?name="+name, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(result));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrDAta = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrDAta.length(); i++) {
				JSONObject jData = jArrDAta.getJSONObject(i);
				type = jData.getString("type");
				if (type.equals("series")){
					series_id = jData.getString("id");
					series_name = jData.getString("name");
					series_img = jData.getString("series_thumb");
					series_level_access = jData.getString("level_access");

				}

				arrDataSeriesSearch.add(new SeriesData(series_id, series_name, series_img));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return arrDataSeriesSearch;

	}

	
}
