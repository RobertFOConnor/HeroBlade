package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.XPLevels;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class PlayerAvatarActivity extends AppCompatActivity implements View.OnClickListener {

    static final int WEAPON_REQUEST = 1;
    private Button weaponButton, chestButton;
    private Player player;
    private ImageView wepaonView;

    private ProgressBar xpBar;
    private TextView xpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_avatar);
        player = GameManager.getInstance().getPlayer();
        GameManager.getInstance().updatePlayerInDatabase();

        String nameField = "Name: " + player.getName();
        ((TextView) findViewById(R.id.name_field)).setText(nameField);

        String goldField = "Gold: $" + player.getMoney();
        ((TextView) findViewById(R.id.gold_field)).setText(goldField);

        String levelField = "LVL: " + player.getLevel();
        ((TextView) findViewById(R.id.level_text)).setText(levelField);


        xpBar = (ProgressBar) findViewById(R.id.xp_bar);
        int maxXP = XPLevels.XP_LEVELS[player.getLevel()];
        int currXP = player.getXp() - (player.getXPNeeded()-XPLevels.XP_LEVELS[player.getLevel()]);

        xpBar.setMax(maxXP);
        xpBar.setProgress(currXP);

        xpText = (TextView) findViewById(R.id.xp_text);
        xpText.setText("XP: " + currXP + "/" + maxXP);

        wepaonView = (ImageView) findViewById(R.id.weapon_view);

        weaponButton = (Button) findViewById(R.id.weapon_button);
        weaponButton.setOnClickListener(this);

        chestButton = (Button) findViewById(R.id.chest_button);
        chestButton.setOnClickListener(this);

        updateWeaponButton();

        Button logOutButton = (Button) findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.weapon_button:
                showWeapons();
                break;
            case R.id.chest_button:
                showChest();
                break;
        }
    }

    private void showWeapons() {
        Intent i = new Intent(this, WeaponSelectionActivity.class);
        startActivityForResult(i, WEAPON_REQUEST);
        updateWeaponButton();
    }

    private void showChest() {
        startActivity(new Intent(this, StepCounterActivity.class));
    }

    private void updateWeaponButton() {
        if (player.getWeapon() != -1) {
            WeaponItem weaponItem = (WeaponItem) ItemFactory.buildItem(player.getWeapon());
            weaponButton.setText("Player Weapon (" + weaponItem.getName() + ")");
            wepaonView.setImageDrawable(getDrawable(weaponItem.getImageID()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a weapon.
                int result = data.getIntExtra(getString(R.string.result), 0);
                player.setWeapon(result);
                updateWeaponButton();
            }
        }
    }
}
