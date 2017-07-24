package ie.ul.postgrad.socialanxietyapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.adapter.WeaponListAdapter;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class WeaponSelectionActivity extends AppCompatActivity {

    public static final String CURR_WEAPON = "curr_weapon";
    private ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weapon_selection);

        itemList = (ListView) findViewById(R.id.item_list);

        Bundle bundle = getIntent().getExtras();
        final String currWeaponUUID = bundle.getString(CURR_WEAPON);

        WeaponListAdapter adapter = new WeaponListAdapter(this, GameManager.getInstance().getInventory().getEquippedWeapons(), currWeaponUUID);
        itemList.setAdapter(adapter);

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeaponItem selectedWeapon = ((WeaponItem) itemList.getItemAtPosition(position));

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", selectedWeapon.getUUID());

                if (currWeaponUUID != null) {
                    if (currWeaponUUID.equals(selectedWeapon.getUUID())) { //Check if they already equipped this weapon.
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    } else if (selectedWeapon.getCurrHealth() < 0) {
                        Toast.makeText(getApplicationContext(), selectedWeapon.getName() + " is broken and cannot be used.", Toast.LENGTH_SHORT).show();
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Choose a weapon");
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
