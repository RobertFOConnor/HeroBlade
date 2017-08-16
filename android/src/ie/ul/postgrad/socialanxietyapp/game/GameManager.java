package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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

import ie.ul.postgrad.socialanxietyapp.App;
import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.R;
import ie.ul.postgrad.socialanxietyapp.database.DBHelper;
import ie.ul.postgrad.socialanxietyapp.database.WebDBHelper;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.screens.LevelUpActivity;

/**
 * Created by Robert on 15-Mar-17.
 * <p>
 * Game Manager singleton class for handling all interactions with the main game objects
 * and syncing game data with the database.
 */

public class GameManager {

    //SQLLite objects
    private static DBHelper dbHelper;
    private static final GameManager ourInstance = new GameManager();

    //Game stats (XP)
    public static final int playerStartHP = 100;
    public static final int villageXP = 50;
    public static final int blacksmithXP = 50;
    public static final int villagerTalkXP = 25;
    public static final boolean TESTING = true;

    private static final boolean REMOTE_DATABASE_ENABLED = true;
    private static final String REMOTE_PLAYER_INSERT = "remote_player";
    private static final String REMOTE_ITEM_INSERT = "remote_item";
    private static final String REMOTE_WEAPON_INSERT = "remote_weapon";

    public static GameManager getInstance() {
        return ourInstance;
    }

    public boolean initDatabaseHelper(Context context, String id, String name, String email, String password) { //returns true if user is already in database
        //Initialize database helper
        initDatabaseHelper(context);

        if (!dbHelper.userExists(id)) { //If player doesn't exist, create a new one.
            dbHelper.insertUser(id, name, email, password);
            dbHelper.insertPlayer(new Player(id, name, 0, 1, 0, playerStartHP, playerStartHP));
            dbHelper.insertAvatar(new Avatar(1, 1, 1, 1));
            dbHelper.insertStats(id);
            giveWeapon(context, WeaponFactory.buildWeapon(context, 43));
            printTables();
            return false;
        }
        printTables();
        return true;
    }

    public DBHelper initDatabaseHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    public Player getPlayer() {
        return dbHelper.getPlayer();
    }

    public Avatar getAvatar() {
        return dbHelper.getAvatar(getPlayer().getId());
    }

    public Inventory getInventory() {
        return new Inventory(dbHelper.getItems(), dbHelper.getWeapons(), dbHelper.getChests());
    }

    public ArrayList<ChestItem> getChests() {
        return dbHelper.getChests();
    }

    public void giveItem(int itemId, int amount) {
        int quantity = getInventory().getItems().get(itemId) + amount;
        String playerId = getPlayer().getId();

        if (dbHelper.getInventoryData(itemId).moveToFirst()) {
            if (quantity > 0) {
                dbHelper.updateItem(playerId, itemId, quantity);
            } else {
                dbHelper.deleteItem(itemId);
            }
        } else {
            dbHelper.insertItem(playerId, itemId, quantity);
        }

        if (REMOTE_DATABASE_ENABLED) {
            new addItemTask().execute(REMOTE_ITEM_INSERT, playerId, Integer.toString(itemId), Integer.toString(getInventory().getItems().get(itemId)));
        }
    }

    public void updateWeaponInDatabase(String UUID, int weaponId, int currHealth, boolean equipped) {
        String playerId = getPlayer().getId();

        if (dbHelper.getWeaponsData(UUID).moveToFirst()) {
            dbHelper.updateWeapon(playerId, UUID, weaponId, currHealth, equipped);
        } else {
            dbHelper.insertWeapon(playerId, UUID, weaponId, currHealth, equipped);
        }

        int equip = 0;
        if (equipped) {
            equip = 1;
        }

        if (REMOTE_DATABASE_ENABLED) {
            new addItemTask().execute(REMOTE_WEAPON_INSERT, playerId, Integer.toString(weaponId), Integer.toString(currHealth), UUID, Integer.toString(equip));
        }
    }

    public void printTables() {
        dbHelper.printAllTables();
    }

    public void updatePlayerInDatabase(Player player) {
        dbHelper.updatePlayer(player);
    }

    public void closeDatabase() {
        dbHelper.close();
    }

    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    public void giveWeapon(Context context, WeaponItem weaponItem) {
        weaponItem.setUUID(UUID.randomUUID().toString());
        boolean equipped = false;
        if (getInventory().getEquippedWeapons().size() < 6) {
            equipped = true;
        }
        updateWeaponInDatabase(weaponItem.getUUID(), weaponItem.getId(), weaponItem.getCurrHealth(), equipped);
        AchievementManager.checkSwordAchievements(context);
    }

    public void awardXP(Context context, int xp) {
        Player player = getPlayer();
        if (player.setXp(player.getXp() + xp)) { //Level up player.
            awardChest(context);

            Intent intent = new Intent(context, LevelUpActivity.class);
            context.startActivity(intent);
        }
        updatePlayerInDatabase(player);
    }

    public void answerSurveyQuestion(int question, int answer) {
        dbHelper.insertSurveyAnswer(getPlayer().getId(), question, answer);
    }

    public int getSurveyQuestion() {
        return dbHelper.getSurveyData(getPlayer().getId()).getCount();
    }

    public void awardMoney(int money) {
        Player player = getPlayer();
        player.setMoney(player.getMoney() + money);
        updatePlayerInDatabase(player);
    }

