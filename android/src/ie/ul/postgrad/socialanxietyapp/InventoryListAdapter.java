package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

/**
 * Created by Robert on 20-Feb-17.
 * <p>
 * List adapter for displaying inventory items.
 */

public class InventoryListAdapter extends ArrayAdapter<Item> {

    private ArrayList<Item> inventoryItems;
    private Context context;


    public InventoryListAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.fragment_inventory_item, items);
        inventoryItems = items;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        Item item = getItem(position);
        ViewHolder viewHolder;
        final View result;


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_inventory_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.quantityText = (TextView) convertView.findViewById(R.id.item_count);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_image);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);

        viewHolder.nameText.setText(item.getName());
        viewHolder.quantityText.setText("x" + GameManager.getInstance().getInventory().getItems().get(item.getId()));
        viewHolder.image.setImageResource(item.getImageID());
        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView nameText;
        TextView quantityText;
        ImageView image;
    }


}
