package ie.ul.postgrad.socialanxietyapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class WeaponDetailActivity extends AppCompatActivity {

    public static String WEAPON_UID = "uid";
    private WeaponItem weapon;
    private Button equipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_detail);
        App.setStatusBarColor(this);
        setupBundleInfo();
        setupActionBar();
        setupTextViews();
        setupImages();
        setupEquipButtonListener();
    }

    private void setupImages() {
        ((ImageView) findViewById(R.id.item_image)).setImageResource(this.getResources().getIdentifier("weapon" + String.format(Locale.ENGLISH, "%04d", weapon.getId()), "drawable", this.getPackageName()));
        ((ImageView) findViewById(R.id.item_image_type)).setImageResource(weapon.getTypeDrawableRes());
    }

    private void setupTextViews() {
        String typeText = "Type: " + weapon.getType();
        String damageText = "Damage: " + weapon.getDamage();
        String healthText = "Health: " + weapon.getCurrHealth();
        String rarityText = "Rarity: " + weapon.getRarity();

        ((TextView) findViewById(R.id.name)).setText(weapon.getName());
        ((TextView) findViewById(R.id.description)).setText(weapon.getDescription());
        ((TextView) findViewById(R.id.type)).setText(typeText);
        ((TextView) findViewById(R.id.damage)).setText(damageText);
        ((TextView) findViewById(R.id.health)).setText(healthText);
        ((TextView) findViewById(R.id.rarity)).setText(rarityText);
    }

    private void setupBundleInfo() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            weapon = GameManager.getInstance().getInventory().getWeapon(bundle.getString(WEAPON_UID));
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(weapon.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void updateWeaponsMenu() {
        GameManager.getInstance().updateWeaponInDatabase(weapon);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.result), weapon.getUUID());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void setEquipButtonText() {
        if (weapon.isEquipped()) {
            equipButton.setText(getString(R.string.unequip_weapon));
        } else {
            equipButton.setText(getString(R.string.equip_weapon));
        }
    }

    private void setupEquipButtonListener() {
        equipButton = findViewById(R.id.equip_button);
        setEquipButtonText();

        findViewById(R.id.equip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weapon.isEquipped()) {
                    weapon.setEquipped(false);
                    updateWeaponsMenu();
                } else {
                    if (GameManager.getInstance().getInventory().getEquippedWeapons().size() < 6) {
                        weapon.setEquipped(true);
                        updateWeaponsMenu();
                    } else {
                        App.showToast(getApplicationContext(), getString(R.string.err_unequip_weapon));
                    }
                }

            }
        });
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
