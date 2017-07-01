package ebook;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import helper.PortalServices;
import helper.UrlApp;
import android.content.Context;
import meklib.MCrypt;

public class EbookData {
	
	private Context context;
	private PortalServices portalServices;
	
	private ArrayList<EbookData> arrDataEbookmain = new ArrayList<>();
	private ArrayList<EbookData> arrDataEbook = new ArrayList<>();
	
	private String id_ebook;
	private String name;
	private String imgs;
	private String colors;
	private String pdf_link;
	private String level_access;
	private MCrypt mcrypt = new MCrypt();
	
	
	public String getLevel_access() {
		return level_access;
	}
	public void setLevel_access(String level_access) {
		this.level_access = level_access;
	}
	public String getId_ebook() {
		return id_ebook;
	}
	public void setId_ebook(String id_ebook) {
		this.id_ebook = id_ebook;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImgs() {
		return imgs;
	}
	public void setImgs(String imgs) {
		this.imgs = imgs;
	}
	public String getColors() {
		return colors;
	}
	public void setColors(String colors) {
		this.colors = colors;
	}
	
	public String getPdf_link() {
		return pdf_link;
	}
	public void setPdf_link(String pdf_link) {
		this.pdf_link = pdf_link;
	}
	public EbookData() {
		portalServices = new PortalServices();
	}
	
	public EbookData(String id,String name,String imgs,String pdf_link) {
		this.id_ebook = id;
		this.name = name;
		this.imgs = imgs;
		this.pdf_link = pdf_link;
	}
	
	public EbookData(String id_ebook,String name,String imgs,String colors,String level_access) {
		this.id_ebook = id_ebook;
		this.name = name;
		this.imgs = imgs;
		this.colors = colors;
		this.level_access =level_access;
	}
	
	public ArrayList<EbookData> getMain_Ebook(){
		String resultData = portalServices.makePortalCall(null, UrlApp.EBOOK_CATEGORY, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				id_ebook = jData.getString("id");
				name = jData.getString("name");
				imgs = jData.getString("imgs");
				colors = jData.getString("colors");
				level_access = jData.getString("level_access");
				
				arrDataEbookmain.add(new EbookData(id_ebook, name, imgs, colors,level_access));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrDataEbookmain;
		
	}
	
	public ArrayList<EbookData> getCate_Ebook(String id){
		String resultData = portalServices.makePortalCall(null, UrlApp.EBOOK_ID+"?id="+id, PortalServices.GET);
		try {
			String decrypted = new String(mcrypt.decrypt(resultData));
			JSONObject jsonObject = new JSONObject(decrypted);
			JSONArray jArrData = jsonObject.getJSONArray("data");
			for (int i = 0; i < jArrData.length(); i++) {
				JSONObject jData = jArrData.getJSONObject(i);
				id_ebook = jData.getString("id");
				name = jData.getString("name");
				imgs = jData.getString("imgs");
				pdf_link = jData.getString("pdf_link");
				
				arrDataEbook.add(new EbookData(id_ebook,name, imgs, pdf_link));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arrDataEbook;
		
	}
	
	

}
