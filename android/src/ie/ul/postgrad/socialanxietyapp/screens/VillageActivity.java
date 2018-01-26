package ie.ul.postgrad.socialanxietyapp.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import ie.ul.postgrad.socialanxietyapp.game.SurveyAnswer;

import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.INFO_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.REVIEW_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.TRANSPARENT_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.HelpActivity.VILLAGE_INFO;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.BUY_KEY;
import static ie.ul.postgrad.socialanxietyapp.screens.ItemSelectActivity.SELL_KEY;

public class VillageActivity extends AndroidApplication implements LibGdxInterface, View.OnClickListener {

    private LinearLayout optionMenu;//Shows 4 button options for interacting with npc
    private LinearLayout textDisplay;//Where text is displayed on screen.
    private LinearLayout questionOptions;//Options for answering npc questions
    private TextView dialogue;//npc dialog text
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
        initViews();
        gm = GameManager.getInstance();
        gm.initDatabase(this);
        setupSaleItems();
        setupLibGdxView();
        showWelcomeText();
        showHelpInfo();
    }

    private void initViews() {
        optionMenu = findViewById(R.id.option_menu);
        textDisplay = findViewById(R.id.text_display);
        questionOptions = findViewById(R.id.question_options);
        dialogue = findViewById(R.id.dialogue);
        nextButton = findViewById(R.id.next_button);
    }

    private void setupSaleItems() {
        itemIdsForSale = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            itemIdsForSale.add((int) (Math.random() * 10) + 1);
        }
    }

    private void setupLibGdxView() {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        MainGame game = new MainGame(this, MainGame.CONVERSATION_SCREEN);
        game.updateConvo(1, "");
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
    }

    private void setupClickListeners() {
        setClickListener(R.id.buy_button);
        setClickListener(R.id.sell_button);
        setClickListener(R.id.talk_button);
        setClickListener(R.id.run_button);
        setClickListener(R.id.ans_0);
        setClickListener(R.id.ans_1);
        setClickListener(R.id.ans_2);
        setClickListener(R.id.ans_3);
        setClickListener(R.id.ans_4);
        setClickListener(R.id.go_back);
        setClickListener(R.id.next_button);
    }

    private void setClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    private void showWelcomeText() {
        showText();
        ((TextView) findViewById(R.id.speaker_name)).setText(getString(R.string.villager));
        setupClickListeners();
        scrollText(getString(R.string.village_welcome, GameManager.villageXP));
    }

    private void showHelpInfo() {
        SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);

        final String firstTimeVillageKey = "firstTimeVillage";
        boolean firstTimeMap = prefs.getBoolean(firstTimeVillageKey, true);
        if (firstTimeMap) {
            Intent tutorialIntent = new Intent(this, HelpActivity.class);
            tutorialIntent.putExtra(INFO_KEY, VILLAGE_INFO);
            tutorialIntent.putExtra(REVIEW_KEY, false);
            tutorialIntent.putExtra(TRANSPARENT_KEY, true);
            startActivity(tutorialIntent);
            prefs.edit().putBoolean(firstTimeVillageKey, false).apply();
        }
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
                showQuestion();
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

    private void showQuestion() {
        try {
            SharedPreferences prefs = this.getSharedPreferences("ie.ul.postgrad.socialanxietyapp", Context.MODE_PRIVATE);
            final String key = "SADKey";
            boolean hasSAD = prefs.getBoolean(key, false);
            if (!hasSAD) {

                surveying = true;
                questionNo = gm.getSurveyQuestion();
                String[] array = getResources().getStringArray(R.array.mini_spin);
                if (questionNo < array.length) {
                    scrollText(array[questionNo]);
                    showText();
                } else {
                    ArrayList<SurveyAnswer> answers = gm.initDatabase(this).getSurveyAnswers();
                    int total = 0;
                    for (SurveyAnswer answer : answers) {
                        total += answer.getAnswer();
                    }
                    if (total >= 6) {
                        prefs.edit().putBoolean(key, true).apply();
                        scrollText("You show some signs of Social Anxiety Disorder. In difficult times it may help to speak to professionals when you need someone to talk to, who can assist you in coping with your current circumstances.");
                    } else {
                        prefs.edit().putBoolean(key, false).apply();
                        scrollText("Based on your answers, it seems that you do not show signs of social anxiety.");
                    }
                    surveying = false;
                    showText();

                    final String surveykey = "firstTimeSurvey";
                    boolean firstTimeSurvey = prefs.getBoolean(surveykey, true);
                    if (firstTimeSurvey) {
                        gm.awardXP(this, 500);
                        prefs.edit().putBoolean(surveykey, false).apply();
                    }
                }
            } else {
                String[] tips = getResources().getStringArray(R.array.anxiety_tips);
                scrollText(tips[(int) (Math.random() * tips.length)]);
                showText();
            }

        } catch (Exception e) {
            showMenu();
        }

    }

    private void answerQuestion(int answer) {
        gm.answerSurveyQuestion(questionNo, answer);
        showQuestion();
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
    public Avatar getAvatar() {
        return new Avatar(1, 1, 1, 1);
        //return gm.getAvatar();
    }

    @Override
    public int getNPCId() {
        return 2;
    }

    @Override
    public void swordGameWon(boolean success) {

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
