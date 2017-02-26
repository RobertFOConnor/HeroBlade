package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ie.ul.postgrad.socialanxietyapp.game.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.WeaponItem;

public class PlayerAvatarActivity extends AppCompatActivity implements View.OnClickListener {

    static final int WEAPON_REQUEST = 1;
    private Button weaponButton;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_player_avatar);
        player = MapsActivity.player;

        String nameField = "Name: "+player.getName();
        ((TextView) findViewById(R.id.name_field)).setText(nameField);

        weaponButton = (Button) findViewById(R.id.weapon_button);
        weaponButton.setOnClickListener(this);

        updateWeaponButton();

        Button logOutButton = (Button) findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.weapon_button:
                showWeapons();
                break;
        }
    }

    private void showWeapons() {
        startActivityForResult(new Intent(getApplicationContext(), WeaponSelectionActivity.class), WEAPON_REQUEST);
        //get selected weapon back from intent.

        //set player weapon to selected weapon.
        //player.setWeapon();

        updateWeaponButton();
    }

    private void updateWeaponButton() {
        if (player.getWeapon() != null) {
            weaponButton.setText("Player Weapon (" + player.getWeapon().getName() + ")");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == WEAPON_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                // The user picked a weapon.
                int result = data.getIntExtra("result", 0);
                player.setWeapon((WeaponItem) ItemFactory.buildItem(result));
                updateWeaponButton();
            }
        }
    }
}
