package ie.ul.postgrad.socialanxietyapp.game;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.Avatar;
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
    public static final int playerStartHP = 50;
    public static final int playerLevelUpHP = 25;
    public static final int villageXP = 150;
    public static final int blacksmithXP = 150;
    public static final int villagerTalkXP = 25;
    public static final int craftingXP = 250;
    public static boolean TESTING = false;

    public static GameManager getInstance() {
        return ourInstance;
    }

    public boolean initDatabaseHelper(Context context, String id, String name, String email, String password) { //returns true if user is already in database
        //Initialize database helper
        initDatabaseHelper(context);

        if (!dbHelper.userExists(id)) { //If player doesn't exist, create a new one.
            WeaponItem weaponItem = generateStartingWeapon(context);
            dbHelper.insertUser(id, name, email, password);
            dbHelper.insertPlayer(new Player(id, name, 0, 1, 0, playerStartHP, playerStartHP, weaponItem.getUUID()));
            dbHelper.insertAvatar(new Avatar(1, 1, 1, 1));
            dbHelper.insertStats(id);
            updateWeaponInDatabase(weaponItem);
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
    }

    public void testRemoteServer() {
        new RemoteServerTask().execute();
    }

    public void updateWeaponInDatabase(WeaponItem weapon) {
        String playerId = getPlayer().getId();

        if (dbHelper.getWeaponsData(weapon.getUUID()).moveToFirst()) {
            dbHelper.updateWeapon(playerId, weapon.getUUID(), weapon.getId(), weapon.getCurrHealth(), weapon.isEquipped());
        } else {
            dbHelper.insertWeapon(playerId, weapon.getUUID(), weapon.getId(), weapon.getCurrHealth(), weapon.isEquipped());
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
        weaponItem.setEquipped(false);
        if (getInventory().getEquippedWeapons().size() < 6) {
            weaponItem.setEquipped(true);
        }
        updateWeaponInDatabase(weaponItem);
    }

    private WeaponItem generateStartingWeapon(Context context) {
        WeaponItem weaponItem = WeaponFactory.buildWeapon(context, 43);
        weaponItem.setUUID(UUID.randomUUID().toString());
        weaponItem.setEquipped(true);
        return weaponItem;
    }

    public void awardXP(Context context, int xp) {
        Player player = getPlayer();
        if (player.setXp(player.getXp() + xp)) { //Level up player.
            levelUp(player, context);
        }
        updatePlayerInDatabase(player);
    }

    private void levelUp(Player player, Context context) {
        player.setXp(player.getXp() - XPLevels.XP_LEVELS[player.getLevel()]);
        player.setLevel(player.getLevel() + 1);
        player.setMaxHealth(player.getMaxHealth() + GameManager.playerLevelUpHP);
        player.setCurrHealth(player.getMaxHealth());
        awardChest(context);
        Intent intent = new Intent(context, LevelUpActivity.class);
        context.startActivity(intent);
    }

    public void answerSurveyQuestion(int question, int answer) {
        dbHelper.insertSurveyAnswer(getPlayer().getId(), question, answer);
    }

    public int getSurveyQuestion() {
        return dbHelper.getSurveyData(getPlayer().getId()).getCount() % 12;
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

    public void awardTestChest(Context context) {
        int random = (int) (Math.random() * 10);
        int chestId = ChestItem.NORMAL_CHEST; // normal chest default.

        if (random == 9) {
            chestId = ChestItem.RARE_CHEST; // 1 in 10 chance of rare chest.
        } else if (random > 5) {
            chestId = ChestItem.GOLD_CHEST; // 4 in 10 chance of gold chest.
        }
        ChestItem chest = (ChestItem) ItemFactory.buildItem(context, chestId);
        chest.setUID(UUID.randomUUID().toString());
        dbHelper.insertChest(getPlayer().getId(), chest.getUID(), chest.getId(), 10);
    }

    public int getVillageCount() {
        return countPMarkerTypes(MarkerFactory.ID_VILLAGE);
    }

    public int getBlacksmithCount() {
        return countPMarkerTypes(MarkerFactory.ID_BLACKSMITH);
    }

    private int countPMarkerTypes(int type) {
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

    public void removeChest(ChestItem chest) {
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
        }
    }

    public void removeWeapon(String UUID) {
        dbHelper.deleteWeapon(UUID);
    }

    private class RemoteServerTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return PostData(params);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private List<NameValuePair> list;

    private String PostData(String[] values) {
        String response = "";

        //android.os.Debug.waitForDebugger();
        try {
            HttpClient httpClient = new DefaultHttpClient();
            String servletName = WebDBHelper.URL + "UpdateTables";
            list = new ArrayList<>();

            Player player = getPlayer();
            keyValuePair("id", player.getId());
            keyValuePair("name", player.getName());
            keyValuePair("level", String.valueOf(player.getLevel()));
            keyValuePair("xp", String.valueOf(player.getXp()));
            keyValuePair("money", String.valueOf(player.getMoney()));
            keyValuePair("max_health", String.valueOf(player.getMaxHealth()));
            keyValuePair("curr_health", String.valueOf(player.getCurrHealth()));
            keyValuePair("sword_id", player.getBaseSword());

            Inventory inventory = getInventory();
            for (int i = 0; i < inventory.getItems().size(); i++) {
                keyValuePair("item_id_" + i, String.valueOf(inventory.getItems().keyAt(i)));
                keyValuePair("quantity_" + i, String.valueOf(inventory.getItems().valueAt(i)));
            }

            for (int i = 0; i < inventory.getWeapons().size(); i++) {
                WeaponItem w = inventory.getWeapons().get(i);
                keyValuePair("weapon_uid_" + i, w.getUUID());
                keyValuePair("weapon_id_" + i, String.valueOf(w.getId()));
                keyValuePair("weapon_curr_health_" + i, String.valueOf(w.getCurrHealth()));
                keyValuePair("weapon_equipped_" + i, w.getEquippedAsString());
            }

            ArrayList<ConsumedLocation> locations = getLocations();
            for (int i = 0; i < locations.size(); i++) {
                ConsumedLocation l = locations.get(i);
                keyValuePair("location_lat_" + i, String.valueOf(l.getLat()));
                keyValuePair("location_lng_" + i, String.valueOf(l.getLng()));
                keyValuePair("location_type_" + i, String.valueOf(l.getType()));
                keyValuePair("location_time_" + i, String.valueOf(l.getTimeUsed()));
            }

            ArrayList<ChestItem> chests = getChests();
            for (int i = 0; i < chests.size(); i++) {
                keyValuePair("chest_uid_" + i, chests.get(i).getUID());
                keyValuePair("chest_id_" + i, String.valueOf(chests.get(i).getId()));
                keyValuePair("chest_distance_left_" + i, String.valueOf(chests.get(i).getCurrDistance()));
            }

            ArrayList<SurveyAnswer> surveyAnswers = dbHelper.getSurveyAnswers();
            for (int i = 0; i < surveyAnswers.size(); i++) {
                SurveyAnswer ans = surveyAnswers.get(i);
                keyValuePair("survey_question_" + i, String.valueOf(ans.getQuestion()));
                keyValuePair("survey_answer_" + i, String.valueOf(ans.getAnswer()));
                keyValuePair("survey_date_" + i, ans.getDate());
            }

            HttpPost httpPost = new HttpPost(servletName);
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            httpResponse.getEntity();
            response = WebDBHelper.readResponse(httpResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void keyValuePair(String key, String value) {
        list.add(new BasicNameValuePair(key, value));
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

    public void addSteps(int steps) {
        Stats stats = dbHelper.getStats();
        stats.setTotalSteps(steps);
        dbHelper.updateStats(getPlayer().getId(), stats);
    }

    public Stats getStats() {
        return dbHelper.getStats();
    }

    public void eraseData() {
        dbHelper.clearTables();
    }

    public float getDistance(Context context) {
        return initDatabaseHelper(context).getDistance();
    }

    public void recordStep(Context context, float totalDistance) {
        initDatabaseHelper(context).insertStepsEntry(totalDistance);
    }
}
