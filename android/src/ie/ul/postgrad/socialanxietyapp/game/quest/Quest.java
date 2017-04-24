package ie.ul.postgrad.socialanxietyapp.game.quest;

import android.util.SparseIntArray;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robert on 16-Mar-17.
 */

public class Quest {

    private int id;
    private int stage;
    private String title;
    private String description;
    private LatLng location;

    private ArrayList<Dialogue> dialogues;

    private SparseIntArray rewardItems = new SparseIntArray();
    private int rewardXP;

    private String type;
    public static String TYPE_TALK = "talk";
    public static String TYPE_BATTLE = "battle";

    private int stageCount;
    private boolean isCompleted = false;


    public Quest(int id, String title, String description, LatLng location, String type, int stageCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        stage = 1;
        this.type = type;
        this.stageCount = stageCount;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        if(stage < stageCount) {
            this.stage = stage;
        } else {
            isCompleted = true;
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void updateQuest() {

    }

    public void setDialogues(ArrayList<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }

    public Dialogue getCurrDialogue() {
        if (dialogues.size() > 0) {
            return dialogues.get(0);
        } else {
            return null;
        }
    }

    public int getCharacterId() {
        return dialogues.get(0).getSpeakerImage();
    }

    public void updateDialogue() {
        dialogues.remove(0);
    }

    public SparseIntArray getRewardItems() {
        return rewardItems;
    }

    public void addRewardItems(int id, int quantity) {
        rewardItems.put(id, quantity);
    }

    public int getRewardXP() {
        return rewardXP;
    }

    public void setRewardXP(int rewardXP) {
        this.rewardXP = rewardXP;
    }
}
