package ie.ul.postgrad.socialanxietyapp.screens;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;

public class ChestOpenActivity extends AppCompatActivity {

    private LinearLayout rewardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chest_open);

        rewardList = (LinearLayout) findViewById(R.id.reward_list);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < 5; i++) {

            LinearLayout chestView = (LinearLayout) inflater.inflate(R.layout.fragment_reward_item, null);
            rewardList.addView(chestView);
        }

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
