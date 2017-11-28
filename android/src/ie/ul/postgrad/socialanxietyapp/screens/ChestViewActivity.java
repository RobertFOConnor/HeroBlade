package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.CHEST_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;

public class ChestViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_view);

        LinearLayout chestList = findViewById(R.id.chest_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        ArrayList<ChestItem> chests = GameManager.getInstance().getInventory().getChests();
        if (chests.size() == 0) {
            ((TextView) findViewById(R.id.subtitle)).setText(getString(R.string.no_chests));
        }

        for (ChestItem chest : chests) {

            LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.chest_view, null);
            ProgressBar progressBar = chestView.findViewById(R.id.progressBar);
            progressBar.setMax((int) chest.getMaxDistance());
            progressBar.setProgress((int) chest.getCurrDistance());

            TextView text = chestView.findViewById(R.id.title);
            text.setText(getString(R.string.chest_unlocked_distance, String.format("%.1f", chest.getCurrDistance() / 1000f)));

            ImageView image = chestView.findViewById(R.id.image);
            image.setImageResource(chest.getImageID());
            chestList.addView(chestView);

        }
        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                SoundManager.getInstance(getApplicationContext()).playSound(SoundManager.Sound.BACK);
            }
        });
        showHelpInfo();
    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String key = "firstTimeChests";
        boolean firstTimeMap = prefs.getBoolean(key, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            //bundle here...
            tutorialIntent.putExtra(INFO_KEY, CHEST_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(key, false).apply();
        }
    }
}
