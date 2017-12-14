package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.LEVEL_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;

public class LevelUpActivity extends AppCompatActivity {

    private LinearLayout resourceList;
    private LayoutInflater inflater;
    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_up);
        resourceList = findViewById(R.id.resource_list);
        inflater = LayoutInflater.from(this);
        gm = GameManager.getInstance();
        String level = String.valueOf(gm.getPlayer().getLevel());
        ((TextView) findViewById(R.id.level_num)).setText(level);
        addChestRewardView(); //Display chest rewarded to player for leveling up.
        MediaPlayer chime = MediaPlayer.create(this, R.raw.level_up);
        if (chime != null) {
            chime.start();
        }

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                showHelpInfo();
            }
        });

        if (gm.getPlayer().getLevel() < ItemFactory.CRAFTABLES.length) {
            addRecipeRewardView();
        }
    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String key = "firstTimeLevelUp";
        boolean firstTimeMap = prefs.getBoolean(key, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            //bundle here...
            tutorialIntent.putExtra(INFO_KEY, LEVEL_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(key, false).apply();
        }
    }

    private void addChestRewardView() {
        LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.chest_view, null);
        ProgressBar progressBar = chestView.findViewById(R.id.progressBar);

        ArrayList<ChestItem> chests = gm.getChests();
        ChestItem chest = chests.get(chests.size() - 1);
        progressBar.setMax((int) chest.getMaxDistance());
        progressBar.setProgress((int) chest.getCurrDistance());
        TextView text = chestView.findViewById(R.id.title);
        text.setText(getString(R.string.chest_unlocked_distance, String.format(Locale.ENGLISH, "%.1f", chest.getCurrDistance() / 1000f)));
        ImageView image = chestView.findViewById(R.id.image);
        image.setImageResource(chest.getImageID());
        resourceList.addView(chestView);
    }

    private void addRecipeRewardView() {
        LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.chest_view, null);
        chestView.findViewById(R.id.progressBar).setVisibility(View.GONE);

        TextView text = chestView.findViewById(R.id.title);
        text.setText(getString(R.string.new_recipe));
        ImageView image = chestView.findViewById(R.id.image);
        Item item = ItemFactory.buildItem(this, ItemFactory.CRAFTABLES[0]);
        image.setImageResource(item.getImageID());
        resourceList.addView(chestView);
    }
}
