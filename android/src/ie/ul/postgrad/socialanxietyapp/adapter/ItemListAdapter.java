package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 12-Aug-17.
 */

public class ItemListAdapter extends ArrayAdapter<Item> {


    public ItemListAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.fragment_priced_item, items);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_priced_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.descText = (TextView) convertView.findViewById(R.id.item_desc);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.cost = (TextView) convertView.findViewById(R.id.item_cost);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.nameText.setText(item.getName());
            viewHolder.descText.setText(item.getDescription());

            if (item instanceof WeaponItem) {
                viewHolder.img.setImageResource(((WeaponItem) item).getTypeDrawableRes());
            } else {
                viewHolder.img.setImageResource(item.getImageID());
            }

            String costText = getContext().getString(R.string.cash_display, item.getWorth());
            viewHolder.cost.setText(costText);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView descText;
        ImageView img;
        TextView cost;
    }
}