package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.DBHelper;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.quest.Quest;

/**
 * Created by Robert on 15-Mar-17.
 * <p>
 * Game Manager singleton class for handling all interactions with the main game objects
 * and syncing game data with the database.
 */

public class GameManager {

    //Game objects
    private Player player;
    private Inventory inventory;
    private ArrayList<ConsumedLocation> visitedLoactions;

    //SQLLite objects
    private static DBHelper databaseHelper;
    // Firebase objects for login.
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private Quest activeQuest;

    private static final GameManager ourInstance = new GameManager();

    public static GameManager getInstance() {
        return ourInstance;
    }

    private GameManager() {
        player = new Player();
        inventory = new Inventory();
        visitedLoactions = new ArrayList<>();
    }

    public void startGame(Context context) {
        //Initialize database helper
        databaseHelper = new DBHelper(context);

        //Add new player to database
        if (databaseHelper.numberOfPlayers() == 0) {
            databaseHelper.insertPlayer(mAuth.getCurrentUser().getEmail(), mAuth.getCurrentUser().getEmail(), 0, 1, 0);
            databaseHelper.insertAvatar(new Avatar(0, 0));
        }

        setPlayer(databaseHelper.getPlayer(1));
        setInventory(new Inventory(databaseHelper.getInventory()));
    }

    public Player getPlayer() {
        return player;
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    private void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }


    public void updateItemInDatabase(int itemId) {
        int quantity = getInventory().getItems().get(itemId);

        if (databaseHelper.getInventoryData(itemId).moveToFirst()) {
            if (quantity > 0) {
                databaseHelper.updateItem(1, itemId, quantity);
            } else {
                databaseHelper.deleteItem(itemId);
            }
        } else {
            databaseHelper.insertItem(1, itemId, quantity);
        }
    }

    public void updatePlayerInDatabase() {
        databaseHelper.updatePlayer(player);
    }

    public void closeDatabase() {
        databaseHelper.close();
    }

    public static DBHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void givePlayer(Context context, int itemId, int quantity) {
        GameManager.getInstance().getInventory().addItem(itemId, quantity);
        GameManager.getInstance().updateItemInDatabase(itemId);

        Toast.makeText(context, "You recieved " + quantity + " " + ItemFactory.buildItem(context, itemId).getName(), Toast.LENGTH_SHORT).show();
    }

    public Quest getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(Quest activeQuest) {
        this.activeQuest = activeQuest;
    }
}
