package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class ChestOpenActivity extends AppCompatActivity {

    private LinearLayout rewardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_open);

        rewardList = (LinearLayout) findViewById(R.id.reward_list);

        //Random chest
        GameManager.getInstance().startGame(getApplicationContext());
        ChestItem chest = GameManager.getInstance().getChests().get(0);
        ((TextView) findViewById(R.id.title)).setText(chest.getName() + " Unlocked!");
        ((ImageView) findViewById(R.id.chest_image)).setImageResource(chest.getImageID());

        LayoutInflater inflater = LayoutInflater.from(this);

        GameManager.getInstance().removeChest();
        WeaponItem weaponReward = GameManager.getInstance().unlockWeapon(this, chest.getRarity());

        LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.fragment_reward_item, null);
        ((TextView) chestView.findViewById(R.id.item_title)).setText(weaponReward.getName());
        ((TextView) chestView.findViewById(R.id.item_description)).setText(weaponReward.getDescription());

        rewardList.addView(chestView);

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GameManager.getInstance().awardChest(this);
    }
}
