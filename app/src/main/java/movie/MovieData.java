package movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helper.PortalServices;
import helper.UrlApp;
import meklib.MCrypt;

public class MovieData {
	
	private PortalServices portalServices;
	
	private ArrayList<MovieData> arrDataMovieMain = new ArrayList<MovieData>();
	private ArrayList<MovieData> arrDataMovieCategory = new ArrayList<MovieData>();
	private ArrayList<MovieData> arrDataMovieList = new ArrayList<MovieData>();
	private ArrayList<MovieData> arrDataMovieLast = new ArrayList<MovieData>();
	private ArrayList<MovieData> arrDataSearch = new ArrayList<MovieData>();
	
	private String movie_id;
	private String movie_name;
	private String movie_img;
	private String movie_color;
	private String movie_link;
	private String movie_link_hd;
	private String movie_link_mobile;
	private String movie_link_4k;
	private String movie_des;
	private String level_access;
	private String type;
	private MCrypt mcrypt = new MCrypt();
	
	public MovieData() {
		portalServices = new PortalServices();
	}
	
	public MovieData(String movie_id,String movie_name,String movie_img,String movie_color,String level_access) {
		this.movie_id = movie_id;
		this.movie_name = movie_name;
		this.movie_img = movie_img;
		this.movie_color = movie_color;
		this.level_access = level_access;
	}
	public MovieData(String movie_id,String movie_name,String movie_img,String movie_des,String movie_link
			,String movie_link_hd,String movie_link_mobile,String movie_4k,String level_access,String type) {
		this.movie_id = movie_id;
		this.movie_name = movie_name;
		this.movie_img = movie_img;
		this.movie_des = movie_des;
		this.movie_link = movie_link;
		this.movie_link_hd = movie_link_hd;
		this.movie_link_mobile = movie_link_mobile;
		this.movie_link_4k = movie_4k;
		this.level_access = level_access;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMovie_id() {
		return movie_id;
	}
	public void setMovie_id(String movie_id) {
		this.movie_id = movie_id;
	}
	public String getMovie_name() {
		return movie_name;
	}
	public void setMovie_name(String movie_name) {
		this.movie_name = movie_name;
	}
	public String getMovie_img() {
		return movie_img;
	}
	public void setMovie_img(String movie_img) {
		this.movie_img = movie_img;
	}
	public String getMovie_color() {
		return movie_color;
	}
	public void setMovie_color(String movie_color) {
		this.movie_color = movie_color;
	}
	public String getMovie_link() {
		return movie_link;
	}
	public void setMovie_link(String movie_link) {
		this.movie_link = movie_link;
	}
	
	public String getMovie_link_hd() {
		return movie_link_hd;
	}

	public void setMovie_link_hd(String movie_link_hd) {
		this.movie_link_hd = movie_link_hd;
	}

	public String getMovie_link_mobile() {
		return movie_link_mobile;
	}

	public void setMovie_link_mobile(String movie_link_mobile) {
		this.movie_link_mobile = movie_link_mobile;
	}

	public String getMovie_des() {
		return movie_des;
	}

	public void setMovie_des(String movie_des) {
		this.movie_des = movie_des;
	}

	public String getMovie_link_4k() {
		return movie_link_4k;
	}

	public void setMovie_link_4k(String movie_link_4k) {
		this.movie_link_4k = movie_link_4k;
	}

	public String getLevel_access() {
		return level_access;
	}

	public void setLevel_access(String level_access) {
		this.level_access = level_access;
	}

	public ArrayList<MovieData> getMainMovie(){
		String resultData = portalServices.makePortalCall(null, UrlApp.MAIN_MOVIE, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				movie_id = jData.getString("id");
				movie_name = jData.getString("main_category_name");
				movie_img = jData.getString("img");
				movie_color = jData.getString("colors");
				level_access = jData.getString("level_access");
				
				arrDataMovieMain.add(new MovieData(movie_id, movie_name, movie_img, movie_color,level_access));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataMovieMain;
		
	}
	public ArrayList<MovieData> getCategoryMovie(String main_id){
		String resultData = portalServices.makePortalCall(null, UrlApp.CATEGORY_MOVIE+"?main_id="+main_id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				movie_id = jData.getString("id");
				movie_name = jData.getString("name");
				movie_img = jData.getString("imgs");
				movie_color = "";
				
				arrDataMovieCategory.add(new MovieData(movie_id, movie_name, movie_img, movie_color,""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataMovieCategory;
		
	}
	
	public ArrayList<MovieData> getListMovie(String id){
		String resultData = portalServices.makePortalCall(null, UrlApp.CATEGORY_MOVIE+"?id="+id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				movie_id = jData.getString("id");
				movie_name = jData.getString("movie_name");
				movie_img = jData.getString("movie_thumb");
				movie_des = jData.getString("movie_description");
				movie_link = jData.getString("link");
				movie_link_hd = jData.getString("link_hd");
				movie_link_mobile = jData.getString("link_mobile");
				movie_link_4k = jData.getString("link_4k");
				
				arrDataMovieList.add(new MovieData(movie_id, movie_name, movie_img, movie_des, movie_link, movie_link_hd, movie_link_mobile,movie_link_4k,level_access,""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrDataMovieList;
		
	}
	
	public ArrayList<MovieData> getLastMovie(){
		String resultData = portalServices.makePortalCall(null, UrlApp.LAST_MOVIE, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONArray jsonArray = new JSONArray(decrypted);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jData = jsonArray.getJSONObject(i);
				movie_id = jData.getString("id");
				movie_name = jData.getString("movie_name");
				movie_img = jData.getString("movie_thumb");
				movie_des = jData.getString("movie_description");
				movie_link = jData.getString("link");
				movie_link_hd = jData.getString("link_hd");
				movie_link_mobile = jData.getString("link_mobile");
				movie_link_4k = jData.getString("link_4k");
				level_access = jData.getString("level_access");
				
				arrDataMovieLast.add(new MovieData(movie_id, movie_name, movie_img, movie_des, movie_link, movie_link_hd, movie_link_mobile,movie_link_4k,level_access,""));
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrDataMovieLast;
		
	}
	public ArrayList<MovieData> getSearch(String name){
		String result = portalServices.makePortalCall(null, UrlApp.SEARCH+"?name="+name, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(result));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrDAta = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrDAta.length(); i++) {
				JSONObject jData = jArrDAta.getJSONObject(i);
				type = jData.getString("type");
				if (type.equals("movie")){
					movie_id = jData.getString("id");
					movie_name = jData.getString("movie_name");
					movie_img = jData.getString("movie_thumb");
					movie_des = jData.getString("movie_description");
					movie_link = jData.getString("link");
					movie_link_hd = jData.getString("link_hd");
					movie_link_mobile = jData.getString("link_mobile");
					movie_link_4k = jData.getString("link_4k");
					level_access = jData.getString("level_access");
				}else {
					movie_id = jData.getString("id");
					movie_name = jData.getString("name");
					movie_img = jData.getString("series_thumb");
					movie_des = "";
					movie_link = "";
					movie_link_hd = "";
					movie_link_mobile = "";
					movie_link_4k = "";
					level_access = jData.getString("level_access");
				}
				
				arrDataSearch.add(new MovieData(movie_id, movie_name, movie_img, movie_des, movie_link, movie_link_hd, movie_link_mobile,movie_link_4k,level_access,type));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return arrDataSearch;
		
	}


	

}
