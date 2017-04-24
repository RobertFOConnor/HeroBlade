package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;

public class CraftingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crafting);

        ArrayList<Item> items = new ArrayList<>();

        for (int i = 1; i < getResources().getStringArray(R.array.item_array_refs).length; i++) {
            Item item = ItemFactory.buildItem(this, i);
            if (item.getIngredients().size() > 0) {
                items.add(item);
            }
        }

        ListView itemList = (ListView) findViewById(R.id.craft_item_list);
        CraftableListAdapter adapter = new CraftableListAdapter(this, items);
        itemList.setAdapter(adapter);
    }
}
