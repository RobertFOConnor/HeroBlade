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

import com.google.android.gms.games.Game;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.Player;
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
    private ArrayList<WeaponItem> items;

    public CraftableListAdapter(Context context, ArrayList<WeaponItem> items) {
        this.items = items;
        result = new String[items.size()];
        ingredients = new String[items.size()];
        imageId = new int[items.size()];

        for (int i = 0; i < items.size(); i++) {
            result[i] = items.get(i).getName();
            ingredients[i] = items.get(i).getIngredientsString();
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

                WeaponItem selectedItem = items.get(position);
                InventoryItemArray playerItems = GameManager.getInstance().getInventory().getItems();
                SparseIntArray ingredients = selectedItem.getIngredients();

                for(int i = 0; i < ingredients.size(); i++) {
                    int playerCount = playerItems.get(ingredients.keyAt(i));
                    int neededCount = ingredients.valueAt(i);

                    if(playerCount < neededCount) {
                        canCraft = false;
                    }
                }

                if(canCraft) {

                    for(int i = 0; i < ingredients.size(); i++) {
                        int itemId = ingredients.keyAt(i);

                        GameManager.getInstance().getInventory().removeItem(ingredients.keyAt(i), ingredients.valueAt(i));
                        GameManager.getInstance().updateItemInDatabase(itemId);

                    }
                    GameManager.getInstance().getInventory().addItem(selectedItem.getId(), 1);
                    GameManager.getInstance().updateItemInDatabase(selectedItem.getId());

                    App.showToast(context, "You crafted a new "+selectedItem.getName());
                } else {
                    App.showToast(context, "You can't craft a "+selectedItem.getName());
                }
            }
        });
        return rowView;
    }
}
