package ie.ul.postgrad.socialanxietyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.Player;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;

public class ResourceResultActivity extends AppCompatActivity {

    public static final String MARKER_ID = "marker_id";
    private int markerId;

    private LinearLayout resourceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_result);

        markerId = getIntent().getIntExtra(MARKER_ID, 0);

        resourceList = (LinearLayout) findViewById(R.id.resource_list);

        ArrayList<Integer> collectedItems = new ArrayList<>();

        //TEMP

        if (markerId == 6) {
            collectedItems.add(2);
            collectedItems.add(24);
        } else if (markerId == 7) {
            collectedItems.add(3);
            collectedItems.add(25);
        } else if (markerId == 8) {
            collectedItems.add(4);
        } else if (markerId == 9) {
            collectedItems.add(14);
            collectedItems.add(15);
        } else if (markerId == 10) {
            collectedItems.add(16);
            collectedItems.add(17);
        } else if (markerId == 11) {
            collectedItems.add(26);
        }

        for (Integer itemId : collectedItems) {
            TextView tv = new TextView(this);
            tv.setText(ItemFactory.buildItem(this, itemId).getName());
            tv.setTextColor(Color.BLACK);
            resourceList.addView(tv);

            GameManager.getInstance().givePlayer(this, itemId, 1);

        }

        TextView tv = new TextView(this);
        tv.setText("+50XP");
        tv.setTextColor(Color.BLACK);
        resourceList.addView(tv);

        GameManager.getInstance().awardXP(50);

        findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
