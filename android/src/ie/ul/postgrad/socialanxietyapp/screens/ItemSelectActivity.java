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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.InventoryListAdapter;
import ie.ul.postgrad.socialanxietyapp.adapter.ItemListAdapter;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

import static ie.ul.postgrad.socialanxietyapp.screens.BlacksmithActivity.BUY_WEAPON_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.BlacksmithActivity.SELL_WEAPON_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.VillageActivity.BUY_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.VillageActivity.SELL_KEY;

public class ItemSelectActivity extends AppCompatActivity {

    public static final String SELECT_TYPE = "select_type"; //specifies what content to show in the list
    public static final String CURR_WEAPON = "curr_weapon";
    private ListView itemList;

    private String title;
    private GameManager gm;
    private Player player;
    private ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_selection);
        itemList = (ListView) findViewById(R.id.item_list);
        Bundle bundle = getIntent().getExtras();
        final String selectType = bundle.getString(SELECT_TYPE);
        gm = GameManager.getInstance();
        gm.initDatabaseHelper(getApplicationContext());
        player = gm.getPlayer();

        if (selectType == null) {
            title = getString(R.string.choose_weapon);

            final String currWeaponUUID = bundle.getString(CURR_WEAPON);

            adapter = new WeaponListAdapter(this, GameManager.getInstance().getInventory().getEquippedWeapons(), currWeaponUUID);
            itemList.setAdapter(adapter);

            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WeaponItem selectedWeapon = ((WeaponItem) itemList.getItemAtPosition(position));

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(getString(R.string.result), selectedWeapon.getUUID());

                    if (currWeaponUUID != null) {
                        if (currWeaponUUID.equals(selectedWeapon.getUUID())) { //Check if they already equipped this weapon.
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
                        } else if (selectedWeapon.getCurrHealth() < 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.broke_weapon, selectedWeapon.getName()), Toast.LENGTH_SHORT).show();
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

        } else if (selectType.equals(BUY_KEY)) {
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
                    buyItem(((Item) itemList.getItemAtPosition(position)), position);
                }
            });

        } else if (selectType.equals(SELL_KEY)) {
            title = getString(R.string.sell_items);

            SparseIntArray itemArray = gm.getInventory().getItems();
            ArrayList<Item> items = new ArrayList<>();

            for (int i = 0; i < itemArray.size(); i++) {
                items.add(ItemFactory.buildItem(this, itemArray.keyAt(i)));
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
                    gm.giveItem(clickedItem.getId(), -1);
                    player.setMoney(player.getMoney() + clickedItem.getWorth());
                    gm.updatePlayerInDatabase(player);
                    setListTitle();
                    int quantity = GameManager.getInstance().getInventory().getItems().get(clickedItem.getId());
                    if (quantity <= 0) {
                        adapter.remove(adapter.getItem(position));
                    } else {
                        String quantityString = getString(R.string.quantity_string, quantity);
                        ((TextView) view.findViewById(R.id.item_count)).setText(quantityString);
                    }
                }
            });
        } else if (selectType.equals(BUY_WEAPON_KEY)) {
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
                        Toast.makeText(getApplicationContext(), getString(R.string.not_enough_money), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (selectType.equals(SELL_WEAPON_KEY)) {
            title = getString(R.string.sell_swords);

            ArrayList<Item> items = new ArrayList<>();

            for (int i = 0; i < gm.getInventory().getWeapons().size(); i++) {
                items.add(gm.getInventory().getWeapons().get(i));
            }

            if (items.size() <= 0) {
                findViewById(R.id.empty_message).setVisibility(View.VISIBLE);
            }

            final ItemListAdapter adapter = new ItemListAdapter(this, items);
            itemList.setAdapter(adapter);

            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WeaponItem clickedItem = (WeaponItem) adapter.getItem(position);
                    gm.removeWeapon(clickedItem.getUUID());
                    player.setMoney(player.getMoney() + clickedItem.getWorth());
                    gm.updatePlayerInDatabase(player);
                    setListTitle();
                    adapter.remove(clickedItem);
                }
            });
        }


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            setListTitle();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }
    }

    private void buyItem(Item item, int pos) {
        if (player.getMoney() >= item.getWorth()) {
            gm.giveItem(item.getId(), 1);
            player.setMoney(player.getMoney() - item.getWorth());
            gm.updatePlayerInDatabase(player);
            setListTitle();
            adapter.remove(item);
            VillageActivity.itemIdsForSale.remove(pos);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.not_enough_money), Toast.LENGTH_SHORT).show();
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

    private void setListTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title + ": $" + player.getMoney());
        }
    }
}
