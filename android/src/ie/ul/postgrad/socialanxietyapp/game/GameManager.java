package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.DBHelper;
import ie.ul.postgrad.socialanxietyapp.game.item.IDs;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
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
    private ArrayList<ConsumedLocation> visitedLocations;

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
        visitedLocations = new ArrayList<>();
    }

    public void startGame(Context context, String userName) {
        //Initialize database helper
        databaseHelper = new DBHelper(context);

        //Add new player to database
        if (databaseHelper.numberOfPlayers() == 0) {
            databaseHelper.insertPlayer(userName, mAuth.getCurrentUser().getEmail(), 0, 1, 0, 20);
            databaseHelper.insertAvatar(new Avatar(0, 0));
        }

        setPlayer(databaseHelper.getPlayer(1));
        setInventory(new Inventory(databaseHelper.getInventory(), databaseHelper.getWeapons(), context));
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

    public void updateWeaponInDatabase(int itemId, int currHealth) {

        if (databaseHelper.getWeaponsData(itemId).moveToFirst()) {
            if (currHealth > 0) {
                databaseHelper.updateWeapon(1, itemId, currHealth);
                System.out.println("WEAPON UPDATED!");
            } else {
                databaseHelper.deleteWeapon(itemId);
            }
        } else {
            databaseHelper.insertWeapon(1, itemId, currHealth);
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
        if (IDs.isWeapon(itemId)) {
            WeaponItem weaponItem = (WeaponItem) ItemFactory.buildItem(context, itemId);
            getInventory().getWeapons().add(weaponItem);
            updateWeaponInDatabase(weaponItem.getId(), weaponItem.getCurrHealth());
        } else {
            getInventory().addItem(itemId, quantity);
            updateItemInDatabase(itemId);
        }

        //Toast.makeText(context, "You received " + quantity + " " + ItemFactory.buildItem(context, itemId).getName(), Toast.LENGTH_SHORT).show();
    }

    public Quest getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(Quest activeQuest) {
        this.activeQuest = activeQuest;
    }

    public void awardXP(int xp) {
        player.setXp(player.getXp() + xp);
        updatePlayerInDatabase();
    }
}
