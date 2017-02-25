package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.WeaponItem;

public class CraftingActivity extends AppCompatActivity {

    private Player player;
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_crafting);

        player = MapsActivity.player;

        ArrayList<WeaponItem> items = new ArrayList<>();
        items.add((WeaponItem) ItemFactory.buildItem(5));

        itemList = (ListView) findViewById(R.id.craft_item_list);
        CraftableListAdapter adapter = new CraftableListAdapter(this, items);
        itemList.setAdapter(adapter);
    }
}