    private void awardChest(Context context) {
        int random = (int) (Math.random() * 10);
        int chestId = ChestItem.NORMAL_CHEST; // normal chest default.

        if (random == 9) {
            chestId = ChestItem.RARE_CHEST; // 1 in 10 chance of rare chest.
        } else if (random > 5) {
            chestId = ChestItem.GOLD_CHEST; // 4 in 10 chance of gold chest.
        }
        ChestItem chest = (ChestItem) ItemFactory.buildItem(context, chestId);
        chest.setUID(UUID.randomUUID().toString());
        dbHelper.insertChest(getPlayer().getId(), chest.getUID(), chest.getId(), chest.getCurrDistance());
    }

    public int getVillageCount() {
        return countPMarkerTypes(MarkerFactory.ID_VILLAGE);
    }

    public int getBlacksmithCount() {
        return countPMarkerTypes(MarkerFactory.ID_BLACKSMITH);
    }

    public int countPMarkerTypes(int type) {
        ArrayList<ConsumedLocation> locations = getLocations();
        int typeCount = 0;
        for (ConsumedLocation l : locations) {
            if (l.getType() == type) {
                typeCount++;
            }
        }
        return typeCount;
    }

    public void updateChest(ChestItem chest) {
        dbHelper.updateChest(getPlayer().getId(), chest.getUID(), chest.getId(), chest.getCurrDistance());
    }

    public void removeChest() {
        ChestItem chest = getChests().get(0);
        dbHelper.removeChest(chest.getUID());
    }

    public ArrayList<Integer> getFoundWeapons() {
        ArrayList<Integer> foundWeapons = new ArrayList<>();
        ArrayList<WeaponItem> weapons = dbHelper.getWeapons();

        for (WeaponItem weaponItem : weapons) {
            if (!foundWeapons.contains(weaponItem.getId())) {
                foundWeapons.add(weaponItem.getId());
            }
        }
        return foundWeapons;
    }

    public WeaponItem unlockWeapon(Context context, int rarity) {
        WeaponItem weaponReward = WeaponFactory.getRandomWeaponByRarity(context, rarity);
        giveWeapon(context, weaponReward);
        return weaponReward;
    }

    public void consumeFoodItem(Context context, int id) {
        Player player = getPlayer();

        if (player.getCurrHealth() < player.getMaxHealth()) {
            FoodItem food = ((FoodItem) ItemFactory.buildItem(context, id));
            player.setCurrHealth(player.getCurrHealth() + food.getEnergy());
            giveItem(id, -1);
            updatePlayerInDatabase(player);
            Toast.makeText(context, player.getName() + " ate a " + food.getName() + " and restored +" + food.getEnergy() + " health.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, player.getName() + " already has full health.", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeWeapon(String UUID) {
        dbHelper.deleteWeapon(UUID);
        if (REMOTE_DATABASE_ENABLED) {
            new addItemTask().execute(REMOTE_WEAPON_INSERT, getPlayer().getId(), "-1", "-1", UUID, "0");
        }
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

    private String PostData(String[] values) {
        final int INSERT_TYPE = 0;//String specifying the type of remote call.
        final int PLAYER_ID = 1;
        final int ITEM_ID = 2;
        final int QUANTITY = 3;
        final int UID = 4;//For weapons.
        final int EQUIPPED = 5;//For weapons.
        String response = "";

        //android.os.Debug.waitForDebugger();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            List<NameValuePair> list = new ArrayList<>();
            String servletName = WebDBHelper.URL;
            String type = values[INSERT_TYPE];

            if (type.equals(REMOTE_ITEM_INSERT)) {
                servletName += "AddItem";
            } else if (type.equals(REMOTE_WEAPON_INSERT)) {
                servletName += "AddWeapon";
                list.add(new BasicNameValuePair("uid", values[UID]));
                list.add(new BasicNameValuePair("equipped", values[EQUIPPED]));
            }
            list.add(new BasicNameValuePair("player_id", values[PLAYER_ID]));
            list.add(new BasicNameValuePair("item_id", values[ITEM_ID]));
            list.add(new BasicNameValuePair("quantity", values[QUANTITY]));

            HttpPost httpPost = new HttpPost(servletName);
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpResponse.getEntity();
            response = WebDBHelper.readResponse(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public void addUsedLocation(LatLng latLng, int type) {
        String id = getPlayer().getId();
        if (dbHelper.getLocationData(latLng.latitude, latLng.longitude).moveToFirst()) {
            dbHelper.updateLocation(id, latLng.latitude, latLng.longitude, type, System.nanoTime());
        } else {
            dbHelper.insertLocation(id, latLng.latitude, latLng.longitude, type, System.nanoTime());
        }
    }

    public ConsumedLocation hasUsedLocation(LatLng latLng) {
        return dbHelper.getLocation(latLng.latitude, latLng.longitude);
    }

    public ArrayList<ConsumedLocation> getLocations() {
        return dbHelper.getLocations();
    }

    public void addChestOpened() {
        Stats stats = dbHelper.getStats();
        stats.addChestsOpened();
        dbHelper.updateStats(getPlayer().getId(), stats);
    }

    public void addWin() {
        Stats stats = dbHelper.getStats();
        stats.addWin();
        dbHelper.updateStats(getPlayer().getId(), stats);
    }

    public Stats getStats() {
        return dbHelper.getStats();
    }
}
