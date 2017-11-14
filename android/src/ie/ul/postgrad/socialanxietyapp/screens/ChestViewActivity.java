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
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.SoundManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;

public class ChestViewActivity extends AppCompatActivity {

    private LinearLayout chestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_view);

        chestList = (LinearLayout) findViewById(R.id.chest_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        ArrayList<ChestItem> chests = GameManager.getInstance().getInventory().getChests();
        if(chests.size() == 0) {
            ((TextView) findViewById(R.id.subtitle)).setText(getString(R.string.no_chests));
        }

        for (ChestItem chest : chests) {

            LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.chest_view, null);
            ProgressBar progressBar = (ProgressBar) chestView.findViewById(R.id.progressBar);
            progressBar.setMax((int) chest.getMaxDistance());
            progressBar.setProgress((int) chest.getCurrDistance());

            TextView text = (TextView) chestView.findViewById(R.id.title);
            text.setText(getString(R.string.chest_unlocked_distance, String.format("%.1f", chest.getCurrDistance() / 1000f)));

            ImageView image = (ImageView) chestView.findViewById(R.id.image);
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
    }
}
