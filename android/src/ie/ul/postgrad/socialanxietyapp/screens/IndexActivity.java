package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponIndexAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        setupWeaponList();
        setupBars();
    }

    private void setupWeaponList() {
        ListView itemList = findViewById(R.id.item_list);
        ArrayList<WeaponItem> weapons = new ArrayList<>();

        for (int i = 0; i < WeaponFactory.SWORD_COUNT; i++) {
            if (GameManager.getInstance().getFoundWeapons().contains(i)) {
                weapons.add(WeaponFactory.buildWeapon(this, i));
            }
        }
        WeaponIndexAdapter adapter = new WeaponIndexAdapter(this, weapons);
        itemList.setAdapter(adapter);
    }

    private void setupBars() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.weapon_index) + ": (" + GameManager.getInstance().getFoundWeapons().size() + "/" + WeaponFactory.SWORD_COUNT + ")");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        App.setStatusBarColor(this);
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
