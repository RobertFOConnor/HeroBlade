package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.LibGdxInterface;
import ie.ul.postgrad.socialanxietyapp.MainGame;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.game.GameManager;

public class BlacksmithActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private MainGame game; //Libgdx game object, displays npc and village background
    private LinearLayout optionMenu;//Shows 4 button options for interacting with npc
    private LinearLayout textDisplay;//Where text is displayed on screen.
    private LinearLayout questionOptions;//Options for answering npc questions
    private TextView dialogue;//npc dialog text
    private boolean doneTalking = false;//true if users already answered npc question
    private boolean leaving = false;//true if user is leaving conversation
    public static ArrayList<Integer> itemIdsForSale;//ids of items user can buy from this village

    public static final String SELL_WEAPON_KEY = "sell_weapons";
    public static final String BUY_WEAPON_KEY = "buy_weapons";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacksmith);
        optionMenu = (LinearLayout) findViewById(R.id.option_menu);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        questionOptions = (LinearLayout) findViewById(R.id.question_options);
        dialogue = (TextView) findViewById(R.id.dialogue);

        itemIdsForSale = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            itemIdsForSale.add((int) (Math.random() * 10) + 1);
        }


        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leaving) {
                    finish();
                } else {
                    showMenu();
                }
            }
        });

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        game = new MainGame(this, MainGame.CONVERSATION_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
        game.updateConvo(1, "SMITH");

        showText();
        ((TextView) findViewById(R.id.speaker_name)).setText(getString(R.string.blacksmith));
        dialogue.setText(getString(R.string.blacksmith_welcome, GameManager.blacksmithXP));

        //Setup click listeners.
        findViewById(R.id.buy_button).setOnClickListener(this);
        findViewById(R.id.sell_button).setOnClickListener(this);
        findViewById(R.id.talk_button).setOnClickListener(this);
        findViewById(R.id.run_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.buy_button:
                intent = new Intent(getApplicationContext(), ItemSelectActivity.class);
                intent.putExtra(ItemSelectActivity.SELECT_TYPE, BUY_WEAPON_KEY);
                startActivity(intent);
                break;
            case R.id.sell_button:
                intent = new Intent(getApplicationContext(), ItemSelectActivity.class);
                intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELL_WEAPON_KEY);
                startActivity(intent);
                break;
            case R.id.talk_button:

                break;
            case R.id.run_button:
                leaving = true;
                showText();
                dialogue.setText(R.string.blacksmith_goodbye);
                break;
        }
    }

    private void showText() {
        optionMenu.setVisibility(View.GONE);
        textDisplay.setVisibility(View.VISIBLE);
        findViewById(R.id.next_button).setVisibility(View.VISIBLE);
    }

    private void showMenu() {
        optionMenu.setVisibility(View.VISIBLE);
        textDisplay.setVisibility(View.GONE);
    }

    @Override
    public void saveAvatar(Avatar avatar) {
        //empty method. (This isn't called from this activity. (used for changing avatar appearance.))
    }

    @Override
    public void collectResource() {
        //empty method. (This isn't called from this activity. (used for changing avatar appearance.))
    }

    @Override
    public Avatar getAvatar() {
        return new Avatar(1, 1, 1, 1);
        //return GameManager.getInstance().getAvatar();
    }

    @Override
    public int getNPCId() {
        return 1;
    }

    @Override
    public void finishGame() {
        finish();
    }
}