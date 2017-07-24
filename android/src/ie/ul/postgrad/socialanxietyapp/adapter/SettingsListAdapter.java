package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.account.WelcomeActivity;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

/**
 * Created by Robert on 10-Jul-17.
 */

public class SettingsListAdapter extends BaseAdapter {

    private String[] result;
    private Context context;
    private int[] imageId;
    private static LayoutInflater inflater = null;

    public SettingsListAdapter(Context context) {
        this.context = context;
        result = new String[]{"Log Out"};
        //imageId = new int[items.size()];

        this.context = context;
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
        TextView tv2;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        SettingsListAdapter.Holder holder = new SettingsListAdapter.Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_settings_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.item_req);
        holder.img = (ImageView) rowView.findViewById(R.id.item_image);
        holder.tv.setText(result[position]);
        //holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rowView;
    }
}
