package ie.ul.postgrad.socialanxietyapp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.InventoryItemArray;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

/**
 * Created by Robert on 20-Feb-17.
 */

public class CraftableListAdapter extends ArrayAdapter<Item> {

    public CraftableListAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.fragment_crafting_item, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Item item = getItem(position);
        final CraftableListAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new CraftableListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_crafting_item, parent, false);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.descText = (TextView) convertView.findViewById(R.id.item_desc);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.ingredients = (TextView) convertView.findViewById(R.id.item_req);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CraftableListAdapter.ViewHolder) convertView.getTag();
        }

        if (item != null) {
            viewHolder.nameText.setText(item.getName());
            viewHolder.descText.setText(item.getDescription());
            viewHolder.ingredients.setText(item.getIngredientsString(getContext()));
            viewHolder.img.setImageResource(item.getImageID());

            if (!canCraft(item)) {
                viewHolder.nameText.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                viewHolder.descText.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                viewHolder.ingredients.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                GameManager gm = GameManager.getInstance();
                Item item = getItem(position);
                if (canCraft(item)) {
                    SparseIntArray ingredients = item.getIngredients();
                    for (int i = 0; i < ingredients.size(); i++) {
                        gm.giveItem(ingredients.keyAt(i), -ingredients.valueAt(i));
                    }
                    gm.giveItem(item.getId(), 1);
                    gm.awardXP(context, GameManager.craftingXP);
                    App.showToast(context, context.getString(R.string.craft_success, item.getName(), GameManager.craftingXP));
                } else {
                    App.showToast(context, context.getString(R.string.craft_fail, item.getName()));
                }
                if (!canCraft(item)) {
                    viewHolder.nameText.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                    viewHolder.descText.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                    viewHolder.ingredients.setTextColor(ContextCompat.getColor(getContext(), R.color.light_gray));
                }

            }
        });
        return convertView;
    }

    private boolean canCraft(Item item) {
        boolean canCraft = true;

        InventoryItemArray playerItems = GameManager.getInstance().getInventory().getItems();
        SparseIntArray ingredients = item.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            int playerCount = playerItems.get(ingredients.keyAt(i));
            int neededCount = ingredients.valueAt(i);

            if (playerCount < neededCount) {
                canCraft = false;
            }
        }
        return canCraft;
    }

    private static class ViewHolder {
        TextView nameText;
        TextView descText;
        ImageView img;
        TextView ingredients;
    }
}
