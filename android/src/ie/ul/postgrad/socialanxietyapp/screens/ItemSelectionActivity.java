package ie.ul.postgrad.socialanxietyapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.InventoryListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;

public class ItemSelectionActivity extends AppCompatActivity {

    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_selection);
        itemList = (ListView) findViewById(R.id.item_list);

        SparseIntArray itemArray = GameManager.getInstance().getInventory().getItems();
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < itemArray.size(); i++) {
            Item item = ItemFactory.buildItem(this, itemArray.keyAt(i));
            if (item instanceof FoodItem) {
                items.add(item);
            }
        }

        if (items.size() <= 0) {
            findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
        }

        final InventoryListAdapter adapter = new InventoryListAdapter(this, items);
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item clickedItem = adapter.getItem(position);

                if (clickedItem instanceof FoodItem) {
                    int itemId = clickedItem.getId();
                    GameManager.getInstance().consumeFoodItem(getApplicationContext(), itemId);
                    int quantity = GameManager.getInstance().getInventory().getItems().get(itemId);
                    if (quantity <= 0) {
                        adapter.remove(adapter.getItem(position));
                    } else {
                        String quantityString = "x" + quantity;
                        ((TextView) view.findViewById(R.id.item_count)).setText(quantityString);
                    }

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", itemId);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Choose an item");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
