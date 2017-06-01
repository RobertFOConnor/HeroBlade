package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.DBHelper;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
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
            databaseHelper.insertPlayer(userName, "", 0, 1, 0, 20);
            databaseHelper.insertAvatar(new Avatar(0, 0));

            System.out.println("PLAYER INSERTED ");
        } else {
            if(!databaseHelper.getPlayer(1).getName().equals(userName)) {
                databaseHelper.deletePlayer(1);
                databaseHelper.deleteAvatar(1);

                databaseHelper.insertPlayer(userName, "", 0, 1, 0, 20);
                databaseHelper.insertAvatar(new Avatar(0, 0));
            }
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

    public void updateWeaponInDatabase(String UUID, int itemId, int currHealth) {

        if (databaseHelper.getWeaponsData(UUID).moveToFirst()) {
            databaseHelper.updateWeapon(1, UUID, itemId, currHealth);
        } else {
            databaseHelper.insertWeapon(1, UUID, itemId, currHealth);
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
        Item item = ItemFactory.buildItem(context, itemId);
        if (item instanceof WeaponItem) {
            WeaponItem weaponItem = (WeaponItem) item;
            weaponItem.setUUID(UUID.randomUUID().toString());
            getInventory().getWeapons().add(weaponItem);
            updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth());
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

    public void consumeFoodItem(Context context, int id) {
        if (player.getCurrHealth() < player.getMaxHealth()) {
            FoodItem food = ((FoodItem) ItemFactory.buildItem(context, id));
            player.setCurrHealth(player.getCurrHealth() + food.getEnergy());
            inventory.removeItem(id, 1);
            updateItemInDatabase(id);
            updatePlayerInDatabase();
            Toast.makeText(context, player.getName() + " ate a " + food.getName() + " and restored +" + food.getEnergy() + " health.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, player.getName() + " already has full health.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeWeapon(String UUID) {
        System.out.println("ITEMS IN WEAPON TABLE: " + databaseHelper.getWeapons().size());
        WeaponItem weaponItem = inventory.getWeapon(UUID);
        inventory.getWeapons().remove(weaponItem);
        databaseHelper.deleteWeapon(UUID);
        System.out.println("ITEMS IN WEAPON TABLE: " + databaseHelper.getWeapons().size());
    }
}
