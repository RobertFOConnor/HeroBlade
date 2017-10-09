package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.CraftableListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;

public class CraftingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crafting);
        ArrayList<Item> items = new ArrayList<>();
        int[] craftItems = ItemFactory.CRAFTABLES;
        for (int i = 0; i < craftItems.length; i++) {
            Item item = ItemFactory.buildItem(this, craftItems[i]);
            if (item.getIngredients().size() > 0) {
                items.add(item);
            }
        }

        ListView itemList = (ListView) findViewById(R.id.craft_item_list);
        CraftableListAdapter adapter = new CraftableListAdapter(this, items);
        itemList.setAdapter(adapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.crafting));
            actionBar.setSubtitle(getString(R.string.crafting_subtitle));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);
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
