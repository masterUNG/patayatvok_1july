package movie;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.royle.beartv2.R;

import java.util.ArrayList;

public class MovieCategoryAdapter extends ArrayAdapter<MovieData>{

	private Context context;
	private ArrayList<MovieData> arrData;
	private LayoutInflater inflater = null;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayImageOptions;

	private Holder holder;

	static final class Holder {
		private ImageView imageView;
		private TextView txtName;
	}

	public MovieCategoryAdapter(Context context, int resource, ArrayList<MovieData> arrData) {
		super(context, resource, arrData);
		this.context = context;
		this.arrData = arrData;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
		displayImageOptions = new DisplayImageOptions.Builder()
				.displayer(new RoundedBitmapDisplayer(5))
				.showImageOnFail(R.drawable.ic_fail)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnLoading(R.drawable.ic_load)
				.resetViewBeforeLoading(true)
				.cacheOnDisc(true)
				.cacheInMemory(true)
				.postProcessor(null)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(200))
				.build();




	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		holder = new Holder();
		if (view == null) {
			view = inflater.inflate(R.layout.movie_row, null);
			holder.imageView = (ImageView) view.findViewById(R.id.img_movieRow);
			holder.txtName = (TextView) view.findViewById(R.id.txtName_movieRow);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		if (arrData != null) {
			holder.txtName.setText(arrData.get(position).getMovie_name());
			imageLoader.displayImage(arrData.get(position).getMovie_img(), holder.imageView, displayImageOptions);
		} else {
			Log.e("", "null");
		}
		return view;
	}

}
