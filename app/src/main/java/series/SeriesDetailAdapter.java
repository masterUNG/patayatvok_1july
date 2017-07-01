package series;

import java.util.ArrayList;

import com.royle.beartv2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SeriesDetailAdapter extends ArrayAdapter<SeriesData>{
	private Context context;
	private ArrayList<SeriesData> arr_data;
	private LayoutInflater inflater = null;
	
	Holder holder;
	
	static class Holder{
		private TextView txtName;
	}
	
	public SeriesDetailAdapter(Context context, int resource,
			ArrayList<SeriesData> arrData) {
		super(context, resource,arrData);
		this.context = context;
		this.arr_data = arrData;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		holder = new Holder();
		if (view==null) {
			view = inflater.inflate(R.layout.row_series, null);
			holder.txtName = (TextView) view.findViewById(R.id.txt_rowSeries);
			view.setTag(holder);
		}else {
			holder = (Holder) view.getTag();
		}
		if (arr_data != null) {
			holder.txtName.setText(arr_data.get(position).getseries_name());
		}
		return view;
	}
	public float getScale(boolean focused, int offset) {
        /* Formula: 1 / (2 ^ offset) */
    return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
}

}
