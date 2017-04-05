package ie.ul.postgrad.socialanxietyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;


public class ActionActivity extends AppCompatActivity implements View.OnClickListener {

    static final int WEAPON_REQUEST = 1;
    public static final String ACTIVE_ITEM_ID = "id";
    //private WorldItem activeItem;
    private Player player;
    WeaponItem weaponItem;

    private Button actionButton;
    private Button leaveButton;
    private Button weaponButton;
    private ImageView itemView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_action);

        Bundle bundle = getIntent().getExtras();
        //activeItem = (WorldItem) ItemFactory.buildItem(this, bundle.getInt(ACTIVE_ITEM_ID));

        player = GameManager.getInstance().getPlayer();

        actionButton = (Button) findViewById(R.id.action_button);
        leaveButton = (Button) findViewById(R.id.leave_button);
        weaponButton = (Button) findViewById(R.id.weapon_button);
        itemView = (ImageView) findViewById(R.id.item_view);

       // itemView.setImageResource(activeItem.getImageID());
        //((TextView) findViewById(R.id.item_title)).setText(activeItem.getName());

        statusText = ((TextView) findViewById(R.id.status_display));

        if (player.getWeapon() != -1) {
            weaponItem = (WeaponItem) ItemFactory.buildItem(this, player.getWeapon());
            updateWeapon();
        }


        actionButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);
        weaponButton.setOnClickListener(this);
    }

    /*private void doAction() {
        if (activeItem.getHitAmount() > 0) {
            int hitCount = activeItem.onHit();

            if (player.getWeapon() != -1) {
                hitCount += weaponItem.getDamage();
            }

            int itemId = activeItem.getDropItemID();

            GameManager.getInstance().getInventory().addItem(itemId, hitCount);
            GameManager.getInstance().updateItemInDatabase(itemId);


            String statusMessage = "You have received +" + hitCount + " " + ItemFactory.buildItem(this, activeItem.getDropItemID()).getName() + ".";
            statusText.setText(statusMessage);

            activeItem.setHitAmount(activeItem.getHitAmount() - 1);

            if (activeItem.getHitAmount() <= 0 && actionButton.isEnabled()) {
                actionButton.setText("Depleted");
                actionButton.setEnabled(false);
                statusText.setText("The resource has been depleted! +100XP");
                player.setXp(player.getXp()+100);
                GameManager.getInstance().updatePlayerInDatabase();
            }
        }

    }*/

    private void leave() {
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.action_button:
                //doAction();
                break;

            case R.id.leave_button:
                leave();
                break;

            case R.id.weapon_button:
                startActivityForResult(new Intent(this, WeaponSelectionActivity.class), WEAPON_REQUEST);
                break;
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
                player.setWeapon(result);
                updateWeapon();
            }
        }
    }

    private void updateWeapon() {
        String weaponText = "Player Weapon: " + weaponItem.getName();
        weaponButton.setText(weaponText);
        ((ImageView) findViewById(R.id.weapon_view)).setImageDrawable(getDrawable(weaponItem.getImageID()));
    }
}
