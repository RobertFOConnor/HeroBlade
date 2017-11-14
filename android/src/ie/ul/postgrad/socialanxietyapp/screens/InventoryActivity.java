package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.InventoryListAdapter;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Inventory;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;

public class InventoryActivity extends AppCompatActivity {

    private ListView itemList;
    private AdapterView.OnItemClickListener itemClickListener;
    private InventoryListAdapter itemAdapter;
    private WeaponListAdapter weaponAdapter;
    private TextView emptyMessage;

    private enum VIEW {ITEM_VIEW, WEAPON_VIEW}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        itemList = (ListView) findViewById(R.id.item_list);
        emptyMessage = (TextView) findViewById(R.id.empty_message);

        Inventory inventory = GameManager.getInstance().getInventory();
        SparseIntArray itemArray = inventory.getItems();
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < itemArray.size(); i++) {
            items.add(ItemFactory.buildItem(this, itemArray.keyAt(i)));
        }

        itemAdapter = new InventoryListAdapter(this, items);
        weaponAdapter = new WeaponListAdapter(this, inventory.getWeapons(), "");

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item clickedItem = itemAdapter.getItem(position);

                if (clickedItem instanceof FoodItem) {
                    if (((FoodItem) clickedItem).getEnergy() > 0) {
                        int itemId = clickedItem.getId();
                        GameManager.getInstance().consumeFoodItem(getApplicationContext(), itemId);
                        int quantity = GameManager.getInstance().getInventory().getItems().get(itemId);
                        if (quantity <= 0) {
                            itemAdapter.remove(itemAdapter.getItem(position));
                        } else {
                            String quantityString = getString(R.string.quantity_string, quantity);
                            ((TextView) view.findViewById(R.id.item_count)).setText(quantityString);
                        }
                    }
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.inventory));
            actionBar.setSubtitle(getString(R.string.inventory_subtitle));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);

        switchView(VIEW.ITEM_VIEW);
    }

    private void switchView(VIEW v) {
        if (v.equals(VIEW.WEAPON_VIEW)) {
            itemList.setAdapter(weaponAdapter);
            itemList.setOnItemClickListener(null);
            setEmptyText(getString(R.string.empty_weapons));
        } else if (v.equals(VIEW.ITEM_VIEW)) {
            itemList.setAdapter(itemAdapter);
            itemList.setOnItemClickListener(itemClickListener);
            setEmptyText(getString(R.string.empty_inventory));
        }
    }

    private void setEmptyText(String text) {
        if (itemList.getCount() <= 0) {
            emptyMessage.setVisibility(View.VISIBLE);
            emptyMessage.setText(text);
        } else {
            emptyMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                SoundManager.getInstance(this).playSound(SoundManager.Sound.BACK);
                break;
        }
        return true;
    }
}
