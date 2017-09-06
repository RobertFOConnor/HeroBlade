package ie.ul.postgrad.socialanxietyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class WeaponDetailActivity extends AppCompatActivity {

    public static String WEAPON_UID = "uid";
    private WeaponItem weapon;
    private Button equipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weapon_detail);
        ActionBar actionBar = getSupportActionBar();

        App.setStatusBarColor(this);


        Bundle bundle = getIntent().getExtras();
        weapon = GameManager.getInstance().getInventory().getWeapon(bundle.getString(WEAPON_UID));

        if (actionBar != null) {
            actionBar.setTitle(weapon.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ((TextView) findViewById(R.id.name)).setText(weapon.getName());
        ((TextView) findViewById(R.id.description)).setText(weapon.getDescription());
        ((TextView) findViewById(R.id.type)).setText("Type: " + weapon.getType());
        ((TextView) findViewById(R.id.damage)).setText("Damage: " + weapon.getDamage());
        ((TextView) findViewById(R.id.health)).setText("Health: " + weapon.getCurrHealth());
        ((TextView) findViewById(R.id.rarity)).setText("Rarity: " + weapon.getRarity());

        equipButton = (Button) findViewById(R.id.equip_button);
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
