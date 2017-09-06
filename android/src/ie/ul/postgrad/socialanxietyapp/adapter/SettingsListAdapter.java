package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;

/**
 * Created by Robert on 10-Jul-17.
 */

public class SettingsListAdapter extends BaseAdapter {

    private String[] result;
    private static LayoutInflater inflater = null;

    public SettingsListAdapter(Context context, String[] menuItems) {
        result = menuItems;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SettingsListAdapter.Holder holder = new SettingsListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_settings_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_title);
        holder.tv.setText(result[position]);
        return rowView;
    }
}
