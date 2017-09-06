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
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * List adapter for displaying inventory items.
 */

public class InventoryListAdapter extends ArrayAdapter<Item> {

    public InventoryListAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.fragment_inventory_item, items);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Item item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_inventory_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.descText = (TextView) convertView.findViewById(R.id.item_desc);
            viewHolder.quantityText = (TextView) convertView.findViewById(R.id.item_count);
            viewHolder.costText = (TextView) convertView.findViewById(R.id.item_cost);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String name = item.getName();
        String desc = item.getDescription();
        String quantity = getContext().getString(R.string.quantity_string, GameManager.getInstance().getInventory().getItems().get(item.getId()));
        String cost = "$" + item.getWorth();

        viewHolder.nameText.setText(name);
        viewHolder.descText.setText(desc);
        viewHolder.quantityText.setText(quantity);
        viewHolder.costText.setText(cost);
        viewHolder.image.setImageResource(item.getImageID());
        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView descText;
        TextView quantityText;
        TextView costText;
        ImageView image;
    }
}
