package ie.ul.postgrad.socialanxietyapp.screens;

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
import ie.ul.postgrad.socialanxietyapp.WeaponDetailActivity;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Inventory;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class WeaponActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int UNEQUIP_WEAPON_REQUEST = 1;
    private static final int EQUIP_WEAPON_REQUEST = 2;

    private ListView itemList;
    private AdapterView.OnItemClickListener itemClickListener;
    private WeaponListAdapter equippedWeaponAdapter, allWeaponAdapter;
    private TextView emptyMessage;
    private int pos;
    private ActionBar actionBar;

    private enum VIEW {EQUIPPED_WEAPONS, ALL_WEAPONS}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bg_color));
        }

        itemList = (ListView) findViewById(R.id.item_list);
        emptyMessage = (TextView) findViewById(R.id.empty_message);

        Inventory inventory = GameManager.getInstance().getInventory();
        SparseIntArray itemArray = inventory.getItems();
        ArrayList<Item> items = new ArrayList<>();

        for (int i = 0; i < itemArray.size(); i++) {
            items.add(ItemFactory.buildItem(this, itemArray.keyAt(i)));
        }

        equippedWeaponAdapter = new WeaponListAdapter(this, inventory.getEquippedWeapons(), "");
        allWeaponAdapter = new WeaponListAdapter(this, inventory.getUnequippedWeapons(), "");

        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.weapon_button).setOnClickListener(this);

        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                WeaponItem clickedWeapon = (WeaponItem) itemList.getAdapter().getItem(position);

                Intent intent = new Intent(getApplicationContext(), WeaponDetailActivity.class);
                intent.putExtra(WeaponDetailActivity.WEAPON_UID, clickedWeapon.getUUID());
                if (clickedWeapon.isEquipped()) {
                    startActivityForResult(intent, UNEQUIP_WEAPON_REQUEST);
                } else {
                    startActivityForResult(intent, EQUIP_WEAPON_REQUEST);
                }
            }
        };


        switchView(VIEW.EQUIPPED_WEAPONS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_button:
                switchView(VIEW.EQUIPPED_WEAPONS);
                break;
            case R.id.weapon_button:
                switchView(VIEW.ALL_WEAPONS);
                break;
        }
    }

    private void switchView(WeaponActivity.VIEW v) {
        if (v.equals(VIEW.EQUIPPED_WEAPONS)) {
            itemList.setAdapter(equippedWeaponAdapter);
            itemList.setOnItemClickListener(itemClickListener);
            setEmptyText("You have no weapons equipped.");
            if (actionBar != null) {
                actionBar.setTitle("Equipped Weapons");
                actionBar.setSubtitle("You can equip up to 6 weapons.");
            }
        } else if (v.equals(VIEW.ALL_WEAPONS)) {
            itemList.setAdapter(allWeaponAdapter);
            itemList.setOnItemClickListener(itemClickListener);
            setEmptyText("You have no weapons in storage.");
            if (actionBar != null) {
                actionBar.setTitle("Stored Weapons");
                actionBar.setSubtitle("These weapons are in storage.");
            }
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
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (requestCode == UNEQUIP_WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                WeaponItem weaponItem = GameManager.getInstance().getInventory().getWeapon(data.getStringExtra("result"));
                equippedWeaponAdapter.remove(weaponItem);
                allWeaponAdapter.add(weaponItem);
            }
        } else if (requestCode == EQUIP_WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                WeaponItem weaponItem = GameManager.getInstance().getInventory().getWeapon(data.getStringExtra("result"));
                allWeaponAdapter.remove(weaponItem);
                equippedWeaponAdapter.add(weaponItem);
            }
        }
    }
}
