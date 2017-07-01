package helper;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PortalServices {

	private static String response = null;
//	public static final String oauth = "http://mingmitroauth.pla2api.com/";
//	public static final String api = "http://mingmitrapi.pla2api.com/";
	public static final String registered = "&access_token=";
	public static final String non_registered = "&android_token=";
	public final static int GET = 1;
	public final static int POST = 2;
	public final static int PUT = 3;
	public final static int DEL = 4;
	public final static String KEY = "X-Auth-Token";
	private final static String ENCODE_FORMAT = "utf-8";
	
	public PortalServices()
	{
		
	}
	public String makePortalCall(String token, String url, int method)
	{
		Log.i("URL : ", url);
		return this.makePortalCall(token, url, method, null);
	}
	public String makePortalCall(String token, String url, int method, List<NameValuePair> params)
	{
		Log.i("URL : ", url);
		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;
			if(method == POST)
			{
				HttpPost httpPost = new HttpPost(url);
					if(token != null && token.length() > 0)
					{
						httpPost.setHeader(KEY, token);
					}
					if(params != null)
					{
					httpPost.setEntity(new UrlEncodedFormEntity(params,ENCODE_FORMAT));
					}
				httpResponse = httpClient.execute(httpPost);
			}
			else if(method == GET)
			{
				if(params != null)
				{
					String paramStr = URLEncodedUtils.format(params, ENCODE_FORMAT);
					url += "?" + paramStr;
					Log.e(url, url);
				}
				HttpGet httpGet = new HttpGet(url);
				if(token != null && token.length() > 0)
				{
					httpGet.setHeader(KEY, token);					
				}
				httpResponse = httpClient.execute(httpGet);
			}
			else if(method  == PUT)
			{
				HttpPut httpPut = new HttpPut(url);
				if(params !=null)
				{
					httpPut.setHeader(KEY, token);
					httpPut.setEntity(new UrlEncodedFormEntity(params,ENCODE_FORMAT));
				}
				httpResponse = httpClient.execute(httpPut);
			}
			else if(method == DEL)
			{
				HttpDelete httpDel = new HttpDelete(url);
				if(token != null && token.length() > 0)
				{
					httpDel.setHeader(KEY, token);
				}
				httpResponse = httpClient.execute(httpDel);
			}
			httpEntity  = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
		}catch(UnsupportedEncodingException e1)
		{
			e1.printStackTrace();
		}catch(ClientProtocolException e2)
		{
			e2.printStackTrace();
		}catch(IOException e3)
		{
			e3.printStackTrace();
		}
		return response;
	}



}
