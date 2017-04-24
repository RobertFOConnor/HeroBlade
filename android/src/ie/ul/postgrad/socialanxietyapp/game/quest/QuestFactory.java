package ie.ul.postgrad.socialanxietyapp.game.quest;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Robert on 07-Apr-17.
 */

public class QuestFactory {

    public static Quest buildQuest(int id, LatLng location) {

        //Below is an example hardcoded dummy quest.
        //This quest has the player help a man find his wife. (Very basic.)
        //Real quests will be stored using xml.

        Quest quest = new Quest(id, "Find the village!", "Seek out and locate the nearby village.", location, Quest.TYPE_TALK, 3) {

            @Override
            public void updateQuest() {

                if (getStage() == 1) {
                    setType(Quest.TYPE_TALK);

                    ArrayList<Dialogue> dialogues = new ArrayList<>();

                    dialogues.add(new Dialogue("Bill", 1, new String[]{"Hey there!", "How are you today?"}));
                    dialogues.add(new Dialogue("You", 0, new String[]{"Can't complain."}));
                    dialogues.add(new Dialogue("Bill", 1, new String[]{"Could you do me a favour? Will you have a look around for my wife?", "I haven't seen her in a while."}));
                    dialogues.add(new Dialogue("You", 0, new String[]{"No problem! I'll do my best."}));
                    dialogues.add(new Dialogue("Bill", 1, new String[]{"Thanks, I last saw her outside the village somewhere.", "Take this sword with you in case there's any trouble."}));
                    setDialogues(dialogues);

                    addRewardItems(59, 1);
                    setRewardXP(500);
                } else if (getStage() == 2) {

                    setType(Quest.TYPE_TALK);
                    setTitle("Find Bill's Wife");
                    setDescription("Help Bill find his wife near the village.");

                    ArrayList<Dialogue> dialogues = new ArrayList<>();

                    dialogues.add(new Dialogue("Nancy", 2, new String[]{"Hello, can I help you?"}));
                    dialogues.add(new Dialogue("You", 0, new String[]{"Your husband is looking for you."}));
                    dialogues.add(new Dialogue("Nancy", 2, new String[]{"Oh!", "I completely forgot.", "Thank you, I'll head home now!"}));
                    dialogues.add(new Dialogue("You", 0, new String[]{"Glad to help."}));
                    setDialogues(dialogues);

                    addRewardItems(29, 3);
                    setRewardXP(500);
                }
            }
        };
        return quest;
    }
}
