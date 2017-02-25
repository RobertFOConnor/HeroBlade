package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ie.ul.postgrad.socialanxietyapp.game.Player;

public class PlayerAvatarActivity extends AppCompatActivity implements View.OnClickListener {

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
        Intent intent = new Intent(getApplicationContext(), InventoryActivity.class);
        startActivity(intent);

        //get selected weapon back from intent.

        //set player weapon to selected weapon.
        //player.setWeapon();

        updateWeaponButton();
    }

    private void updateWeaponButton() {
        if (player.getWeapon() != null) {
            weaponButton.setText("Player Weapon (" + player.getWeapon() + ")");
        }
    }
}
