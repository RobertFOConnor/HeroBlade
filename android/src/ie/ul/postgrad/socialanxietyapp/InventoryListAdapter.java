package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;

/**
 * Created by Robert on 20-Feb-17.
 *
 * List adapter for displaying inventory items.
 */

public class InventoryListAdapter extends BaseAdapter {

    private String[] result;
    private Context context;
    private int[] itemCount;
    private int[] imageId;
    private static LayoutInflater inflater = null;

    public InventoryListAdapter(Context context, SparseIntArray inventory) {

        result = new String[inventory.size()];
        itemCount = new int[inventory.size()];
        imageId = new int[inventory.size()];

        for (int i = 0; i < inventory.size(); i++) {
            int itemId = inventory.keyAt(i);
            result[i] = ItemFactory.buildItem(context, itemId).getName();
            itemCount[i] = inventory.valueAt(i);
            imageId[i] = ItemFactory.buildItem(context, itemId).getImageID();
        }

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
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_inventory_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.item_count);
        holder.img = (ImageView) rowView.findViewById(R.id.item_image);
        holder.tv.setText(result[position]);
        holder.tv2.setText("x" + itemCount[position]);
        holder.img.setImageResource(imageId[position]);
        return rowView;
    }
}
