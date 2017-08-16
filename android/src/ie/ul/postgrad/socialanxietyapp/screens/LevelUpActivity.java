package ie.ul.postgrad.socialanxietyapp.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.AchievementManager;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;

public class LevelUpActivity extends AppCompatActivity {

    private LinearLayout resourceList;
    private LayoutInflater inflater;
    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);
        resourceList = (LinearLayout) findViewById(R.id.resource_list);
        inflater = LayoutInflater.from(this);
        gm = GameManager.getInstance();
        String level = String.valueOf(gm.getPlayer().getLevel());
        ((TextView) findViewById(R.id.level_num)).setText(level);
        addChestRewardView(); //Display chest rewarded to player for leveling up.
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        AchievementManager.checkLevelAchievements(this);
    }

    private void addChestRewardView() {
        LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.chest_view, null);
        ProgressBar progressBar = (ProgressBar) chestView.findViewById(R.id.progressBar);

        ArrayList<ChestItem> chests = gm.getChests();
        ChestItem chest = chests.get(chests.size() - 1);
        progressBar.setMax((int) chest.getMaxDistance());
        progressBar.setProgress((int) chest.getCurrDistance());
        TextView text = (TextView) chestView.findViewById(R.id.title);
        text.setText(getString(R.string.chest_unlocked_distance, String.format("%.1f", chest.getCurrDistance() / 1000f)));
        ImageView image = (ImageView) chestView.findViewById(R.id.image);
        image.setImageResource(chest.getImageID());
        resourceList.addView(chestView);
    }
}
