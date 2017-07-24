package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.database.DBHelper;
import ie.ul.postgrad.socialanxietyapp.database.WebDBHelper;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.Item;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.game.quest.Quest;
import ie.ul.postgrad.socialanxietyapp.screens.LevelUpActivity;

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

    public void startGame(Context context, String id, String name, String email, String password) {
        //Initialize database helper
        databaseHelper = new DBHelper(context);

        if (!databaseHelper.userExists()) {
            player = new Player(id, name, 0, 1, 0, 150, 150);
            inventory = new Inventory();
            databaseHelper.insertUser(id, name, email, password);
            databaseHelper.insertPlayer(player);
            databaseHelper.insertAvatar(new Avatar(0, 0, 0, 0));
            awardChest(context);
        } else {
            player = databaseHelper.getPlayer();
            inventory = new Inventory(databaseHelper.getItems(), databaseHelper.getWeapons(), databaseHelper.getChests(), context);
        }
    }

    public void startGame(Context context) {
        databaseHelper = new DBHelper(context);
    }

    public Player getPlayer() {
        return databaseHelper.getPlayer();
    }

    public Avatar getAvatar() {
        return databaseHelper.getAvatar(player.getId());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ArrayList<ChestItem> getChests() {
        return databaseHelper.getChests();
    }

    public void updateItemInDatabase(int itemId) {
        int quantity = getInventory().getItems().get(itemId);

        if (databaseHelper.getInventoryData(itemId).moveToFirst()) {
            if (quantity > 0) {
                databaseHelper.updateItem(player.getId(), itemId, quantity);
            } else {
                databaseHelper.deleteItem(itemId);
            }
        } else {
            databaseHelper.insertItem(player.getId(), itemId, quantity);
        }
    }

    public void updateWeaponInDatabase(String UUID, int itemId, int currHealth, boolean equipped) {

        if (databaseHelper.getWeaponsData(UUID).moveToFirst()) {
            databaseHelper.updateWeapon(player.getId(), UUID, itemId, currHealth, equipped);
        } else {
            databaseHelper.insertWeapon(player.getId(), UUID, itemId, currHealth, equipped);
        }
    }

    public void printTables() {
        databaseHelper.printAllTables();
    }

    public void updatePlayerInDatabase(Player player) {
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
            boolean equipped = false;
            if (inventory.getEquippedWeapons().size() < 6) {
                equipped = true;
            }
            weaponItem.setEquipped(equipped);
            inventory.getWeapons().add(weaponItem);
            updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth(), equipped);
        } else {
            getInventory().addItem(itemId, quantity);
            updateItemInDatabase(itemId);
            //new addItemTask().execute(player.getId(), Integer.toString(itemId), Integer.toString(getInventory().getItems().get(itemId)));
        }

        //Toast.makeText(context, "You received " + quantity + " " + ItemFactory.buildItem(context, itemId).getName(), Toast.LENGTH_SHORT).show();
    }

    public Quest getActiveQuest() {
        return activeQuest;
    }

    public void setActiveQuest(Quest activeQuest) {
        this.activeQuest = activeQuest;
    }

    public void awardXP(Context context, int xp) {
        if (player.setXp(player.getXp() + xp)) {
            awardChest(context);

            Intent intent = new Intent(context, LevelUpActivity.class);
            context.startActivity(intent);
        }
        updatePlayerInDatabase(player);
    }

    public void awardMoney(int money) {
        player.setMoney(player.getMoney()+ money);
        updatePlayerInDatabase(player);
    }

    public void awardChest(Context context) {
        int random = (int) (Math.random() * 10);
        int chestId = ChestItem.NORMAL_CHEST; // normal chest default.

        if (random == 9) {
            chestId = ChestItem.RARE_CHEST; // 1 in 10 chance of rare chest.
        } else if (random > 5) {
            chestId = ChestItem.GOLD_CHEST; // 4 in 10 chance of gold chest.
        }
        ChestItem chest = (ChestItem) ItemFactory.buildItem(context, chestId);
        chest.setUID(UUID.randomUUID().toString());
        inventory.getChests().add(chest);
        databaseHelper.insertChest(player.getId(), chest.getUID(), chest.getId(), chest.getCurrDistance());
    }

    public void updateChest(ChestItem chest) {
        databaseHelper.updateChest(player.getId(), chest.getUID(), chest.getId(), chest.getCurrDistance());
    }

    public void removeChest() {
        ChestItem chest = getChests().get(0);
        databaseHelper.removeChest(chest.getUID());
    }

    private void openChest(int chestId) {
        if (chestId == ChestItem.NORMAL_CHEST) {

        }
    }

    public WeaponItem unlockWeapon(Context context, int rarity) {
        WeaponItem weaponReward = WeaponFactory.getRandomWeaponByRarity(context, rarity);
        weaponReward.setUUID(UUID.randomUUID().toString());
        boolean equipped = false;
        if (inventory.getEquippedWeapons().size() < 6) {
            equipped = true;
        }
        weaponReward.setEquipped(equipped);
        inventory.getWeapons().add(weaponReward);
        databaseHelper.insertWeapon(player.getId(), weaponReward.getUUID(), weaponReward.getId(), weaponReward.getCurrHealth(), weaponReward.isEquipped());
        return weaponReward;
    }

    public void consumeFoodItem(Context context, int id) {

        if (player.getCurrHealth() < player.getMaxHealth()) {
            FoodItem food = ((FoodItem) ItemFactory.buildItem(context, id));
            player.setCurrHealth(player.getCurrHealth() + food.getEnergy());
            inventory.removeItem(id, 1);
            updateItemInDatabase(id);
            updatePlayerInDatabase(player);
            Toast.makeText(context, player.getName() + " ate a " + food.getName() + " and restored +" + food.getEnergy() + " health.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, player.getName() + " already has full health.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeWeapon(String UUID) {
        WeaponItem weaponItem = inventory.getWeapon(UUID);
        inventory.getWeapons().remove(weaponItem);
        databaseHelper.deleteWeapon(UUID);
    }

    private class addItemTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            return PostData(params);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    private String PostData(String[] valuse) {
        String response = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(WebDBHelper.URL + "AddItem");
            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("player_id", valuse[0]));
            list.add(new BasicNameValuePair("item_id", valuse[1]));
            list.add(new BasicNameValuePair("quantity", valuse[2]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            httpResponse.getEntity();
            response = WebDBHelper.readResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
