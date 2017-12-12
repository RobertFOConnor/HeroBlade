package ie.ul.postgrad.socialanxietyapp.game;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AndroidRuntimeException;
import android.util.Log;

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
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import ie.ul.postgrad.socialanxietyapp.Avatar;
import ie.ul.postgrad.socialanxietyapp.MoodEntry;
import ie.ul.postgrad.socialanxietyapp.database.DBHelper;
import ie.ul.postgrad.socialanxietyapp.database.WebDBHelper;
import ie.ul.postgrad.socialanxietyapp.game.factory.ItemFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.MarkerFactory;
import ie.ul.postgrad.socialanxietyapp.game.factory.WeaponFactory;
import ie.ul.postgrad.socialanxietyapp.game.item.ChestItem;
import ie.ul.postgrad.socialanxietyapp.game.item.FoodItem;
import ie.ul.postgrad.socialanxietyapp.game.item.WeaponItem;
import ie.ul.postgrad.socialanxietyapp.receiver.AlarmReceiver;
import ie.ul.postgrad.socialanxietyapp.screens.LevelUpActivity;

import static android.content.Context.ALARM_SERVICE;

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
    private static final int playerStartHP = 50;
    private static final int playerLevelUpHP = 25;
    public static final int villageXP = 150;
    public static final int blacksmithXP = 150;
    public static final int craftingXP = 250;
    public static boolean TESTING = false;

    public static GameManager getInstance() {
        return ourInstance;
    }

    public boolean initDatabase(Context context, String id, String name, String email, String password) { //returns true if user is already in database
        //Initialize database helper
        initDatabase(context);

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

    public DBHelper initDatabase(Context context) {
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
        new RemoteServerTask().execute(this);
    }

    public void updateWeaponInDatabase(WeaponItem weapon) {
        String playerId = getPlayer().getId();

        if (dbHelper.getWeaponsData(weapon.getUUID()).moveToFirst()) {
            dbHelper.updateWeapon(playerId, weapon);
        } else {
            dbHelper.insertWeapon(playerId, weapon);
        }
    }

    private void printTables() {
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

    public void giveWeapon(WeaponItem weaponItem) {
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
        awardMoney(100);

        try {
            Intent intent = new Intent(context, LevelUpActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (AndroidRuntimeException e) {
            e.printStackTrace();
        }
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
        dbHelper.insertChest(getPlayer().getId(), chest);
    }

    public int getVillageCount() {
        return countPMarkerTypes(MarkerFactory.ID_VILLAGE);
    }

    int getBlacksmithCount() {
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
        dbHelper.updateChest(getPlayer().getId(), chest);
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
        giveWeapon(weaponReward);
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

    private static class RemoteServerTask extends AsyncTask<GameManager, Integer, String> {

        @Override
        protected String doInBackground(GameManager... params) {
            return PostData(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
        }
    }

    private static List<NameValuePair> list;

    private static String PostData(GameManager gm) {
        String response = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            String servletName = WebDBHelper.URL + "UpdateTables";
            list = new ArrayList<>();

            Player player = gm.getPlayer();
            keyValuePair("id", player.getId());
            keyValuePair("name", player.getName());
            keyValuePair("level", String.valueOf(player.getLevel()));
            keyValuePair("xp", String.valueOf(player.getXp()));
            keyValuePair("money", String.valueOf(player.getMoney()));
            keyValuePair("max_health", String.valueOf(player.getMaxHealth()));
            keyValuePair("curr_health", String.valueOf(player.getCurrHealth()));
            keyValuePair("sword_id", player.getBaseSword());

            Inventory inventory = gm.getInventory();
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

            ArrayList<ConsumedLocation> locations = gm.getLocations();
            for (int i = 0; i < locations.size(); i++) {
                ConsumedLocation l = locations.get(i);
                keyValuePair("location_lat_" + i, String.valueOf(l.getLat()));
                keyValuePair("location_lng_" + i, String.valueOf(l.getLng()));
                keyValuePair("location_type_" + i, String.valueOf(l.getType()));
                keyValuePair("location_time_" + i, String.valueOf(l.getTimeUsed()));
            }

            ArrayList<ChestItem> chests = gm.getChests();
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

            ArrayList<MoodEntry> moodRatings = dbHelper.getMoodEntries();
            for (int i = 0; i < moodRatings.size(); i++) {
                MoodEntry rating = moodRatings.get(i);
                keyValuePair("mood_rating_" + i, String.valueOf(rating.getRating()));
                keyValuePair("mood_description_" + i, rating.getDescription());
                keyValuePair("mood_date_" + i, rating.getDate());
            }

            ArrayList<StepEntry> steps = dbHelper.getDailySteps();
            for (int i = 0; i < steps.size(); i++) {
                StepEntry step = steps.get(i);
                keyValuePair("step_count_" + i, String.valueOf(step.getSteps()));
                keyValuePair("step_date_" + i, step.getDate());
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

    private static void keyValuePair(String key, String value) {
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

    ConsumedLocation hasUsedLocation(LatLng latLng) {
        return dbHelper.getLocation(latLng.latitude, latLng.longitude);
    }

    private ArrayList<ConsumedLocation> getLocations() {
        return dbHelper.getLocations();
    }

    public void addChestOpened() {
        Stats stats = dbHelper.getStats();
        stats.addChestsOpened();
        dbHelper.updateStats(getPlayer().getId(), stats);
    }

    public void setMoodRatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class); // AlarmReceiver1 = broadcast receiver

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);

            Calendar alarmStartTime = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            alarmStartTime.set(Calendar.HOUR_OF_DAY, 20);
            alarmStartTime.set(Calendar.MINUTE, 0);
            alarmStartTime.set(Calendar.SECOND, 0);
            if (now.after(alarmStartTime)) {
                Log.d("Hey", "Added a day");
                alarmStartTime.add(Calendar.DATE, 1);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        Log.d("Alarm", "Alarms set for everyday 8 am.");
    }

    public void addWin() {
        Stats stats = dbHelper.getStats();
        stats.addWin();
        dbHelper.updateStats(getPlayer().getId(), stats);
    }

    Stats getStats() {
        return dbHelper.getStats();
    }

    public void eraseData() {
        dbHelper.clearTables();
    }

    public float getDistance(Context context) {
        return initDatabase(context).getDistance();
    }

    public void recordStep(Context context, float totalDistance, int steps) {
        initDatabase(context).insertStepsEntry(totalDistance, steps);
    }
}
