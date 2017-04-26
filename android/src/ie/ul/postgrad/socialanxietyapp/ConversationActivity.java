package ie.ul.postgrad.socialanxietyapp;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ie.ul.postgrad.socialanxietyapp.game.GameManager;
import ie.ul.postgrad.socialanxietyapp.game.quest.Dialogue;
import ie.ul.postgrad.socialanxietyapp.game.quest.Quest;

public class ConversationActivity extends AndroidApplication implements LibGdxInterface {

    private Quest quest;
    private MainGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        quest = GameManager.getInstance().getActiveQuest();

        updateUI();

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        game = new MainGame(this, MainGame.CONVERSATION_SCREEN);
        View v = initializeForView(game, config);
        ((LinearLayout) findViewById(R.id.character_layout)).addView(v);
    }

    private void updateUI() {

        Dialogue dialogue = quest.getCurrDialogue();

        String name = dialogue.getSpeaker();
        String text = dialogue.getSentence();

        if (text.equals("")) {
            quest.updateDialogue();
            dialogue = quest.getCurrDialogue();

            if (dialogue != null) {

                name = dialogue.getSpeaker();
                text = dialogue.getSentence();

                game.updateConvo(quest.getCharacterId());
            } else {

                SparseIntArray rewardItems = quest.getRewardItems();

                for (int i = 0; i < rewardItems.size(); i++) {
                    GameManager.getInstance().givePlayer(this, rewardItems.keyAt(i), rewardItems.valueAt(i));
                }
                quest.setStage(quest.getStage() + 1);
                quest.updateQuest();

                if (quest.isCompleted()) {
                    GameManager.getInstance().setActiveQuest(null);
                }
                finish();
            }
        }

        ((TextView) findViewById(R.id.speaker_name)).setText(name);
        ((TextView) findViewById(R.id.dialogue)).setText(text);
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
        return GameManager.getDatabaseHelper().getAvatar(1);
    }

    @Override
    public int getNPCId() {
        return quest.getCharacterId();
    }

    @Override
    public void finishGame() {
        finish();
    }
}
