package iptv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.royle.beartv2.R;

import java.util.ArrayList;

public class IpTvAdapter extends ArrayAdapter<IpTvData>{
	private Context context;
	private ArrayList<IpTvData> arr_data;
	private LayoutInflater inflater = null;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayImageOptions;
	
	private int widthImg;
	private int heightImg;
	
	Holder holder;
	
	static class Holder{
		private ImageView img;
		private TextView txtName;
		private LinearLayout lnBg;
	}
	
	public IpTvAdapter(Context context, int resource,
			ArrayList<IpTvData> arrData,int widthImg,int heightImg) {
		super(context, resource,arrData);
		this.context = context;
		this.arr_data = arrData;
		this.widthImg = widthImg;
		this.heightImg = heightImg;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		displayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_fail)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnLoading(R.drawable.ic_load)
				.cacheOnDisc(true)
				.cacheInMemory(true)
				.postProcessor(null)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(200))
				.build();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		holder = new Holder();
		if (view==null) {
			view = inflater.inflate(R.layout.gallery_row, null);
			holder.img = (ImageView) view.findViewById(R.id.img_galleryRow);
			holder.txtName = (TextView) view.findViewById(R.id.txtName_galleryRow);
			holder.lnBg = (LinearLayout) view.findViewById(R.id.lnBg_galleryRow);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
		if (arr_data != null) {
			holder.txtName.setText(arr_data.get(position).getTv_name());
			LinearLayout.LayoutParams relative = new LinearLayout.LayoutParams(
					widthImg, heightImg);
			holder.img.setLayoutParams(relative);
			imageLoader.displayImage(arr_data.get(position).getTv_img()
					, holder.img, displayImageOptions);
			if (!arr_data.get(position).getTv_color().equals("")) {
				holder.lnBg.setBackgroundColor(Color.parseColor(arr_data.get(position).getTv_color()));
			}else {
				holder.lnBg.setBackgroundColor(Color.parseColor("#5C5A5A"));
				}
		}
		return view;
	}

}
