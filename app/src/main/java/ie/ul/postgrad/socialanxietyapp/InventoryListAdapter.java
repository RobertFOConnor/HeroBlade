package ie.ul.postgrad.socialanxietyapp;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;

/**
 * Created by Robert on 20-Feb-17.
 */

public class InventoryListAdapter extends BaseAdapter {

    private String[] result;
    private Context context;
    private int[] itemCount;
    private int[] imageId;
    private static LayoutInflater inflater = null;

    public InventoryListAdapter(Context context, SparseIntArray inventory) {
        // TODO Auto-generated constructor stub

        SparseIntArray items = inventory;
        result = new String[items.size()];
        itemCount = new int[items.size()];
        imageId = new int[items.size()];

        for (int i = 0; i < items.size(); i++) {
            int itemId = items.keyAt(i);
            result[i] = ItemFactory.buildItem(itemId).getName();
            itemCount[i] = items.valueAt(i);
            imageId[i] = ItemFactory.buildItem(itemId).getImageID();
        }

        this.context = context;
        //imageId = prgmImages;
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
