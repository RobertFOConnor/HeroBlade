package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Inventory;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class WeaponActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int UNEQUIP_WEAPON_REQUEST = 1;
    private static final int EQUIP_WEAPON_REQUEST = 2;
    private ListView itemList;
    private AdapterView.OnItemClickListener itemClickListener;
    private WeaponListAdapter equippedWeaponAdapter, allWeaponAdapter;
    private TextView emptyMessage;
    private ActionBar actionBar;

    private enum VIEW {EQUIPPED_WEAPONS, ALL_WEAPONS}

    private Inventory inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon);
        setupBars();
        itemList = findViewById(R.id.item_list);
        emptyMessage = findViewById(R.id.empty_message);
        inventory = GameManager.getInstance().getInventory();
        equippedWeaponAdapter = new WeaponListAdapter(this, inventory.getEquippedWeapons(), "");
        allWeaponAdapter = new WeaponListAdapter(this, inventory.getUnequippedWeapons(), "");
        findViewById(R.id.item_button).setOnClickListener(this);
        findViewById(R.id.weapon_button).setOnClickListener(this);
        setupWeaponClickListener();
        switchView(VIEW.EQUIPPED_WEAPONS);
    }

    private void setupWeaponClickListener() {
        itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeaponItem clickedWeapon = (WeaponItem) itemList.getAdapter().getItem(position);
                Intent intent = new Intent(WeaponActivity.this, WeaponDetailActivity.class);
                intent.putExtra(WeaponDetailActivity.WEAPON_UID, clickedWeapon.getUUID());
                if (clickedWeapon.isEquipped()) {
                    WeaponActivity.this.startActivityForResult(intent, UNEQUIP_WEAPON_REQUEST);
                } else {
                    WeaponActivity.this.startActivityForResult(intent, EQUIP_WEAPON_REQUEST);
                }
            }
        };
    }

    private void setupBars() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);
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
            setEmptyText(getString(R.string.no_weapon_equipped));
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.equipped_weapons));
                actionBar.setSubtitle(getString(R.string.weapons_limit));
            }
        } else if (v.equals(VIEW.ALL_WEAPONS)) {
            itemList.setAdapter(allWeaponAdapter);
            itemList.setOnItemClickListener(itemClickListener);
            setEmptyText(getString(R.string.no_weapon_stored));
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.stored_weapons));
                actionBar.setSubtitle(getString(R.string.stored_weapons_subtitle));
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
                SoundManager.getInstance(this).playSound(SoundManager.Sound.BACK);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request to which we're responding
        if (resultCode == RESULT_OK) {
            WeaponItem weaponItem = inventory.getWeapon(data.getStringExtra(getString(R.string.result)));
            if (requestCode == UNEQUIP_WEAPON_REQUEST) {
                weaponItem.setEquipped(false);
                equippedWeaponAdapter.remove(weaponItem);
                allWeaponAdapter.add(weaponItem);
                App.showToast(getApplicationContext(), "Unequipped " + weaponItem.getName());
            } else if (requestCode == EQUIP_WEAPON_REQUEST) {
                weaponItem.setEquipped(true);
                allWeaponAdapter.remove(weaponItem);
                equippedWeaponAdapter.add(weaponItem);
                App.showToast(getApplicationContext(), "Equipped " + weaponItem.getName());
            }
        }
    }
}
