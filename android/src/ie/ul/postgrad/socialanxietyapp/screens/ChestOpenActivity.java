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
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;

public class ChestOpenActivity extends AppCompatActivity {

    public static String CHEST_ID_KEY = "chest_id";
    public static String SWORD_ID_KEY = "sword_id";
    private int chestId = 76; //DEFAULT VALUE
    private int swordId = 1; //DEFAULT VALUE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_open);
        getBundleInfo();
        GameManager gm = GameManager.getInstance();
        gm.initDatabase(this);
        updateChestView();
        updateRewardView();
        setupButtonListener();
    }

    private void getBundleInfo() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            chestId = bundle.getInt(CHEST_ID_KEY);
            swordId = bundle.getInt(SWORD_ID_KEY);
        }
    }

    private void updateChestView() {
        ChestItem chestItem = (ChestItem) ItemFactory.buildItem(this, chestId);
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.chest_unlocked, chestItem.getName()));
        ((TextView) findViewById(R.id.cash_text)).setText(getString(R.string.cash_display, chestItem.getRarity() * 500));
        ((ImageView) findViewById(R.id.chest_image)).setImageResource(chestItem.getImageID());
    }

    private void updateRewardView() {
        LinearLayout rewardList = findViewById(R.id.reward_list);
        WeaponItem weaponItem = WeaponFactory.buildWeapon(this, swordId);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.fragment_reward_item, null);
        ((TextView) chestView.findViewById(R.id.item_title)).setText(weaponItem.getName());
        ((TextView) chestView.findViewById(R.id.item_description)).setText(weaponItem.getDescription());
        ((ImageView) chestView.findViewById(R.id.item_image)).setImageResource(weaponItem.getTypeDrawableRes());
        rewardList.addView(chestView);
    }

    private void setupButtonListener() {
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SoundManager.getInstance(getApplicationContext()).playSound(SoundManager.Sound.BACK);
            }
        });
    }
}
