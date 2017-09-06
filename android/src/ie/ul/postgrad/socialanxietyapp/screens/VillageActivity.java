package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.BUY_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELL_KEY;

public class VillageActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private MainGame game; //Libgdx game object, displays npc and village background
    private LinearLayout optionMenu;//Shows 4 button options for interacting with npc
    private LinearLayout textDisplay;//Where text is displayed on screen.
    private LinearLayout questionOptions;//Options for answering npc questions
    private TextView dialogue;//npc dialog text
    private int talkCount = 0;//number of times npc has been talked to.
    private boolean doneTalking = false;//true if users already answered npc question
    private boolean leaving = false;//true if user is leaving conversation
    public static ArrayList<Integer> itemIdsForSale;//ids of items user can buy from this village
    private Button nextButton;
    private boolean surveying = false;
    private int questionNo;
    private GameManager gm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village);
        optionMenu = (LinearLayout) findViewById(R.id.option_menu);
        textDisplay = (LinearLayout) findViewById(R.id.text_display);
        questionOptions = (LinearLayout) findViewById(R.id.question_options);
        dialogue = (TextView) findViewById(R.id.dialogue);
        nextButton = (Button) findViewById(R.id.next_button);
        gm = GameManager.getInstance();
        gm.initDatabaseHelper(this);

        itemIdsForSale = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            itemIdsForSale.add((int) (Math.random() * 10) + 1);
        }

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        game = new MainGame(this, MainGame.CONVERSATION_SCREEN);
        game.updateConvo(1, "");
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);

        showText();
        ((TextView) findViewById(R.id.speaker_name)).setText(getString(R.string.villager));

        //Setup click listeners.
        findViewById(R.id.buy_button).setOnClickListener(this);
        findViewById(R.id.sell_button).setOnClickListener(this);
        findViewById(R.id.talk_button).setOnClickListener(this);
        findViewById(R.id.run_button).setOnClickListener(this);
        findViewById(R.id.ans_0).setOnClickListener(this);
        findViewById(R.id.ans_1).setOnClickListener(this);
        findViewById(R.id.ans_2).setOnClickListener(this);
        findViewById(R.id.ans_3).setOnClickListener(this);
        findViewById(R.id.ans_4).setOnClickListener(this);
        findViewById(R.id.go_back).setOnClickListener(this);
        findViewById(R.id.next_button).setOnClickListener(this);

        scrollText(getString(R.string.village_welcome, GameManager.villageXP));

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.next_button:
                if (leaving) {
                    finish();
                } else {
                    showMenu();
                }
                break;
            case R.id.buy_button:
                intent = new Intent(getApplicationContext(), ItemSelectActivity.class);
                intent.putExtra(ItemSelectActivity.SELECT_TYPE, BUY_KEY);
                startActivity(intent);
                break;
            case R.id.sell_button:
                intent = new Intent(getApplicationContext(), ItemSelectActivity.class);
                intent.putExtra(ItemSelectActivity.SELECT_TYPE, SELL_KEY);
                startActivity(intent);
                break;
            case R.id.talk_button:
                    showText();
                    if (doneTalking) {
                        scrollText(getString(R.string.village_done_talking));
                    } else {
                        surveying = true;
                        questionNo = gm.getSurveyQuestion();
                        if (questionNo >= 20) {
                            doneTalking = true;
                            surveying = false;
                        }

                        String[] array = getResources().getStringArray(R.array.anxiety_questions);
                        scrollText(array[questionNo] + " " + getString(R.string.question_part_2));
                    }

                break;
            case R.id.run_button:
                leaving = true;
                showText();
                scrollText(getString(R.string.village_goodbye));
                break;
            case R.id.ans_0:
                answerQuestion(0);
                break;
            case R.id.ans_1:
                answerQuestion(1);
                break;
            case R.id.ans_2:
                answerQuestion(2);
                break;
            case R.id.ans_3:
                answerQuestion(3);
                break;
            case R.id.ans_4:
                answerQuestion(4);
                break;
            case R.id.go_back:
                questionOptions.setVisibility(View.GONE);
                surveying = false;
                showMenu();
                break;
        }
    }

    private void answerQuestion(int answer) {
        showText();
        scrollText(getString(R.string.village_reaction, GameManager.villagerTalkXP));
        gm.awardXP(this, GameManager.villagerTalkXP);
        talkCount++;
        if (talkCount > 2) {
            doneTalking = true;
        }

        gm.answerSurveyQuestion(questionNo, answer);
        surveying = false;
    }

    private void showText() {
        optionMenu.setVisibility(View.GONE);
        textDisplay.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        questionOptions.setVisibility(View.GONE);
    }

    private void showMenu() {
        if (surveying) {
            questionOptions.setVisibility(View.VISIBLE);
        } else {
            optionMenu.setVisibility(View.VISIBLE);
            textDisplay.setVisibility(View.GONE);
        }
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
        //return gm.getAvatar();
    }

    @Override
    public int getNPCId() {
        return 2;
    }

    @Override
    public void finishGame() {
        finish();
    }

    public void scrollText(final String message) {
        nextButton.setVisibility(View.GONE);

        final Handler mainHandler = new Handler();
        Runnable r = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                try {
                    while (count <= message.length()) {
                        Thread.sleep(50);
                        final String temp = message.substring(0, count);
                        count++;
                        mainHandler.post(new Runnable() {
                            public void run() {
                                dialogue.setText(temp);
                            }
                        });
                    }
                    mainHandler.post(new Runnable() {
                        public void run() {
                            nextButton.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        public void run() {
                            dialogue.setText(message);
                            showText();
                        }
                    });

                }
            }
        };
        new Thread(r).start();
    }
}
