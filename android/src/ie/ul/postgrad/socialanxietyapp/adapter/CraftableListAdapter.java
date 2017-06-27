package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

/**
 * Created by Robert on 20-Feb-17.
 */

public class CraftableListAdapter extends BaseAdapter {

    private String[] result;
    private Context context;
    private String[] ingredients;
    private int[] imageId;
    private static LayoutInflater inflater = null;
    private ArrayList<Item> items;

    public CraftableListAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        result = new String[items.size()];
        ingredients = new String[items.size()];
        imageId = new int[items.size()];

        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i).getName();
            ingredients[i] = items.get(i).getIngredientsString(context);
            imageId[i] = items.get(i).getImageID();
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
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.fragment_crafting_item, null);
        holder.tv = (TextView) rowView.findViewById(R.id.item_title);
        holder.tv2 = (TextView) rowView.findViewById(R.id.item_req);
        holder.img = (ImageView) rowView.findViewById(R.id.item_image);
        holder.tv.setText(result[position]);
        holder.tv2.setText(ingredients[position]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean canCraft = true;

                Item selectedItem = items.get(position);
                InventoryItemArray playerItems = GameManager.getInstance().getInventory().getItems();
                SparseIntArray ingredients = selectedItem.getIngredients();

                for (int i = 0; i < ingredients.size(); i++) {
                    int playerCount = playerItems.get(ingredients.keyAt(i));
                    int neededCount = ingredients.valueAt(i);

                    if (playerCount < neededCount) {
                        canCraft = false;
                    }
                }

                if (canCraft) {

                    for (int i = 0; i < ingredients.size(); i++) {
                        int itemId = ingredients.keyAt(i);

                        GameManager.getInstance().getInventory().removeItem(ingredients.keyAt(i), ingredients.valueAt(i));
                        GameManager.getInstance().updateItemInDatabase(itemId);

                    }
                    GameManager.getInstance().givePlayer(context, selectedItem.getId(), 1);

                    Toast.makeText(context, "You crafted a new " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You can't craft a " + selectedItem.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rowView;
    }
}
