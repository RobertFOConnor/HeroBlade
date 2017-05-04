package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;

public class InventoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView itemList;
    private InventoryListAdapter itemAdapter;
    private WeaponListAdapter weaponAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        itemList = (ListView) findViewById(R.id.item_list);

        SparseIntArray itemArray = GameManager.getInstance().getInventory().getItems();
        ArrayList<Item> items = new ArrayList<>();

        for(int i = 0; i < itemArray.size(); i++) {
            items.add(ItemFactory.buildItem(this, itemArray.keyAt(i)));
        }

        itemAdapter = new InventoryListAdapter(this, items);
        weaponAdapter = new WeaponListAdapter(this, GameManager.getInstance().getInventory().getWeapons());
        itemList.setAdapter(itemAdapter);

        /*if (array.size() == 0) {
            findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
        }*/

        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.weapon_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_button:
                itemList.setAdapter(itemAdapter);
                break;
            case R.id.weapon_button:
                itemList.setAdapter(weaponAdapter);
                break;
        }
    }
}
