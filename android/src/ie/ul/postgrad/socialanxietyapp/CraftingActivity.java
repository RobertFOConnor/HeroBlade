package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class CraftingActivity extends AppCompatActivity {

    private static final int CRAFTABLE_START_ID = 600;
    private static final int CRAFTABLE_ITEM_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crafting);

        ArrayList<WeaponItem> items = new ArrayList<>();

        for (int i = 0; i < CRAFTABLE_ITEM_COUNT; i++) {
            items.add((WeaponItem) ItemFactory.buildItem(CRAFTABLE_START_ID + i));
        }

        ListView itemList = (ListView) findViewById(R.id.craft_item_list);
        CraftableListAdapter adapter = new CraftableListAdapter(this, items);
        itemList.setAdapter(adapter);
    }
}
