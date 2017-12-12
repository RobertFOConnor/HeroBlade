package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.CraftableListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CRAFT_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;

public class CraftingActivity extends AppCompatActivity {

    private ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crafting);
        addCraftingItemsToList();
        setupListAdapter();
        setupBars();
        showHelpInfo();
    }

    private void addCraftingItemsToList() {
        items = new ArrayList<>();
        int[] craftItems = ItemFactory.CRAFTABLES;
        int level = GameManager.getInstance().getPlayer().getLevel();
        for (int i = 0; i < craftItems.length; i++) {
            if (i < level - 1) {
                int craftItem = craftItems[i];
                Item item = ItemFactory.buildItem(this, craftItem);
                if (item.getIngredients().size() > 0) {
                    items.add(item);
                }
            }
        }
        if (items.size() > 0) {
            findViewById(R.id.empty_message).setVisibility(View.GONE);
        }
    }

    private void setupListAdapter() {
        ListView itemList = findViewById(R.id.craft_item_list);
        CraftableListAdapter adapter = new CraftableListAdapter(this, items);
        itemList.setAdapter(adapter);
    }

    private void setupBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.crafting));
            actionBar.setSubtitle(getString(R.string.crafting_subtitle));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);
    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String key = "firstTimeCraft";
        boolean firstTimeMap = prefs.getBoolean(key, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            //bundle here...
            tutorialIntent.putExtra(INFO_KEY, CRAFT_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(key, false).apply();
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
