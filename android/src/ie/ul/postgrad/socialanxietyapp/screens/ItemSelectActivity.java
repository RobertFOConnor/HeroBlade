package ie.ul.postgrad.socialanxietyapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.InventoryListAdapter;
import ie.ul.postgrad.socialanxietyapp.adapter.ItemListAdapter;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Inventory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class ItemSelectActivity extends AppCompatActivity {

    public static final String SELECT_TYPE = "select_type"; //specifies what content to show in the list
    public static final String CURR_WEAPON = "curr_weapon";
    public static final String SELECT_WEAPON = "choose_weapon";
    public static final String SELECT_ITEM = "choose_item";
    public static final String SELL_KEY = "sell_items";
    public static final String BUY_KEY = "buy_items";
    public static final String SELL_WEAPON_KEY = "sell_weapons";
    public static final String BUY_WEAPON_KEY = "buy_weapons";
    private ListView itemList;
    private String title;
    private GameManager gm;
    private Player player;
    private Inventory inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_selection);
        itemList = (ListView) findViewById(R.id.item_list);
        Bundle bundle = getIntent().getExtras();
        final String selectType = bundle.getString(SELECT_TYPE, SELECT_WEAPON);
        gm = GameManager.getInstance();
        gm.initDatabaseHelper(getApplicationContext());
        player = gm.getPlayer();
        inventory = gm.getInventory();
        final ArrayAdapter adapter;

        switch (selectType) {

            case SELECT_WEAPON:
                title = getString(R.string.choose_weapon);

                final String currWeaponUUID = bundle.getString(CURR_WEAPON);

                adapter = new WeaponListAdapter(this, inventory.getEquippedWeapons(), currWeaponUUID);
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WeaponItem selectedWeapon = ((WeaponItem) itemList.getItemAtPosition(position));

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(getString(R.string.result), selectedWeapon.getUUID());

                        if (!currWeaponUUID.equals("")) {
                            if (currWeaponUUID.equals(selectedWeapon.getUUID())) { //Check if they already equipped this weapon.
                                setResult(Activity.RESULT_CANCELED, returnIntent);
                                finish();
                            } else if (selectedWeapon.getCurrHealth() < 0) {
                                App.showToast(getApplicationContext(), getString(R.string.broke_weapon, selectedWeapon.getName()));
                            } else {
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        } else {
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }

                    }
                });
                break;

            case SELECT_ITEM:
                title = getString(R.string.choose_item);
                adapter = new InventoryListAdapter(this, getItemList());
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item clickedItem = (Item) adapter.getItem(position);

                        if (clickedItem instanceof FoodItem) {
                            int itemId = clickedItem.getId();
                            if (((FoodItem) clickedItem).getEnergy() > 0) {
                                gm.consumeFoodItem(getApplicationContext(), itemId);

                                int quantity = gm.getInventory().getItems().get(itemId);
                                if (quantity <= 0) {
                                    adapter.remove(adapter.getItem(position));
                                } else {
                                    String quantityString = getString(R.string.quantity_string, quantity);
                                    ((TextView) view.findViewById(R.id.item_count)).setText(quantityString);
                                }
                            } else {
                                gm.giveItem(itemId, -1); //For poison.
                            }

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra(getString(R.string.result), itemId);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }
                });
                break;


            case BUY_KEY:
                title = getString(R.string.buy_items);

                ArrayList<Item> items = new ArrayList<>();
                for (int i = 0; i < VillageActivity.itemIdsForSale.size(); i++) {
                    items.add(ItemFactory.buildItem(getApplicationContext(), VillageActivity.itemIdsForSale.get(i))); //Populate 'for sale' item list
                }

                if (items.size() <= 0) {
                    findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
                }

                adapter = new ItemListAdapter(this, items);
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        buyItem(adapter, ((Item) itemList.getItemAtPosition(position)), position);
                    }
                });
                break;

            case SELL_KEY:
                title = getString(R.string.sell_items);
                adapter = new InventoryListAdapter(this, getItemList());
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Item clickedItem = (Item) adapter.getItem(position);
                        gm.giveItem(clickedItem.getId(), -1);
                        player.setMoney(player.getMoney() + clickedItem.getWorth());
                        gm.updatePlayerInDatabase(player);
                        setListTitle();
                        int quantity = gm.getInventory().getItems().get(clickedItem.getId());
                        if (quantity <= 0) {
                            adapter.remove(adapter.getItem(position));
                        } else {
                            String quantityString = getString(R.string.quantity_string, quantity);
                            ((TextView) view.findViewById(R.id.item_count)).setText(quantityString);
                        }
                    }
                });
                break;
            case BUY_WEAPON_KEY:
                title = getString(R.string.buy_swords);

                ArrayList<Item> weapons = new ArrayList<>();
                for (int i = 0; i < BlacksmithActivity.itemIdsForSale.size(); i++) {
                    weapons.add(WeaponFactory.buildWeapon(getApplicationContext(), BlacksmithActivity.itemIdsForSale.get(i))); //Populate 'for sale' item list
                }

                if (weapons.size() <= 0) {
                    findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
                }

                adapter = new ItemListAdapter(this, weapons);
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WeaponItem weaponItem = (WeaponItem) adapter.getItem(position);
                        if (player.getMoney() >= weaponItem.getWorth()) {
                            gm.giveWeapon(getApplicationContext(), weaponItem);
                            player.setMoney(player.getMoney() - weaponItem.getWorth());
                            gm.updatePlayerInDatabase(player);
                            setListTitle();
                            adapter.remove(weaponItem);
                            BlacksmithActivity.itemIdsForSale.remove(position);
                        } else {
                            App.showToast(getApplicationContext(), getString(R.string.not_enough_money));
                        }
                    }
                });
                break;
            case SELL_WEAPON_KEY:
                title = getString(R.string.sell_swords);

                items = new ArrayList<>();

                for (int i = 0; i < inventory.getWeapons().size(); i++) {
                    items.add(inventory.getWeapons().get(i));
                }

                if (items.size() <= 0) {
                    findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
                }

                adapter = new ItemListAdapter(this, items);
                itemList.setAdapter(adapter);

                itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        WeaponItem clickedItem = (WeaponItem) adapter.getItem(position);
                        if (!clickedItem.getUUID().equals(player.getBaseSword())) {
                            gm.removeWeapon(clickedItem.getUUID());
                            player.setMoney(player.getMoney() + clickedItem.getWorth());
                            gm.updatePlayerInDatabase(player);
                            setListTitle();
                            adapter.remove(clickedItem);
                        } else {
                            App.showToast(getApplicationContext(), getString(R.string.no_sale_base_sword));
                        }
                    }
                });
                break;
        }


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setListTitle();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        App.setStatusBarColor(this);
    }

    private ArrayList<Item> getItemList() {
        SparseIntArray itemArray = inventory.getItems();
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < itemArray.size(); i++) {
            items.add(ItemFactory.buildItem(this, itemArray.keyAt(i)));
        }

        if (items.size() <= 0) {
            findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
        }
        return items;
    }

    private void buyItem(final ArrayAdapter adapter, Item item, int pos) {
        if (player.getMoney() >= item.getWorth()) {
            gm.giveItem(item.getId(), 1);
            player.setMoney(player.getMoney() - item.getWorth());
            gm.updatePlayerInDatabase(player);
            setListTitle();
            adapter.remove(item);
            VillageActivity.itemIdsForSale.remove(pos);
        } else {
            App.showToast(getApplicationContext(), getString(R.string.not_enough_money));
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

    private void setListTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title + ": $" + player.getMoney());
        }
    }
}
